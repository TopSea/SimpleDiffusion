package top.topsea.simplediffusion.event

import top.topsea.simplediffusion.data.param.TaskParam

sealed class GenerateEvent {
    data class IsGeneratingImages(val isGeneratingImages: Boolean, val taskID: String = ""): GenerateEvent()

    data class RemoveTask(val task: TaskParam, val isGenThis: Boolean = false): GenerateEvent()

    data class RefreshTask(val task: TaskParam): GenerateEvent()

    object GeneratingProgress: GenerateEvent()

}

