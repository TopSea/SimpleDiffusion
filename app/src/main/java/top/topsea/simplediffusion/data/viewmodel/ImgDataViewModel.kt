package top.topsea.simplediffusion.data.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.api.impl.GenImgApiImp
import top.topsea.simplediffusion.data.param.ImageDataDao
import top.topsea.simplediffusion.data.state.ImgDataState
import top.topsea.simplediffusion.event.ImageEvent
import top.topsea.simplediffusion.util.FileUtil
import javax.inject.Inject

@HiltViewModel
class ImgDataViewModel @Inject constructor(
    private val dao: ImageDataDao,
    private val newbieApi: GenImgApiImp,
): ViewModel() {
    private val _state = MutableStateFlow(ImgDataState())
    private val _images = dao.getAllImages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val state = combine(_state, _images) { state, images ->
        state.copy(
            images = images,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ImgDataState())

    fun onEvent(event: ImageEvent) {
        when (event) {
            is ImageEvent.AddImages -> {
                viewModelScope.launch {
                    dao.insert(event.imageData)
                }
            }
            is ImageEvent.DeleteImage -> {
                viewModelScope.launch {
                    dao.delete(event.imageData)
                    FileUtil.deleteImage(event.imageData.imageName, event.context)
                }
            }
            is ImageEvent.DeleteByDate -> {
                viewModelScope.launch {
                    val days = event.days
                    if (days > 0) {
                        val images = dao.getDeleteImages(days)
                        FileUtil.deleteImageByDate(images, event.context)
                        dao.delete(days)
                    }
                }
            }
            is ImageEvent.DownloadImage -> {
                viewModelScope.launch {
                    // TODO
                    val isDown = FileUtil.downloadImageToAlbum(image = event.imageData, context = event.context)
                    if (isDown) {
                        Toast.makeText(event.context, event.context.resources.getString(R.string.download_sucecssed), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(event.context, event.context.resources.getString(R.string.download_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is ImageEvent.LikeImage -> {
                viewModelScope.launch {
                    dao.likeImage(event.imageData)
                }
            }
            is ImageEvent.ShareImage -> {
                viewModelScope.launch {
                    FileUtil.shareImage(event.imageData.imageName, event.context)
                }
            }
        }
    }
}