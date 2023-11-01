package top.topsea.simplediffusion.data.state

import top.topsea.simplediffusion.data.param.TaskParam

data class TaskState (
    val errorTasks: List<TaskParam> = emptyList(),
    val capGenImgList: List<TaskParam> = emptyList(),
)
