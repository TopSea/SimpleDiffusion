package top.topsea.simplediffusion.data.state

import top.topsea.simplediffusion.api.dto.Img2Img
import top.topsea.simplediffusion.data.param.ImageData
import top.topsea.simplediffusion.data.param.ImgParam

data class ImgDataState (
    val images: List<ImageData> = emptyList(),
    val imageName: String = "",
    val activateImage: Int = -1,
    val isGeneratingImages: Boolean = false,
    val generatingProgress: Float = 0f,
)
