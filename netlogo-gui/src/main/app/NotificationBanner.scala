// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser

import java.awt.{ BorderLayout, Dimension, GridBagConstraints, GridBagLayout, Insets }
import java.awt.event.{ MouseAdapter, MouseEvent }
import java.util.prefs.Preferences
import javax.swing.{ JEditorPane, JLabel, JPanel, JScrollPane, SwingConstants }
import org.nlogo.app.infotab.InfoFormatter
import org.nlogo.core.I18N
import org.nlogo.swing.{ CloseButton, CustomOptionPane, HoverDecoration, OptionPane }
import org.nlogo.theme.{ InterfaceColors, ThemeSync }
import org.json.simple.parser.JSONParser
import org.json.simple.{ JSONArray, JSONObject }

import scala.io.Source

case class JsonObject(eventId: Int, date: String, title: String, fullText: String)

class NotificationBanner extends JPanel with ThemeSync with HoverDecoration {
  private val JsonUrl = "https://ccl.northwestern.edu/netlogo/announce-test.json"

  private val jsonObjectList: Seq[JsonObject] = parseJsonToSeq(fetchJsonFromUrl())

  private val LastSeenEventIdKey: String = "lastSeenEventId" // The key for the most recently seen event-id
  private val editorPane: JEditorPane = new JEditorPane {
    setDragEnabled(false)
    setEditable(false)
    setContentType("text/html")
    setOpaque(true)
    setBackground(InterfaceColors.CODE_BACKGROUND)
    setForeground(InterfaceColors.DEFAULT_COLOR) // Set the font color
  }

  private val scrollPane: JScrollPane = new JScrollPane(editorPane) {
    setPreferredSize(new Dimension(500, 400))
  }
  private val messageLabel = new JLabel(s" ${getJsonObjectHead.getOrElse("")}")

  private val viewMoreLabel = new JLabel(s"<html><u>${I18N.gui.get("dialog.interface.viewMore")}</u></html>")
  private val closeButton = new CloseButton()
  closeButton.setPreferredSize(new Dimension(50, 50))
  setVisible(isShowNeeded)
  setPreferredSize(new Dimension(super.getPreferredSize.width, 40))

  setLayout(new GridBagLayout())

  // Configure constraints for messageLabel
  val labelConstraints = new GridBagConstraints()
  labelConstraints.gridx = 0
  labelConstraints.gridy = 0
  labelConstraints.weightx = 1.0 // Take as much horizontal space as possible
  labelConstraints.fill = GridBagConstraints.HORIZONTAL
  labelConstraints.anchor = GridBagConstraints.WEST
  labelConstraints.insets = new Insets(0, 5, 0, 5)

  add(messageLabel, labelConstraints)

  // Configure constraints for viewMoreLabel
  val viewMoreConstraints = new GridBagConstraints()
  viewMoreConstraints.gridx = 1
  viewMoreConstraints.gridy = 0 // Same row as messageLabel
  viewMoreConstraints.weightx = 0.0 // Do not take extra horizontal space
  viewMoreConstraints.anchor = GridBagConstraints.EAST
  viewMoreConstraints.insets = new Insets(0, 5, 0, 5)

  add(viewMoreLabel, viewMoreConstraints)

  // Configure constraints for closeButton
  val closeButtonConstraints = new GridBagConstraints()
  closeButtonConstraints.gridx = 2
  closeButtonConstraints.gridy = 0 // Same row as messageLabel
  closeButtonConstraints.weightx = 0.0 // Do not take extra horizontal space
  closeButtonConstraints.anchor = GridBagConstraints.EAST
  closeButtonConstraints.insets = new Insets(0, 5, 0, 5)

  add(closeButton, closeButtonConstraints)

