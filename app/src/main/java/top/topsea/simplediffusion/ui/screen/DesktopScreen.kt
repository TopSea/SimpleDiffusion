package top.topsea.simplediffusion.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.UISetsViewModel

@Composable
fun DesktopScreen(
    uiSetsViewModel: UISetsViewModel,
) {
    val context = LocalContext.current
    val info = context.packageManager.getPackageInfo(context.packageName, 0)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "App icon.",
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Text(text = stringResource(R.string.a_develop_by, "TopSea"), fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))

            DesktopCommands(uiSetsViewModel = uiSetsViewModel)

        }
    }
}

@Composable
fun DesktopCommands(
    modifier: Modifier = Modifier,
    uiSetsViewModel: UISetsViewModel,
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .padding(top = 24.dp, start = 8.dp, end = 8.dp)
            .border(
                1.dp, Color.LightGray, RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 8.dp)
        ) {
//            SettingGoTo(title = stringResource(id = R.string.a_goto_user)) {
//            }
//            Divider()
            SettingGoTo(title = stringResource(id = R.string.a_goto_version)) {
                uiSetsViewModel.onEvent(UIEvent.Send2Desktop("abcdef"))
            }
            Divider()
            SettingGoTo(title = "stringResource(id = R.string.a_goto_advanced)") {

            }
        }
    }
}
