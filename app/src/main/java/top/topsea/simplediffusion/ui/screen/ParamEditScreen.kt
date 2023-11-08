package top.topsea.simplediffusion.ui.screen

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.topsea.simplediffusion.BaseScreen
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.param.BasicParam
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.param.TxtParam
import top.topsea.simplediffusion.data.param.scripts
import top.topsea.simplediffusion.data.state.ParamLocalState
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.event.ControlNetEvent
import top.topsea.simplediffusion.event.ParamEvent
import top.topsea.simplediffusion.ui.component.ParamRowChangeName
import top.topsea.simplediffusion.ui.component.ParamRowChoice
import top.topsea.simplediffusion.ui.component.ParamRowChooseSampler
import top.topsea.simplediffusion.ui.component.ParamRowControlNet
import top.topsea.simplediffusion.ui.component.ParamRowFloat
import top.topsea.simplediffusion.ui.component.ParamRowImgChoose
import top.topsea.simplediffusion.ui.component.ParamRowImgSize
import top.topsea.simplediffusion.ui.component.SettingRowInt
import top.topsea.simplediffusion.ui.component.ParamRowNegPrompt
import top.topsea.simplediffusion.ui.component.ParamRowPrompt
import top.topsea.simplediffusion.ui.scripts.XYZPlotScript
import top.topsea.simplediffusion.util.TextUtil

@Composable
fun ParamEditScreen(
    navController: NavController,
    cardColor: Color,
    paramState: ParamLocalState,
    paramEvent: (ParamEvent) -> Unit,
    uiEvent: (UIEvent) -> Unit,
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
            uiEvent = uiEvent,
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
    paramEvent: (ParamEvent) -> Unit,
    uiEvent: (UIEvent) -> Unit,
    normalViewModel: NormalViewModel,
) {
    val isCamera = navController.previousBackStackEntry?.destination?.route == top.topsea.simplediffusion.CameraSettingScreen.route
    val name = remember { mutableStateOf(currParam.name) }
    val order = remember { mutableStateOf(currParam.priority_order) }

    val cnState by normalViewModel.cnState.collectAsState()
    val baseState by normalViewModel.baseState.collectAsState()
    val loraState by normalViewModel.loraState.collectAsState()
    val sdPrompt by normalViewModel.sdPrompt.collectAsState()
    val models = baseState.models
    val cnModels = cnState.cnParams
    val loras = loraState.loras
    val prompts = loraState.prompts

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
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    SettingRowInt(
                        name = stringResource(id = R.string.r_sort_order),
                        int = order,
                        step = 1,
                        max = Int.MAX_VALUE
                    )
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
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowFloat(
                        name = stringResource(id = R.string.r_refiner_at),
                        float = refinerAt,
                        step = 0.1f,
                        max = 1f,
                    )
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowImgSize(
                        width = width,
                        height = height
                    )
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    SettingRowInt(
                        name = stringResource(id = R.string.r_gen_steps),
                        int = steps,
                        step = 1,
                        max = 150
                    )
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowFloat(
                        name = stringResource(id = R.string.r_cfg_scales),
                        float = cfgScale,
                        step = 0.1f,
                        max = 15f,
                    )
                    // 采样器
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowChooseSampler(
                        name = stringResource(id = R.string.r_choose_sampler),
                        currChoice = sampler_index,
                        content = baseState.samplers
                    )
                    // 每批生成图片数
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    SettingRowInt(
                        name = stringResource(id = R.string.r_batch_size),
                        int = batch_size,
                        max = 5
                    )
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
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowPrompt(
                        name = stringResource(id = R.string.r_prompt),
                        models = loras,
                        prompts = prompts,
                        prompt = defaultPrompt,
                        onRefresh = suspend {
                            normalViewModel.refreshLoras()
                        }
                    )
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowNegPrompt(
                        name = stringResource(id = R.string.r_neg_prompt),
                        prompt = defaultNegPrompt,
                        onRefresh = suspend { }
                    )
                    if (currParam is ImgParam) {
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        ParamRowFloat(
                            name = stringResource(id = R.string.r_denoising_strength),
                            float = denoising_strength,
                            max = 1f,
                            step = 0.01f,
                            ff = "%.2f",
                        )
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                        ParamRowImgChoose(base64Str = image)
                    }
                    // 脚本
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowChoice(
                        name = stringResource(id = R.string.r_scripts),
                        currChoice = script_name,
                        content = scripts
                    )
                    // 脚本参数
                    if (script_name.value == "X/Y/Z plot") {
                        XYZPlotScript(script_args)
                    }
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowControlNet(
                        name = stringResource(id = R.string.r_control_net),
                        cnModels = cnModels,
                        controlNets = controlNets
                    )
                    Spacer(modifier = Modifier.size(64.dp))
                }
            }
        }

        EditBottomBar(
            modifier = Modifier.align(Alignment.BottomStart),
            onDismiss = {
                paramEvent(ParamEvent.EditParam(currParam, false))

                if (isCamera)
                    uiEvent(UIEvent.Navigate(top.topsea.simplediffusion.CameraSettingScreen){
                        navController.popBackStack()
                    })
                else
                    uiEvent(UIEvent.Navigate(BaseScreen){
                        navController.popBackStack()
                    })
            },
        ) {
            TextUtil.topsea("ParamEditScreen currParam: ${currParam}")

            if (baseModel.value != currParam.baseModel) {
                uiEvent(UIEvent.ModelChanging(true))
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


            if (isCamera)
                uiEvent(UIEvent.Navigate(top.topsea.simplediffusion.CameraSettingScreen){
                    navController.popBackStack()
                })
            else
                uiEvent(UIEvent.Navigate(BaseScreen){
                    navController.popBackStack()
                })
        }
    }
}
