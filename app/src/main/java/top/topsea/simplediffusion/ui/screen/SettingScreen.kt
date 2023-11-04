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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tencent.mmkv.MMKV
import top.topsea.simplediffusion.AboutScreen
import top.topsea.simplediffusion.DesktopScreen
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.api.dto.VaeModel
import top.topsea.simplediffusion.data.param.TaskParam
import top.topsea.simplediffusion.data.param.UserPrompt
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.event.PromptEvent
import top.topsea.simplediffusion.ui.component.ChangePromptPopup
import top.topsea.simplediffusion.ui.component.PromptField
import top.topsea.simplediffusion.ui.component.RequestErrorPopup
import top.topsea.simplediffusion.ui.component.SettingRowInt
import top.topsea.simplediffusion.ui.component.SettingSwitch
import top.topsea.simplediffusion.util.DeleteImage
import top.topsea.simplediffusion.util.TextUtil

@Composable
fun SettingScreen(
    navController: NavController,
    uiViewModel: UIViewModel,
    normalViewModel: NormalViewModel,
    tasks: List<TaskParam>,
) {
    // 生图出错时的弹框
    if (uiViewModel.warningStr.isNotEmpty()) {
        RequestErrorPopup(errorMsg = uiViewModel.warningStr) { uiViewModel.onEvent(UIEvent.UIWarning("")) }
    }

    LazyColumn(
        modifier = Modifier
            .background(color = Color.LightGray)
    ) {
        item { Spacer(modifier = Modifier.size(4.dp)) }
        item {
            BasicSettingBlock(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                uiViewModel = uiViewModel,
                normalViewModel = normalViewModel
            ) { uiViewModel, normalViewModel ->
                DefaultSettings(
                    uiViewModel = uiViewModel,
                    normalViewModel = normalViewModel,
                    navController = navController,
                    tasks = tasks,
                )
            }
        }
        item {
            CNSettingBlock(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                uiViewModel = uiViewModel,
                normalViewModel = normalViewModel
            ) { uiViewModel, normalViewModel ->
                ControlNetSettings(normalViewModel = normalViewModel, uiViewModel = uiViewModel)
            }
        }
        item {
            AgentSchedulerBlock(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                uiViewModel = uiViewModel,
                normalViewModel = normalViewModel,
                tasks = tasks,
            ) { _, normalViewModel ->

            }
        }
        item { Spacer(modifier = Modifier.size(4.dp)) }
    }
}


@OptIn(ExperimentalUnitApi::class)
@Composable
fun BasicSettingBlock(
    modifier: Modifier,
    uiViewModel: UIViewModel,
    normalViewModel: NormalViewModel,
    setting: @Composable() ((UIViewModel, NormalViewModel) -> Unit),
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
            setting(uiViewModel, normalViewModel)
        }
    }
}
@Composable
fun CNSettingBlock(
    modifier: Modifier,
    uiViewModel: UIViewModel,
    normalViewModel: NormalViewModel,
    setting: @Composable() ((UIViewModel, NormalViewModel) -> Unit),
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
                isOn = uiViewModel.exControlNet,
                tint = MaterialTheme.colorScheme.secondary,
            ) {}
        }
        Divider(color = MaterialTheme.colorScheme.secondary, thickness = 1.5.dp)
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            setting(uiViewModel, normalViewModel)
        }
    }
}
@Composable
fun AgentSchedulerBlock(
    modifier: Modifier,
    uiViewModel: UIViewModel,
    normalViewModel: NormalViewModel,
    tasks: List<TaskParam>,
    setting: @Composable() ((UIViewModel, NormalViewModel) -> Unit),
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
                isOn = uiViewModel.exAgentScheduler,
            ) {
                // 先检查 SD 服务器的连接，再检查生成队列是否为空，再检查 SD 服务器是否有这个插件，然后再更改
                if (uiViewModel.serverConnected) {
                    uiViewModel.onEvent(UIEvent.IsSaveControl(it))
                    if (tasks.isNotEmpty()) {
                        uiViewModel.onEvent(UIEvent.UIWarning(context.getString(R.string.s_agent_set_later)))
                    } else {
                        uiViewModel.onEvent(UIEvent.ExSettingChange("AgentScheduler", context))
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
        if (uiViewModel.exAgentScheduler) {
            Divider(color = MaterialTheme.colorScheme.secondary, thickness = 1.5.dp)
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                setting(uiViewModel, normalViewModel)
            }
        }
    }
}

