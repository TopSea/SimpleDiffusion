package top.topsea.simplediffusion.data.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.api.SocketClient
import top.topsea.simplediffusion.api.dto.SimpleSdConfig
import top.topsea.simplediffusion.api.dto.VaeModel
import top.topsea.simplediffusion.api.impl.NormalApiImp
import top.topsea.simplediffusion.currentScreen
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.event.ExecuteState
import top.topsea.simplediffusion.util.Constant
import top.topsea.simplediffusion.util.DeleteImage
import top.topsea.simplediffusion.util.TextUtil
import javax.inject.Inject

@SuppressLint("SimpleDateFormat")
@HiltViewModel
class UIViewModel @Inject constructor(
    private val kv: MMKV,
    private val normalApiImp: NormalApiImp,
    private val socketClient: SocketClient,
    @ApplicationContext context: Context
): ViewModel() {
    // 页面相关
    var displaying by mutableStateOf(false)
        private set     // 是否在展示图片
    var longPressImage by mutableStateOf(false)
        private set     // 是否在展示图片
    val fullSelected: SnapshotStateList<String> = mutableStateListOf()      // 全选了的模块
    var modelChanging by mutableStateOf(false)
        private set     // 是否正在修改基础模型，初次进入时要先更新模型
    var displayingImg by mutableStateOf(-1)
        private set     // 当前展示的图片
    var warningStr by mutableStateOf("")
        private set     // 弹窗警告字符串

    // 设置相关
    var showGenOn1 by mutableStateOf(
        kv.decodeBool(Constant.k_show_gen_on_1, true)
    )
        private set     // 生成队列的大小为一时拍摄后是否直接显示生成页面
    var saveCapImage by mutableStateOf(
        kv.decodeBool(Constant.k_save_cap_img, true)
    )
        private set     // 是否保存拍摄的照片
    var saveControlNet by mutableStateOf(
        kv.decodeBool(Constant.k_save_control, true)
    )
        private set     // 是否保存 ControlNet 产生的图片
    var saveOnServer by mutableStateOf(
        kv.decodeBool(Constant.k_save_on_server, true)
    )
        private set     // 是否将生成的图片保持到 SD 服务器
    var currentVae by mutableStateOf(
        kv.decodeParcelable(Constant.sd_vae, VaeModel::class.java)?: VaeModel("", "")
    )
        private set     // 当前 Vae 模型
    var taskQueueSize by mutableStateOf(
        kv.decodeInt(context.getString(R.string.kv_gen_size), 1)
    )
        private set     // 生成队列的大小

    // 插件相关
    var exControlNet by mutableStateOf(true)
        private set     // 是否开启 ControlNet 插件，默认开启不可修改
    var exAgentScheduler by mutableStateOf(
        kv.decodeBool(Constant.k_ex_agent_scheduler, false)
    )
        private set     // 是否开启 GenScheduler 插件

    // SimpleDiffusion Desktop 相关
    var enableDesktop by mutableStateOf(false)
        private set     // 启用 SimpleDiffusion Desktop
    var connectDesktop by mutableStateOf(false)
        private set     // 是否连接到了 SimpleDiffusion Desktop
    var messageDesktop by mutableStateOf("")
        private set     // 来自 SimpleDiffusion Desktop 的信息

    // SD 服务器相关
    var serverConnected by mutableStateOf(false)
        private set     // 是否已连接到 SD 服务器


    init {
        socketClient.startClient({ str ->
            TextUtil.topsea("Socket message: $str")
            str?.let {messageDesktop = str}
        }) {
            TextUtil.topsea("Socket client error: ${it?.message}")
        }
    }

    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.Display -> {
                displaying = event.display
            }
            is UIEvent.DisplayImg -> {
                displaying = !displaying
                displayingImg = event.index
                TextUtil.topsea("DisplayImg: ${event.index}", Log.ERROR)
            }
            is UIEvent.LongPressImage -> {
                longPressImage = event.longPressImage
            }
            is UIEvent.IsSaveCapImg -> {
                saveCapImage = event.saveCapImage
                kv.encode(Constant.k_save_cap_img, event.saveCapImage)
            }
            is UIEvent.IsSaveControl -> {
                saveControlNet = event.saveControlNet
                kv.encode(Constant.k_save_control, event.saveControlNet)
            }

            // SD 服务器相关
            is UIEvent.ServerConnected -> {
                serverConnected = event.serverConnected
            }
            is UIEvent.SaveOnServer -> {
                saveOnServer = event.saveOnServer
                kv.encode(Constant.k_save_on_server, event.saveOnServer)
            }
            is UIEvent.ModelChanging -> {
                modelChanging = event.modelChanging
            }
            is UIEvent.ExSettingChange -> {
                when (event.whichOne) {
                    "AgentScheduler" -> {
                        if (exAgentScheduler) {
                            // 关闭直接关
                            exAgentScheduler = false
                            kv.encode(Constant.k_ex_agent_scheduler, false)
                        } else {
                            // 打开需要检查
                            viewModelScope.launch {
                                normalApiImp.checkAgentScheduler({
                                    exAgentScheduler = true
                                    kv.encode(Constant.k_ex_agent_scheduler, true)
                                }){
                                    warningStr = event.context.getString(R.string.t_no_ex)
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
            is UIEvent.Navigate -> {
                currentScreen = event.screen
                event.navOp()
            }
            is UIEvent.AddGenSize -> {
                if (taskQueueSize < 10) {
                    taskQueueSize += 1
                    kv.encode(event.context.getString(R.string.kv_gen_size), taskQueueSize)

                    // 大于一自动关闭拍摄后显示
                    showGenOn1 = false
                    kv.encode(Constant.k_show_gen_on_1, false)
                }
            }
            is UIEvent.MinusGenSize -> {
                if (taskQueueSize > 1) {
                    taskQueueSize -= 1
                    kv.encode(event.context.getString(R.string.kv_gen_size), taskQueueSize)

                    // 等于一自动开启拍摄后显示
                    if (taskQueueSize == 1) {
                        showGenOn1 = true
                        kv.encode(Constant.k_show_gen_on_1, true)
                    }
                }
            }
            is UIEvent.ShowGenOn1 -> {
                if (taskQueueSize == 1) {
                    showGenOn1 = event.showGenOn1
                    kv.encode(Constant.k_show_gen_on_1, event.showGenOn1)
                }
            }

            // UI 相关的弹窗
            is UIEvent.UIWarning -> {
                warningStr = event.warningStr
            }


            is UIEvent.UpdateVae -> {
                val vae = event.vae
                val config = SimpleSdConfig(Constant.sd_vae, value = vae.model_name)
                viewModelScope.launch {
                    normalApiImp.updateSdConfig(config).collectLatest { state ->
                        when (state) {
                            is ExecuteState.ExecuteSuccess -> {
                                event.onSuccess()
                                TextUtil.topsea("UpdateVae: ${vae}")
                                currentVae = vae
                                kv.encode(Constant.sd_vae, vae)
                            }
                            else -> event.onFailure()
                        }
                    }
                }
            }
            is UIEvent.ConnectDesktop -> {
                connectDesktop = event.connectDesktop
            }
            is UIEvent.Send2Desktop -> {
                val message = event.str
                socketClient.sendMsg(message) {
                    TextUtil.topsea("Socket send error: ${it?.message}")
                }
            }
        }
    }
}