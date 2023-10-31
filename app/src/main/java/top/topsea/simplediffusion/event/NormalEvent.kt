package top.topsea.simplediffusion.event

import androidx.compose.runtime.MutableState
import top.topsea.simplediffusion.api.dto.SimpleSdConfig
import top.topsea.simplediffusion.data.param.CNParam

sealed class ControlNetEvent {
    data class ActivateByRequest(val index: List<Int>) : ControlNetEvent()
    data class AddCNParam(val cnModel: CNParam) : ControlNetEvent()
    data class AddImage(val base64Str: String, val index: Int) : ControlNetEvent()
    data class CloseImage(val index: Int) : ControlNetEvent()
    data class DeleteCNParam(val cnModel: CNParam) : ControlNetEvent()
    data class EditCNParam(val cnModel: CNParam?, val editing: Boolean) : ControlNetEvent()
    data class UpdateCNParam(val cnModel: CNParam, val afterUpdate: () -> Unit) : ControlNetEvent()
    data class UpdateConfig<T>(val simpleSdConfig: SimpleSdConfig<T>, val onFailure: () -> Unit,
                               val onSuccess: () -> Unit) : ControlNetEvent()
    data class ChooseType(val type: String, val module: MutableState<String>, val model: MutableState<String>, ) : ControlNetEvent()
}