@Composable
fun DefaultSettings(
    uiViewModel: UIViewModel,
    normalViewModel: NormalViewModel,
    navController: NavController,
    tasks: List<TaskParam>,
) {
    val context = LocalContext.current
    val isCamera = navController.previousBackStackEntry?.destination?.route == top.topsea.simplediffusion.CameraScreen.route
    TextUtil.topsea("DefaultSettings isCamera: $isCamera", Log.ERROR)

    val kv = MMKV.defaultMMKV()
    val serverIP: String = kv.decodeString(stringResource(id = R.string.server_ip), "http://192.168.0.107:7860")!!
    var serverIPStr by remember { mutableStateOf(serverIP) }

    var enableServerIP by remember { mutableStateOf(true) }
    var textColor by remember {
        mutableStateOf(Color.LightGray)
    }

    var btnIP by remember {
        mutableStateOf(context.getString(R.string.s_btn_modify))
    }

    var changePrompt by remember { mutableStateOf(false) }
    val loraState = normalViewModel.loraState.collectAsState()
    val vaes = normalViewModel.vaeState.collectAsState()
    val prompts = loraState.value.prompts
    var chosenPrompt by remember { mutableStateOf(UserPrompt()) }

    if (changePrompt) {
        ChangePromptPopup(
            title = if (chosenPrompt.id == 0)
                stringResource(id = R.string.p_addable_prompt_title_add)
            else stringResource(id = R.string.p_addable_prompt_title_mod),
            userPrompt =  chosenPrompt,
            { changePrompt = false }
        ) {
            normalViewModel.promptEvent(PromptEvent.UpdatePrompt(it))
            changePrompt = false
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
                    BasicTextField(
                        modifier = Modifier,
                        value = serverIPStr,
                        onValueChange = { serverIPStr = it },
                        enabled = true,
                        readOnly = enableServerIP,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        textStyle = TextStyle(color = textColor)
                    )
                }
                Button(
                    modifier = Modifier
                        .height(36.dp),
                    onClick = {
                        enableServerIP = !enableServerIP
                        if (!enableServerIP) {
                            textColor = Color.Black
                            btnIP = context.getString(R.string.s_btn_confirm)
                        } else {
                            textColor = Color.LightGray
                            btnIP = context.getString(R.string.s_btn_modify)
                            kv.encode(context.resources.getString(R.string.server_ip), serverIPStr)
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp, horizontal = 8.dp)
                ) {
                    Text(text = btnIP)
                }
            }
            Divider(color = Color.LightGray, thickness = 1.dp)
            SettingGoTo(title = stringResource(id = R.string.a_top_bar)) {
                uiViewModel.onEvent(UIEvent.Navigate(AboutScreen){
                    navController.navigate(AboutScreen.route)
                })
            }
        }

        if (uiViewModel.enableDesktop) {
            Divider(color = Color.LightGray, thickness = 1.dp)
            SettingGoTo(title = stringResource(id = R.string.sd_top_bar)) {
                uiViewModel.onEvent(UIEvent.Navigate(DesktopScreen){
                    navController.navigate(DesktopScreen.route)
                })
            }
        }
        Divider(color = Color.LightGray, thickness = 1.dp)
        SettingRowInt(
            name = stringResource(id = R.string.s_gen_size),
            value = uiViewModel.taskQueueSize,
            onMinus = {
                // 先检查生成队列是否为空
                if (tasks.isNotEmpty()) {
                    uiViewModel.onEvent(UIEvent.UIWarning(context.getString(R.string.s_agent_set_later)))
                } else {
                    uiViewModel.onEvent(UIEvent.MinusGenSize(context))
                }
            }
        ){
            // 先检查生成队列是否为空
            if (tasks.isNotEmpty()) {
                uiViewModel.onEvent(UIEvent.UIWarning(context.getString(R.string.s_agent_set_later)))
            } else {
                uiViewModel.onEvent(UIEvent.AddGenSize(context))
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
                isOn = uiViewModel.showGenOn1,
                tint = if (uiViewModel.taskQueueSize > 1) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            ) {
                uiViewModel.onEvent(UIEvent.ShowGenOn1(it))
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
                isOn = uiViewModel.saveOnServer,
            ) {
                uiViewModel.onEvent(UIEvent.SaveOnServer(it))
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
                selected = uiViewModel.currentVae.model_name,
            ) {
                uiViewModel.onEvent(UIEvent.UpdateVae(it, {
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
                isOn = uiViewModel.saveCapImage,
            ) {
                uiViewModel.onEvent(UIEvent.IsSaveCapImg(it))
            }
        }
        Divider(color = Color.LightGray, thickness = 1.dp)
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.s_normal_height)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingTitle(name = stringResource(id = R.string.s_addable_prompt))
                Button(
                    onClick = { changePrompt = true }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "")
                        Text(text = stringResource(id = R.string.s_addable_prompt_add))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PromptField(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1F)
                        .height(dimensionResource(id = R.dimen.param_prompt_height))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                    models = prompts,
                    onDelete = { normalViewModel.promptEvent(PromptEvent.DeletePrompt(it)) }
                ) {
                    chosenPrompt = it
                    changePrompt = true
                }
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
    uiViewModel: UIViewModel
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
                isOn = uiViewModel.saveControlNet && !uiViewModel.exAgentScheduler,
                tint = if (uiViewModel.exAgentScheduler) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            ) {
                uiViewModel.onEvent(UIEvent.IsSaveControl(it))
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