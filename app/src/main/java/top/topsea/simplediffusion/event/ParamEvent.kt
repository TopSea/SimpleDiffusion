package top.topsea.simplediffusion.event

import top.topsea.simplediffusion.data.param.BasicParam
import top.topsea.simplediffusion.data.param.CNParam
import android.util.Size
import top.topsea.simplediffusion.data.param.ImgParam

sealed class ParamEvent {
    data class LoadParam(val bps: List<BasicParam>) : ParamEvent()
    data class AddParam(val bp: BasicParam?) : ParamEvent()
    data class UpdateParam(val bp: BasicParam) : ParamEvent()
    data class DeleteParam(val bp: BasicParam) : ParamEvent()
    data class GenImage(val controlNets: List<CNParam>, val onGenError: (String) -> Unit, val onGenSuccess: (Array<String>?, String?) -> Unit) : ParamEvent()
    data class GenImageInList(val controlNets: List<CNParam>, val imgParam: ImgParam,
                              val onGenError: (String) -> Unit, val onGenSuccess: (Array<String>?, String?) -> Unit) : ParamEvent()
    data class CaptureImage(val imgBase64: String, val size: Size, val controlNets: List<CNParam>,
                            val onError: (String) -> Unit, val onGenSuccess: (Array<String>?, String?) -> Unit) : ParamEvent()
    data class CheckCapture(val notInI2I: () -> Unit, val checkPass: () -> Unit) : ParamEvent()
    data class ShareParam(val bp: BasicParam) : ParamEvent()
    data class SearchParam(val txt: String, val isi2i: Boolean = false) : ParamEvent()
    data class ActivateParam(val bp: BasicParam) : ParamEvent()
    data class EditActivate(val editing: Boolean = true, val editingActivate: Boolean = true, val onNoAct: () -> Unit) : ParamEvent()
    data class EditParam(val bp: BasicParam?, val editing: Boolean = true) : ParamEvent()
    data class AddImage(val base64Str: String): ParamEvent()
    object CloseImage: ParamEvent()
    data class AddControlNet(val index: Int): ParamEvent()
    data class CloseControlNet(val index: Int): ParamEvent()
    data class DeleteControlNet(val index: Int): ParamEvent()
}
