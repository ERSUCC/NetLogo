// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.swing

import java.awt.{ Color, Dimension, Graphics }
import javax.swing.JPanel

class DropdownArrow extends JPanel {
  setOpaque(false)
  setPreferredSize(new Dimension(9, 5))

  override def paintComponent(g: Graphics) {
    val g2d = Utils.initGraphics2D(g)

    g2d.setColor(new Color(100, 100, 100))
    g2d.drawLine(0, 0, getWidth / 2, getHeight - 1)
    g2d.drawLine(getWidth / 2, getHeight - 1, getWidth - 1, 0)
  }
}
