package top.topsea.simplediffusion.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.param.BasicParam
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.param.TxtParam
import top.topsea.simplediffusion.data.param.scriptsImg
import top.topsea.simplediffusion.data.param.scriptsTxt
import top.topsea.simplediffusion.data.state.ParamLocalState
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UISetsViewModel
import top.topsea.simplediffusion.event.ControlNetEvent
import top.topsea.simplediffusion.event.ParamEvent
import top.topsea.simplediffusion.navUp
import top.topsea.simplediffusion.ui.component.ParamRowChangeName
import top.topsea.simplediffusion.ui.component.ParamRowChoice
import top.topsea.simplediffusion.ui.component.ParamRowChooseSampler
import top.topsea.simplediffusion.ui.component.ParamRowControlNet
import top.topsea.simplediffusion.ui.component.StepRowFloat
import top.topsea.simplediffusion.ui.component.ParamRowImgChoose
import top.topsea.simplediffusion.ui.component.StepRowInt
import top.topsea.simplediffusion.ui.component.ParamRowPrompt
import top.topsea.simplediffusion.ui.component.SwipeInt
import top.topsea.simplediffusion.ui.scripts.USDUpscaleScript
import top.topsea.simplediffusion.ui.scripts.XYZPlotScript
import top.topsea.simplediffusion.util.Constant
import top.topsea.simplediffusion.util.TextUtil

@Composable
fun ParamEditScreen(
    navController: NavController,
    cardColor: Color,
    paramState: ParamLocalState,
    uiSetsViewModel: UISetsViewModel,
    paramEvent: (ParamEvent) -> Unit,
    normalViewModel: NormalViewModel,
) {
    val currParam = paramState.editingParam!!

    LaunchedEffect(key1 = null) {
        paramEvent(ParamEvent.EditParam(currParam, true))
    }

    if (paramState.editing) {
        ParamEditContent(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background),
            cardColor = cardColor,
            currParam = currParam,
            navController = navController,
            paramEvent = paramEvent,
            uiSetsViewModel = uiSetsViewModel,
            normalViewModel = normalViewModel,
        )
    }
}

@Composable
fun EditBottomBar(
    modifier: Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.top_bar_height))
            .background(color = MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { onDismiss() }, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = stringResource(id = R.string.p_btn_dismiss))
        }

        TextButton(onClick = { onConfirm() }, modifier = Modifier.padding(end = 16.dp)) {
            Text(text = stringResource(id = R.string.p_btn_confirm))
        }
    }
}

