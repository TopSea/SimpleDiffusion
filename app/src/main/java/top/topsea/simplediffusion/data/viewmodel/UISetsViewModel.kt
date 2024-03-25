package top.topsea.simplediffusion.data.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
class UISetsViewModel @Inject constructor(
    private val kv: MMKV,
    private val normalApiImp: NormalApiImp,
    private val promptApi: PromptApiImp,
    private val socketClient: SocketClient,
    @ApplicationContext context: Context
): ViewModel() {
    // 页面相关
    var _displaying by mutableStateOf(false) // 是否在展示图片
    var _longPressImage by mutableStateOf(false) // 是否在展示图片
    val _fullSelected: SnapshotStateList<String> = mutableStateListOf()      // 全选了的模块
    var _modelChanging by mutableStateOf(false) // 是否正在修改基础模型，初次进入时要先更新模型
    var _displayingImg by mutableStateOf(-1) // 当前展示的图片
    var _displayingTask by mutableStateOf(-1) // 当前展示的生成任务
    var _warningStr by mutableStateOf("") // 弹窗警告字符串
    var _paramTab by mutableStateOf(0) // 主页 Tab 序号
    var _cameraTab by mutableStateOf(0) // 拍摄设置页面 Tab 序号

    val displaying: Boolean = _displaying
    val longPressImage: Boolean = _longPressImage
    val fullSelected: MutableList<String> = _fullSelected
    val modelChanging: Boolean = _modelChanging
    val displayingImg: Int = _displayingImg
    val displayingTask: Int = _displayingTask
    val warningStr: String = _warningStr
    val paramTab: Int = _paramTab
    val cameraTab: Int = _cameraTab


    var _tempParamShow by mutableStateOf(false)
    var _tDisplayPriSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_display_pri_s, true))        // 是否显示显示优先级
    var _tSDModelSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_sdmodel_s, true))               // 是否显示基础大模型
    var _tRefineModelSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_refinermodel_s, true))      // 是否显示 Refiner 大模型
    var _tRefineAtSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_refinerat_s, true))            // 是否显示Refiner 时机
    var _tPromptSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_prompt_s, true))                 // 是否显示正面提示词
    var _tPromptAdSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_promptad_s, true))             // 是否显示正面提示词的可添加项
    var _tNPromptSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_nprompt_s, true))               // 是否显示负面提示词
    var _tNPromptAdSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_npromptad_s, true))           // 是否显示负面提示词的可添加项
    var _tImgWidthSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_imgw_s, true))                 // 是否显示图片宽度
    var _tImgHeightSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_imgh_s, true))                // 是否显示图片高度
    var _tStepsSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_step_s, true))                    // 是否显示生成步数
    var _tCFGSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_cfg_s, true))                       // 是否显示提示词相关性
    var _tSamplerSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_sampler_s, true))               // 是否显示 sampler
    var _tBatchSizeSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_bsize_s, true))               // 是否显示每批次生成数
    var _tSDPromptSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_sdprompt_s, true))             // 是否显示脚本
    var _tScriptSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_script_s, true))                 // 是否显示脚本
    var _tCNSwitch by mutableStateOf(kv.decodeBool(Constant.k_t_cn_s, true))                         // 是否显示 ControlNet

    val tempParamShow: Boolean = _tempParamShow
    val tDisplayPriSwitch: Boolean = _tDisplayPriSwitch
    val tSDModelSwitch: Boolean = _tSDModelSwitch
    val tRefineModelSwitch: Boolean = _tRefineModelSwitch
    val tRefineAtSwitch: Boolean = _tRefineAtSwitch
    val tPromptSwitch: Boolean = _tPromptSwitch
    val tPromptAdSwitch: Boolean = _tPromptAdSwitch
    val tNPromptSwitch: Boolean = _tNPromptSwitch
    val tNPromptAdSwitch: Boolean = _tNPromptAdSwitch
    val tImgWidthSwitch: Boolean = _tImgWidthSwitch
    val tImgHeightSwitch: Boolean = _tImgHeightSwitch
    val tStepsSwitch: Boolean = _tStepsSwitch
    val tCFGSwitch: Boolean = _tCFGSwitch
    val tSamplerSwitch: Boolean = _tSamplerSwitch
    val tBatchSizeSwitch: Boolean = _tBatchSizeSwitch
    val tSDPromptSwitch: Boolean = _tSDPromptSwitch
    val tScriptSwitch: Boolean = _tScriptSwitch
    val tCNSwitch: Boolean = _tCNSwitch

    
    var _iDisplayPriSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_display_pri_s, true))        // 是否显示显示优先级
    var _iSDModelSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_sdmodel_s, true))               // 是否显示基础大模型
    var _iRefineModelSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_refinermodel_s, true))      // 是否显示 Refiner 大模型
    var _iRefineAtSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_refinerat_s, true))            // 是否显示Refiner 时机
    var _iPromptSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_prompt_s, true))                 // 是否显示正面提示词
    var _iPromptAdSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_promptad_s, true))             // 是否显示正面提示词的可添加项
    var _iNPromptSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_nprompt_s, true))               // 是否显示负面提示词
    var _iNPromptAdSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_npromptad_s, true))           // 是否显示负面提示词的可添加项
    var _iDnoiseSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_dnoise_s, true))                 // 是否显示重绘幅度
    var _iImgWidthSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_imgw_s, true))                 // 是否显示图片宽度
    var _iImgHeightSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_imgh_s, true))                // 是否显示图片高度
    var _iStepsSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_step_s, true))                    // 是否显示生成步数
    var _iCFGSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_cfg_s, true))                       // 是否显示提示词相关性
    var _iSamplerSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_sampler_s, true))               // 是否显示 sampler
    var _iBatchSizeSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_bsize_s, true))               // 是否显示每批次生成数
    var _iSDPromptSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_sdprompt_s, true))             // 是否显示脚本
    var _iScriptSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_script_s, true))                 // 是否显示脚本
    var _iCNSwitch by mutableStateOf(kv.decodeBool(Constant.k_i_cn_s, true))                         // 是否显示 ControlNet

    val iDisplayPriSwitch: Boolean = _iDisplayPriSwitch
    val iSDModelSwitch: Boolean = _iSDModelSwitch
    val iRefineModelSwitch: Boolean = _iRefineModelSwitch
    val iRefineAtSwitch: Boolean = _iRefineAtSwitch
    val iPromptSwitch: Boolean = _iPromptSwitch
    val iPromptAdSwitch: Boolean = _iPromptAdSwitch
    val iNPromptSwitch: Boolean = _iNPromptSwitch
    val iNPromptAdSwitch: Boolean = _iNPromptAdSwitch
    val iDnoiseSwitch: Boolean = _iDnoiseSwitch
    val iImgWidthSwitch: Boolean = _iImgWidthSwitch
    val iImgHeightSwitch: Boolean = _iImgHeightSwitch
    val iStepsSwitch: Boolean = _iStepsSwitch
    val iCFGSwitch: Boolean = _iCFGSwitch
    val iSamplerSwitch: Boolean = _iSamplerSwitch
    val iBatchSizeSwitch: Boolean = _iBatchSizeSwitch
    val iSDPromptSwitch: Boolean = _iSDPromptSwitch
    val iScriptSwitch: Boolean = _iScriptSwitch
    val iCNSwitch: Boolean = _iCNSwitch


    // 设置相关
    var _showGenOn1 by mutableStateOf(kv.decodeBool(Constant.k_show_gen_on_1, true))// 生成队列的大小为一时拍摄后是否直接显示生成页面
    var _saveCapImage by mutableStateOf(kv.decodeBool(Constant.k_save_cap_img, true))// 是否保存拍摄的照片
    var _saveGridImage by mutableStateOf(kv.decodeBool(Constant.k_save_grid_img, true))// 是否保存拍摄的照片
    var _saveControlNet by mutableStateOf(kv.decodeBool(Constant.k_save_control, true))// 是否保存 ControlNet 产生的图片
    var _saveOnServer by mutableStateOf(kv.decodeBool(Constant.k_save_on_server, true))// 是否将生成的图片保持到 SD 服务器
    var _currentVae by mutableStateOf(kv.decodeParcelable(Constant.sd_vae, VaeModel::class.java)?: VaeModel("", ""))// 当前 Vae 模型
    var _taskQueueSize by mutableIntStateOf(kv.decodeInt(context.getString(R.string.kv_gen_size), 1))// 生成队列的大小

    val showGenOn1: Boolean = _showGenOn1
    val saveCapImage: Boolean = _saveCapImage
    val saveGridImage: Boolean = _saveGridImage
    val saveControlNet: Boolean = _saveControlNet
    val saveOnServer: Boolean = _saveOnServer
    val currentVae: VaeModel = _currentVae
    val taskQueueSize: Int = _taskQueueSize

    // 插件相关
    var _exControlNet by mutableStateOf(true)// 是否开启 ControlNet 插件，默认开启不可修改
    var _exAgentScheduler by mutableStateOf(kv.decodeBool(Constant.k_ex_agent_scheduler, false)) // 是否开启 GenScheduler 插件
    var _exSdPrompt by mutableStateOf(kv.decodeBool(Constant.k_ex_sd_prompt, false))// 是否开启 GenScheduler 插件

    val exControlNet: Boolean = _exControlNet
    val exAgentScheduler: Boolean = _exAgentScheduler
    val exSdPrompt: Boolean = _exSdPrompt

    // SimpleDiffusion Desktop 相关
    var _enableDesktop by mutableStateOf(false)// 启用 SimpleDiffusion Desktop
    var _connectDesktop by mutableStateOf(false)// 是否连接到了 SimpleDiffusion Desktop
    var _messageDesktop by mutableStateOf("")// 来自 SimpleDiffusion Desktop 的信息

    // SimpleDiffusion Desktop 相关
    val enableDesktop: Boolean = _enableDesktop
    val connectDesktop: Boolean = _connectDesktop
    val messageDesktop = _messageDesktop

    // SD 服务器相关
    var _serverConnected by mutableStateOf(false)  // 是否已连接到 SD 服务器
    val serverConnected  = _serverConnected


    init {
        socketClient.startClient({ str ->
            TextUtil.topsea("Socket message: $str")
            str?.let {_messageDesktop = str}
        }) {
            TextUtil.topsea("Socket client error: ${it?.message}")
        }
    }

    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.Display -> {
                _displaying = event.display
            }
            is UIEvent.DisplayImg -> {
                _displayingImg = event.index
                _displaying = !displaying
                TextUtil.topsea("DisplayImg: ${event.index}", Log.ERROR)
            }
            is UIEvent.DisplayTask -> {
                _displayingTask = event.index
                _displaying = !displaying
                TextUtil.topsea("DisplayTask : ${event.index}", Log.ERROR)
            }
            is UIEvent.LongPressImage -> {
                _longPressImage = event.longPressImage
            }
            is UIEvent.IsSaveCapImg -> {
                _saveCapImage = event.saveCapImage
                kv.encode(Constant.k_save_cap_img, saveCapImage)
            }
            is UIEvent.IsSaveGridImg -> {
                _saveGridImage = event.saveGridImage
                kv.encode(Constant.k_save_grid_img, saveGridImage)
            }
            is UIEvent.IsSaveControl -> {
                _saveControlNet = event.saveControlNet
                kv.encode(Constant.k_save_control, saveControlNet)
            }

            // SD 服务器相关
            is UIEvent.ServerConnected -> {
                _serverConnected = event.serverConnected
            }
            is UIEvent.SaveOnServer -> {
                _saveOnServer = event.saveOnServer
                kv.encode(Constant.k_save_on_server, saveOnServer)
            }
            is UIEvent.ModelChanging -> {
                _modelChanging = event.modelChanging
            }
            is UIEvent.ExSettingChange -> {
                when (event.whichOne) {
                    "AgentScheduler" -> {
                        if (exAgentScheduler) {
                            // 关闭直接关
                            _exAgentScheduler = false
                            kv.encode(Constant.k_ex_agent_scheduler, false)
                            event.onChangeSuccess(false)
                        } else {
                            // 打开需要检查
                            viewModelScope.launch {
                                normalApiImp.checkAgentScheduler({
                                    _exAgentScheduler = true
                                    kv.encode(Constant.k_ex_agent_scheduler, true)
                                    event.onChangeSuccess(true)
                                }){
                                    _warningStr = event.context.getString(R.string.t_no_ex)
                                }
                            }
                        }
                    }
                    "SdPrompt" -> {
                        if (exSdPrompt) {
                            // 关闭直接关
                            _exSdPrompt = false
                            kv.encode(Constant.k_ex_sd_prompt, false)
                            event.onChangeSuccess(false)
                        } else {
                            // 打开需要检查
                            viewModelScope.launch {
                                val sdPromptVersion = promptApi.checkSdPrompt()
                                if (sdPromptVersion) {
                                    _warningStr = event.context.getString(R.string.t_no_ex)
                                } else {
                                    _exSdPrompt = true
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
                _paramTab = event.tabIndex
            }
            is UIEvent.ChangeCameraTab -> {
                _cameraTab = event.tabIndex
            }
            is UIEvent.AddGenSize -> {
                if (taskQueueSize < 10) {
                    _taskQueueSize += 1
                    kv.encode(event.context.getString(R.string.kv_gen_size), taskQueueSize)

                    // 大于一自动关闭拍摄后显示
                    _showGenOn1 = false
                    kv.encode(Constant.k_show_gen_on_1, false)
                }
            }
            is UIEvent.MinusGenSize -> {
                if (_taskQueueSize > 1) {
                    _taskQueueSize -= 1
                    kv.encode(event.context.getString(R.string.kv_gen_size), taskQueueSize)

                    // 等于一自动开启拍摄后显示
                    if (_taskQueueSize == 1) {
                        _showGenOn1 = true
                        kv.encode(Constant.k_show_gen_on_1, true)
                    }
                }
            }
            is UIEvent.ShowGenOn1 -> {
                if (taskQueueSize == 1) {
                    _showGenOn1 = event.showGenOn1
                    kv.encode(Constant.k_show_gen_on_1, event.showGenOn1)
                }
            }

            // UI 相关的弹窗
            is UIEvent.UIWarning -> {
                _warningStr = event.warningStr
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
                                _currentVae = vae
                                kv.encode(Constant.sd_vae, vae)
                            }
                            else -> event.onFailure()
                        }
                    }
                }
            }
            is UIEvent.ConnectDesktop -> {
                _connectDesktop = event.connectDesktop
            }
            is UIEvent.Send2Desktop -> {
                val message = event.str
                socketClient.sendMsg(message) {
                    TextUtil.topsea("Socket send error: ${it?.message}")
                }
            }


            is UIEvent.TempParamShow -> {
                _tempParamShow = event.switch
            }
            is UIEvent.UpdateTDPS -> {
                _tDisplayPriSwitch = event.switch
                kv.encode(Constant.k_t_display_pri_s, event.switch)
            }
            is UIEvent.UpdateTSDMS -> {
                _tSDModelSwitch = event.switch
                kv.encode(Constant.k_t_sdmodel_s, event.switch)
            }
            is UIEvent.UpdateTRMS -> {
                _tRefineModelSwitch = event.switch
                kv.encode(Constant.k_t_refinermodel_s, event.switch)
            }
            is UIEvent.UpdateTRAS -> {
                _tRefineAtSwitch = event.switch
                kv.encode(Constant.k_t_refinerat_s, event.switch)
            }
            is UIEvent.UpdateTPS -> {
                _tPromptSwitch = event.switch
                kv.encode(Constant.k_t_prompt_s, event.switch)
            }
            is UIEvent.UpdateTPAS -> {
                _tPromptAdSwitch = event.switch
                kv.encode(Constant.k_t_promptad_s, event.switch)
            }
            is UIEvent.UpdateTNPS -> {
                _tNPromptSwitch = event.switch
                kv.encode(Constant.k_t_nprompt_s, event.switch)
            }
            is UIEvent.UpdateTNPAS -> {
                _tNPromptAdSwitch = event.switch
                kv.encode(Constant.k_t_npromptad_s, event.switch)
            }
            is UIEvent.UpdateTIWS -> {
                _tImgWidthSwitch = event.switch
                kv.encode(Constant.k_t_imgw_s, event.switch)
            }
            is UIEvent.UpdateTIHS -> {
                _tImgHeightSwitch = event.switch
                kv.encode(Constant.k_t_imgh_s, event.switch)
            }
            is UIEvent.UpdateTSS -> {
                _tStepsSwitch = event.switch
                kv.encode(Constant.k_t_step_s, event.switch)
            }
            is UIEvent.UpdateTCS -> {
                _tCFGSwitch = event.switch
                kv.encode(Constant.k_t_cfg_s, event.switch)
            }
            is UIEvent.UpdateTSamplerS -> {
                _tSamplerSwitch = event.switch
                kv.encode(Constant.k_t_sampler_s, event.switch)
            }
            is UIEvent.UpdateTBSS -> {
                _tBatchSizeSwitch = event.switch
                kv.encode(Constant.k_t_bsize_s, event.switch)
            }
            is UIEvent.UpdateTScriptS -> {
                _tScriptSwitch = event.switch
                kv.encode(Constant.k_t_script_s, event.switch)
            }
            is UIEvent.UpdateTSDPS -> {
                _tSDPromptSwitch = event.switch
                kv.encode(Constant.k_t_sdprompt_s, event.switch)
            }
            is UIEvent.UpdateTCNS -> {
                _tCNSwitch = event.switch
                kv.encode(Constant.k_t_cn_s, event.switch)
            }


            is UIEvent.UpdateIDPS -> {
                _iDisplayPriSwitch = event.switch
                kv.encode(Constant.k_i_display_pri_s, event.switch)
            }
            is UIEvent.UpdateISDMS -> {
                _iSDModelSwitch = event.switch
                kv.encode(Constant.k_i_sdmodel_s, event.switch)
            }
            is UIEvent.UpdateIRMS -> {
                _iRefineModelSwitch = event.switch
                kv.encode(Constant.k_i_refinermodel_s, event.switch)
            }
            is UIEvent.UpdateIRAS -> {
                _iRefineAtSwitch = event.switch
                kv.encode(Constant.k_i_refinerat_s, event.switch)
            }
            is UIEvent.UpdateIPS -> {
                _iPromptSwitch = event.switch
                kv.encode(Constant.k_i_prompt_s, event.switch)
            }
            is UIEvent.UpdateIPAS -> {
                _iPromptAdSwitch = event.switch
                kv.encode(Constant.k_i_promptad_s, event.switch)
            }
            is UIEvent.UpdateINPS -> {
                _iNPromptSwitch = event.switch
                kv.encode(Constant.k_i_nprompt_s, event.switch)
            }
            is UIEvent.UpdateINPAS -> {
                _iNPromptAdSwitch = event.switch
                kv.encode(Constant.k_i_npromptad_s, event.switch)
            }
            is UIEvent.UpdateIDNS -> {
                _iDnoiseSwitch = event.switch
                kv.encode(Constant.k_i_dnoise_s, event.switch)
            }
            is UIEvent.UpdateIIWS -> {
                _iImgWidthSwitch = event.switch
                kv.encode(Constant.k_i_imgw_s, event.switch)
            }
            is UIEvent.UpdateIIHS -> {
                _iImgHeightSwitch = event.switch
                kv.encode(Constant.k_i_imgh_s, event.switch)
            }
            is UIEvent.UpdateISS -> {
                _iStepsSwitch = event.switch
                kv.encode(Constant.k_i_step_s, event.switch)
            }
            is UIEvent.UpdateICS -> {
                _iCFGSwitch = event.switch
                kv.encode(Constant.k_i_cfg_s, event.switch)
            }
            is UIEvent.UpdateISamplerS -> {
                _iSamplerSwitch = event.switch
                kv.encode(Constant.k_i_sampler_s, event.switch)
            }
            is UIEvent.UpdateIBSS -> {
                _iBatchSizeSwitch = event.switch
                kv.encode(Constant.k_i_bsize_s, event.switch)
            }
            is UIEvent.UpdateIScriptS -> {
                _iScriptSwitch = event.switch
                kv.encode(Constant.k_i_script_s, event.switch)
            }
            is UIEvent.UpdateISDPS -> {
                _iSDPromptSwitch = event.switch
                kv.encode(Constant.k_i_sdprompt_s, event.switch)
            }
            is UIEvent.UpdateICNS -> {
                _iCNSwitch = event.switch
                kv.encode(Constant.k_i_cn_s, event.switch)
            }
        }
    }
}