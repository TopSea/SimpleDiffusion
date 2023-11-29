package top.topsea.simplediffusion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.param.UserPrompt
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.event.PromptEvent
import top.topsea.simplediffusion.ui.component.ChangePromptPopup
import top.topsea.simplediffusion.ui.component.PromptField

@Composable
fun SetPEScreen(
    navController: NavController,
    uiViewModel: UIViewModel,
    normalViewModel: NormalViewModel,
) {
    var changePrompt by remember { mutableStateOf(false) }
    val prompts by normalViewModel.localPrompts.collectAsState()
    var chosenPrompt by remember { mutableStateOf(UserPrompt()) }

    if (changePrompt) {
        ChangePromptPopup(
            title = if (chosenPrompt.id == 0)
                stringResource(id = R.string.p_addable_prompt_title_add)
            else stringResource(id = R.string.p_addable_prompt_title_mod),
            userPrompt = chosenPrompt,
            {
                chosenPrompt = UserPrompt()
                changePrompt = false
            }
        ) {
            chosenPrompt = UserPrompt()
            normalViewModel.promptEvent(PromptEvent.UpdatePrompt(it))
            changePrompt = false
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .border(
                1.dp, Color.LightGray, RoundedCornerShape(8.dp)
            ),
    ) {
        SetPERows(navController = navController, uiViewModel = uiViewModel)
        Divider()
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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
                    onDelete = {
                        chosenPrompt = UserPrompt()
                        normalViewModel.promptEvent(PromptEvent.DeletePrompt(it))
                    }
                ) {
                    chosenPrompt = it
                    changePrompt = true
                }
            }
        }
    }
}

@Composable
fun SetPERows(
    modifier: Modifier = Modifier,
    navController: NavController,
    uiViewModel: UIViewModel,
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
            uiViewModel.onEvent(UIEvent.Navigate(top.topsea.simplediffusion.SetImgParamScreen){
                navController.navigate(top.topsea.simplediffusion.SetImgParamScreen.route)
            })
        }
    }
}
