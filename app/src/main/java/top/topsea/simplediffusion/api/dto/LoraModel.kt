package top.topsea.simplediffusion.api.dto

import androidx.annotation.Keep
import top.topsea.simplediffusion.data.param.AddablePrompt

@Keep
data class LoraModel(
    val name: String,
    val alias: String,
    override val path: String,
): AddablePrompt()

