// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.window

import java.awt.Color

import org.nlogo.api.Constants

object InterfaceColors {
  private var theme: String = null
  
  def setTheme(theme: String) {
    this.theme = theme
  }

  def getTheme = theme

  private val CLASSIC_LAVENDER = new Color(188, 188, 230)
  private val CLASSIC_LIGHT_GREEN = new Color(130, 188, 183)
  private val CLASSIC_DARK_GREEN = new Color(65, 94, 91)
  private val CLASSIC_ORANGE = new Color(200, 103, 103)
  private val CLASSIC_BEIGE = new Color(225, 225, 182)

  private val LIGHT_BLUE = new Color(207, 229, 255)
  private val MEDIUM_BLUE = new Color(6, 112, 237)
  private val DARK_BLUE = new Color(0, 54, 117)
  private val LIGHT_GRAY = new Color(238, 238, 238)
  private val MEDIUM_GRAY = new Color(175, 175, 175)
  private val DARK_GRAY = new Color(79, 79, 79)
  private val LIGHT_RED = new Color(251, 96, 85)
  private val ALMOST_BLACK = new Color(22, 22, 22)

  val TRANSPARENT = new Color(0, 0, 0, 0)

  def WIDGET_TEXT =
    theme match {
      case "classic" => Color.BLACK
      case "light" => new Color(85, 87, 112)
      case "dark" => Color.WHITE
    }
  
  def WIDGET_TEXT_ERROR = Color.RED

  def WIDGET_HOVER_SHADOW = new Color(75, 75, 75)

  def WIDGET_PREVIEW_COVER = new Color(255, 255, 255, 150)
  def WIDGET_PREVIEW_COVER_NOTE = new Color(225, 225, 225, 150)

  def DISPLAY_AREA_BACKGROUND =
    theme match {
      case "classic" | "light" => Color.WHITE
      case "dark" => Color.BLACK
    }
  
  def DISPLAY_AREA_TEXT =
    theme match {
      case "classic" | "light" => Color.BLACK
      case "dark" => Color.WHITE
    }

  def TEXT_BOX_BACKGROUND = Color.WHITE

  def INTERFACE_BACKGROUND =
    theme match {
      case "classic" | "light" => Color.WHITE
      case "dark" => ALMOST_BLACK
    }

  def COMMAND_CENTER_BACKGROUND =
    theme match {
      case "classic" | "light" => LIGHT_GRAY
      case "dark" => DARK_GRAY
    }
  
  def COMMAND_CENTER_TEXT = WIDGET_TEXT

  def COMMAND_LINE_BACKGROUND =
    theme match {
      case "classic" | "light" => Color.WHITE
      case "dark" => DARK_GRAY
    }
  
  def SPLIT_PANE_DIVIDER_BACKGROUND =
    theme match {
      case "classic" | "light" => MEDIUM_GRAY
      case "dark" => new Color(204, 204, 204)
    }

  def BUTTON_BACKGROUND =
    theme match {
      case "classic" => CLASSIC_LAVENDER
      case "light" | "dark" => MEDIUM_BLUE
    }

  def BUTTON_BACKGROUND_HOVER =
    theme match {
      case "classic" => CLASSIC_LAVENDER
      case "light" | "dark" => new Color(62, 150, 253)
    }

  def BUTTON_BACKGROUND_PRESSED =
    theme match {
      case "classic" => Color.BLACK
      case "light" | "dark" => new Color(0, 49, 106)
    }

  def BUTTON_BACKGROUND_PRESSED_HOVER =
    theme match {
      case "classic" => Color.BLACK
      case "light" | "dark" => new Color(9, 89, 183)
    }

  def BUTTON_BACKGROUND_DISABLED =
    theme match {
      case "classic" => CLASSIC_LAVENDER
      case "light" | "dark" => new Color(213, 213, 213)
    }

  def BUTTON_TEXT =
    theme match {
      case "classic" => Color.BLACK
      case "light" | "dark" => Color.WHITE
    }

  def BUTTON_TEXT_PRESSED =
    theme match {
      case "classic" => CLASSIC_LAVENDER
      case "light" | "dark" => Color.WHITE
    }

  def BUTTON_TEXT_DISABLED =
    theme match {
      case "classic" => Color.BLACK
      case "light" | "dark" => new Color(154, 154, 154)
    }

  def SLIDER_BACKGROUND =
    theme match {
      case "classic" => CLASSIC_LIGHT_GREEN
      case "light" => LIGHT_BLUE
      case "dark" => DARK_BLUE
    }

  def SLIDER_BAR_BACKGROUND =
    theme match {
      case "classic" => CLASSIC_DARK_GREEN
      case "light" => MEDIUM_GRAY
      case "dark" => Color.BLACK
    }

  def SLIDER_BAR_BACKGROUND_FILLED =
    theme match {
      case "classic" => CLASSIC_DARK_GREEN
      case "light" | "dark" => MEDIUM_BLUE
    }

  def SLIDER_THUMB_BORDER =
    theme match {
      case "classic" => CLASSIC_ORANGE
      case "light" | "dark" => MEDIUM_BLUE
    }

  def SLIDER_THUMB_BACKGROUND =
    theme match {
      case "classic" => CLASSIC_ORANGE
      case "light" | "dark" => Color.WHITE
    }

  def SLIDER_THUMB_BACKGROUND_PRESSED =
    theme match {
      case "classic" => CLASSIC_ORANGE
      case "light" | "dark" => MEDIUM_BLUE
    }
  
