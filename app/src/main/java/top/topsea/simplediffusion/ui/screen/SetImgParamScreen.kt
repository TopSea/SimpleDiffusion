package top.topsea.simplediffusion.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UISetsViewModel
import top.topsea.simplediffusion.event.ParamEvent
import top.topsea.simplediffusion.navUp
import top.topsea.simplediffusion.ui.component.ParamRowChoice
import top.topsea.simplediffusion.ui.component.ParamRowChooseSampler
import top.topsea.simplediffusion.ui.component.ParamRowPrompt
import top.topsea.simplediffusion.ui.component.StepRowFloat
import top.topsea.simplediffusion.ui.component.StepRowInt
import top.topsea.simplediffusion.ui.component.SwipeInt
import top.topsea.simplediffusion.util.TextUtil

@Composable
fun SetImgParamScreen(
    navController: NavController,
    uiSetsViewModel: UISetsViewModel,
    normalViewModel: NormalViewModel,
    defaultImgParam: ImgParam,
    paramEvent: (ParamEvent) -> Unit
) {
    val dividerColor = Color.LightGray
    TextUtil.topsea("iparam: $defaultImgParam")

    val order = remember { mutableStateOf(defaultImgParam.priority_order) }

    val baseState by normalViewModel.baseState.collectAsState()
    val loraState by normalViewModel.loraState.collectAsState()
    val models = baseState.models
    val loras = loraState.loras
    val prompts = loraState.local
    val promptSets = loraState.promptSets

    val width = remember { mutableStateOf(defaultImgParam.width) }
    val height = remember { mutableStateOf(defaultImgParam.height) }
    val baseModel = remember { mutableStateOf(defaultImgParam.baseModel) }
    val refinerModel = remember { mutableStateOf(defaultImgParam.refinerModel) }
    val refinerAt = remember { mutableStateOf(defaultImgParam.refinerAt) }
    val steps = remember { mutableStateOf(defaultImgParam.steps) }
    val cfgScale = remember { mutableStateOf(defaultImgParam.cfgScale) }

    val sampler_index = remember { mutableStateOf(defaultImgParam.sampler_index) }
    val batch_size = remember { mutableStateOf(defaultImgParam.batch_size) }

    val image = remember { defaultImgParam.image }
    val denoising_strength = remember { mutableStateOf(defaultImgParam.denoising_strength) }

    val defaultPrompt = remember { mutableStateOf(defaultImgParam.defaultPrompt) }
    val defaultNegPrompt = remember { mutableStateOf(defaultImgParam.defaultNegPrompt) }

    val insets = WindowInsets.ime.add(WindowInsets(bottom = 50.dp))

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(insets)
                .verticalScroll(rememberScrollState())
        ) {
            SetParam(
                title = stringResource(id = R.string.r_param_name),
                enabled = false,
            ){  }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_sort_order),
                switch = uiSetsViewModel.iDisplayPriSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateIDPS(it)) },
            ){
                StepRowInt(
                    name = stringResource(id = R.string.sp_default_value),
                    int = order,
                    step = 1,
                    max = Int.MAX_VALUE
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_base_model),
                switch = uiSetsViewModel.iSDModelSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateISDMS(it)) },
            ){
                ParamRowChoice(
                    name = stringResource(id = R.string.sp_default_value),
                    currChoice = baseModel,
                    content = models,
                    onRefresh = suspend {
                        normalViewModel.refreshBases()
                    }
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_refiner_model),
                switch = uiSetsViewModel.iRefineModelSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateIRMS(it)) },
            ){
                ParamRowChoice(
                    name = stringResource(id = R.string.sp_default_value),
                    currChoice = refinerModel,
                    content = models,
                    onRefresh = suspend {
                        normalViewModel.refreshBases()
                    }
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_refiner_at),
                switch = uiSetsViewModel.iRefineAtSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateIRAS(it)) },
            ){
                StepRowFloat(
                    name = stringResource(id = R.string.sp_default_value),
                    float = refinerAt,
                    step = 0.1f,
                    max = 1f,
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_img_width),
                switch = uiSetsViewModel.iImgWidthSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateIIWS(it)) },
            ){
                SwipeInt(
                    boldTitle = true,
                    name = stringResource(id = R.string.sp_default_value),
                    int = width,
                    max = 2048
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_img_height),
                switch = uiSetsViewModel.iImgHeightSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateIIHS(it)) },
            ){
                SwipeInt(
                    boldTitle = true,
                    name = stringResource(id = R.string.sp_default_value),
                    int = height,
                    max = 2048
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_gen_steps),
                switch = uiSetsViewModel.iStepsSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateISS(it)) },
            ){
                StepRowInt(
                    name = stringResource(id = R.string.sp_default_value),
                    int = steps,
                    step = 1,
                    max = 150
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_cfg_scales),
                switch = uiSetsViewModel.iCFGSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateICS(it)) },
            ){
                StepRowFloat(
                    name = stringResource(id = R.string.sp_default_value),
                    float = cfgScale,
                    step = 0.1f,
                    max = 15f,
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_choose_sampler),
                switch = uiSetsViewModel.iSamplerSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateISamplerS(it)) },
            ){
                ParamRowChooseSampler(
                    name = stringResource(id = R.string.sp_default_value),
                    currChoice = sampler_index,
                    content = baseState.samplers
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_batch_size),
                switch = uiSetsViewModel.iBatchSizeSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateIBSS(it)) },
            ){
                StepRowInt(
                    name = stringResource(id = R.string.sp_default_value),
                    int = batch_size,
                    max = 20,
                    min = 1
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_sd_prompt),
                switch = uiSetsViewModel.iSDPromptSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateISDPS(it)) },
            )
            Divider(thickness = 2.dp, color = dividerColor)
            SetParamForPrompt(
                title = stringResource(id = R.string.r_prompt),
                titleA = stringResource(id = R.string.sp_show_addable),
                switch = uiSetsViewModel.iPromptSwitch,
                switchA = uiSetsViewModel.iPromptAdSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateIPS(it)) },
                onSwitchA = { uiSetsViewModel.onEvent(UIEvent.UpdateIPAS(it)) },
            ){
                ParamRowPrompt(
                    name = stringResource(id = R.string.sp_default_value),
                    loras = loras,
                    local = prompts,
                    showAddable = true,
                    promptSets = promptSets,
                    prompt = defaultPrompt,
                    onRefresh = suspend {
                        normalViewModel.refreshLoras()
                    }
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParamForPrompt(
                title = stringResource(id = R.string.r_neg_prompt),
                titleA = stringResource(id = R.string.sp_show_addable),
                switch = uiSetsViewModel.iNPromptSwitch,
                switchA = uiSetsViewModel.iNPromptAdSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateINPS(it)) },
                onSwitchA = { uiSetsViewModel.onEvent(UIEvent.UpdateINPAS(it)) },
            ){
                ParamRowPrompt(
                    name = stringResource(id = R.string.sp_default_value),
                    loras = loras,
                    local = prompts,
                    showAddable = true,
                    promptSets = promptSets,
                    prompt = defaultNegPrompt,
                    onRefresh = suspend {
                        normalViewModel.refreshLoras()
                    }
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_denoising_strength),
                switch = uiSetsViewModel.iDnoiseSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateIDNS(it)) },
            ){
                StepRowFloat(
                    name = stringResource(id = R.string.sp_default_value),
                    float = denoising_strength,
                    max = 1f,
                    step = 0.01f,
                    ff = "%.2f",
                )
            }
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_scripts),
                switch = uiSetsViewModel.iScriptSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateIScriptS(it)) },
            )
            Divider(thickness = 2.dp, color = dividerColor)
            SetParam(
                title = stringResource(id = R.string.r_control_net),
                switch = uiSetsViewModel.iCNSwitch,
                onSwitch = { uiSetsViewModel.onEvent(UIEvent.UpdateICNS(it)) },
            )
        }

        EditBottomBar(
            modifier = Modifier.align(Alignment.BottomStart),
            onDismiss = {
                navUp(navController)
            },
        ) {
            val param = ImgParam(
                id = defaultImgParam.id,
                name = defaultImgParam.name,
                priority_order = order.value,
                activate = false,
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
            )

            TextUtil.topsea("DefaultParam: $param")

            paramEvent(ParamEvent.UpdateParam(param))

            navUp(navController)
        }
    }
}
