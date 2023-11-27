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
import top.topsea.simplediffusion.api.impl.PromptApiImp
import top.topsea.simplediffusion.currentScreen
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.event.ExecuteState
import top.topsea.simplediffusion.util.Constant
import top.topsea.simplediffusion.util.TextUtil
import javax.inject.Inject

@SuppressLint("SimpleDateFormat")
@HiltViewModel
class UIViewModel @Inject constructor(
    private val kv: MMKV,
    private val normalApiImp: NormalApiImp,
    private val promptApi: PromptApiImp,
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
    var displayingTask by mutableStateOf(-1)
        private set     // 当前展示的生成任务
    var warningStr by mutableStateOf("")
        private set     // 弹窗警告字符串
    var paramTab by mutableStateOf(0)
        private set     // 主页 Tab 序号
    var cameraTab by mutableStateOf(0)
        private set     // 拍摄设置页面 Tab 序号

    var tDisplayPri by mutableStateOf(
        kv.decodeInt(Constant.k_t_display_pri, 0)
    )
        private set     // 文生图显示优先级默认值
    var tDisplayPriSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_display_pri_s, true)
    )
        private set     // 是否显示显示优先级
    var tSDModel by mutableStateOf(
        kv.decodeString(Constant.k_t_sdmodel, "")!!
    )
        private set     // 基础大模型默认值
    var tSDModelSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_sdmodel_s, true)
    )
        private set     // 是否显示基础大模型
    var tRefineModel by mutableStateOf(
        kv.decodeString(Constant.k_t_refinermodel, "")!!
    )
        private set     // Refiner 大模型默认值
    var tRefineModelSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_refinermodel_s, true)
    )
        private set     // 是否显示 Refiner 大模型
    var tRefineAt by mutableStateOf(
        kv.decodeFloat(Constant.k_t_refinerat, 0f)
    )
        private set     // Refiner 时机默认值
    var tRefineAtSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_refinerat_s, true)
    )
        private set     // 是否显示Refiner 时机
    var tPrompt by mutableStateOf(
        kv.decodeString(Constant.k_t_prompt, "")!!
    )
        private set     // 正面提示词默认值
    var tPromptSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_prompt_s, true)
    )
        private set     // 是否显示正面提示词
    var tPromptAdSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_promptad_s, true)
    )
        private set     // 是否显示正面提示词的可添加项
    var tNPrompt by mutableStateOf(
        kv.decodeString(Constant.k_t_nprompt, "")!!
    )
        private set     // 负面提示词默认值
    var tNPromptSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_nprompt_s, true)
    )
        private set     // 是否显示负面提示词
    var tImgWidth by mutableStateOf(
        kv.decodeInt(Constant.k_t_imgw, 512)
    )
        private set     // 图片宽度默认值
    var tImgWidthSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_imgw_s, true)
    )
        private set     // 是否显示图片宽度
    var tImgHeight by mutableStateOf(
        kv.decodeInt(Constant.k_t_imgh, 512)
    )
        private set     // 图片高度默认值
    var tImgHeightSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_imgh_s, true)
    )
        private set     // 是否显示图片高度
    var tSteps by mutableStateOf(
        kv.decodeInt(Constant.k_t_step, 30)
    )
        private set     // 生成步数默认值
    var tStepsSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_step_s, true)
    )
        private set     // 是否显示生成步数
    var tCFG by mutableStateOf(
        kv.decodeFloat(Constant.k_t_cfg, 7f)
    )
        private set     // 提示词相关性默认值
    var tCFGSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_cfg_s, true)
    )
        private set     // 是否显示提示词相关性
    var tSampler by mutableStateOf(
        kv.decodeString(Constant.k_t_sampler, "")!!
    )
        private set     // sampler 默认值
    var tSamplerSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_sampler_s, true)
    )
        private set     // 是否显示 sampler
    var tBatchSize by mutableStateOf(
        kv.decodeInt(Constant.k_t_bsize, 1)
    )
        private set     // 每批次生成数默认值
    var tBatchSizeSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_bsize_s, true)
    )
        private set     // 是否显示每批次生成数
    var tSDPromptSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_sdprompt_s, true)
    )
        private set     // 是否显示脚本
    var tScriptSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_script_s, true)
    )
        private set     // 是否显示脚本
    var tCNSwitch by mutableStateOf(
        kv.decodeBool(Constant.k_t_cn_s, true)
    )
        private set     // 是否显示 ControlNet


    // 设置相关
    var showGenOn1 by mutableStateOf(
        kv.decodeBool(Constant.k_show_gen_on_1, true)
    )
        private set     // 生成队列的大小为一时拍摄后是否直接显示生成页面
    var saveCapImage by mutableStateOf(
        kv.decodeBool(Constant.k_save_cap_img, true)
    )
        private set     // 是否保存拍摄的照片
    var saveGridImage by mutableStateOf(
        kv.decodeBool(Constant.k_save_grid_img, true)
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
    var exSdPrompt by mutableStateOf(
        kv.decodeBool(Constant.k_ex_sd_prompt, false)
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
                displayingImg = event.index
                displaying = !displaying
                TextUtil.topsea("DisplayImg: ${event.index}", Log.ERROR)
            }
            is UIEvent.DisplayTask -> {
                displayingTask = event.index
                displaying = !displaying
                TextUtil.topsea("DisplayTask : ${event.index}", Log.ERROR)
            }
            is UIEvent.LongPressImage -> {
                longPressImage = event.longPressImage
            }
            is UIEvent.IsSaveCapImg -> {
                saveCapImage = event.saveCapImage
                kv.encode(Constant.k_save_cap_img, saveCapImage)
            }
            is UIEvent.IsSaveGridImg -> {
                saveGridImage = event.saveGridImage
                kv.encode(Constant.k_save_grid_img, saveGridImage)
            }
            is UIEvent.IsSaveControl -> {
                saveControlNet = event.saveControlNet
                kv.encode(Constant.k_save_control, saveControlNet)
            }

            // SD 服务器相关
            is UIEvent.ServerConnected -> {
                serverConnected = event.serverConnected
            }
            is UIEvent.SaveOnServer -> {
                saveOnServer = event.saveOnServer
                kv.encode(Constant.k_save_on_server, saveOnServer)
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
                            event.onChangeSuccess(false)
                        } else {
                            // 打开需要检查
                            viewModelScope.launch {
                                normalApiImp.checkAgentScheduler({
                                    exAgentScheduler = true
                                    kv.encode(Constant.k_ex_agent_scheduler, true)
                                    event.onChangeSuccess(true)
                                }){
                                    warningStr = event.context.getString(R.string.t_no_ex)
                                }
                            }
                        }
                    }
                    "SdPrompt" -> {
                        if (exSdPrompt) {
                            // 关闭直接关
                            exSdPrompt = false
                            kv.encode(Constant.k_ex_sd_prompt, false)
                            event.onChangeSuccess(false)
                        } else {
                            // 打开需要检查
                            viewModelScope.launch {
                                val sdPromptVersion = promptApi.checkSdPrompt()
                                if (sdPromptVersion) {
                                    warningStr = event.context.getString(R.string.t_no_ex)
                                } else {
                                    exSdPrompt = true
                                    kv.encode(Constant.k_ex_sd_prompt, true)
                                    event.onChangeSuccess(true)
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
            is UIEvent.ChangeParamTab -> {
                paramTab = event.tabIndex
            }
            is UIEvent.ChangeCameraTab -> {
                cameraTab = event.tabIndex
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


            is UIEvent.UpdateTDP -> {
                tDisplayPri = event.value
                kv.encode(Constant.k_t_display_pri, event.value)
                TextUtil.topsea("onValueChange int: ${event.value}")
            }
            is UIEvent.UpdateTDPS -> {
                tDisplayPriSwitch = event.switch
                kv.encode(Constant.k_t_display_pri_s, event.switch)
            }
            is UIEvent.UpdateTSDM -> {
                tSDModel = event.value
                kv.encode(Constant.k_t_sdmodel, event.value)
            }
            is UIEvent.UpdateTSDMS -> {
                tSDModelSwitch = event.switch
                kv.encode(Constant.k_t_sdmodel_s, event.switch)
            }
            is UIEvent.UpdateTRM -> {
                tRefineModel = event.value
                kv.encode(Constant.k_t_refinermodel, event.value)
            }
            is UIEvent.UpdateTRMS -> {
                tRefineModelSwitch = event.switch
                kv.encode(Constant.k_t_refinermodel_s, event.switch)
            }
            is UIEvent.UpdateTRA -> {
                tRefineAt = event.value
                kv.encode(Constant.k_t_refinerat, event.value)
            }
            is UIEvent.UpdateTRAS -> {
                tRefineAtSwitch = event.switch
                kv.encode(Constant.k_t_refinerat_s, event.switch)
            }
            is UIEvent.UpdateTP -> {
                tPrompt = event.value
                kv.encode(Constant.k_t_prompt, event.value)
            }
            is UIEvent.UpdateTPS -> {
                tPromptSwitch = event.switch
                kv.encode(Constant.k_t_prompt_s, event.switch)
            }
            is UIEvent.UpdateTPAS -> {
                tPromptAdSwitch = event.switch
                kv.encode(Constant.k_t_promptad_s, event.switch)
            }
            is UIEvent.UpdateTNP -> {
                tNPrompt = event.value
                kv.encode(Constant.k_t_nprompt, event.value)
            }
            is UIEvent.UpdateTNPS -> {
                tNPromptSwitch = event.switch
                kv.encode(Constant.k_t_nprompt_s, event.switch)
            }
            is UIEvent.UpdateTIW -> {
                tImgWidth = event.value
                kv.encode(Constant.k_t_imgw, event.value)
            }
            is UIEvent.UpdateTIWS -> {
                tImgWidthSwitch = event.switch
                kv.encode(Constant.k_t_imgw_s, event.switch)
            }
            is UIEvent.UpdateTIH -> {
                tImgHeight = event.value
                kv.encode(Constant.k_t_imgh, event.value)
            }
            is UIEvent.UpdateTIHS -> {
                tImgHeightSwitch = event.switch
                kv.encode(Constant.k_t_imgh_s, event.switch)
            }
            is UIEvent.UpdateTS -> {
                tSteps = event.value
                kv.encode(Constant.k_t_step, event.value)
            }
            is UIEvent.UpdateTSS -> {
                tStepsSwitch = event.switch
                kv.encode(Constant.k_t_step_s, event.switch)
            }
            is UIEvent.UpdateTC -> {
                tCFG = event.value
                kv.encode(Constant.k_t_cfg, event.value)
            }
            is UIEvent.UpdateTCS -> {
                tCFGSwitch = event.switch
                kv.encode(Constant.k_t_cfg_s, event.switch)
            }
            is UIEvent.UpdateTSampler -> {
                tSampler = event.value
                kv.encode(Constant.k_t_sampler, event.value)
            }
            is UIEvent.UpdateTSamplerS -> {
                tSamplerSwitch = event.switch
                kv.encode(Constant.k_t_sampler_s, event.switch)
            }
            is UIEvent.UpdateTBS -> {
                tBatchSize = event.value
                kv.encode(Constant.k_t_bsize, event.value)
            }
            is UIEvent.UpdateTBSS -> {
                tBatchSizeSwitch = event.switch
                kv.encode(Constant.k_t_bsize_s, event.switch)
            }
            is UIEvent.UpdateTScriptS -> {
                tScriptSwitch = event.switch
                kv.encode(Constant.k_t_script_s, event.switch)
            }
            is UIEvent.UpdateTSDPS -> {
                tSDPromptSwitch = event.switch
                kv.encode(Constant.k_t_sdprompt_s, event.switch)
            }
            is UIEvent.UpdateTCNS -> {
                tCNSwitch = event.switch
                kv.encode(Constant.k_t_cn_s, event.switch)
            }
        }
    }
}