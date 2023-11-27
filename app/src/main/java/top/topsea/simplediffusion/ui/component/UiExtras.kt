
package top.topsea.simplediffusion.ui.component

import android.widget.Toast
import androidx.annotation.Keep
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.topsea.simplediffusion.CameraSettingScreen
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.SettingScreen
import top.topsea.simplediffusion.data.param.UserPrompt
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.navUp
import top.topsea.simplediffusion.util.Constant


@Keep
enum class Screen {
    BASE,
    SETTING,
    EDIT,
    EDIT_CN,
    ABOUT,
    CAMERA,
    CAMERA_SETTINGS,
    DESKTOP,
}

@Composable
fun TopBar(
    navController: NavController,
    title: String,
    screen: Screen,
    backIcon: @Composable() (BoxScope.(() -> Unit) -> Unit) = { navUp ->
        Icon(
            imageVector = Icons.Rounded.ArrowBack, contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(32.dp)
                .clickable {
                    navUp()
                },
            tint = Color.White
        )
    },
    navOp: (UIEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        when(screen) {
            Screen.BASE -> BaseTopBar(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
                title = title,
                startTitle = true
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp, top = 4.dp, bottom = 8.dp)
                        .size(32.dp)
                        .align(Alignment.CenterEnd)
                        .clickable {
                            navOp(UIEvent.Navigate(SettingScreen) {
                                navController.navigate(SettingScreen.route)
                            })
                        },
                    tint = Color.White
                )
            }

            Screen.SETTING -> BaseTopBar(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
                title = title,
                startIcon = {
                backIcon {
                    navUp(navController)
                }
            })
            Screen.EDIT -> BaseTopBar(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
                title = title,
                startIcon = {
                backIcon {
                    navUp(navController)
                }
            })
            Screen.EDIT_CN -> BaseTopBar(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
                title = title,
                startIcon = {
                backIcon {
                    navUp(navController)
                }
            })
            Screen.ABOUT -> BaseTopBar(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
                title = title,
                startIcon = {
                backIcon {
                    navUp(navController)
                }
            })
            Screen.CAMERA -> BaseTopBar(
                modifier = Modifier.background(color = Color.Black),
                title = "",
                startIcon = {
                    backIcon {  }
            }){
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp, top = 4.dp, bottom = 8.dp)
                        .size(32.dp)
                        .align(Alignment.CenterEnd)
                        .clickable {
                            navOp(UIEvent.Navigate(CameraSettingScreen) {
                                navController.navigate(CameraSettingScreen.route)
                            })
                        },
                    tint = Color.White
                )
            }
            Screen.CAMERA_SETTINGS -> BaseTopBar(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
                title = title,
                startIcon = {
                    backIcon {
                        navUp(navController)
                    }
            }){ }
            Screen.DESKTOP -> BaseTopBar(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
                title = title,
                startIcon = {
                backIcon {
                    navUp(navController)
                }
            })
        }
    }
}

@Composable
fun BaseTopBar(
    title: String,
    modifier: Modifier = Modifier,
    startTitle: Boolean = false,
    startIcon: @Composable() (BoxScope.() -> Unit) = {},
    endIcon: @Composable() (BoxScope.() -> Unit) = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        startIcon()
        if (title.isNotEmpty())
            Text(
                text = title,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(if (startTitle) Alignment.CenterStart else Alignment.Center),
                fontSize = TextUnit(value = 20F, type = TextUnitType.Sp),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        endIcon()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePromptPopup(
    title: String,
    userPrompt: UserPrompt,
    onDismiss: () -> Unit,
    onConfirm: (UserPrompt) -> Unit,
) {
    val truePrompt = userPrompt.alias.replace(Constant.addableFirst, "").replace(Constant.addableSecond, "")
    val temp1 = remember { mutableStateOf(userPrompt.name) }
    val temp2 = remember { mutableStateOf(truePrompt) }
    var temp1TooLong by remember { mutableStateOf(false) }
    var temp2TooLong by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.heightIn(min = 240.dp, max = 280.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                Text(
                    text = stringResource(id = R.string.p_addable_prompt_name),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = temp1.value,
                    onValueChange = {
                        if (it.length >= 20) {
                            temp1.value = it
                            temp1TooLong = true
                        } else {
                            temp1.value = it
                            temp1TooLong = false
                        }
                    },
                    textStyle = TextStyle(fontSize = 18.sp),
                    maxLines = 1
                )
                if (temp1TooLong) {
                    Text(
                        text = stringResource(id = R.string.p_addable_prompt_name_2l),
                        fontSize = 12.sp,
                        color = Color.Red,
                    )
                }
                Text(
                    text = stringResource(id = R.string.p_addable_prompt_value),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                TextField(
                    value = temp2.value,
                    onValueChange = {
                        if (it.length >= 50) {
                            temp2.value = it
                            temp2TooLong = true
                        } else {
                            temp2.value = it
                            temp2TooLong = false
                        }
                    },
                    textStyle = TextStyle(fontSize = 18.sp),
                    maxLines = 2
                )
                if (temp2TooLong) {
                    Text(
                        text = stringResource(id = R.string.p_addable_prompt_value_2l),
                        fontSize = 12.sp,
                        color = Color.Red,
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(id = R.string.p_btn_dismiss))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val addablePrompt = Constant.addableFirst + temp2.value + Constant.addableSecond
                if (!temp1TooLong && !temp2TooLong)
                    onConfirm(UserPrompt(id = userPrompt.id, name = temp1.value, alias = addablePrompt))
            }) {
                Text(text = stringResource(id = R.string.p_btn_confirm))
            }
        }
    )
}

@Composable
fun RequestErrorPopup(
    errorMsg: String,
    onDismiss: () -> Unit,
) {

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(id = R.string.p_request_error),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = errorMsg,
                fontSize = 15.sp,
                modifier = Modifier
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(
                    fontSize = 15.sp,
                    text = stringResource(id = R.string.p_btn_confirm)
                )
            }
        }
    )
}