@Composable
fun ParamEditContent(
    modifier: Modifier,
    cardColor: Color,
    currParam: BasicParam,
    navController: NavController,
    uiSetsViewModel: UISetsViewModel,
    paramEvent: (ParamEvent) -> Unit,
    normalViewModel: NormalViewModel,
) {
    val context = LocalContext.current
    val isImg2Img = currParam is ImgParam
    val name = remember { mutableStateOf(currParam.name) }
    val order = remember { mutableStateOf(currParam.priority_order) }

    val cnState by normalViewModel.cnState.collectAsState()
    val baseState by normalViewModel.baseState.collectAsState()
    val loraState by normalViewModel.loraState.collectAsState()
    val sdPrompt by normalViewModel.sdPrompt.collectAsState()
    val models = baseState.models
    val cnModels = cnState.cnParams
    val loras = loraState.loras
    val prompts = loraState.local
    val promptSets = loraState.promptSets

    val width = remember { mutableStateOf(currParam.width) }
    val height = remember { mutableStateOf(currParam.height) }
    val baseModel = remember { mutableStateOf(currParam.baseModel) }
    val refinerModel = remember { mutableStateOf(currParam.refinerModel) }
    val refinerAt = remember { mutableStateOf(currParam.refinerAt) }
    val steps = remember { mutableStateOf(currParam.steps) }
    val cfgScale = remember { mutableStateOf(currParam.cfgScale) }

    val sampler_index = remember { mutableStateOf(currParam.sampler_index) }
    val batch_size = remember { mutableStateOf(currParam.batch_size) }
    val script_name = remember { mutableStateOf(currParam.script_name) }
    val script_args = remember {
        mutableStateOf(currParam.script_args)
    }
    val controlNets = remember {
        currParam.control_net
    }

    val image = remember {
        if (currParam is ImgParam)
            currParam.image
        else mutableStateOf("")
    }
    val denoising_strength = remember {
        if (currParam is ImgParam)
            mutableStateOf(currParam.denoising_strength)
        else mutableStateOf(0.75f)
    }

    val defaultPrompt = remember { mutableStateOf(currParam.defaultPrompt) }
    val defaultNegPrompt = remember { mutableStateOf(currParam.defaultNegPrompt) }
    val currPromptStyle = remember { mutableStateOf("None") }

    val insets = WindowInsets.ime.add(WindowInsets(bottom = 50.dp))

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .windowInsetsPadding(insets)
                .fillMaxSize()
        ) {
            item {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                ) {
                    ParamRowChangeName(
                        title = stringResource(id = R.string.r_param_name),
                        name = name
                    )
                    if ((!isImg2Img && uiSetsViewModel.tDisplayPriSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iDisplayPriSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        StepRowInt(
                            name = stringResource(id = R.string.r_sort_order),
                            int = order,
                            step = 1,
                            max = Int.MAX_VALUE
                        )
                    }
                    if ((!isImg2Img && uiSetsViewModel.tSDModelSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iSDModelSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        ParamRowChoice(
                            name = stringResource(id = R.string.r_base_model),
                            currChoice = baseModel,
                            content = models,
                            onRefresh = suspend {
                                normalViewModel.refreshBases()
                            }
                        )
                    }
                    if ((!isImg2Img && uiSetsViewModel.tRefineModelSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iRefineModelSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        ParamRowChoice(
                            name = stringResource(id = R.string.r_refiner_model),
                            currChoice = refinerModel,
                            content = models,
                            onRefresh = suspend {
                                normalViewModel.refreshBases()
                            }
                        )
                    }
                    if ((!isImg2Img && uiSetsViewModel.tRefineAtSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iRefineAtSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        StepRowFloat(
                            name = stringResource(id = R.string.r_refiner_at),
                            float = refinerAt,
                            step = 0.1f,
                            max = 1f,
                        )
                    }
                    if ((!isImg2Img && uiSetsViewModel.tImgWidthSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iImgWidthSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        SwipeInt(
                            boldTitle = true,
                            name = stringResource(id = R.string.r_img_width),
                            int = width,
                            max = 2048
                        )
                    }
                    if ((!isImg2Img && uiSetsViewModel.tImgHeightSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iImgHeightSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        SwipeInt(
                            boldTitle = true,
                            name = stringResource(id = R.string.r_img_height),
                            int = height,
                            max = 2048
                        )
                    }
                    if ((!isImg2Img && uiSetsViewModel.tStepsSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iStepsSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        StepRowInt(
                            name = stringResource(id = R.string.r_gen_steps),
                            int = steps,
                            step = 1,
                            max = 150
                        )
                    }
                    if ((!isImg2Img && uiSetsViewModel.tCFGSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iCFGSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        StepRowFloat(
                            name = stringResource(id = R.string.r_cfg_scales),
                            float = cfgScale,
                            step = 0.1f,
                            max = 15f,
                        )
                    }
                    if ((!isImg2Img && uiSetsViewModel.tSamplerSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iSamplerSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        ParamRowChooseSampler(
                            name = stringResource(id = R.string.r_choose_sampler),
                            currChoice = sampler_index,
                            content = baseState.samplers
                        )
                    }
                    if ((!isImg2Img && uiSetsViewModel.tBatchSizeSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iBatchSizeSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        StepRowInt(
                            name = stringResource(id = R.string.r_batch_size),
                            int = batch_size,
                            max = 20,
                            min = 1
                        )
                    }
                    if ((!isImg2Img && uiSetsViewModel.tSDPromptSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iSDPromptSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        // SD 服务器中保存的 Prompt 样式
                        ParamRowChoice(
                            name = stringResource(id = R.string.r_sd_prompt),
                            currChoice = currPromptStyle,
                            content = sdPrompt,
                            onChangeItem = { sdp ->
                                currPromptStyle.value = sdp.name
                                defaultPrompt.value = sdp.prompt
                                defaultNegPrompt.value = sdp.negative_prompt
                            },
                        ) {
                            Text(
                                text = it.name,
                                modifier = Modifier
                                    .widthIn(min = 160.dp),
                                maxLines = 1
                            )
                        }
                    }
                    if ((!isImg2Img && uiSetsViewModel.tPromptSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iPromptSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        ParamRowPrompt(
                            name = stringResource(id = R.string.r_prompt),
                            loras = loras,
                            local = prompts,
                            showAddable = (!isImg2Img && uiSetsViewModel.tPromptAdSwitch) || (isImg2Img && uiSetsViewModel.iPromptAdSwitch),
                            promptSets = promptSets,
                            prompt = defaultPrompt,
                            onRefresh = suspend {
                                normalViewModel.refreshLoras()
                            }
                        )
                    }
                    if ((!isImg2Img && uiSetsViewModel.tNPromptSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iNPromptSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        ParamRowPrompt(
                            name = stringResource(id = R.string.r_neg_prompt),
                            loras = loras,
                            local = prompts,
                            showAddable = (!isImg2Img && uiSetsViewModel.tNPromptAdSwitch) || (isImg2Img && uiSetsViewModel.iNPromptAdSwitch),
                            promptSets = promptSets,
                            prompt = defaultNegPrompt,
                            onRefresh = suspend {
                                normalViewModel.refreshLoras()
                            }
                        )
                    }
                    if (isImg2Img) {
                        if (uiSetsViewModel.iDnoiseSwitch) {
                            Divider(
                                thickness = 2.dp,
                                color = cardColor
                            )
                            StepRowFloat(
                                name = stringResource(id = R.string.r_denoising_strength),
                                float = denoising_strength,
                                max = 1f,
                                step = 0.01f,
                                ff = "%.2f",
                            )
                        }
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        ParamRowImgChoose(base64Str = image)
                    }
                    if ((!isImg2Img && uiSetsViewModel.tScriptSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iScriptSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        // 脚本
                        ParamRowChoice(
                            name = stringResource(id = R.string.r_scripts),
                            currChoice = script_name,
                            content = if (currParam is TxtParam) scriptsTxt else scriptsImg
                        )
                        // 脚本参数
                        if (script_name.value == "X/Y/Z plot") {
                            XYZPlotScript(script_args)
                        } else if (script_name.value == "Ultimate SD upscale") {
                            USDUpscaleScript(script_args)
                        }
                    }
                    if ((!isImg2Img && uiSetsViewModel.tCNSwitch) || uiSetsViewModel.tempParamShow || (isImg2Img && uiSetsViewModel.iCNSwitch)) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        ParamRowControlNet(
                            name = stringResource(id = R.string.r_control_net),
                            cnModels = cnModels,
                            controlNets = controlNets
                        )
                    }
                    Spacer(modifier = Modifier.size(64.dp))
                }
            }
        }

        EditBottomBar(
            modifier = Modifier.align(Alignment.BottomStart),
            onDismiss = {
                paramEvent(ParamEvent.EditParam(currParam, false))

                navUp(navController)
            },
        ) {
            val nameStr = name.value
            val invalidStr = Constant.addableSecond + Constant.addableFirst
            if (nameStr == invalidStr) {
                Toast.makeText(context, context.getString(R.string.t_invalid_name), Toast.LENGTH_SHORT).show()
            } else {
                TextUtil.topsea("ParamEditScreen currParam: ${currParam}")

                if (baseModel.value != currParam.baseModel) {
                    uiSetsViewModel.onEvent(UIEvent.ModelChanging(true))
                }

                val param = when (currParam) {
                    is TxtParam -> {
                        TxtParam(
                            id = currParam.id,
                            name = name.value,
                            priority_order = order.value,
                            activate = currParam.activate,
                            refinerModel = refinerModel.value,
                            refinerAt = refinerAt.value,
                            width = width.value,
                            height = height.value,
                            baseModel = baseModel.value,
                            steps = steps.value,
                            cfgScale = cfgScale.value,
                            sampler_index = sampler_index.value,
                            batch_size = batch_size.value,
                            defaultPrompt = defaultPrompt.value,
                            defaultNegPrompt = defaultNegPrompt.value,
                            script_name = script_name.value,
                            script_args = script_args.value,
                            control_net = controlNets,
                        )
                    }

                    is ImgParam -> {
                        ImgParam(
                            id = currParam.id,
                            name = name.value,
                            priority_order = order.value,
                            activate = currParam.activate,
                            refinerModel = refinerModel.value,
                            refinerAt = refinerAt.value,
                            image = image,
                            denoising_strength = denoising_strength.value,
                            width = width.value,
                            height = height.value,
                            baseModel = baseModel.value,
                            steps = steps.value,
                            cfgScale = cfgScale.value,
                            sampler_index = sampler_index.value,
                            batch_size = batch_size.value,
                            defaultPrompt = defaultPrompt.value,
                            defaultNegPrompt = defaultNegPrompt.value,
                            script_name = script_name.value,
                            script_args = script_args.value,
                            control_net = controlNets,
                        )
                    }

                    else -> null
                }
                if (param!!.activate) {
                    normalViewModel.cnEvent(ControlNetEvent.ActivateByRequest(param.control_net))
                }

                TextUtil.topsea("EditParam: ${param.toRequest()}")

                paramEvent(ParamEvent.UpdateParam(param))

                navUp(navController)
            }
        }
    }
}
