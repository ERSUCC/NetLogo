// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.window

import java.awt.{ Graphics, GridBagConstraints, GridBagLayout, Insets }
import java.awt.event.{ ActionEvent, ItemEvent, ItemListener }
import javax.swing.{ AbstractAction, Action, JButton, JCheckBox, JPanel }

import org.nlogo.core.I18N, I18N.Prefix
import org.nlogo.window.Events.LoadEndEvent

class ViewUpdatePanel(workspace: GUIWorkspace, displaySwitch: JCheckBox, tickCounter: TickCounterLabel)
    extends JPanel(new GridBagLayout) with LoadEndEvent.Handler {
  implicit val prefix = Prefix("tabs.run")

  private val updateModeChooser = new UpdateModeChooser(workspace)
  private val speedSlider       = new SpeedSliderPanel(workspace, tickCounter)

  private val settingsButton = new SettingsButton(new EditSettings(workspace.viewWidget.settings))

  displaySwitch.addItemListener(new ViewUpdateListener(speedSlider))

  updateModeChooser.refreshSelection()

  locally {
    val c = new GridBagConstraints

    c.gridy = 0
    c.gridheight = 2
    c.insets = new Insets(6, 24, 6, 24)

    add(speedSlider, c)

    c.gridheight = 1
    c.insets = new Insets(6, 0, 3, 12)

    add(displaySwitch, c)

    c.gridy = 1
    c.insets = new Insets(0, 0, 6, 12)

    add(updateModeChooser, c)

    c.insets = new Insets(0, 0, 6, 6)

    add(settingsButton, c)
  }

  override def addNotify(): Unit = {
    super.addNotify()
    getComponents.foreach(_.setFocusable(false))
  }

  def handle(e: LoadEndEvent): Unit = {
    updateModeChooser.refreshSelection()
    speedSlider.setValue(workspace.speedSliderPosition.toInt)
  }

  override def paintComponent(g: Graphics) {
    setBackground(InterfaceColors.TOOLBAR_BACKGROUND)

    displaySwitch.setForeground(InterfaceColors.TOOLBAR_TEXT)

    settingsButton.setBackground(InterfaceColors.TOOLBAR_CONTROL_BACKGROUND)
    settingsButton.setForeground(InterfaceColors.TOOLBAR_TEXT)

    super.paintComponent(g)
  }

  private class ViewUpdateListener(slider: SpeedSliderPanel) extends ItemListener {
    private var speed = 0

    def itemStateChanged(e: ItemEvent): Unit = {
      val selected = e.getStateChange == ItemEvent.SELECTED
      if (selected != speedSlider.isEnabled) {
        slider.setEnabled(selected)
        if (selected)
          slider.setValue(speed)
        else {
          speed = slider.getValue
          slider.setValue(speedSlider.getMaximum)
        }
      }
    }
  }

  private class SettingsButton(action: Action) extends JButton(action) {
    setFocusable(false)
  }

  private class EditSettings(settings: WorldViewSettings)
    extends AbstractAction(I18N.gui("settingsButton")) {
    putValue(Action.SHORT_DESCRIPTION, I18N.gui("settingsButton.tooltip"))
    def actionPerformed(e: ActionEvent) {
      new Events.EditWidgetEvent(settings).raise(e.getSource)
    }
  }
}
