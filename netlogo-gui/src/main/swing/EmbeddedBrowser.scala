// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.swing

import java.awt.Frame
import java.net.URI
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import javax.swing.JDialog

class EmbeddedBrowser(frame: Frame, title: String, uri: URI) extends JDialog(frame, title, false) {
  setSize(600, 600)

  val panel = new JFXPanel

  add(panel)

  Platform.runLater(() => {
    val webView = new WebView

    webView.getEngine.load(uri.toString)

    panel.setScene(new Scene(new VBox(webView)))
  })
}
