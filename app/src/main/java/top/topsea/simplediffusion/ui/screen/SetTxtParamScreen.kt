package top.topsea.simplediffusion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.ui.component.ParamRowChoice
import top.topsea.simplediffusion.ui.component.ParamRowChooseSampler
import top.topsea.simplediffusion.ui.component.ParamRowNegPrompt
import top.topsea.simplediffusion.ui.component.ParamRowPrompt
import top.topsea.simplediffusion.ui.component.ParamTitle
import top.topsea.simplediffusion.ui.component.SettingSwitch
import top.topsea.simplediffusion.ui.component.StepRowFloat
import top.topsea.simplediffusion.ui.component.StepRowInt
import top.topsea.simplediffusion.ui.component.SwipeInt

@Composable
fun SetTxtParamScreen(
    navController: NavController,
    uiViewModel: UIViewModel,
    normalViewModel: NormalViewModel,
) {
    val dividerColor = Color.LightGray

    val cnState by normalViewModel.cnState.collectAsState()
    val baseState by normalViewModel.baseState.collectAsState()
    val loraState by normalViewModel.loraState.collectAsState()
    val sdPrompt by normalViewModel.sdPrompt.collectAsState()
    val models = baseState.models
    val cnModels = cnState.cnParams
    val loras = loraState.loras
    val prompts = loraState.local
    val promptSets = loraState.promptSets

    Column(
        modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        SetParam(
            title = stringResource(id = R.string.r_param_name),
            enabled = false,
        ){  }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_sort_order),
            switch = uiViewModel.tDisplayPriSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTDPS(it)) },
        ){
            StepRowInt(
                name = stringResource(id = R.string.sp_default_value),
                int = uiViewModel.tDisplayPri,
                max = Int.MAX_VALUE
            ) {
                uiViewModel.onEvent(UIEvent.UpdateTDP(it))
            }
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_base_model),
            switch = uiViewModel.tSDModelSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTSDMS(it)) },
        ){
            ParamRowChoice(
                name = stringResource(id = R.string.sp_default_value),
                currChoice = uiViewModel.tSDModel,
                content = models,
                onValueChange = { uiViewModel.onEvent(UIEvent.UpdateTSDM(it)) },
                onRefresh = suspend {
                    normalViewModel.refreshBases()
                }
            )
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_refiner_model),
            switch = uiViewModel.tRefineModelSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTRMS(it)) },
        ){
            ParamRowChoice(
                name = stringResource(id = R.string.sp_default_value),
                currChoice = uiViewModel.tRefineModel,
                content = models,
                onValueChange = { uiViewModel.onEvent(UIEvent.UpdateTRM(it)) },
                onRefresh = suspend {
                    normalViewModel.refreshBases()
                }
            )
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_refiner_at),
            switch = uiViewModel.tRefineAtSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTRAS(it)) },
        ){
            StepRowFloat(
                name = stringResource(id = R.string.sp_default_value),
                float = uiViewModel.tRefineAt,
                step = 0.1f,
                max = 1f,
            ) {
                uiViewModel.onEvent(UIEvent.UpdateTRA(it))
            }
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_img_width),
            switch = uiViewModel.tImgWidthSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTIWS(it)) },
        ){
            SwipeInt(
                boldTitle = true,
                name = stringResource(id = R.string.sp_default_value),
                int = uiViewModel.tImgWidth,
                max = 2048,
            ){
                uiViewModel.onEvent(UIEvent.UpdateTIW(it))
            }
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_img_height),
            switch = uiViewModel.tImgHeightSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTIHS(it)) },
        ){
            SwipeInt(
                boldTitle = true,
                name = stringResource(id = R.string.sp_default_value),
                int = uiViewModel.tImgHeight,
                max = 2048,
            ){
                uiViewModel.onEvent(UIEvent.UpdateTIW(it))
            }
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_gen_steps),
            switch = uiViewModel.tStepsSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTSS(it)) },
        ){
            StepRowInt(
                name = stringResource(id = R.string.sp_default_value),
                int = uiViewModel.tSteps,
                max = 150
            ) {
                uiViewModel.onEvent(UIEvent.UpdateTS(it))
            }
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_cfg_scales),
            switch = uiViewModel.tCFGSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTCS(it)) },
        ){
            StepRowFloat(
                name = stringResource(id = R.string.sp_default_value),
                float = uiViewModel.tCFG,
                step = 0.1f,
                max = 15f,
            ) {
                uiViewModel.onEvent(UIEvent.UpdateTC(it))
            }
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_choose_sampler),
            switch = uiViewModel.tSamplerSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTSamplerS(it)) },
        ){
            ParamRowChooseSampler(
                name = stringResource(id = R.string.sp_default_value),
                currChoice = uiViewModel.tSampler,
                content = baseState.samplers,
                onValueChange = { uiViewModel.onEvent(UIEvent.UpdateTRM(it)) },
            )
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_batch_size),
            switch = uiViewModel.tBatchSizeSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTBSS(it)) },
        ){
            StepRowInt(
                name = stringResource(id = R.string.sp_default_value),
                int = uiViewModel.tBatchSize,
                max = 20,
                min = 1,
            ) {
                uiViewModel.onEvent(UIEvent.UpdateTBS(it))
            }
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_sd_prompt),
            switch = uiViewModel.tSDPromptSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTSDPS(it)) },
        ){  }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_prompt),
            switch = uiViewModel.tPromptSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTPS(it)) },
        ){
            ParamRowPrompt(
                name = stringResource(id = R.string.sp_default_value),
                loras = loras,
                local = prompts,
                promptSets = promptSets,
                prompt = uiViewModel.tPrompt,
                onRefresh = suspend {
                    normalViewModel.refreshLoras()
                }
            ){
                uiViewModel.onEvent(UIEvent.UpdateTP(it))
            }
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_neg_prompt),
            switch = uiViewModel.tNPromptSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTNPS(it)) },
        ){
            ParamRowNegPrompt(
                name = stringResource(id = R.string.sp_default_value),
                prompt = uiViewModel.tNPrompt,
                onRefresh = suspend {
                    normalViewModel.refreshLoras()
                }
            ){
                uiViewModel.onEvent(UIEvent.UpdateTNP(it))
            }
        }
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_scripts),
            switch = uiViewModel.tScriptSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTScriptS(it)) },
        )
        Divider(thickness = 2.dp, color = dividerColor)
        SetParam(
            title = stringResource(id = R.string.r_control_net),
            switch = uiViewModel.tCNSwitch,
            onSwitch = { uiViewModel.onEvent(UIEvent.UpdateTCNS(it)) },
        )
    }
}

// 没有设置默认值，只有是否显示选项
@Composable
fun SetParam(
    title: String,
    enabled: Boolean = true,
    switch: Boolean = true,
    onSwitch: (enabled: Boolean) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.s_normal_height))
                // 不要点击效果
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    expanded = !expanded
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ParamTitle(title = title)

            SettingSwitch(
                modifier = Modifier.size(DpSize(54.dp, 42.dp)),
                isOn = switch,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ) {
                if (enabled)
                    onSwitch(it)
            }
        }
    }
}

@Composable
fun SetParam(
    title: String,
    enabled: Boolean = true,
    switch: Boolean = true,
    onSwitch: (enabled: Boolean) -> Unit,
    defaultValue: @Composable() () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.s_normal_height))
                // 不要点击效果
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    expanded = !expanded
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ParamTitle(title = title)

            Row(verticalAlignment = Alignment.CenterVertically) {
                SettingSwitch(
                    modifier = Modifier.size(DpSize(54.dp, 42.dp)),
                    isOn = switch,
                    tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                ) {
                    if (enabled)
                        onSwitch(it)
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Open menu.",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(32.dp),
                )
            }

        }
        if (expanded)
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                defaultValue()
            }
    }
}