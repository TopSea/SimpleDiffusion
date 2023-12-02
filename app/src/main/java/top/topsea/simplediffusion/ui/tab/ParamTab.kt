package top.topsea.simplediffusion.ui.tab

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import top.topsea.simplediffusion.EditCNScreen
import top.topsea.simplediffusion.EditScreen
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.api.dto.SimpleSdConfig
import top.topsea.simplediffusion.data.param.BasicParam
import top.topsea.simplediffusion.data.param.CNParam
import top.topsea.simplediffusion.data.state.ParamLocalState
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.event.ControlNetEvent
import top.topsea.simplediffusion.event.ParamEvent
import top.topsea.simplediffusion.ui.component.AddParam
import top.topsea.simplediffusion.ui.component.SearchRequest
import top.topsea.simplediffusion.util.Constant
import top.topsea.simplediffusion.util.TextUtil

@Composable
fun ParamTab(
    navController: NavController,
    modifier: Modifier = Modifier,
    isi2i: Boolean = false,
    params: List<BasicParam>,
    uiViewModel: UIViewModel,
    paramEvent: (ParamEvent) -> Unit,
    cnEvent: (ControlNetEvent) -> Unit,
    addParam: () -> Unit,
) {
    Column(modifier = modifier) {
        SearchRequest {
            paramEvent(ParamEvent.SearchParam(it, isi2i))
        }
        LazyColumn(
            contentPadding = PaddingValues(top = 6.dp),
        ) {
            item { Spacer(modifier = Modifier.size(6.dp)) }
            items(
                items = params,
                key = { r ->
                    r.id
                }
            ) {
                ParamItem(
                    navController = navController,
                    param = it,
                    uiViewModel = uiViewModel,
                    paramEvent = paramEvent,
                    cnEvent = cnEvent,
                )
            }
            item {
                AddParam {
                    addParam()
                }
            }
        }
    }
}

@Composable
fun CNParamTab(
    navController: NavController,
    modifier: Modifier = Modifier,
    cnModels: List<CNParam>,
    uiViewModel: UIViewModel,
    paramState: ParamLocalState,
    paramEvent: (ParamEvent) -> Unit,
    cnEvent: (ControlNetEvent) -> Unit,
    addParam: () -> Unit,
) {
    Column(modifier = modifier) {
        SearchRequest {
            cnEvent(ControlNetEvent.SearchCNParam(it))
        }
        LazyColumn(
            contentPadding = PaddingValues(top = 6.dp),
        ) {
            item { Spacer(modifier = Modifier.size(6.dp)) }
            items(
                items = cnModels,
                key = { r ->
                    r.id
                }
            ) {
                CNParamItem(
                    navController = navController,
                    cnModel = it,
                    paramState = paramState,
                    paramEvent = paramEvent,
                    uiEvent = uiViewModel::onEvent,
                    cnEvent = cnEvent,
                )
            }
            item {
                AddParam {
                    addParam()
                }
            }
        }
    }
}

