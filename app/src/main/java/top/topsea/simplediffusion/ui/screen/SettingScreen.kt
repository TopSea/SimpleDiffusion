package top.topsea.simplediffusion.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tencent.mmkv.MMKV
import top.topsea.simplediffusion.AboutScreen
import top.topsea.simplediffusion.SetPEScreen
import top.topsea.simplediffusion.DesktopScreen
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.api.dto.VaeModel
import top.topsea.simplediffusion.data.param.TaskParam
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UISetsViewModel
import top.topsea.simplediffusion.ui.component.RequestErrorPopup
import top.topsea.simplediffusion.ui.component.StepRowInt
import top.topsea.simplediffusion.ui.component.SettingSwitch
import top.topsea.simplediffusion.ui.dialog.ChangeUrl
import top.topsea.simplediffusion.util.TextUtil

@Composable
fun SettingScreen(
    navController: NavController,
    uiSetsViewModel: UISetsViewModel,
    normalViewModel: NormalViewModel,
    tasks: List<TaskParam>,
) {
    // 生图出错时的弹框
    if (uiSetsViewModel.warningStr.isNotEmpty()) {
        RequestErrorPopup(errorMsg = uiSetsViewModel.warningStr) { uiSetsViewModel.onEvent(UIEvent.UIWarning("")) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item {
            BasicSettingBlock(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                uiSetsViewModel = uiSetsViewModel,
                normalViewModel = normalViewModel
            ) { uiViewModel, normalViewModel ->
                DefaultSettings(
                    uiSetsViewModel = uiViewModel,
                    normalViewModel = normalViewModel,
                    navController = navController,
                    tasks = tasks,
                )
            }
        }
        item {
            CNSettingBlock(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                uiSetsViewModel = uiSetsViewModel,
                normalViewModel = normalViewModel
            ) { uiViewModel, normalViewModel ->
                ControlNetSettings(normalViewModel = normalViewModel, uiSetsViewModel = uiViewModel)
            }
        }
        item {
            AgentSchedulerBlock(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                uiSetsViewModel = uiSetsViewModel,
                normalViewModel = normalViewModel,
                tasks = tasks,
            ) { _, normalViewModel ->

            }
        }
        item {
            SdPromptBlock(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                uiSetsViewModel = uiSetsViewModel,
                normalViewModel = normalViewModel,
            )
        }
    }
}


@OptIn(ExperimentalUnitApi::class)
@Composable
fun BasicSettingBlock(
    modifier: Modifier,
    uiSetsViewModel: UISetsViewModel,
    normalViewModel: NormalViewModel,
    setting: @Composable() ((UISetsViewModel, NormalViewModel) -> Unit),
) {
    Column(modifier = modifier.background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .height(dimensionResource(id = R.dimen.s_normal_height)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.s_default_set),
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                fontWeight = FontWeight.Bold
            )
//            Text(
//                text = stringResource(id = R.string.s_btn_sd_connection),
//                fontSize = 15.sp,
//                modifier = Modifier
//                    .padding(vertical = 4.dp),
//                fontWeight = FontWeight.Bold,
//                textDecoration = TextDecoration.Underline,
//                color = MaterialTheme.colorScheme.inversePrimary
//            )
        }
        Divider(color = MaterialTheme.colorScheme.secondary, thickness = 1.5.dp)
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            setting(uiSetsViewModel, normalViewModel)
        }
    }
}
@Composable
fun CNSettingBlock(
    modifier: Modifier,
    uiSetsViewModel: UISetsViewModel,
    normalViewModel: NormalViewModel,
    setting: @Composable() ((UISetsViewModel, NormalViewModel) -> Unit),
) {
    Column(modifier = modifier.background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .height(dimensionResource(id = R.dimen.s_normal_height)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.s_control_net_set),
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                fontWeight = FontWeight.Bold
            )

            SettingSwitch(
                modifier = Modifier.size(DpSize(42.dp, 36.dp)),
                isOn = uiSetsViewModel.exControlNet,
                tint = MaterialTheme.colorScheme.secondary,
            ) {}
        }
        Divider(color = MaterialTheme.colorScheme.secondary, thickness = 1.5.dp)
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            setting(uiSetsViewModel, normalViewModel)
        }
    }
}
@Composable
fun AgentSchedulerBlock(
    modifier: Modifier,
    uiSetsViewModel: UISetsViewModel,
    normalViewModel: NormalViewModel,
    tasks: List<TaskParam>,
    setting: @Composable() ((UISetsViewModel, NormalViewModel) -> Unit),
) {
    val context = LocalContext.current
    Column(modifier = modifier.background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .height(dimensionResource(id = R.dimen.s_normal_height)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.s_gen_scheduler_set),
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                fontWeight = FontWeight.Bold
            )

            SettingSwitch(
                modifier = Modifier.size(DpSize(42.dp, 36.dp)),
                isOn = uiSetsViewModel.exAgentScheduler,
            ) {
                // 先检查 SD 服务器的连接，再检查生成队列是否为空，再检查 SD 服务器是否有这个插件，然后再更改
                if (uiSetsViewModel.serverConnected) {
                    if (tasks.isNotEmpty()) {
                        uiSetsViewModel.onEvent(UIEvent.UIWarning(context.getString(R.string.s_agent_set_later)))
                    } else {
                        uiSetsViewModel.onEvent(UIEvent.ExSettingChange("AgentScheduler", context){})
                    }
                }
                else
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
}

@Composable
fun SdPromptBlock(
    modifier: Modifier,
    uiSetsViewModel: UISetsViewModel,
    normalViewModel: NormalViewModel
) {
    val context = LocalContext.current
    Column(modifier = modifier.background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .height(dimensionResource(id = R.dimen.s_normal_height)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.s_sd_prompt_set),
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                fontWeight = FontWeight.Bold
            )

            SettingSwitch(
                modifier = Modifier.size(DpSize(42.dp, 36.dp)),
                isOn = uiSetsViewModel.exSdPrompt,
            ) {
                // 先检查 SD 服务器的连接，再检查是否有这个插件
                if (uiSetsViewModel.serverConnected) {
                    uiSetsViewModel.onEvent(UIEvent.ExSettingChange("SdPrompt", context){
                        normalViewModel.exSdPrompt(it)
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
            }
        }
    }
}

@Composable
fun DefaultSettings(
    uiSetsViewModel: UISetsViewModel,
    normalViewModel: NormalViewModel,
    navController: NavController,
    tasks: List<TaskParam>,
) {
    val context = LocalContext.current
    val isCamera = navController.previousBackStackEntry?.destination?.route == top.topsea.simplediffusion.CameraScreen.route
    TextUtil.topsea("DefaultSettings isCamera: $isCamera", Log.ERROR)

    val kv = MMKV.defaultMMKV()
    val serverIP: String = kv.decodeString(stringResource(id = R.string.server_ip), "http://192.168.0.107:7860")!!
    val serverIPStr = remember { mutableStateOf(serverIP) }

    var changeIP by remember { mutableStateOf(false) }

    val vaes = normalViewModel.vaeState.collectAsState()

    if (changeIP) {
        ChangeUrl(serverIP = serverIPStr) {
            changeIP = false
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (!isCamera) {
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .height(dimensionResource(id = R.dimen.s_normal_height))
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingTitle(name = stringResource(id = R.string.s_sd_dress))
                    Text(
                        modifier = Modifier,
                        text = serverIPStr.value,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Button(
                    modifier = Modifier
                        .height(36.dp),
                    onClick = {
                        changeIP = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp, horizontal = 8.dp)
                ) {
                    Text(text = stringResource(id = R.string.s_btn_modify))
                }
            }
            Divider(color = Color.LightGray, thickness = 1.dp)
            SettingGoTo(title = stringResource(id = R.string.a_top_bar)) {
                uiSetsViewModel.onEvent(UIEvent.Navigate(AboutScreen){
                    navController.navigate(AboutScreen.route)
                })
            }
            Divider(color = Color.LightGray, thickness = 1.dp)
            SettingGoTo(title = stringResource(id = R.string.sp_top_bar)) {
                uiSetsViewModel.onEvent(UIEvent.Navigate(SetPEScreen){
                    navController.navigate(SetPEScreen.route)
                })
            }
        }

        if (uiSetsViewModel.enableDesktop) {
            Divider(color = Color.LightGray, thickness = 1.dp)
            SettingGoTo(title = stringResource(id = R.string.sd_top_bar)) {
                uiSetsViewModel.onEvent(UIEvent.Navigate(DesktopScreen){
                    navController.navigate(DesktopScreen.route)
                })
            }
        }
        Divider(color = Color.LightGray, thickness = 1.dp)
        StepRowInt(
            name = stringResource(id = R.string.s_gen_size),
            value = uiSetsViewModel.taskQueueSize,
            onMinus = {
                // 先检查生成队列是否为空
                if (tasks.isNotEmpty()) {
                    uiSetsViewModel.onEvent(UIEvent.UIWarning(context.getString(R.string.s_agent_set_later)))
                } else {
                    uiSetsViewModel.onEvent(UIEvent.MinusGenSize(context))
                }
            }
        ){
            // 先检查生成队列是否为空
            if (tasks.isNotEmpty()) {
                uiSetsViewModel.onEvent(UIEvent.UIWarning(context.getString(R.string.s_agent_set_later)))
            } else {
                uiSetsViewModel.onEvent(UIEvent.AddGenSize(context))
            }
        }
        Divider(color = Color.LightGray, thickness = 1.dp)
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.s_normal_height)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingTitle(name = stringResource(id = R.string.s_show_gen_one))
            SettingSwitch(
                modifier = Modifier.size(DpSize(42.dp, 36.dp)),
                isOn = uiSetsViewModel.showGenOn1,
                tint = if (uiSetsViewModel.taskQueueSize > 1) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            ) {
                uiSetsViewModel.onEvent(UIEvent.ShowGenOn1(it))
            }
        }
        Divider(color = Color.LightGray, thickness = 1.dp)
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.s_normal_height)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingTitle(name = stringResource(id = R.string.s_save_on_server))
            SettingSwitch(
                modifier = Modifier.size(DpSize(42.dp, 36.dp)),
                isOn = uiSetsViewModel.saveOnServer,
            ) {
                uiSetsViewModel.onEvent(UIEvent.SaveOnServer(it))
            }
        }
        Divider(color = Color.LightGray, thickness = 1.dp)
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.s_normal_height)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingTitle(name = stringResource(id = R.string.s_vae))
            MenuVae(
                items = vaes.value.models,
                selected = uiSetsViewModel.currentVae.model_name,
            ) {
                uiSetsViewModel.onEvent(UIEvent.UpdateVae(it, {
                    Toast.makeText(context, context.getString(R.string.p_request_error), Toast.LENGTH_SHORT).show()
                }){
                    Toast.makeText(context, context.getString(R.string.p_change_vae_success), Toast.LENGTH_SHORT).show()
                })
            }
        }
        Divider(color = Color.LightGray, thickness = 1.dp)
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.s_normal_height)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingTitle(name = stringResource(id = R.string.s_save_cap_img))
            SettingSwitch(
                modifier = Modifier.size(DpSize(42.dp, 36.dp)),
                isOn = uiSetsViewModel.saveCapImage,
            ) {
                uiSetsViewModel.onEvent(UIEvent.IsSaveCapImg(it))
            }
        }
        Divider(color = Color.LightGray, thickness = 1.dp)
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.s_normal_height)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingTitle(name = stringResource(id = R.string.s_save_grid_img))
            SettingSwitch(
                modifier = Modifier.size(DpSize(42.dp, 36.dp)),
                isOn = uiSetsViewModel.saveGridImage,
            ) {
                uiSetsViewModel.onEvent(UIEvent.IsSaveGridImg(it))
            }
        }
        // 测试时使用
