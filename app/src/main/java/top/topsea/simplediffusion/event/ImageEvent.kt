package top.topsea.simplediffusion.event

import android.content.Context
import top.topsea.simplediffusion.api.dto.Img2Img
import top.topsea.simplediffusion.data.param.ImageData
import top.topsea.simplediffusion.data.param.ImgParam

sealed interface ImageEvent {

    data class AddImages(
        val imageData: ImageData,
    ): ImageEvent

    data class DownloadImage(
        val imageData: ImageData,
        val context: Context
    ): ImageEvent

    data class DeleteImage(
        val imageData: ImageData,
        val context: Context
    ): ImageEvent

    data class DeleteByDate(
        val days: Int,
        val context: Context
    ): ImageEvent

    data class ShareImage(
        val imageData: ImageData,
        val context: Context
    ): ImageEvent

    data class LikeImage(
        val imageData: ImageData,
    ): ImageEvent
}