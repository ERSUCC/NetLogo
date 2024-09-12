// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app

import java.awt.Frame
import java.awt.event.ActionEvent
import java.net.URI
import javax.swing.{ Action, AbstractAction }

import org.nlogo.core.I18N
import org.nlogo.api.Version
import org.nlogo.swing.{ BrowserLauncher, EmbeddedBrowser, UserAction },
  BrowserLauncher.docPath,
  UserAction._

class BrowseAction(frame: Frame, name: String, title: String, uri: URI)
extends AbstractAction(name)
with MenuAction {
  category = HelpCategory
  group    = HelpWebGroup

  override def actionPerformed(e: ActionEvent) {
    new EmbeddedBrowser(frame, title, uri).setVisible(true)
  }
}

object HelpActions {
  def apply(frame: Frame): Seq[Action] = {
    Seq(
    new BrowseAction(frame, I18N.gui.get("menu.help.netLogoUserManual"), I18N.gui.get("menu.help.netLogoUserManual"),
      docPath("index.html").toUri),
    new BrowseAction(frame, I18N.gui.get("menu.help.netLogoDictionary"), I18N.gui.get("menu.help.netLogoDictionary"),
      docPath("index2.html").toUri),
    new BrowseAction(frame, I18N.gui.get("menu.help.bind"), I18N.gui.get("menu.help.bind"),
      new URI("https://ccl.northwestern.edu/netlogo/bind")),
    new BrowseAction(frame, I18N.gui.get("menu.help.netLogoUsersGroup"), I18N.gui.get("menu.help.netLogoUsersGroup"),
      new URI("http://groups.google.com/d/forum/netlogo-users")),
    new BrowseAction(frame, I18N.gui.get("menu.help.introToABM"), I18N.gui.get("menu.help.introToABM"),
      new URI("https://mitpress.mit.edu/books/introduction-agent-based-modeling")),
    new BrowseAction(frame, I18N.gui.get("menu.help.donate"), I18N.gui.get("menu.help.donate"),
      new URI("http://ccl.northwestern.edu/netlogo/giving.shtml")) {
      putValue(ActionGroupKey, HelpAboutGroup)
    })
  }
}

class ShowAboutWindow(frame: Frame)
extends AbstractAction(I18N.gui.getN("menu.help.aboutVersion", Version.versionDropZeroPatch))
with MenuAction {
  category = HelpCategory
  group    = HelpAboutGroup

  override def actionPerformed(e: ActionEvent): Unit = {
    println(e.getSource)
    new AboutWindow(frame).setVisible(true)
  }
}