//        Row(
//            modifier = Modifier
//                .padding(vertical = 8.dp)
//                .fillMaxWidth()
//                .height(dimensionResource(id = R.dimen.s_normal_height)),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Button(onClick = { /*TODO*/ }) {
//                Text(text = "T-Base64")
//            }
//        }
    }
}

@Composable
fun ControlNetSettings(
    normalViewModel: NormalViewModel,
    uiSetsViewModel: UISetsViewModel
) {
    val cnState by normalViewModel.cnState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .height(dimensionResource(id = R.dimen.s_normal_height))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingTitle(name = stringResource(id = R.string.s_control_net_max))
            SettingPlain(plain = if (cnState.maxModelsNum < 0) stringResource(id = R.string.t_sd_not_connected) else "${cnState.maxModelsNum}")
        }
        Divider(color = Color.LightGray, thickness = 1.dp)
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.s_normal_height)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingTitle(name = stringResource(id = R.string.s_control_net_version))
            SettingPlain(plain = if (cnState.version < 0) stringResource(id = R.string.t_sd_not_connected) else "${cnState.version}")
        }
        Divider(color = Color.LightGray, thickness = 1.dp)
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.s_normal_height)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingTitle(name = stringResource(id = R.string.s_save_control))
            SettingSwitch(
                modifier = Modifier.size(DpSize(42.dp, 36.dp)),
                isOn = uiSetsViewModel.saveControlNet && !uiSetsViewModel.exAgentScheduler,
                tint = if (uiSetsViewModel.exAgentScheduler) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            ) {
                uiSetsViewModel.onEvent(UIEvent.IsSaveControl(it))
            }
        }
    }
}

