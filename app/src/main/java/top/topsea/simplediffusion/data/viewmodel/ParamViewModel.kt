package top.topsea.simplediffusion.data.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import top.topsea.simplediffusion.api.dto.Img2Img
import top.topsea.simplediffusion.api.dto.Txt2Img
import top.topsea.simplediffusion.api.impl.GenImgApiImp
import top.topsea.simplediffusion.data.param.CNParam
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.param.ImgParamDao
import top.topsea.simplediffusion.data.param.ParamActivate
import top.topsea.simplediffusion.data.param.ParamControlNet
import top.topsea.simplediffusion.data.param.TxtParam
import top.topsea.simplediffusion.data.param.TxtParamDao
import top.topsea.simplediffusion.data.state.ParamLocalState
import top.topsea.simplediffusion.event.ParamEvent
import top.topsea.simplediffusion.event.RequestState
import top.topsea.simplediffusion.util.TextUtil
import javax.inject.Inject

@HiltViewModel
class ParamViewModel @Inject constructor(
    private val newbieApi: GenImgApiImp,
    private val aTxtDao: TxtParamDao,
    private val aImgDao: ImgParamDao,
): BasicViewModel() {
    private val searchTxt = MutableStateFlow("")
    private val searchImg = MutableStateFlow("")

    private val _param_state = MutableStateFlow(ParamLocalState())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _tparams: StateFlow<List<TxtParam>> = searchTxt.flatMapLatest { txt ->
        if (txt.isEmpty()) {
            aTxtDao.getTxtParams()
        } else {
            aTxtDao.getSearchParams(txt)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _iparams: StateFlow<List<ImgParam>> = searchImg.flatMapLatest { txt ->
        if (txt.isEmpty()) {
            aImgDao.getImgParams()
        } else {
            aImgDao.getSearchParams(txt)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    override val paramState = combine(_param_state, _tparams, _iparams) { state, tparams, iparams ->
        val iParam = iparams.find { it.activate }
        val currParam = tparams.find { it.activate } ?: iParam
        TextUtil.topsea("currParam: $currParam")
        state.copy(
            tParams = tparams,
            iParams = iparams,
            currParam = currParam,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ParamLocalState())

    private suspend fun image2Image(img2Img: Img2Img, controlNets: List<CNParam>, onError: (String) -> Unit, onImageReceived: (Array<String>?, String?) -> Unit) {
        // 判断 ControlNet 的图片类型
        controlNets.forEach {
            if (it.use_imgImg) {
                it.input_image.value = img2Img.init_images[0]
            }
            TextUtil.topsea("ControlNets: ${it}")
        }

        val request = img2Img.requestWithCN(controlNets)
        TextUtil.topsea(request)

        newbieApi.img2Img(request, onImageReceived).collect{
            when (it) {
                is RequestState.OnAppError -> onError(it.errorStr)
                is RequestState.OnRequestError -> onError(it.error)
                is RequestState.OnRequestFailure -> onError(it.error)
                is RequestState.OnRequestSuccess -> { }
            }
        }
    }

    private suspend fun text2Image(txt2Img: Txt2Img, controlNets: List<CNParam>, onError: (String) -> Unit, onImageReceived: (Array<String>?, String?) -> Unit) {
        TextUtil.topsea("text2Image: ${txt2Img.requestWithCN(controlNets)}")
        newbieApi.txt2Img(txt2Img.requestWithCN(controlNets), onImageReceived).collect{
            when (it) {
                is RequestState.OnAppError -> onError(it.errorStr)
                is RequestState.OnRequestError -> onError(it.error)
                is RequestState.OnRequestFailure -> onError(it.error)
                is RequestState.OnRequestSuccess -> { }
            }
        }
    }

    override fun paramEvent(event: ParamEvent) {
        when (event) {
            is ParamEvent.ActivateParam -> {
                val bp = paramState.value.currParam
                viewModelScope.launch {
                    bp?.let {
                        if (it is TxtParam) {
                            val pa = ParamActivate(it.id, false)
                            aTxtDao.update(pa)
                        }
                        if (it is ImgParam) {
                            val pa = ParamActivate(it.id, false)
                            aImgDao.update(pa)
                        }
                    }

                    val nbp = event.bp
                    if (nbp is TxtParam) {
                        val pa = ParamActivate(nbp.id, true)
                        aTxtDao.update(pa)
                    }
                    if (nbp is ImgParam) {
                        val pa = ParamActivate(nbp.id, true)
                        aImgDao.update(pa)
                    }
                    _param_state.update {
                        it.copy(
                            currParam = nbp
                        )
                    }
                }
            }
            is ParamEvent.AddControlNet -> {
                viewModelScope.launch {
                    val bp = paramState.value.currParam
                    TextUtil.topsea("AddControlNet currParam: $bp")
                    if (bp is TxtParam) {
                        bp.control_net.add(event.index)
                        aTxtDao.update(bp)
                    }
                    if (bp is ImgParam) {
                        bp.control_net.add(event.index)
                        aImgDao.update(bp)
                    }
                }
            }
            is ParamEvent.AddImage -> {
                val param = paramState.value.editingParam as ImgParam?
                if (param != null)
                    param.image.value = event.base64Str
                else {
                    val cParam = paramState.value.currParam as ImgParam
                    cParam.image.value = event.base64Str
                }
            }
            is ParamEvent.AddParam -> {
                viewModelScope.launch {
                    val param = event.bp
                    if (param is TxtParam) {

                        val txt2Img = TxtParam(
                            baseModel = param.baseModel,
                            defaultPrompt = param.defaultPrompt,
                            defaultNegPrompt = param.defaultNegPrompt,
                            width = param.width,
                            height = param.height,
                            steps = param.steps,
                            cfgScale = param.cfgScale,
                            sampler_index = param.sampler_index,
                            batch_size = param.batch_size,
                            script_name = param.script_name,
                            script_args = param.script_args,
                            control_net = param.control_net,
                        )
                        aTxtDao.insert(txt2Img)
                    }
                    if (param is ImgParam) {
                        val img2Img = ImgParam(
                            image = param.image,
                            denoising_strength = param.denoising_strength,
                            baseModel = param.baseModel,
                            defaultPrompt = param.defaultPrompt,
                            defaultNegPrompt = param.defaultNegPrompt,
                            width = param.width,
                            height = param.height,
                            steps = param.steps,
                            cfgScale = param.cfgScale,
                            sampler_index = param.sampler_index,
                            resize_mode = param.resize_mode,
                            batch_size = param.batch_size,
                            script_name = param.script_name,
                            script_args = param.script_args,
                            control_net = param.control_net,
                        )
                        aImgDao.insert(img2Img)
                    }
                }
            }
            is ParamEvent.CloseControlNet -> {
                viewModelScope.launch {
                    val bp = paramState.value.currParam
                    TextUtil.topsea("AddControlNet currParam: $bp")
                    if (bp is TxtParam) {
                        bp.control_net.remove(event.index)
                        val pcn = ParamControlNet(id = bp.id, control_net = bp.control_net)
                        aTxtDao.update(pcn)
                    }
                    if (bp is ImgParam) {
                        bp.control_net.remove(event.index)
                        val pcn = ParamControlNet(id = bp.id, control_net = bp.control_net)
                        aImgDao.update(pcn)
                    }
                }
            }
            is ParamEvent.DeleteControlNet -> {
                viewModelScope.launch {
                    val bp = paramState.value.currParam
                    bp?.control_net?.remove(event.index)

                    val tps = paramState.value.tParams
                    val ips = paramState.value.iParams

                    tps.forEach {
                        it.control_net.remove(event.index)
                        aTxtDao.update(it)
                    }
                    ips.forEach {
                        it.control_net.remove(event.index)
                        aImgDao.update(it)
                    }

                }
            }
            ParamEvent.CloseImage -> {
                val param = paramState.value.editingParam as ImgParam
                param.image.value = ""
            }
            is ParamEvent.DeleteParam -> {
                val bp = event.bp
                if (bp.activate) {
                    _param_state.update {
                        it.copy(currParam = null)
                    }
                }
                viewModelScope.launch {
                    if (bp is TxtParam) {
                        aTxtDao.delete(bp)
                    }
                    if (bp is ImgParam) {
                        aImgDao.delete(bp)
                    }
                }
            }
            is ParamEvent.EditActivate -> {
                val bp = paramState.value.currParam
                if (bp != null) {
                    _param_state.update {
                        it.copy(
                            editing = event.editing,
                            editingCurr = event.editingActivate,
                            editingParam = bp
                        )
                    }
                } else {
                    event.onNoAct()
                }
            }
            is ParamEvent.EditParam -> {
                _param_state.update {
                    it.copy(
                        editing = event.editing,
                        editingParam = event.bp
                    )
                }
            }
            is ParamEvent.GenImage -> {
                val param = paramState.value.currParam
                viewModelScope.launch {
                    if (param is ImgParam) {
                        val img2Img = Img2Img(
                            init_images = arrayOf(param.image.value),
                            denoising_strength = param.denoising_strength,
                            prompt = param.defaultPrompt,
                            negative_prompt = param.defaultNegPrompt,
                            width = param.width,
                            height = param.height,
                            steps = param.steps,
                            cfg_scale = param.cfgScale,
                            sampler_name = param.sampler_index,
                            resize_mode = param.resize_mode,
                            batch_size = param.batch_size,
                            script_name = param.script_name,
                            script_args = param.script_args,
                        )
                        image2Image(img2Img, event.controlNets, event.onGenError, event.onGenSuccess)
                    }
                    if (param is TxtParam) {
                        val txt2Img = Txt2Img(
                            prompt = param.defaultPrompt,
                            negative_prompt = param.defaultNegPrompt,
                            width = param.width,
                            height = param.height,
                            steps = param.steps,
                            cfg_scale = param.cfgScale,
                            sampler_name = param.sampler_index,
                            script_name = param.script_name,
                            script_args = param.script_args,
                            batch_size = param.batch_size
                        )
                        text2Image(txt2Img, event.controlNets, event.onGenError, event.onGenSuccess)
                    }
                }
            }
            is ParamEvent.GenImageInList -> {
                val param = event.imgParam
                viewModelScope.launch {
                    val img2Img = Img2Img(
                        init_images = arrayOf(param.image.value),
                        denoising_strength = param.denoising_strength,
                        prompt = param.defaultPrompt,
                        negative_prompt = param.defaultNegPrompt,
                        width = param.width,
                        height = param.height,
                        steps = param.steps,
                        cfg_scale = param.cfgScale,
                        sampler_name = param.sampler_index,
                        resize_mode = param.resize_mode,
                        batch_size = param.batch_size,
                        script_name = param.script_name,
                        script_args = param.script_args,
                    )
                    image2Image(img2Img, event.controlNets, event.onGenError, event.onGenSuccess)
                }
            }
            is ParamEvent.CheckCapture -> {
                val param = paramState.value.currParam
                if (param !is ImgParam) {
                    event.notInI2I()
                    return
                }
                event.checkPass()
            }
            is ParamEvent.CaptureImage -> {
                val param = paramState.value.currParam
                viewModelScope.launch {
                    val size = event.size
                    if (param is ImgParam) {
                        val img2Img = Img2Img(
                            init_images = arrayOf(event.imgBase64),
                            denoising_strength = param.denoising_strength,
                            prompt = param.defaultPrompt,
                            negative_prompt = param.defaultNegPrompt,
                            width = size.width,
                            height = size.height,
                            steps = param.steps,
                            cfg_scale = param.cfgScale,
                            sampler_name = param.sampler_index,
                            resize_mode = param.resize_mode,
                            batch_size = param.batch_size,
                            script_name = param.script_name,
                            script_args = param.script_args,
                        )
                        image2Image(img2Img, event.controlNets, event.onError, event.onGenSuccess)
                    }
                }
            }
            is ParamEvent.SearchParam -> {
                if (event.isi2i) {
                    searchImg.value = event.txt
                } else {
                    searchTxt.value = event.txt
                }
            }
            is ParamEvent.ShareParam -> TODO()
            is ParamEvent.UpdateParam -> {
                viewModelScope.launch {
                    val bp = event.bp
                    TextUtil.topsea("UpdateParam bp: $bp")
                    if (bp is TxtParam) {
                        aTxtDao.update(bp)
                    }
                    if (bp is ImgParam) {
                        aImgDao.update(bp)
                    }
                    if (bp.activate) {
                        _param_state.update {
                            it.copy(
                                currParam = bp
                            )
                        }
                    }
                }
            }
        }
    }
}