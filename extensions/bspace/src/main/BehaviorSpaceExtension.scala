package org.nlogo.extensions.bspace

import org.nlogo.api
import org.nlogo.core.I18N
import org.nlogo.core.Syntax._
import org.nlogo.lab.gui.Supervisor
import org.nlogo.swing.BrowserLauncher
import org.nlogo.window.GUIWorkspace

import javax.swing.JOptionPane

import scala.collection.mutable.Map

class ExperimentData {
  var name = api.LabDefaultValues.getDefaultName
  var preExperimentCommands = api.LabDefaultValues.getDefaultPreExperimentCommands
  var setupCommands = api.LabDefaultValues.getDefaultSetupCommands
  var goCommands = api.LabDefaultValues.getDefaultGoCommands
  var postRunCommands = api.LabDefaultValues.getDefaultPostRunCommands
  var postExperimentCommands = api.LabDefaultValues.getDefaultPostExperimentCommands
  var repetitions = api.LabDefaultValues.getDefaultRepetitions
  var sequentialRunOrder = api.LabDefaultValues.getDefaultSequentialRunOrder
  var runMetricsEveryStep = api.LabDefaultValues.getDefaultRunMetricsEveryStep
  var runMetricsCondition = api.LabDefaultValues.getDefaultRunMetricsCondition
  var timeLimit = api.LabDefaultValues.getDefaultTimeLimit
  var exitCondition = api.LabDefaultValues.getDefaultExitCondition
  var metrics: List[String] = api.LabDefaultValues.getDefaultMetrics
  var constants = api.LabDefaultValues.getDefaultConstants
  var subExperiments = api.LabDefaultValues.getDefaultSubExperiments
  var threadCount = api.LabDefaultValues.getDefaultThreads
  var table = api.LabDefaultValues.getDefaultTable
  var spreadsheet = api.LabDefaultValues.getDefaultSpreadsheet
  var stats = api.LabDefaultValues.getDefaultStats
  var lists = api.LabDefaultValues.getDefaultLists
  var updateView = api.LabDefaultValues.getDefaultUpdateView
  var updatePlotsAndMonitors = api.LabDefaultValues.getDefaultUpdatePlotsAndMonitors
}

class BehaviorSpaceExtension extends api.DefaultClassManager {
  private implicit val i18NPrefix = I18N.Prefix("tools.behaviorSpace.extension")

  val experiments = Map[String, ExperimentData]()

  def load(manager: api.PrimitiveManager) {
    manager.addPrimitive("goto-behaviorspace-documentation", GotoBehaviorspaceDocumentation)
    manager.addPrimitive("goto-bspace-extension-documentation", GotoBspaceExtensionDocumentation)

    manager.addPrimitive("create-experiment", CreateExperiment)
    manager.addPrimitive("run-experiment", RunExperiment)

    manager.addPrimitive("rename-experiment", RenameExperiment)
    manager.addPrimitive("set-pre-experiment-commands", SetPreExperimentCommands)
    manager.addPrimitive("set-setup-commands", SetSetupCommands)
    manager.addPrimitive("set-go-commands", SetGoCommands)
    manager.addPrimitive("set-post-run-commands", SetPostRunCommands)
    manager.addPrimitive("set-post-experiment-commands", SetPostExperimentCommands)
    manager.addPrimitive("set-repetitions", SetRepetitions)
    manager.addPrimitive("set-sequential-run-order", SetSequentialRunOrder)
    manager.addPrimitive("set-run-metrics-every-step", SetRunMetricsEveryStep)
    manager.addPrimitive("set-run-metrics-condition", SetRunMetricsCondition)
    manager.addPrimitive("set-time-limit", SetTimeLimit)
    manager.addPrimitive("set-stop-condition", SetStopCondition)
    manager.addPrimitive("set-metrics", SetMetrics)
    manager.addPrimitive("set-variables", SetVariables)
    manager.addPrimitive("set-parallel-runs", SetParallelRuns)
    manager.addPrimitive("set-table", SetTable)
    manager.addPrimitive("set-spreadsheet", SetSpreadsheet)
    manager.addPrimitive("set-stats", SetStats)
    manager.addPrimitive("set-lists", SetLists)
    manager.addPrimitive("set-update-view", SetUpdateView)
    manager.addPrimitive("set-update-plots", SetUpdatePlots)

    manager.addPrimitive("get-default-parallel-runs", GetDefaultParallelRuns)
    manager.addPrimitive("get-recommended-max-parallel-runs", GetRecommendedMaxParallelRuns)
  }

  override def clearAll() {
    experiments.clear()
  }

  object ExperimentType extends Enumeration {
    type ExperimentType = Value
    val GUI, Code, None = Value
  }

  def experimentType(name: String, context: api.Context): ExperimentType.ExperimentType = {
    if (context.workspace.getBehaviorSpaceExperiments.find(x => x.name == name).isDefined)
      ExperimentType.GUI
    else if (experiments.contains(name))
      ExperimentType.Code
    else
      ExperimentType.None
  }

  def validateForEditing(name: String, context: api.Context): Boolean = {
    return experimentType(name, context) match {
      case ExperimentType.None =>
        nameError(I18N.gui("noExperiment", name), context)
        false
      case ExperimentType.GUI =>
        nameError(I18N.gui("guiExperiment", name), context)
        false
      case ExperimentType.Code => true
    }
  }

