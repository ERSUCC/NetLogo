// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser

import java.awt.{ BorderLayout, Dimension, Graphics, GridBagConstraints, GridBagLayout, Insets }
import java.awt.event.{ MouseAdapter, MouseEvent }
import java.util.prefs.Preferences
import javax.swing.{ JEditorPane, JLabel, JPanel, JScrollPane, SwingConstants }
import org.nlogo.app.infotab.InfoFormatter
import org.nlogo.core.I18N
import org.nlogo.swing.{ CustomOptionPane, HoverDecoration, OptionDialog }
import org.nlogo.theme.{ InterfaceColors, ThemeSync }
import org.json.simple.parser.JSONParser
import org.json.simple.{ JSONArray, JSONObject }

import scala.io.Source

case class JsonObject(eventId: Int, date: String, title: String, fullText: String)

class NotificationBanner() extends JPanel with ThemeSync with HoverDecoration {
  private var JsonObjectList: List[JsonObject] = List()
  private val JsonUrl = "https://ccl.northwestern.edu/netlogo/announce-test.json"

  JsonObjectList = parseJsonToList(fetchJsonFromUrl())
  private var ScrollPane = new JScrollPane()
  private val LastSeenEventIdKey: String = "lastSeenEventId" // The key for the most recently seen event-id
  private var EditorPane: JEditorPane = new JEditorPane()
  // Label to display notification messages
  private val MessageLabel = new JLabel(s" ${getJsonObjectHead.getOrElse("")}   -  ${I18N.gui.get("dialog.interface.viewMore")}")
  private val CloseButton = new CloseButton()
  CloseButton.setPreferredSize(new Dimension(50, 50))
  setVisible(isShowNeeded())
  setPreferredSize(new Dimension(super.getPreferredSize.width, 40))

  setLayout(new GridBagLayout())

  // Configure constraints for MessageLabel
  val LabelConstraints = new GridBagConstraints()
  LabelConstraints.gridx = 0
  LabelConstraints.gridy = 0
  LabelConstraints.weightx = 1.0 // Take as much horizontal space as possible
  LabelConstraints.fill = GridBagConstraints.HORIZONTAL
  LabelConstraints.anchor = GridBagConstraints.WEST
  LabelConstraints.insets = new Insets(0, 5, 0, 5)

  add(MessageLabel, LabelConstraints)

  // Configure constraints for CloseButton
  val ButtonConstraints = new GridBagConstraints()
  ButtonConstraints.gridx = 1
  ButtonConstraints.gridy = 0 // Same row as MessageLabel
  ButtonConstraints.weightx = 0.0 // Do not take extra horizontal space
  ButtonConstraints.anchor = GridBagConstraints.EAST
  ButtonConstraints.fill = GridBagConstraints.NONE
  ButtonConstraints.insets = new Insets(0, 5, 0, 5)


  MessageLabel.setHorizontalAlignment(SwingConstants.LEFT)

  add(CloseButton, ButtonConstraints)