@Composable
fun RowScope.Menu(
    items: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .width(dimensionResource(id = R.dimen.s_menu_width)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.param_drop_menu))
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                .clickable { menuExpanded = true },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .widthIn(max = 120.dp),
                fontSize = 13.sp
            )
            Icon(
                imageVector = if (menuExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                contentDescription = "Open menu.",
                modifier = Modifier
                    .padding(horizontal = 8.dp),
            )
        }

        DropdownMenu(
            expanded = menuExpanded, onDismissRequest = { menuExpanded = false },
            modifier = Modifier
        ) {
            items.forEach {
                DropdownMenuItem(text = {
                    Text(
                        text = it.second,
                        modifier = Modifier
                            .width(dimensionResource(id = R.dimen.s_menu_width))
                    )
                }, onClick = {
                    onSelect(it.first)
                    menuExpanded = false
                })
            }
        }
    }
}

@Composable
fun RowScope.MenuVae(
    items: List<VaeModel>,
    selected: String,
    onSelect: (VaeModel) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .width(dimensionResource(id = R.dimen.s_vae_width)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.param_drop_menu))
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                .clickable { menuExpanded = true },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .widthIn(max = 180.dp),
                fontSize = 13.sp,
                maxLines = 1
            )
            Icon(
                imageVector = if (menuExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                contentDescription = "Open menu.",
                modifier = Modifier
                    .padding(horizontal = 8.dp),
            )
        }

        DropdownMenu(
            expanded = menuExpanded, onDismissRequest = { menuExpanded = false },
            modifier = Modifier
        ) {
            items.forEach {
                DropdownMenuItem(text = {
                    Text(
                        text = it.model_name,
                        modifier = Modifier
                            .width(dimensionResource(id = R.dimen.s_vae_width))
                    )
                }, onClick = {
                    onSelect(it)
                    menuExpanded = false
                })
            }
        }
    }
}

@Composable
fun SettingTitle(name: String) {
    Text(
        text = name,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SettingPlain(plain: String) {
    Text(
        text = plain,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(end = 16.dp)
    )
}

@Composable
fun SettingGoTo(title: String, goTo: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.s_normal_height))
            .clickable { goTo() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingTitle(name = title)
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Go to $title", modifier = Modifier.size(28.dp))
    }
}