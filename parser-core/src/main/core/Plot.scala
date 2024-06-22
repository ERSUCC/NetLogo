// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.core

case class Plot(display: Option[String],
  left: Int = 0, top: Int = 0, right: Int = 5, bottom: Int = 5,
  xAxis: Option[String] = None, yAxis: Option[String] = None,
  xmin: Double = 0, xmax: Double = 0,
  ymin: Double = 0, ymax: Double = 0,
  autoPlotX: Boolean = true, autoPlotY: Boolean = true,
  legendOn: Boolean = false, setupCode: String = "", updateCode: String = "",
  pens: List[Pen] = Nil) extends Widget {
    override def convertSource(conversion: String => String): Plot =
      copy(setupCode = conversion(setupCode), updateCode = conversion(updateCode), pens = pens.map(_.convertSource(conversion)))
  }

case class Pen(display: String,
  interval: Double = 1,
  mode: Int = 0,
  color: Int = 0,
  inLegend: Boolean = false,
  setupCode: String = "",
  updateCode: String = "") {
    def convertSource(conversion: String => String): Pen =
      copy(setupCode = conversion(setupCode), updateCode = conversion(updateCode))
  }