  def nameError(message: String, context: api.Context) {
    JOptionPane.showMessageDialog(context.workspace.asInstanceOf[GUIWorkspace].getFrame,
                                  message,
                                  I18N.gui("invalid"),
                                  JOptionPane.ERROR_MESSAGE)
  }

  object GotoBehaviorspaceDocumentation extends api.Command {
    override def getSyntax = {
      commandSyntax()
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      BrowserLauncher.openPath(BrowserLauncher.docPath("behaviorspace.html"), "")
    }
  }

  object GotoBspaceExtensionDocumentation extends api.Command {
    override def getSyntax = {
      commandSyntax()
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      // will fill in once i know the link
    }
  }

  object CreateExperiment extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (experimentType(args(0).getString, context) != ExperimentType.None)
        return nameError(I18N.gui("alreadyExists", args(0).getString), context)

      experiments += ((args(0).getString, new ExperimentData()))
    }
  }

  object RunExperiment extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      val ws = context.workspace.asInstanceOf[GUIWorkspace]

      val protocol = experimentType(args(0).getString, context) match {
        case ExperimentType.GUI =>
          context.workspace.getBehaviorSpaceExperiments.find(x => x.name == args(0).getString).get
        case ExperimentType.Code =>
          val data = experiments(args(0).getString)

          new api.LabProtocol(data.name, data.preExperimentCommands, data.setupCommands, data.goCommands,
                                         data.postRunCommands, data.postExperimentCommands, data.repetitions,
                                         data.sequentialRunOrder, data.runMetricsEveryStep, data.runMetricsCondition,
                                         data.timeLimit, data.exitCondition, data.metrics, data.constants, data.subExperiments,
                                         runOptions = new api.LabRunOptions(data.threadCount, data.table,
                                                                            data.spreadsheet, data.stats, data.lists,
                                                                            data.updateView, data.updatePlotsAndMonitors))
        case _ => return nameError(I18N.gui("noExperiment", args(0).getString), context)
      }

      javax.swing.SwingUtilities.invokeLater(() => {
        Supervisor.runFromExtension(protocol, context.workspace.asInstanceOf[GUIWorkspace])
      })
    }
  }

  object RenameExperiment extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return
      if (experimentType(args(1).getString, context) != ExperimentType.None)
        return nameError(I18N.gui("alreadyExists", args(1).getString), context)

      val data = experiments(args(0).getString)

      data.name = args(1).getString

      experiments -= args(0).getString
      experiments += ((args(1).getString, data))
    }
  }

  object SetPreExperimentCommands extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).preExperimentCommands = args(1).getString
    }
  }

  object SetSetupCommands extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).setupCommands = args(1).getString
    }
  }

  object SetGoCommands extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).goCommands = args(1).getString
    }
  }

  object SetPostRunCommands extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).postRunCommands = args(1).getString
    }
  }

  object SetPostExperimentCommands extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).postExperimentCommands = args(1).getString
    }
  }

  object SetRepetitions extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, NumberType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).repetitions = args(1).getIntValue
    }
  }

  object SetSequentialRunOrder extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, BooleanType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).sequentialRunOrder = args(1).getBooleanValue
    }
  }

  object SetRunMetricsEveryStep extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, BooleanType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).runMetricsEveryStep = args(1).getBooleanValue
    }
  }

  object SetRunMetricsCondition extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).runMetricsCondition = args(1).getString
    }
  }

  object SetTimeLimit extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, NumberType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).timeLimit = args(1).getIntValue
    }
  }

  object SetStopCondition extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).exitCondition = args(1).getString
    }
  }

  object SetMetrics extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, ListType | StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).metrics = args(1).getList.toList.map(_.toString)
    }
  }

  object SetVariables extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      val parsed = api.LabProtocol.parseVariables(args(1).getString, context.workspace.world,
                                                  context.workspace.asInstanceOf[GUIWorkspace], message => {
        nameError(I18N.gui.getN("edit.behaviorSpace.invalidVarySpec", message), context)
      })

      experiments(args(0).getString).constants = parsed.get._1
      experiments(args(0).getString).subExperiments = parsed.get._2
    }
  }

  object SetParallelRuns extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, NumberType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).threadCount = args(1).getIntValue
    }
  }

  object SetTable extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).table = args(1).getString
    }
  }

  object SetSpreadsheet extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).spreadsheet = args(1).getString
    }
  }

  object SetStats extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).stats = args(1).getString
    }
  }

  object SetLists extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, StringType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).lists = args(1).getString
    }
  }

  object SetUpdateView extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, BooleanType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).updateView = args(1).getBooleanValue
    }
  }

  object SetUpdatePlots extends api.Command {
    override def getSyntax = {
      commandSyntax(right = List(StringType, BooleanType))
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      if (!validateForEditing(args(0).getString, context)) return

      experiments(args(0).getString).updatePlotsAndMonitors = args(1).getBooleanValue
    }
  }

  object GetDefaultParallelRuns extends api.Command {
    override def getSyntax = {
      reporterSyntax(ret = NumberType)
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      // how to return???
      // api.LabDefaultValues.getDefaultThreads
    }
  }

  object GetRecommendedMaxParallelRuns extends api.Command {
    override def getSyntax = {
      reporterSyntax(ret = NumberType)
    }

    def perform(args: Array[api.Argument], context: api.Context) {
      // how to return???
      // api.LabDefaultValues.getRecommendedMaxThreads
    }
  }
}
