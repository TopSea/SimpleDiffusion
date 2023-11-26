package top.topsea.simplediffusion.data.state

import android.content.Context
import top.topsea.simplediffusion.SimpleDestination
import top.topsea.simplediffusion.api.dto.VaeModel


sealed class UIEvent {
    data class Navigate(val screen: SimpleDestination, val navOp: () -> Unit) : UIEvent()
    data class ChangeParamTab(val tabIndex: Int) : UIEvent()
    data class ChangeCameraTab(val tabIndex: Int) : UIEvent()
    data class UpdateVae(val vae: VaeModel, val onFailure: () -> Unit,
                         val onSuccess: () -> Unit) : UIEvent()
    data class Display(val display: Boolean = false) : UIEvent()
    data class DisplayImg(val index: Int) : UIEvent()
    data class LongPressImage(val longPressImage: Boolean) : UIEvent()
    data class IsSaveCapImg(val saveCapImage: Boolean = true) : UIEvent()
    data class IsSaveGridImg(val saveGridImage: Boolean = true) : UIEvent()
    data class IsSaveControl(val saveControlNet: Boolean = true) : UIEvent()
    data class AddGenSize(val context: Context): UIEvent()
    data class MinusGenSize(val context: Context): UIEvent()
    data class ShowGenOn1(val showGenOn1: Boolean): UIEvent()

    // UI 相关的弹窗
    data class UIWarning(val warningStr: String): UIEvent()

    // SD 服务器相关
    data class ServerConnected(val serverConnected: Boolean) : UIEvent()
    data class SaveOnServer(val saveOnServer: Boolean) : UIEvent()
    data class ModelChanging(val modelChanging: Boolean) : UIEvent()

    // 是否启用 SD 插件
    data class ExSettingChange(val whichOne: String, val context: Context, val onChangeSuccess: (isOn: Boolean) -> Unit) : UIEvent()

    // SimpleDiffusion Desktop 相关
    data class ConnectDesktop(val connectDesktop: Boolean): UIEvent()
    data class Send2Desktop(val str: String): UIEvent()
}