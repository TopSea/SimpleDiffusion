package top.topsea.simplediffusion.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.param.CNParam
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.param.TaskParam
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.BasicViewModel
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UISetsViewModel
import top.topsea.simplediffusion.event.ControlNetEvent
import top.topsea.simplediffusion.event.ParamEvent
import top.topsea.simplediffusion.ui.tab.CNParamTab
import top.topsea.simplediffusion.ui.tab.ParamTab


@Composable
fun CameraSettingScreen(
    navController: NavController,
    uiSetsViewModel: UISetsViewModel,
    normalViewModel: NormalViewModel = hiltViewModel(),
    paramViewModel: BasicViewModel = hiltViewModel(),
    tasks: List<TaskParam>,
) {
    // 从拍摄页面出来后要改一下 statusBar 的颜色
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = MaterialTheme.colorScheme.primary,
        darkIcons = false
    )

    val paramState by paramViewModel.paramState.collectAsState()
    val cnState by normalViewModel.cnState.collectAsState()

    val titles =
        listOf(stringResource(id = R.string.r_request_card_a2), stringResource(id = R.string.r_request_card_a3), stringResource(id = R.string.r_request_card_a4))

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = uiSetsViewModel.cameraTab, modifier = Modifier.fillMaxWidth()) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = uiSetsViewModel.cameraTab == index,
                    onClick = { uiSetsViewModel.onEvent(UIEvent.ChangeCameraTab(index)) },
                    text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                )
            }
        }

        when (uiSetsViewModel.cameraTab) {
            0 -> {
                ParamTab(
                    navController = navController,
                    params = paramState.iParams,
                    uiSetsViewModel = uiSetsViewModel,
                    paramEvent = paramViewModel::paramEvent,
                    cnEvent = normalViewModel::cnEvent,
                ){
                    paramViewModel.paramEvent(ParamEvent.AddParam(ImgParam()))
                }
            }
            1 -> {
                CNParamTab(
                    navController = navController,
                    cnModels = cnState.cnParams,
                    uiSetsViewModel = uiSetsViewModel,
                    paramState = paramState,
                    paramEvent = paramViewModel::paramEvent,
                    cnEvent = normalViewModel::cnEvent
                ){
                    normalViewModel.cnEvent(ControlNetEvent.AddCNParam(CNParam()))
                }
            }
            2 -> {
                SettingScreen(
                    navController = navController,
                    uiSetsViewModel = uiSetsViewModel,
                    normalViewModel = normalViewModel,
                    tasks = tasks,
                )
            }
        }
    }
}
