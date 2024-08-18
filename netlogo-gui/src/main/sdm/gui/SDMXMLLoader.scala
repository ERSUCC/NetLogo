// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.sdm.gui

import java.awt.Point

import org.jhotdraw.framework.Figure

import org.nlogo.core.XMLElement

object SDMXMLLoader {
  def readDrawing(element: XMLElement): AggregateDrawing = {
    val drawing = new AggregateDrawing

    drawing.getModel.setDt(element.attributes("dt").toDouble)

    var refs = Map[Int, Figure]()
    
    for (element <- element.children) {
      element.name match {
        case "stock" =>
          val stock = new StockFigure

          stock.nameWrapper(element.attributes("name"))
          stock.initialValueExpressionWrapper(element.attributes("initialValue"))
          stock.allowNegative(element.attributes("allowNegative").toBoolean)

          stock.displayBox(new Point(element.attributes("centerX").toInt, element.attributes("centerY").toInt),
                           new Point(element.attributes("startX").toInt, element.attributes("startY").toInt))

          drawing.add(stock)

          refs += ((refs.size, stock))

        case "converter" =>
          val converter = new ConverterFigure

          converter.nameWrapper(element.attributes("name"))
          converter.expressionWrapper(element.attributes("expression"))

          converter.displayBox(new Point(element.attributes("centerX").toInt, element.attributes("centerY").toInt),
                               new Point(element.attributes("startX").toInt, element.attributes("startY").toInt))

          drawing.add(converter)

          refs += ((refs.size, converter))
        
        case "reservoir" =>
          val reservoir = new ReservoirFigure

          reservoir.displayBox(new Point(element.attributes("centerX").toInt, element.attributes("centerY").toInt),
                               new Point(element.attributes("startX").toInt, element.attributes("startY").toInt))

          drawing.add(reservoir)

          refs += ((refs.size, reservoir))

        case "binding" =>
          val binding = new BindingConnection

          binding.displayBox(new Point(element.attributes("centerX").toInt, element.attributes("centerY").toInt),
                             new Point(element.attributes("startX").toInt, element.attributes("startY").toInt))

          val start = refs(element.attributes("startFigure").toInt)
          val end = refs(element.attributes("endFigure").toInt)

          binding.connectStart(start.connectorAt(start.center.x, start.center.y))
          binding.connectEnd(end.connectorAt(end.center.x, end.center.y))
          
          drawing.add(binding)

          refs += ((refs.size, binding))

        case "rate" =>
          val rate = new RateConnection

          rate.nameWrapper(element.attributes("name"))
          rate.expressionWrapper(element.attributes("expression"))
          rate.bivalentWrapper(element.attributes("bivalent").toBoolean)

          rate.startPoint(element.attributes("startX").toInt, element.attributes("startY").toInt)

          if (element.attributes.contains("middleX"))
            rate.insertPointAt(new Point(element.attributes("middleX").toInt, element.attributes("middleY").toInt), 1)

          rate.endPoint(element.attributes("endX").toInt, element.attributes("endY").toInt)

          val start = refs(element.attributes("startFigure").toInt)
          val end = refs(element.attributes("endFigure").toInt)

          rate.connectStart(start.connectorAt(start.center.x, start.center.y))
          rate.connectEnd(end.connectorAt(end.center.x, end.center.y))

          drawing.add(rate)

          refs += ((refs.size, rate))

      }
    }

    drawing
  }

  def writeDrawing(drawingRef: AnyRef): XMLElement = {
    val drawing = drawingRef.asInstanceOf[AggregateDrawing]

    val attributes = Map[String, String](
      ("dt", drawing.getModel.dt.toString)
    )

    var children = List[XMLElement]()

    var refs = Map[Figure, Int]()

    val figures = drawing.figures

    while (figures.hasNextFigure) {
      figures.nextFigure match {
        case stock: StockFigure =>
          val attributes = Map[String, String](
            ("name", stock.nameWrapper),
            ("initialValue", stock.initialValueExpressionWrapper),
            ("allowNegative", stock.allowNegative.toString),
            ("centerX", stock.center.x.toString),
            ("centerY", stock.center.y.toString),
            ("startX", stock.displayBox.x.toString),
            ("startY", stock.displayBox.y.toString)
          )

          children = children :+ XMLElement("stock", attributes, "", Nil)

          refs += ((stock, refs.size))
        
        case converter: ConverterFigure =>
          val attributes = Map[String, String](
            ("name", converter.nameWrapper),
            ("expression", converter.expressionWrapper),
            ("centerX", converter.center.x.toString),
            ("centerY", converter.center.y.toString),
            ("startX", converter.displayBox.x.toString),
            ("startY", converter.displayBox.y.toString)
          )

          children = children :+ XMLElement("converter", attributes, "", Nil)

          refs += ((converter, refs.size))
        
        case reservoir: ReservoirFigure =>
          val attributes = Map[String, String](
            ("centerX", reservoir.center.x.toString),
            ("centerY", reservoir.center.y.toString),
            ("startX", reservoir.displayBox.x.toString),
            ("startY", reservoir.displayBox.y.toString)
          )

          children = children :+ XMLElement("reservoir", attributes, "", Nil)

          refs += ((reservoir, refs.size))
        
        case binding: BindingConnection =>
          val attributes = Map[String, String](
            ("startX", binding.startPoint.x.toString),
            ("startY", binding.startPoint.y.toString),
            ("endX", binding.endPoint.x.toString),
            ("endY", binding.endPoint.y.toString),
            ("startFigure", refs(binding.startFigure).toString),
            ("endFigure", refs(binding.endFigure).toString)
          )

          children = children :+ XMLElement("binding", attributes, "", Nil)

          refs += ((binding, refs.size))

        case rate: RateConnection =>
          var attributes = Map[String, String](
            ("name", rate.nameWrapper),
            ("expression", rate.expressionWrapper),
            ("bivalent", rate.bivalentWrapper.toString),
            ("startX", rate.startPoint.x.toString),
            ("startY", rate.startPoint.y.toString),
            ("endX", rate.endPoint.x.toString),
            ("endY", rate.endPoint.y.toString),
            ("startFigure", refs(rate.startFigure).toString),
            ("endFigure", refs(rate.endFigure).toString)
          )

          if (rate.pointCount == 3) {
            attributes += (("middleX", rate.pointAt(1).x.toString))
            attributes += (("middleY", rate.pointAt(2).y.toString))
          }

          children = children :+ XMLElement("rate", attributes, "", Nil)

          refs += ((rate, refs.size))

        case _ =>
      }
    }

    XMLElement("systemDynamics", attributes, "", children)
  }

  def defaultDrawing = new AggregateDrawing
}