@Composable
fun StringInput(
    modifier: Modifier,
    value: MutableState<String>,
    textStyle: TextStyle,
) {
    val context = LocalContext.current
    
    Column(
        modifier = modifier.width(200.dp)
    ) {
        BasicTextField(
            modifier = Modifier
                .padding(bottom = 2.dp)
                .fillMaxWidth()
                .weight(1f)
                .onFocusChanged {
                    if (!it.isFocused) {
                        if (value.value.isEmpty()) {
                            value.value = "Name"
                            Toast
                                .makeText(
                                    context,
                                    context.getText(R.string.t_empty_name),
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                },
            value = value.value,
            onValueChange = {
                value.value = it
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send
            ),
            maxLines = 1,
            textStyle = textStyle,
        )
        Divider(color = MaterialTheme.colorScheme.primary, thickness = 1.5.dp)
    }
}

@Composable
fun IntInput(
    modifier: Modifier,
    value: MutableState<Int>,
    textStyle: TextStyle,
) {
    val context = LocalContext.current
    var input by remember { mutableStateOf("${value.value}") }

    Column(modifier = modifier) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .onFocusChanged {
                    if (!it.isFocused) {
                        val inputNum = runCatching {
                            if (input.isEmpty()) {
                                input = "0"
                                0
                            } else {
                                input.toInt()
                            }
                        }
                        if (inputNum.isSuccess) {
                            value.value = inputNum.getOrDefault(value.value)
                        } else {
                            input = "0"
                            Toast
                                .makeText(
                                    context,
                                    context.getText(R.string.t_input_int),
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                },
            value = input,
            onValueChange = {
                input = it
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Send
            ),
            maxLines = 1,
            textStyle = textStyle,
        )
        Divider(color = MaterialTheme.colorScheme.primary, thickness = 1.5.dp)
    }
}

@Composable
fun FloatInput(
    modifier: Modifier,
    value: MutableState<Float>,
    textStyle: TextStyle,
) {
    val context = LocalContext.current
    BasicTextField(
        modifier = modifier,
        value = "${value.value}",
        onValueChange = {
            val inputNum = runCatching {
                if (it.isEmpty()) {
                    0f
                } else {
                    it.toFloat()
                }
            }
            if (inputNum.isSuccess) {
                value.value = inputNum.getOrDefault(value.value)
            } else {
                Toast.makeText(context, context.getText(R.string.t_input_int), Toast.LENGTH_SHORT).show()
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Send
        ),
        maxLines = 1,
        textStyle = textStyle,
    )
}

@Composable
fun RoundedCheck(
    modifier: Modifier = Modifier,
    check: MutableState<Boolean>,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { check.value = !check.value },
        contentAlignment = Alignment.Center
    ) {
        if (check.value) {
            Icon(
                painter = painterResource(id = R.drawable.rounded_check),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.rounded_uncheck),
                contentDescription = "",
                tint = Color.Gray,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UpscalersChoose(
    chosen: Int,
    choices: List<Int>,
    onChoose: (Int) -> Unit,
) {
    FlowRow(modifier = Modifier.padding(horizontal = 8.dp)) {
        choices.forEachIndexed { index, it ->
            TypeChooseItem(title = stringResource(id = it), index = index, isChosen = index == chosen, onChoose = onChoose)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TypeChoose(
    chosen: String,
    choices: List<Pair<String, Int>>,
    onChoose: (String) -> Unit,
) {
    FlowRow(modifier = Modifier.padding(vertical = 8.dp)) {
        choices.forEach {
            TypeChooseItem(title = stringResource(id = it.second), choice = it.first, isChosen = it.first == chosen, onChoose = onChoose)
        }
    }
}

@Composable
fun TypeChooseItem(
    title: String,
    choice: String,
    isChosen: Boolean,
    onChoose: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .height(28.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onChoose(choice)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(16.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .background(
                    if (isChosen) MaterialTheme.colorScheme.primary else Color.White,
                    CircleShape
                )
                .clip(CircleShape)
        )
        Text(text = title,modifier = Modifier
            .padding(end = 4.dp))
    }
}

@Composable
fun TypeChooseItem(
    title: String,
    index: Int,
    isChosen: Boolean,
    onChoose: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .height(28.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onChoose(index)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(16.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .background(
                    if (isChosen) MaterialTheme.colorScheme.primary else Color.White,
                    CircleShape
                )
                .clip(CircleShape)
        )
        Text(text = title, modifier = Modifier
            .padding(end = 4.dp))
    }
}