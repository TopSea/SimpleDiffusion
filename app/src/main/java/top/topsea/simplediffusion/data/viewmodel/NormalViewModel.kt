package top.topsea.simplediffusion.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import top.topsea.simplediffusion.api.dto.BaseModel
import top.topsea.simplediffusion.api.dto.ControlTypes
import top.topsea.simplediffusion.api.dto.VaeModel
import top.topsea.simplediffusion.api.impl.NormalApiImp
import top.topsea.simplediffusion.api.impl.PromptApiImp
import top.topsea.simplediffusion.data.param.CNParam
import top.topsea.simplediffusion.data.param.CNParamDao
import top.topsea.simplediffusion.data.param.UserPrompt
import top.topsea.simplediffusion.data.param.UserPromptDao
import top.topsea.simplediffusion.data.state.ControlNetState
import top.topsea.simplediffusion.data.state.NormalState
import top.topsea.simplediffusion.data.state.PromptCategory
import top.topsea.simplediffusion.data.state.PromptFile
import top.topsea.simplediffusion.data.state.PromptState
import top.topsea.simplediffusion.event.ControlNetEvent
import top.topsea.simplediffusion.event.ExecuteState
import top.topsea.simplediffusion.event.PromptEvent
import top.topsea.simplediffusion.util.Constant
import top.topsea.simplediffusion.util.TextUtil
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NormalViewModel @Inject constructor(
    private val normalApiImp: NormalApiImp,
    private val promptApi: PromptApiImp,
    private val dao: CNParamDao,
    private val promptDao: UserPromptDao,
    private val kv: MMKV,
): ViewModel() {
    private val searchTxt = MutableStateFlow("")
    private val exSdPrompt = MutableStateFlow(kv.decodeBool(Constant.k_ex_sd_prompt, false))

    // 模型状态
    private val _baseState = MutableStateFlow(NormalState<BaseModel>())
    private val _loraState = MutableStateFlow(PromptState())
    private val _vaeState = MutableStateFlow(NormalState<VaeModel>())

    private val _bases = normalApiImp.getModels(BaseModel::class.java)
        .map {
            it.sortedBy {sort ->
                sort.model_name
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _samplers = normalApiImp.getSDSamplers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val sdPrompt = normalApiImp.getSDPrompts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _loras = normalApiImp.getModels(UserPrompt::class.java)
        .map { models ->
            val pairs: MutableList<Pair<String, MutableList<UserPrompt>>> = ArrayList()
            pairs.add("\\" to ArrayList())
            var dir = ""
            models.forEach { lora ->
                val withParent = lora.path.split("Lora\\")
                if (withParent.size > 1) {
                    val subDir = withParent[1]
                    if (subDir.contains("\\")) {
                        val temp = subDir.split("\\")
                        if (temp[0] == dir) {
                            pairs.find { it.first == dir }!!.second.add(lora)
                        } else {
                            dir = temp[0]
                            pairs.add(dir to arrayListOf(lora))
                        }
                    } else {
                        pairs.find { it.first == "\\" }!!.second.add(lora)
                    }
                }
                TextUtil.topsea(withParent.toTypedArray().contentToString())
            }
            pairs.toList()
        }
        .map { loras ->
            val categories = ArrayList<PromptCategory>()
            loras.forEach {
                categories.add(PromptCategory(it.first, TextUtil.addable2SD(it.second, isLora = true)))
            }

            PromptFile("Lora", categories)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PromptFile())
    private val _local = promptDao.getAllPrompt()
        .map { prompts ->
            val categories = PromptCategory("SimDiff", TextUtil.addable2SD(prompts))
            PromptFile("本地", listOf(categories))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PromptFile())

    val localPrompts: StateFlow<List<UserPrompt>> = promptDao.getAllPrompt()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _exPrompt = exSdPrompt.flatMapLatest {
        if (it)
            promptApi.getAllPrompts()
        else
            flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _vaes = normalApiImp.getModels(VaeModel::class.java)
        .map {
            it.sortedBy {sort ->
                sort.model_name
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val baseState: StateFlow<NormalState<BaseModel>> = combine(_baseState, _bases, _samplers) { baseState, bases, samplers ->
        baseState.copy(models = bases, samplers = samplers)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NormalState())

    val loraState = combine(_loraState, _loras, _local, _exPrompt) { loraState, loras, local, exPrompt ->
        loraState.copy(loras = loras, local = local, promptSets = exPrompt)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PromptState())

    val vaeState = combine(_vaeState, _vaes) { vaeState, vaes ->
        vaeState.copy(models = vaes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NormalState())

    // ControlNet
    private val _cnState = MutableStateFlow(ControlNetState())
    private val _cnParams: StateFlow<List<CNParam>> = searchTxt.flatMapLatest { txt ->
        if (txt.isEmpty()) {
            dao.getParams()
        } else {
            dao.getSearchParams(txt)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _cnTypes = normalApiImp.getCNTypes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val cnState = combine(_cnState, _cnParams, _cnTypes)  { cnState, cnParams, cnTypes ->
        cnState.copy(
            cnParams = cnParams,
            controlTypes = cnTypes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ControlNetState())

    init {
        viewModelScope.launch {
            normalApiImp.getSDPrompts().collectLatest { controlTypes ->
                controlTypes.forEach {
                    TextUtil.topsea(it.toString())
                }
            }

            _cnState.update {
                it.copy(
                    version = normalApiImp.getCNVersion(),
                    maxModelsNum = normalApiImp.getCNSettings(),
                )
            }
        }
    }

    fun cnEvent(event: ControlNetEvent) {
        when (event) {
            is ControlNetEvent.ActivateByRequest -> {
                viewModelScope.launch {
                    cnState.value.cnParams.forEach {
                        it.enabled.value = event.index.contains(it.id)
                        dao.update(it)
                    }
                }
            }
            is ControlNetEvent.AddCNParam -> {
                viewModelScope.launch {
                    dao.insert(event.cnModel)
                }
            }
            is ControlNetEvent.AddImage -> {
                val param = cnState.value.editParam!!
                param.input_image.value = event.base64Str
            }
            is ControlNetEvent.CloseImage -> {
                val param = cnState.value.editParam!!
                param.input_image.value = ""
            }
            is ControlNetEvent.DeleteCNParam -> {
                viewModelScope.launch {
                    dao.delete(event.cnModel)
                }
            }
            is ControlNetEvent.EditCNParam -> {
                _cnState.update {
                    it.copy(
                        editing = event.editing,
                        editParam = event.cnModel
                    )
                }
            }
            is ControlNetEvent.SearchCNParam -> {
                searchTxt.value = event.txt
            }
            is ControlNetEvent.UpdateCNParam -> {
                val cnParam = event.cnModel
                viewModelScope.launch {
                    dao.update(cnParam)
                    delay(100)
                    event.afterUpdate()
                }
            }
            is ControlNetEvent.UpdateConfig<*> -> {
                viewModelScope.launch {
                    normalApiImp.updateSdConfig(event.simpleSdConfig).collectLatest {
                        when (it) {
                            is ExecuteState.ExecuteSuccess -> event.onSuccess()
                            else -> event.onFailure()
                        }
                    }
                }
            }
            is ControlNetEvent.ChooseType -> {
                val type = event.type
                val controlType = cnState.value.controlTypes.find {
                    it.first == type
                }
                if (controlType != null) {
                    event.module.value = controlType.second.default_option
                    event.model.value = controlType.second.default_model
                }
                _cnState.update {
                    it.copy(
                        currType = event.type,
                        currControlType = controlType?.second ?: ControlTypes(emptyList(), emptyList(), "", "")
                    )
                }
            }
        }
    }

    fun promptEvent(event: PromptEvent) {
        when (event) {
            is PromptEvent.AddPrompt -> {
                viewModelScope.launch {
                    promptDao.insert(event.up)
                }
            }
            is PromptEvent.DeletePrompt -> {
                viewModelScope.launch {
                    promptDao.delete(event.up)
                }
            }
            is PromptEvent.UpdatePrompt -> {
                viewModelScope.launch {
                    promptDao.update(event.up)
                }
            }
        }
    }

    fun refreshLoras() {
//        viewModelScope.launch {
//            val loras = normalApiImp.refreshModels(UserPrompt::class.java)
//
//            val pairs: MutableList<Pair<String, MutableList<UserPrompt>>> = ArrayList()
//            pairs.add("\\" to ArrayList())
//            var dir = ""
//            loras.forEach { lora ->
//                val withParent = lora.path.split("Lora\\")
//                if (withParent.size > 1) {
//                    val subDir = withParent[1]
//                    if (subDir.contains("\\")) {
//                        val temp = subDir.split("\\")
//                        if (temp[0] == dir) {
//                            pairs.find { it.first == dir }!!.second.add(lora)
//                        } else {
//                            dir = temp[0]
//                            pairs.add(dir to arrayListOf(lora))
//                        }
//                    } else {
//                        pairs.find { it.first == "\\" }!!.second.add(lora)
//                    }
//                }
//                TextUtil.topsea(withParent.toTypedArray().contentToString())
//            }
//
//            if (pairs.isNotEmpty())
//                _loraState.update {
//                    it.copy(
//                        loras = pairs
//                    )
//                }
//        }
    }

    fun refreshBases() {
        viewModelScope.launch {
            val bases = normalApiImp.refreshModels(BaseModel::class.java)
            if (bases.isNotEmpty())
                _baseState.update {
                    it.copy(
                        models = bases.sortedBy { model ->
                            model.model_name
                        }
                    )
                }
        }
    }

    fun refreshVaes() {
        viewModelScope.launch {
            _vaeState.update {
                it.copy(
                    models = normalApiImp.refreshModels(VaeModel::class.java)
                )
            }
        }
    }

    fun exSdPrompt(isOn: Boolean) {
        exSdPrompt.value = isOn
    }

    suspend fun checkSDConnect(checkConnect: (Boolean) -> Unit) {
        normalApiImp.checkSDConnect(checkConnect)
    }

    fun cancelGenerate(taskID: String = "") {
        viewModelScope.launch {
            normalApiImp.skipGenerate(taskID)
        }
    }
}