  messageLabel.setHorizontalAlignment(SwingConstants.LEFT)
  closeButton.addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit = {
      if (e.getButton == MouseEvent.BUTTON1) {
        setVisible(false) // Hide the banner when the close button is pressed
      }
    }
  })

  viewMoreLabel.addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit = {
      showJsonInDialog(true)
      setVisible(false) //hide once clicked.
    }
  })
  
  def syncTheme(): Unit = {
    setBackground(InterfaceColors.ANNOUNCEMENTS_BANNER_BACKGROUND)
    messageLabel.setForeground(InterfaceColors.ANNOUNCEMENTS_BANNER_TEXT)
    closeButton.setForeground(InterfaceColors.ANNOUNCEMENTS_BANNER_TEXT)
    viewMoreLabel.setForeground(InterfaceColors.ANNOUNCEMENTS_BANNER_TEXT)
    scrollPane.getHorizontalScrollBar.setBackground(InterfaceColors.DIALOG_BACKGROUND)
    scrollPane.getVerticalScrollBar.setBackground(InterfaceColors.DIALOG_BACKGROUND)
    editorPane.setBackground(InterfaceColors.CODE_BACKGROUND)
    editorPane.setForeground(InterfaceColors.DEFAULT_COLOR) //Set the font color
  }
  private def fetchJsonFromUrl(): String = {
    try {
      val source = Source.fromURL(JsonUrl)
      val content = source.mkString
      source.close()
      content
    } catch {
      case e: Exception =>
        // NO-OP; likely the file could not be reached and we don't want to disable the app
        // Also possible the network connection failed or the URL has changed. This ensures the app can start and operate
        ""
    }
  }

  // Method to parse JSON content to a Seq of JsonObject instances
  private def parseJsonToSeq(jsonContent: String): Seq[JsonObject] = {
    if(jsonContent.isEmpty) {
      return Seq()
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
    }.toList.sortBy(_.eventId).reverse

  }

  // Method to show JSON content in a dialog
  //alwaysShow allows accessing this from the help menu forces it to be shown, even if it has been viewed before
  def showJsonInDialog(alwaysShow:Boolean) : Unit = {

    val prefs = Preferences.userRoot.node("/org/nlogo/NetLogo")
    try {
      if (isShowNeeded || alwaysShow) {

        val jsonContent = fetchJsonFromUrl
        val formattedString = formatJsonObjectList(jsonObjectList)

        val lastSeenEventId = prefs.getInt(LastSeenEventIdKey, -1); // Returns -1 if "event-id" is not found
        if (jsonObjectList.head.eventId > lastSeenEventId || alwaysShow) {

          val html = InfoFormatter.toInnerHtml(formattedString)

          if (!jsonContent.trim.isEmpty) {
            editorPane.setText(html)
            editorPane.setCaretPosition(0)
            val panel = new JPanel(new BorderLayout())
            panel.add(scrollPane, BorderLayout.CENTER)
            val options: List[String] = List(I18N.gui.get("common.buttons.ok"))

            val optionPane = new CustomOptionPane(this, I18N.gui.get("dialog.interface.newsNotificationTitle"), scrollPane,
              options)
            optionPane.setSize(new Dimension(500, 500))

            if (optionPane.getSelectedIndex == 0) {
              setVisible(false) // Hide NotificationBanner
              prefs.putInt("lastSeenEventId", jsonObjectList.head.eventId)
            }
          }
        }
      }
    }
    catch {
        case e: Exception =>
          new OptionPane(this, I18N.gui.get("error.dialog.connection"), I18N.gui.get("error.dialog.unableToConnect"), List(I18N.gui.get("common.buttons.ok")))
    }
  }

  private val markdownParser = Parser.builder().build()
  private val htmlRenderer = HtmlRenderer.builder().build()

  private def formatJsonObjectList(jsonObjectListArg: Seq[JsonObject]): String = {
    jsonObjectListArg.map { obj =>
      // Convert fullText from Markdown to HTML
      val fullTextHtml = htmlRenderer.render(markdownParser.parse(obj.fullText))

      // Format title, date, and the converted HTML fullText
      s"<h3>* ${I18N.gui.get("dialog.interface.update")}: ${obj.date} -- ${obj.title}</h3>" +
        s"$fullTextHtml"
    }.mkString("<html>", "", "</html>")
  }

  private def getJsonObjectHead: Option[String] = {

    jsonObjectList match {
      case head :: xs =>
        Option(head.title)
      case _ =>
        this.setVisible(false) // Hide the NotificationBanner panel
        None
    }
  }

  private def isShowNeeded: Boolean = {
    val Prefs = Preferences.userRoot.node("/org/nlogo/NetLogo")
    val LastSeenEventId = Prefs.getInt(LastSeenEventIdKey, -1); // Returns -1 if "event-id" is not found
    //return true if jsonObjectList is non-empty and eventId of the first element is> lastSeenEventId; otherwise return false
    jsonObjectList.nonEmpty && jsonObjectList.head.eventId > LastSeenEventId
  }
}
