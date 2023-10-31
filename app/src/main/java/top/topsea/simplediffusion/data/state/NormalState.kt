package top.topsea.simplediffusion.data.state

import top.topsea.simplediffusion.api.dto.Sampler
import top.topsea.simplediffusion.data.param.AddablePrompt

data class NormalState <T> (
    val models: List<T> = emptyList(),
    val samplers: List<Sampler> = emptyList<Sampler>(),
    val loras: List<Pair<String, MutableList<AddablePrompt>>> = emptyList(),
    val prompts: List<AddablePrompt> = emptyList(),
    val modelName: String = "",
    val modelParentDir: String = "",
)
