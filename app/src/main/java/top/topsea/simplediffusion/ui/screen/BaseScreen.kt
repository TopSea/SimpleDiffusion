package top.topsea.simplediffusion.ui.screen

import android.Manifest
import android.widget.Toast
import androidx.annotation.Keep
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import top.topsea.simplediffusion.CameraScreen
import top.topsea.simplediffusion.EditScreen
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.param.TaskParam
import top.topsea.simplediffusion.data.state.GenerateState
import top.topsea.simplediffusion.data.state.ParamLocalState
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.BasicViewModel
import top.topsea.simplediffusion.data.viewmodel.ImgDataViewModel
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.event.ControlNetEvent
import top.topsea.simplediffusion.event.TaskListEvent
import top.topsea.simplediffusion.event.GenerateEvent
import top.topsea.simplediffusion.event.ParamEvent
import top.topsea.simplediffusion.ui.component.DisplayImages
import top.topsea.simplediffusion.ui.component.DisplayInGrid
import top.topsea.simplediffusion.util.getWidthDp


@Keep
enum class Bottom{
    PHOTO,
    PARAM
}

@Composable
fun BaseScreen(
    navController: NavController,
    uiViewModel: UIViewModel,
    selectedItem: MutableState<Bottom>,
    normalViewModel: NormalViewModel = hiltViewModel(),
    imgDataViewModel: ImgDataViewModel = hiltViewModel(),
    paramViewModel: BasicViewModel = hiltViewModel(),

    tasks: List<TaskParam>,
    errorTasks: List<TaskParam>,
    genState: GenerateState,
    generateEvent: (GenerateEvent) -> Unit
) {
    val paramState = paramViewModel.paramState.collectAsState()
    val imgState by imgDataViewModel.state.collectAsState()
    val cnState by normalViewModel.cnState.collectAsState()

    LaunchedEffect(key1 = null) {
        // 检查SD的连接状态
        normalViewModel.checkSDConnect {
            uiViewModel.onEvent(UIEvent.ServerConnected(it))
        }

        // 激活对应的 ControlNet
        val currParam = paramState.value.currParam
        if (currParam != null) {
            normalViewModel.cnEvent(ControlNetEvent.ActivateByRequest(currParam.control_net))
        }
    }

    // 从拍摄页面出来后要改一下 statusBar 的颜色
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = MaterialTheme.colorScheme.primary,
        darkIcons = false
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (selectedItem.value) {
            Bottom.PHOTO -> {
                DisplayInGrid(
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    imageState = imgState,
                    uiViewModel = uiViewModel,
                    tasks = tasks,
                    errorTasks = errorTasks,
                    genState = genState,
                    generateEvent = generateEvent,
                )
            }
            Bottom.PARAM -> {
                ParamScreen(
                    navController = navController,
                    uiViewModel = uiViewModel,
                    cnState = cnState,
                    paramState = paramState.value,
                    paramEvent = paramViewModel::paramEvent,
                    cnEvent = normalViewModel::cnEvent,
                )
            }
        }
    }

    AnimatedVisibility(
        visible = uiViewModel.displaying && uiViewModel.displayingImg >= 0,
    ) {
        DisplayImages(
            modifier = Modifier
                .fillMaxSize(1F)
                .background(Color(50, 50, 50, 0x88))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    uiViewModel.onEvent(UIEvent.DisplayImg(-1))
                },
            uiViewModel = uiViewModel,
            imgDataViewModel = imgDataViewModel,
            paramViewModel = paramViewModel,
            tasks = tasks,
            errorTasks = errorTasks,
            genState = genState,
            generateEvent = generateEvent,
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BaseBottomBar(
    selectedItem: MutableState<Bottom>,
    navController: NavController,
    uiViewModel: UIViewModel,
    paramState: ParamLocalState,
    paramEvent: (ParamEvent) -> Unit,
    tasks: List<TaskParam>,
    taskListEvent: (TaskListEvent) -> Unit,
) {
    val permissionCamera = rememberPermissionState(Manifest.permission.CAMERA)
    val context = LocalContext.current

    NavigationBar {
        NavigationBarItem(
            icon = {
                Box(modifier = Modifier.size(24.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.bottom_photo),
                        contentDescription = "item",
                        modifier = Modifier.fillMaxSize()
                    )
                    if (tasks.isNotEmpty())
                        Box(
                            modifier = Modifier
                                .offset(x = 8.dp, y = -(4).dp)
                                .size(12.dp)
                                .background(Color.Red, CircleShape)
                                .clip(CircleShape)
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ){
                            Text(text = "${tasks.size}", fontSize = 8.sp, color = Color.White)
                        }
                }
            },
            label = { Text("图片") },
            selected = selectedItem.value == Bottom.PHOTO,
            onClick = { selectedItem.value = Bottom.PHOTO },
            modifier = Modifier.weight(1f)
        )
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.inversePrimary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (uiViewModel.displaying) {
                            uiViewModel.onEvent(UIEvent.DisplayImg(-1))
                        } else {
                            paramEvent(ParamEvent.CheckCapture(
                                notInI2I = {
                                    Toast
                                        .makeText(
                                            context,
                                            context.getText(R.string.t_incorrect_param),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            ) {
                                if (uiViewModel.serverConnected)
                                    if (!permissionCamera.status.isGranted) {
                                        permissionCamera.launchPermissionRequest()
                                    } else {
                                        uiViewModel.onEvent(UIEvent.Navigate(CameraScreen) {
                                            navController.navigate(CameraScreen.route)
                                        })
                                    }
                                else
                                    Toast
                                        .makeText(
                                            context,
                                            context.getText(R.string.t_sd_not_connected),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                            })
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.camera_gen),
                    contentDescription = "item",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.inversePrimary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (uiViewModel.displaying) {
                            uiViewModel.onEvent(UIEvent.DisplayImg(-1))
                        } else {
                            if (uiViewModel.serverConnected) {
                                val currParam = paramState.currParam
                                if (currParam != null) {
                                    taskListEvent(TaskListEvent.AddTaskImage(null to currParam){
                                        Toast.makeText(context, context.getString(R.string.t_too_many_gen), Toast.LENGTH_SHORT).show()
                                    })
                                }
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        context.getText(R.string.t_sd_not_connected),
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.photo_gen),
                    contentDescription = "item",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.inversePrimary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (uiViewModel.displaying) {
                            uiViewModel.onEvent(UIEvent.DisplayImg(-1))
                        } else {
                            paramEvent(
                                ParamEvent.EditActivate(
                                    editingActivate = true,
                                    editing = false
                                ) {
                                    Toast
                                        .makeText(
                                            context,
                                            context.getString(R.string.t_no_param_selected),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                })
                            if (paramState.currParam != null) {
                                uiViewModel.onEvent(UIEvent.Navigate(EditScreen) {
                                    navController.navigate(EditScreen.route)
                                })
                            }
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "item",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                )
            }
        }
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.bottom_param),
                    contentDescription = "item",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("参数") },
            selected = selectedItem.value == Bottom.PARAM,
            onClick = {
                if (uiViewModel.displaying) {
                    uiViewModel.onEvent(UIEvent.DisplayImg(-1))
                }
                selectedItem.value = Bottom.PARAM
            },
            modifier = Modifier.weight(1f)
        )
    }
}