  def SWITCH_BACKGROUND =
    theme match {
      case "classic" => CLASSIC_LIGHT_GREEN
      case "light" => LIGHT_BLUE
      case "dark" => DARK_BLUE
    }

  def SWITCH_TOGGLE =
    theme match {
      case "classic" => CLASSIC_ORANGE
      case "light" | "dark" => Color.WHITE
    }
    
  def SWITCH_TOGGLE_BACKGROUND_ON =
    theme match {
      case "classic" => CLASSIC_DARK_GREEN
      case "light" | "dark" => MEDIUM_BLUE
    }

  def SWITCH_TOGGLE_BACKGROUND_OFF =
    theme match {
      case "classic" => CLASSIC_DARK_GREEN
      case "light" => MEDIUM_GRAY
      case "dark" => Color.BLACK
    }

  def CHOOSER_BACKGROUND =
    theme match {
      case "classic" => CLASSIC_LIGHT_GREEN
      case "light" => LIGHT_BLUE
      case "dark" => DARK_BLUE
    }

  def CHOOSER_BORDER =
    theme match {
      case "classic" => CLASSIC_DARK_GREEN
      case "light" | "dark" => MEDIUM_BLUE
    }

  def INPUT_BACKGROUND =
    theme match {
      case "classic" => CLASSIC_LIGHT_GREEN
      case "light" => LIGHT_BLUE
      case "dark" => DARK_BLUE
    }

  def INPUT_BORDER =
    theme match {
      case "classic" => CLASSIC_DARK_GREEN
      case "light" | "dark" => MEDIUM_BLUE
    }

  def GRAPHICS_BACKGROUND = Constants.ViewBackground

  def MONITOR_BACKGROUND =
    theme match {
      case "classic" => CLASSIC_BEIGE
      case "light" => LIGHT_GRAY
      case "dark" => DARK_GRAY
    }

  def MONITOR_BORDER = MEDIUM_GRAY

  def PLOT_BACKGROUND =
    theme match {
      case "classic" => CLASSIC_BEIGE
      case "light" => LIGHT_GRAY
      case "dark" => DARK_GRAY
    }

  def PLOT_BORDER = MEDIUM_GRAY

  def PLOT_MOUSE_BACKGROUND =
    theme match {
      case "classic" => CLASSIC_BEIGE
      case "light" | "dark" => LIGHT_GRAY
    }
  
  def PLOT_MOUSE_TEXT = Color.BLACK

  def OUTPUT_BACKGROUND =
    theme match {
      case "classic" => CLASSIC_BEIGE
      case "light" => LIGHT_GRAY
      case "dark" => DARK_GRAY
    }

  def OUTPUT_BORDER = MEDIUM_GRAY

  def AGENT_EDITOR_BACKGROUND = LIGHT_GRAY

  def AGENT_COMMANDER_BACKGROUND = LIGHT_GRAY

  def TOOLBAR_BACKGROUND =
    theme match {
      case "classic" | "light" => LIGHT_GRAY
      case "dark" => DARK_GRAY
    }

  def TAB_BACKGROUND =
    theme match {
      case "classic" | "light" => LIGHT_GRAY
      case "dark" => ALMOST_BLACK
    }

  def TAB_BACKGROUND_SELECTED = MEDIUM_BLUE

  def TAB_BACKGROUND_ERROR = LIGHT_RED

  def TAB_TEXT =
    theme match {
      case "classic" | "light" => Color.BLACK
      case "dark" => Color.WHITE
    }
  
  def TAB_TEXT_SELECTED = Color.WHITE

  def TAB_TEXT_ERROR = LIGHT_RED

  def TAB_BORDER =
    theme match {
      case "classic" | "light" => MEDIUM_GRAY
      case "dark" => LIGHT_GRAY
    }

  def TAB_SEPARATOR =
    theme match {
      case "classic" | "light" => MEDIUM_GRAY
      case "dark" => LIGHT_GRAY
    }
  
  def TOOLBAR_TEXT =
    theme match {
      case "classic" | "light" => Color.BLACK
      case "dark" => Color.WHITE
    }
  
  def TOOLBAR_CONTROL_BACKGROUND =
    theme match {
      case "classic" | "light" => Color.WHITE
      case "dark" => ALMOST_BLACK
    }
  
  def TOOLBAR_CONTROL_BORDER = MEDIUM_GRAY

  def TOOLBAR_BUTTON_PRESSED =
    theme match {
      case "classic" | "light" => MEDIUM_GRAY
      case "dark" => ALMOST_BLACK
    }

  def ERROR_LABEL_BACKGROUND = LIGHT_RED

  def CODE_BACKGROUND =
    theme match {
      case "classic" | "light" => Color.WHITE
      case "dark" => ALMOST_BLACK
    }

  // Syntax highlighting colors

  def COMMENT_COLOR = new Color(90, 90, 90) // gray

  def COMMAND_COLOR =
    theme match {
      case "classic" | "light" => new Color(0, 0, 170) // blue
      case "dark" => new Color(107, 107, 237) // lighter blue
    }

  def REPORTER_COLOR =
    theme match {
      case "classic" | "light" => new Color(102, 0, 150) // purple
      case "dark" => new Color(151, 71, 255) // lighter purple
    }

  def KEYWORD_COLOR =
    theme match {
      case "classic" | "light" => new Color(0, 127, 105) // bluish green
      case "dark" => new Color(6, 142, 120) // lighter bluish green
    }

  def CONSTANT_COLOR = new Color(150, 55, 0) // orange

  def DEFAULT_COLOR =
    theme match {
      case "classic" | "light" => Color.BLACK
      case "dark" => Color.WHITE
    }
}
