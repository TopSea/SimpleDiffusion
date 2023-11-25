package top.topsea.simplediffusion.data.state

import top.topsea.simplediffusion.api.dto.Sampler

data class NormalState <T> (
    val models: List<T> = emptyList(),
    val samplers: List<Sampler> = emptyList<Sampler>(),
    val modelName: String = "",
    val modelParentDir: String = "",
)

data class PromptFile(
    var filename: String = "",
    var categories: List<PromptCategory> = emptyList(),
)

data class PromptCategory(
    var category: String = "",
    var prompts: List<Pair<String, String>> = emptyList(),
)

data class PromptState (
    val loras: PromptFile = PromptFile(),
    val local: PromptFile = PromptFile(),
    val promptSets: List<PromptFile> = emptyList(),
)