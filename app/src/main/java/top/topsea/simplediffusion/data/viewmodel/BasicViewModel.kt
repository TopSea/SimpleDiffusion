package top.topsea.simplediffusion.data.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import top.topsea.simplediffusion.data.state.ParamLocalState
import top.topsea.simplediffusion.event.ParamEvent

abstract class BasicViewModel : ViewModel() {
    abstract val paramState: StateFlow<ParamLocalState>
    val image = mutableStateOf("")

    abstract fun paramEvent(event: ParamEvent)

}