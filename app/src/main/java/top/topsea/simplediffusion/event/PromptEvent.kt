package top.topsea.simplediffusion.event

import top.topsea.simplediffusion.data.param.UserPrompt

sealed class PromptEvent {
    data class AddPrompt(val up: UserPrompt) : PromptEvent()
    data class UpdatePrompt(val up: UserPrompt) : PromptEvent()
    data class DeletePrompt(val up: UserPrompt) : PromptEvent()
}