@Composable
fun ParamItem(
    navController: NavController,
    param: BasicParam,
    uiViewModel: UIViewModel,
    paramEvent: (ParamEvent) -> Unit,
    cnEvent: (ControlNetEvent) -> Unit,
) {
    val context = LocalContext.current
    // 修改基模时的响应动画
    val cardColor =
        if (param.activate) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.inverseOnSurface
    var modelChanged by remember { mutableStateOf((!uiViewModel.modelChanging) && param.activate) }
    var bmChangingProgress by remember { mutableStateOf(if (!modelChanged) 0f else 1f) }

    LaunchedEffect(key1 = uiViewModel.modelChanging) {
        while (uiViewModel.modelChanging) {
            delay(200L)
            if (bmChangingProgress < 0.75F)
                bmChangingProgress += 0.1f
        }
    }
    LaunchedEffect(key1 = modelChanged) {
        bmChangingProgress = if (modelChanged) {
            delay(100L)
            1f
        } else {
            delay(100L)
            0f
        }
    }

    val bmChangingColors = arrayOf(
        bmChangingProgress to cardColor,
        bmChangingProgress + 0.05F to MaterialTheme.colorScheme.inverseOnSurface,
        1f to MaterialTheme.colorScheme.inverseOnSurface
    )

    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth()
            .height(64.dp)
            .background(
                brush = Brush.horizontalGradient(colorStops = bmChangingColors),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                paramEvent(ParamEvent.ActivateParam(param))
                uiViewModel.onEvent(UIEvent.ModelChanging(true))

                // 更改相应的 ControlNet
                cnEvent(ControlNetEvent.ActivateByRequest(param.control_net))
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = param.name,
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            maxLines = 1
        )

        Row(
            modifier = Modifier
                .padding(end = 16.dp)
        ) {
            IconButton(onClick = {
                paramEvent(ParamEvent.DeleteParam(bp = param))
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "",
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = {
                paramEvent(ParamEvent.AddParam(param))
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    modifier = Modifier.size(34.dp)
                )
            }
            IconButton(onClick = {
//                    if (uiState.serverConnected) {

//                    if (wdp < 450.dp) {
                paramEvent(ParamEvent.EditParam(bp = param, editing = false))
                uiViewModel.onEvent(UIEvent.Navigate(EditScreen) {
                    navController.navigate("param_edit")
                })
//                    } else {
//                        paramEvent(ParamEvent.EditParam(bp = param, editing = true))
//                    }
//                    } else {
//                        Toast.makeText(context, context.getText(R.string.t_sd_not_connected), Toast.LENGTH_SHORT).show()
//                    }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

    // 更新大模型
    LaunchedEffect(key1 = param.baseModel, key2 = param.activate) {
        if (uiViewModel.serverConnected && param.activate && uiViewModel.modelChanging) {
            if (param.baseModel.isNotEmpty()) {
                // 进度条置零
                modelChanged = false
                TextUtil.topsea("Changing Base Model to: ${param.baseModel}.", Log.ERROR)
                // 开始读条
                uiViewModel.onEvent(UIEvent.ModelChanging(true))
                val config = SimpleSdConfig(
                    configName = Constant.sd_model_checkpoint,
                    value = if (param.baseModel.contains("\\")) {
                        param.baseModel.split("\\")[1]
                    } else {
                        param.baseModel
                    }
                )

                // 运行结果
                cnEvent(ControlNetEvent.UpdateConfig(config, onFailure = {
                    Toast.makeText(context, context.getString(R.string.p_request_error), Toast.LENGTH_SHORT).show()
                    uiViewModel.onEvent(UIEvent.ModelChanging(true))
                    uiViewModel.onEvent(UIEvent.ServerConnected(false))
                }){
                    modelChanged = true
                    uiViewModel.onEvent(UIEvent.ModelChanging(false))
                    uiViewModel.onEvent(UIEvent.ServerConnected(true))
                })
            }
        }
    }
}


@Composable
fun CNParamItem(
    navController: NavController,
    cnModel: CNParam,
    paramState: ParamLocalState,
    paramEvent: (ParamEvent) -> Unit,
    uiEvent: (UIEvent) -> Unit,
    cnEvent: (ControlNetEvent) -> Unit,
) {
    val context = LocalContext.current

    val ap = paramState.currParam
    val checkState = remember {
        if (ap == null)
            mutableStateOf(false)
        else
            mutableStateOf(ap.control_net.contains(cnModel.id))
    }

    val cardColor =
        if (checkState.value) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.inverseOnSurface

    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth()
            .height(64.dp)
            .background(
                color = cardColor,
                shape = RoundedCornerShape(8.dp)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = cnModel.cn_name,
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            maxLines = 1
        )

        Row(
            modifier = Modifier
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                paramEvent(ParamEvent.CloseControlNet(cnModel.id))
                cnEvent(ControlNetEvent.DeleteCNParam(cnModel))
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "",
                    modifier = Modifier.size(28.dp)
                )
            }
            Checkbox(
                checked = checkState.value,
                onCheckedChange = {
                    if (ap == null) {

                        Toast
                            .makeText(
                                context,
                                context.getString(R.string.t_no_param_selected),
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    } else {
                        checkState.value = it
                        if (it)     // 加入到Request
                            paramEvent(ParamEvent.AddControlNet(cnModel.id))
                        else
                            paramEvent(ParamEvent.CloseControlNet(cnModel.id))
                    }
                },
                modifier = Modifier.size(34.dp)
            )
            IconButton(onClick = {
                val model = CNParam(
                    input_image = cnModel.input_image,
                    mask = cnModel.mask,
                    module = cnModel.module,
                    model = cnModel.model,
                    weight = cnModel.weight,
                    resize_mode = cnModel.resize_mode,
                    lowvram = cnModel.lowvram,
                    processor_res = cnModel.processor_res,
                    threshold_a = cnModel.threshold_a,
                    threshold_b = cnModel.threshold_b,
                    guidance_start = cnModel.guidance_start,
                    guidance_end = cnModel.guidance_end,
                    control_mode = cnModel.control_mode,
                    pixel_perfect = cnModel.pixel_perfect,
                )
                cnEvent(ControlNetEvent.AddCNParam(model))
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    modifier = Modifier.size(34.dp)
                )
            }
            IconButton(onClick = {
//                    if (serverState.value) {

//                    if (wdp < 450.dp) {
                cnEvent(ControlNetEvent.EditCNParam(cnModel, editing = false))
                uiEvent(UIEvent.Navigate(EditCNScreen) {
                    navController.navigate("cnparam_edit")
                })
//                    } else {
//                        cnEvent(ControlNetEvent.EditCNParam(cnModel, editing = true))
//                    }
//                    } else {
//                        Toast.makeText(context, context.getText(R.string.t_sd_not_connected), Toast.LENGTH_SHORT).show()
//                    }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
