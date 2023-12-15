package top.topsea.simplediffusion

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalProvider
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
        SetPEScreen.route -> { SetPEScreen }
        SetTxtParamScreen.route -> { SetTxtParamScreen }
        SetImgParamScreen.route -> { SetImgParamScreen }
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

@Composable
fun BoxScope.GeneralStartIcon(
    navOp: () -> Unit
) {
    Icon(
        imageVector = Icons.Rounded.ArrowBack, contentDescription = null,
        modifier = Modifier
            .align(Alignment.CenterStart)
            .padding(start = 16.dp)
            .size(32.dp)
            .clickable {
                navOp()
            },
        tint = Color.White
    )
}

object BaseScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(
            title = if (uiViewModel.serverConnected) stringResource(id = R.string.app_sd_connected) else stringResource(id = R.string.app_sd_unconnected),
            screen = Screen.BASE,
            startIcon = { }
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(32.dp)
                    .align(Alignment.CenterEnd)
                    .clickable {
                        if (uiViewModel.displaying) {
                            uiViewModel.onEvent(UIEvent.Display(false))
                        }
                        uiViewModel.onEvent(UIEvent.Navigate(SettingScreen) {
                            navController.navigate(SettingScreen.route)
                        })
                    },
                tint = Color.White
            )
        }
    }
    override val route: String = "base_screen"
}

object SettingScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(
            title = stringResource(R.string.s_top_bar),
            screen = Screen.SETTING,
            startIcon = {
                GeneralStartIcon {
                    navUp(navController)
                }
            }
        )
    }
    override val route: String = "settings"
}

object SetPEScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(
            title = stringResource(R.string.sp_top_bar),
            screen = Screen.SET_PARAM,
            startIcon = {
                GeneralStartIcon {
                    navUp(navController)
                }
            }
        )
    }
    override val route: String = "set_param"
}

object SetTxtParamScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(
            title = stringResource(R.string.sp_top_bar_txt),
            screen = Screen.SET_PARAM_TXT,
            startIcon = {
                GeneralStartIcon {
                    navUp(navController)
                }
            }
        )
    }
    override val route: String = "set_param_txt"
}

object SetImgParamScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(
            title = stringResource(R.string.sp_top_bar_img),
            screen = Screen.SET_PARAM_IMG,
            startIcon = {
                GeneralStartIcon {
                    navUp(navController)
                }
            }
        )
    }
    override val route: String = "set_param_img"
}

object EditScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        var tempParamShow by remember { mutableStateOf(false) }
        TopBar(title = stringResource(R.string.e_top_bar),
            screen = Screen.EDIT,
            startIcon = {
                GeneralStartIcon {
                    if (uiViewModel.tempParamShow) {
                        uiViewModel.onEvent(UIEvent.TempParamShow(false))
                    }
                    navUp(navController)
                }
            }
        ) {
            Icon(
                imageVector = if (tempParamShow) Icons.Filled.Info else Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp, top = 4.dp, bottom = 8.dp)
                    .size(32.dp)
                    .align(Alignment.CenterEnd)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        tempParamShow = !tempParamShow
                        uiViewModel.onEvent(UIEvent.TempParamShow(tempParamShow))
                    },
                tint = Color.White
            )
        }
    }
    override val route: String = "param_edit"
}

object EditCNScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(
            title = stringResource(R.string.e_top_bar),
            screen = Screen.EDIT_CN,
            startIcon = {
                GeneralStartIcon {
                    navUp(navController)
                }
            }
        )
    }
    override val route: String = "cnparam_edit"
}

object AboutScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(
            title = stringResource(R.string.a_top_bar),
            screen = Screen.ABOUT,
            startIcon = {
                GeneralStartIcon {
                    navUp(navController)
                }
            }
        )
    }
    override val route: String = "about"
}

object CameraScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(
            title = stringResource(R.string.s_top_bar),
            screen = Screen.CAMERA,
            startIcon = {
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
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp, top = 4.dp, bottom = 8.dp)
                    .size(32.dp)
                    .align(Alignment.CenterEnd)
                    .clickable {
                        if (uiViewModel.displaying) {
                            uiViewModel.onEvent(UIEvent.Display(false))
                        }
                        uiViewModel.onEvent(UIEvent.Navigate(CameraSettingScreen) {
                            navController.navigate(CameraSettingScreen.route)
                        })
                    },
                tint = Color.White
            )
        }
    }
    override val route: String = "camera"
}

object CameraSettingScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(
            title = stringResource(R.string.s_top_bar),
            screen = Screen.CAMERA_SETTINGS,
            startIcon = {
                GeneralStartIcon {
                    navUp(navController)
                }
            }
        )
    }
    override val route: String = "camera_settings"
}

object DesktopScreen : SimpleDestination {
    override val topBar: @Composable (UIViewModel, NavController) -> Unit = { uiViewModel, navController: NavController ->
        TopBar(
            title = stringResource(R.string.sd_top_bar),
            screen = Screen.DESKTOP,
            startIcon = {
                GeneralStartIcon {
                    navUp(navController)
                }
            }
        )
    }
    override val route: String = "desktop"
}