package top.topsea.simplediffusion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.UIViewModel

@Composable
fun SetPEScreen(
    navController: NavController,
    uiViewModel: UIViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        ) {
            SetPERows(navController = navController, uiViewModel = uiViewModel)
        }
    }
}

@Composable
fun SetPERows(
    modifier: Modifier = Modifier,
    navController: NavController,
    uiViewModel: UIViewModel,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .border(
                1.dp, Color.LightGray, RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 8.dp)
        ) {
            SettingGoTo(title = stringResource(id = R.string.sp_top_bar_txt)) {
                uiViewModel.onEvent(UIEvent.Navigate(top.topsea.simplediffusion.SetTxtParamScreen){
                    navController.navigate(top.topsea.simplediffusion.SetTxtParamScreen.route)
                })
            }
            Divider()
            SettingGoTo(title = stringResource(id = R.string.sp_top_bar_img)) {
                // TODO
            }
        }
    }
}
