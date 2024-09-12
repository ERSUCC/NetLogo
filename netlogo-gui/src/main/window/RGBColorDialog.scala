// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.window

import java.awt.Frame

import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.embed.swing.JFXPanel
import javafx.concurrent.Worker.State
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.scene.web.WebView

import javax.swing.{ JDialog, WindowConstants }

import netscape.javascript.JSObject

import org.nlogo.core.I18N

class Bridge {
  def print(text: String) {
    println(text)
  }
}

class RGBColorDialog(frame: Frame, modal: Boolean) extends JDialog(frame, I18N.gui.get("tools.colorpicker"), modal) {
  val bridge = new Bridge

  setSize(600, 400)
  setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)

  val panel = new JFXPanel

  add(panel)

  Platform.runLater(() => {
    val webView = new WebView

    webView.getEngine.load(getClass.getResource("/web/color-picker/index.html").toString)

    webView.getEngine.getLoadWorker.stateProperty.addListener({
      (value: ObservableValue[_ <: State], oldState: State, newState: State) => {
        webView.getEngine.executeScript("window").asInstanceOf[JSObject].setMember("bridge", bridge)
      }
    })

    panel.setScene(new Scene(new VBox(webView)))
  })

  def showDialog() {
    setVisible(true)
  }
}