  CloseButton.addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit = {
      if (e.getButton == MouseEvent.BUTTON1) {
        setVisible(false) // Hide the banner when the close button is pressed
      }
    }
  })

  addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit = {
      showJsonInDialog()
    }
  })

  override def paintComponent(g: Graphics) {
    if (isHover)
      setBackground(InterfaceColors.ANNOUNCEMENTS_BANNER_BACKGROUND_HOVER)
    else
      setBackground(InterfaceColors.ANNOUNCEMENTS_BANNER_BACKGROUND)

    super.paintComponent(g)
  }
  def syncTheme(): Unit = {
    setBackground(InterfaceColors.ANNOUNCEMENTS_BANNER_BACKGROUND)
    MessageLabel.setForeground(InterfaceColors.ANNOUNCEMENTS_BANNER_TEXT)
    CloseButton.setForeground(InterfaceColors.ANNOUNCEMENTS_BANNER_TEXT)
    ScrollPane.getHorizontalScrollBar.setBackground(InterfaceColors.DIALOG_BACKGROUND)
    ScrollPane.getVerticalScrollBar.setBackground(InterfaceColors.DIALOG_BACKGROUND)
  }
  private def fetchJsonFromUrl(): String = {
    try {
      val source = Source.fromURL(JsonUrl)
      val content = source.mkString
      source.close()
      content
    } catch {
      case e: Exception =>
        ""
    }
  }

  // Method to parse JSON content to a list of JsonObject instances
  private def parseJsonToList(jsonContent: String): List[JsonObject] = {
    if(jsonContent.isEmpty){
      return Nil
    }

    val parser = new JSONParser()
    val jsonArray = parser.parse(jsonContent).asInstanceOf[JSONArray]
    jsonArray.toArray.flatMap { obj =>
      val jsonObject = obj.asInstanceOf[JSONObject]
      Option(jsonObject.get("event-id").asInstanceOf[Long].toInt).map { eventId =>
        val title = Option(jsonObject.get("title")).map(_.toString).getOrElse("")
        val fullText = Option(jsonObject.get("fullText")).map(_.toString).getOrElse("")
        val date = Option(jsonObject.get("date")).map(_.toString).getOrElse("")
        JsonObject(eventId, date, title, fullText)
      }
    }.toList.sortBy(_.eventId)(Ordering[Int].reverse)

  }

  // Method to show JSON content in a dialog
  private def showJsonInDialog(): Unit = {

    val prefs = Preferences.userRoot.node("/org/nlogo/NetLogo")
    try {
      if (isShowNeeded()) {
        val jsonContent = fetchJsonFromUrl()
        val formattedString = formatJsonObjectList(JsonObjectList)

        val lastSeenEventId = prefs.getInt(LastSeenEventIdKey, -1); // Returns -1 if "event-id" is not found
        if (JsonObjectList.head.eventId > lastSeenEventId) {


          val html = InfoFormatter.toInnerHtml(formattedString)

          if (!jsonContent.trim.isEmpty) {
            EditorPane = new JEditorPane {

              setDragEnabled(false)
              setEditable(false)
              setContentType("text/html")
              setOpaque(true)
              setBackground(InterfaceColors.CODE_BACKGROUND)
              setForeground(InterfaceColors.DEFAULT_COLOR) //Set the font color
              setText(html)
              setCaretPosition(0)
            }

            ScrollPane = new JScrollPane(EditorPane)
            ScrollPane.setPreferredSize(new Dimension(500, 400))
            val panel = new JPanel(new BorderLayout())
            panel.add(ScrollPane, BorderLayout.CENTER)
            val options: List[String] = List(I18N.gui.get("common.buttons.ok"))

            val optionPane = new CustomOptionPane(this, I18N.gui.get("dialog.interface.newsNotificationTitle"), ScrollPane,
              options)
            optionPane.setSize(new Dimension(500, 500))

            if (optionPane.getSelectedOption == "OK") {
              setVisible(false) // Hide NotificationBanner
              prefs.putInt("lastSeenEventId", JsonObjectList.head.eventId)
            }
          }
        }
      }

    }
  catch {
      case e: Exception =>{
        OptionDialog.showCustom(this, I18N.gui.get("error.dialog.unknown"), e.getMessage, null)
      }
    }
  }

  private val markdownParser = Parser.builder().build()
  private val htmlRenderer = HtmlRenderer.builder().build()

  private def formatJsonObjectList(jsonObjectList: List[JsonObject]): String = {
    jsonObjectList.map { obj =>
      // Convert fullText from Markdown to HTML
      val fullTextHtml = htmlRenderer.render(markdownParser.parse(obj.fullText))

      // Format title, date, and the converted HTML fullText
      s"<h3>* ${I18N.gui.get("dialog.interface.update")}: ${obj.date} -- ${obj.title}</h3>" +
        s"$fullTextHtml"
    }.mkString("<html>", "", "</html>")
  }

  private def getJsonObjectHead: Option[String] = {
    val jsonContent = fetchJsonFromUrl()
    JsonObjectList = parseJsonToList(jsonContent)

    // Return the title of the first item if JsonObjectList is not null and has elements
    JsonObjectList match {
      case Nil =>
        println("JsonObjectList is empty or Nil; hiding NotificationBanner.")
        this.setVisible(false) // Hide the NotificationBanner panel
        None
      case list =>
        this.setVisible(true)
        list.headOption.map(_.title)
    }
  }

  private def isShowNeeded(): Boolean = {
    val Prefs = Preferences.userRoot.node("/org/nlogo/NetLogo")
    val LastSeenEventId = Prefs.getInt(LastSeenEventIdKey, -1); // Returns -1 if "event-id" is not found
    //return true if JsonObjectList is non-empty and eventId of the first element is> lastSeenEventId; otherwise return false
    JsonObjectList.nonEmpty && JsonObjectList.head.eventId > LastSeenEventId
  }
}
