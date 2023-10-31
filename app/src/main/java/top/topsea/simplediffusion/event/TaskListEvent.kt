package top.topsea.simplediffusion.event

import top.topsea.simplediffusion.data.param.BasicParam
import top.topsea.simplediffusion.data.param.ImageData
import top.topsea.simplediffusion.data.param.TaskParam

sealed class TaskListEvent {
    data class RemoveTask(val gen: TaskParam) : TaskListEvent()
    data class AddTaskImage(val gen: Pair<ImageData?, BasicParam>, val onAddFailure: () -> Unit) : TaskListEvent()
}
