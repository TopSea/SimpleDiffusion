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
    data class DisplayTask(val index: Int) : UIEvent()
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


    data class TempParamShow(val switch: Boolean): UIEvent()
    data class UpdateTDPS(val switch: Boolean): UIEvent()
    data class UpdateTSDMS(val switch: Boolean): UIEvent()
    data class UpdateTRMS(val switch: Boolean): UIEvent()
    data class UpdateTRAS(val switch: Boolean): UIEvent()
    data class UpdateTPS(val switch: Boolean): UIEvent()
    data class UpdateTPAS(val switch: Boolean): UIEvent()
    data class UpdateTNPS(val switch: Boolean): UIEvent()
    data class UpdateTNPAS(val switch: Boolean): UIEvent()
    data class UpdateTIWS(val switch: Boolean): UIEvent()
    data class UpdateTIHS(val switch: Boolean): UIEvent()
    data class UpdateTSS(val switch: Boolean): UIEvent()
    data class UpdateTCS(val switch: Boolean): UIEvent()
    data class UpdateTSamplerS(val switch: Boolean): UIEvent()
    data class UpdateTBSS(val switch: Boolean): UIEvent()
    data class UpdateTSDPS(val switch: Boolean): UIEvent()
    data class UpdateTScriptS(val switch: Boolean): UIEvent()
    data class UpdateTCNS(val switch: Boolean): UIEvent()


    data class UpdateIDPS(val switch: Boolean): UIEvent()
    data class UpdateISDMS(val switch: Boolean): UIEvent()
    data class UpdateIRMS(val switch: Boolean): UIEvent()
    data class UpdateIRAS(val switch: Boolean): UIEvent()
    data class UpdateIPS(val switch: Boolean): UIEvent()
    data class UpdateIPAS(val switch: Boolean): UIEvent()
    data class UpdateINPS(val switch: Boolean): UIEvent()
    data class UpdateINPAS(val switch: Boolean): UIEvent()
    data class UpdateIDNS(val switch: Boolean): UIEvent()
    data class UpdateIIWS(val switch: Boolean): UIEvent()
    data class UpdateIIHS(val switch: Boolean): UIEvent()
    data class UpdateISS(val switch: Boolean): UIEvent()
    data class UpdateICS(val switch: Boolean): UIEvent()
    data class UpdateISamplerS(val switch: Boolean): UIEvent()
    data class UpdateIBSS(val switch: Boolean): UIEvent()
    data class UpdateISDPS(val switch: Boolean): UIEvent()
    data class UpdateIScriptS(val switch: Boolean): UIEvent()
    data class UpdateICNS(val switch: Boolean): UIEvent()
}