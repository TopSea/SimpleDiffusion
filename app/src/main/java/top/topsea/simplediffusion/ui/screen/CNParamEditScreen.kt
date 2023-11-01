package top.topsea.simplediffusion.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.topsea.simplediffusion.BaseScreen
import top.topsea.simplediffusion.CameraSettingScreen
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.api.dto.listTypes
import top.topsea.simplediffusion.data.param.CNParam
import top.topsea.simplediffusion.data.param.getCNControlMode
import top.topsea.simplediffusion.data.param.getCNResizeMode
import top.topsea.simplediffusion.data.state.ControlNetState
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.event.ControlNetEvent
import top.topsea.simplediffusion.ui.component.ParamRowChangeName
import top.topsea.simplediffusion.ui.component.ParamRowCheck
import top.topsea.simplediffusion.ui.component.ParamRowChoice
import top.topsea.simplediffusion.ui.component.ParamRowFloat
import top.topsea.simplediffusion.ui.component.ParamRowImgChoose
import top.topsea.simplediffusion.ui.component.SettingRowInt
import top.topsea.simplediffusion.ui.component.ParamRowRadio
import top.topsea.simplediffusion.ui.component.TypeChoose
import top.topsea.simplediffusion.util.TextUtil


@Composable
fun CNParamEditScreen(
    navController: NavController,
    cardColor: Color,
    cnState: ControlNetState,
    uiEvent: (UIEvent) -> Unit,
    cnEvent: (ControlNetEvent) -> Unit,
) {
    val currCNParam = cnState.editParam!!

    LaunchedEffect(key1 = null) {
        cnEvent(ControlNetEvent.EditCNParam(currCNParam, true))
    }

    if (cnState.editing) {
        CNParamEditContent(
            navController = navController,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background),
            cardColor = cardColor,
            cnState = cnState,
            cnModel = currCNParam,
            uiEvent = uiEvent,
            cnEvent = cnEvent,
        )
    }
}

@Composable
fun CNParamEditContent(
    navController: NavController,
    modifier: Modifier,
    cardColor: Color,
    cnModel: CNParam,
    cnState: ControlNetState,
    uiEvent: (UIEvent) -> Unit,
    cnEvent: (ControlNetEvent) -> Unit,
) {
    val isCamera = navController.previousBackStackEntry?.destination?.route == CameraSettingScreen.route
    val context = LocalContext.current
    val name = remember { mutableStateOf(cnModel.cn_name) }

    val module = remember { mutableStateOf(cnModel.module) }
    val model = remember { mutableStateOf(cnModel.model) }
    val weight = remember { mutableStateOf(cnModel.weight) }
    val guidance_start = remember { mutableStateOf(cnModel.guidance_start) }
    val guidance_end = remember { mutableStateOf(cnModel.guidance_end) }
    val processor_res = remember { mutableStateOf(cnModel.processor_res) }
    val control_mode = remember { mutableStateOf(cnModel.control_mode) }
    val resize_mode = remember { mutableStateOf(cnModel.resize_mode) }
    val input_image = remember { cnModel.input_image }
    val lowvram = remember { mutableStateOf(cnModel.lowvram) }
    val pixel_perfect = remember { mutableStateOf(cnModel.pixel_perfect) }
    val use_imgImg = remember { mutableStateOf(cnModel.use_imgImg) }
    val types = cnState.controlTypes

    val insets = WindowInsets.ime.add(WindowInsets(bottom = 50.dp))

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier
            .windowInsetsPadding(insets)
            .fillMaxSize()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    ParamRowChangeName(
                        title = stringResource(id = R.string.r_param_name),
                        name = name
                    )
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    TypeChoose(chosen = cnState.currType, choices = listTypes) {
                        cnEvent(ControlNetEvent.ChooseType(it, module = module, model = model))
                    }
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    // 预处理器
                    ParamRowChoice(
                        name = stringResource(id = R.string.rs_cn_module),
                        currChoice = module,
                        content = cnState.currControlType.module_list,
                    )
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    // 模型选择
                    ParamRowChoice(
                        name = stringResource(id = R.string.rs_cn_model),
                        currChoice = model,
                        content = cnState.currControlType.model_list,
                    )
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowCheck(name = stringResource(id = R.string.rs_cn_low_vram), check = lowvram)
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowFloat(
                        name = stringResource(id = R.string.rs_cn_weight),
                        float = weight,
                        max = 2f,
                        step = 0.05f,
                        ff = "%.2f"
                    )
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowFloat(
                        name = stringResource(id = R.string.rs_cn_guid_start),
                        float = guidance_start,
                        max = 1f,
                        step = 0.05f,
                        ff = "%.2f"
                    )
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowFloat(
                        name = stringResource(id = R.string.rs_cn_guid_end),
                        float = guidance_end,
                        max = 1f,
                        step = 0.05f,
                        ff = "%.2f"
                    )
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    if (!pixel_perfect.value) {
                        SettingRowInt(
                            name = stringResource(id = R.string.rs_cn_pre_res),
                            int = processor_res,
                            max = 2048
                        )
                        Divider(
                            thickness = 2.dp,
                            color = cardColor
                        )
                    }
                    ParamRowCheck(name = stringResource(id = R.string.rs_cn_pixel_perfect), check = pixel_perfect)
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowRadio(name = stringResource(id = R.string.rs_cn_control_t), curerChoice = control_mode, radioList = getCNControlMode(context))
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowRadio(name = stringResource(id = R.string.rs_cn_resize_t), curerChoice = resize_mode, radioList = getCNResizeMode(context))
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    ParamRowCheck(name = stringResource(id = R.string.rs_cn_use_img), check = use_imgImg)
                    Divider(
                        thickness = 2.dp,
                        color = cardColor
                    )
                    if (!use_imgImg.value)
                        ParamRowImgChoose(base64Str = input_image, picking = cnModel.id)
                    Spacer(modifier = Modifier.size(64.dp))
                }
            }
        }
        EditBottomBar(
            modifier = Modifier.align(Alignment.BottomStart),
            onDismiss = {
                cnEvent(ControlNetEvent.EditCNParam(cnModel, false))
                if (isCamera)
                    uiEvent(UIEvent.Navigate(CameraSettingScreen){
                        navController.popBackStack()
                    })
                else
                    uiEvent(UIEvent.Navigate(BaseScreen){
                        navController.popBackStack()
                    })
            },
        ) {
            val param = CNParam(
                id = cnModel.id,
                cn_name = name.value,
                module = module.value,
                model = model.value,
                lowvram = lowvram.value,
                guidance_start = guidance_start.value,
                guidance_end = guidance_end.value,
                processor_res = processor_res.value,
                weight = weight.value,
                control_mode = control_mode.value,
                resize_mode = resize_mode.value,
                input_image = input_image,
                pixel_perfect = pixel_perfect.value,
                use_imgImg = use_imgImg.value,
            )
            TextUtil.topsea("ControlNets: $param", Log.ERROR)

            cnEvent(ControlNetEvent.UpdateCNParam(param){
                if (isCamera)
                    uiEvent(UIEvent.Navigate(CameraSettingScreen){
                        navController.popBackStack()
                    })
                else
                    uiEvent(UIEvent.Navigate(BaseScreen){
                        navController.popBackStack()
                    })
            })
        }
    }
}
