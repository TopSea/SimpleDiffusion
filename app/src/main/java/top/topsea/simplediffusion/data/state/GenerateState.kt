package top.topsea.simplediffusion.data.state

data class GenerateState (
    val isGeneratingImages: Boolean = false,
    val generatingProgress: Float = 0f,
)
