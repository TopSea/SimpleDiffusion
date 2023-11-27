package top.topsea.simplediffusion

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.ui.component.Screen
import top.topsea.simplediffusion.ui.component.TopBar
import top.topsea.simplediffusion.util.TextUtil

interface SimpleDestination {
    val topBar: @Composable () (UIViewModel, NavController) -> Unit
    val route: String
}

fun navUp(navController: NavController) {
    navController.navigateUp()
    navController.currentDestination?.route?.let {
        TextUtil.topsea("navController.currentDestination: $it", Log.ERROR)
        updateCurrScreen(it)
    }
}

fun updateCurrScreen(route: String) {
    TextUtil.topsea("UpdateCurrScreen: ${route}")
    val screen = when(route) {
        SettingScreen.route -> { SettingScreen }
        EditScreen.route -> { EditScreen }
        EditCNScreen.route -> { EditCNScreen }
        AboutScreen.route -> { AboutScreen }
        CameraScreen.route -> { CameraScreen }
        CameraSettingScreen.route -> { CameraSettingScreen }
        DesktopScreen.route -> { DesktopScreen }
        else -> {
            BaseScreen
        }
    }
    TextUtil.topsea("CurrScreen: $screen")
    currentScreen = screen
}

object BaseScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(navController = navController, title = stringResource(id = R.string.app_name), screen = Screen.BASE) {
            if (uiViewModel.displaying) {
                uiViewModel.onEvent(UIEvent.Display(false))
            }
            uiViewModel.onEvent(it)
        }
    }
    override val route: String = "base_screen"
}

object SettingScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(navController = navController, title = stringResource(R.string.s_top_bar), screen = Screen.SETTING) {
            uiViewModel.onEvent(it)
        }
    }
    override val route: String = "settings"
}

object EditScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(navController = navController, title = stringResource(R.string.e_top_bar), screen = Screen.EDIT) {
            uiViewModel.onEvent(it)
        }
    }
    override val route: String = "param_edit"
}

object EditCNScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(navController = navController, title = stringResource(R.string.e_top_bar), screen = Screen.EDIT_CN) {
            uiViewModel.onEvent(it)
        }
    }
    override val route: String = "cnparam_edit"
}

object AboutScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(navController = navController, title = stringResource(R.string.a_top_bar), screen = Screen.ABOUT) {
            uiViewModel.onEvent(it)
        }
    }
    override val route: String = "about"
}

object CameraScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(navController = navController, title = stringResource(R.string.s_top_bar), screen = Screen.CAMERA,
            backIcon = {
                Icon(
                    imageVector = Icons.Rounded.Close, contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(32.dp)
                        .clickable {
                            if (uiViewModel.displaying) {
                                uiViewModel.onEvent(UIEvent.Display(false))
                            } else {
                                navUp(navController)
                            }
                        },
                    tint = Color.White
                )
            }) {
            if (uiViewModel.displaying) {
                uiViewModel.onEvent(UIEvent.Display(false))
            }
            uiViewModel.onEvent(it)
        }
    }
    override val route: String = "camera"
}

object CameraSettingScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(navController = navController, title = stringResource(R.string.s_top_bar), screen = Screen.CAMERA_SETTINGS) {
            uiViewModel.onEvent(it)
        }
    }
    override val route: String = "camera_settings"
}

object DesktopScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(navController = navController, title = stringResource(R.string.sd_top_bar), screen = Screen.DESKTOP) {
            uiViewModel.onEvent(it)
        }
    }
    override val route: String = "desktop"
}