// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app.common

import java.awt.{ Color, Graphics }
import java.awt.event.{ MouseAdapter, MouseEvent }
import javax.swing.{ JLabel, JMenuItem, JPopupMenu }

import org.nlogo.core.{ AgentKind, I18N }

class LinePrompt(commandLine: CommandLine) extends JLabel {
  setText(getPrompt)

  private var mouseInBounds = false

  addMouseListener(new MouseAdapter {
    override def mouseEntered(e: MouseEvent) {
      mouseInBounds = true

      repaint()
    }

    override def mouseExited(e: MouseEvent) {
      mouseInBounds = false

      repaint()
    }

    override def mousePressed(e: MouseEvent)  {
      if (isEnabled) {
        val popMenu = new JPopupMenu("Ask who?")
        def addItem(name: String, clazz: AgentKind) {
          popMenu.add(new JMenuItem(name) {
            addActionListener { _ =>
              commandLine.agentKind(clazz)
              LinePrompt.this.repaint()
              commandLine.requestFocus()
            }
          })
        }
        addItem(I18N.gui.get("common.observer"), AgentKind.Observer)
        addItem(I18N.gui.get("common.turtles"), AgentKind.Turtle)
        addItem(I18N.gui.get("common.patches"), AgentKind.Patch)
        addItem(I18N.gui.get("common.links"), AgentKind.Link)
        popMenu.add(new JPopupMenu.Separator)
        val hintItem = new JMenuItem(I18N.gui.get("tabs.run.commandcenter.orusetabkey")) {setEnabled(false)}
        popMenu.add(hintItem)
        popMenu.show(LinePrompt.this, 0, getHeight)
      }
    }
  })

  private def getPrompt = {
    commandLine.kind match {
      case AgentKind.Observer => CommandLine.OBSERVER_PROMPT
      case AgentKind.Turtle   => CommandLine.TURTLE_PROMPT
      case AgentKind.Patch    => CommandLine.PATCH_PROMPT
      case AgentKind.Link     => CommandLine.LINK_PROMPT
    }
  }

  override def paintComponent(g: Graphics) {
    setText(getPrompt)

    if (mouseInBounds)
      setForeground(Color.BLUE)
    else
      setForeground(Color.BLACK)

    super.paintComponent(g)
  }
}
