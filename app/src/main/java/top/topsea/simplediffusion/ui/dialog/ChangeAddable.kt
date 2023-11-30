package top.topsea.simplediffusion.ui.dialog

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tencent.mmkv.MMKV
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.param.UserPrompt
import top.topsea.simplediffusion.util.Constant

@Composable
fun ChangeAddable(
    title: String,
    userPrompt: UserPrompt,
    onDismiss: () -> Unit,
    onConfirm: (UserPrompt) -> Unit,
) {
    val truePrompt = userPrompt.alias.replace(Constant.addableFirst, "").replace(Constant.addableSecond, "")
    var temp1 by remember { mutableStateOf(userPrompt.name) }
    var temp2 by remember { mutableStateOf(truePrompt) }
    var temp1TooLong by remember { mutableStateOf(false) }
    var temp2TooLong by remember { mutableStateOf(false) }

    val context = LocalContext.current

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
                modifier = Modifier
                    .heightIn(min = 240.dp, max = 280.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                OutlineTF(value = temp1, onValueChange = {
                    if (it.length >= 20) {
                        temp1 = it
                        temp1TooLong = true
                    } else {
                        temp1 = it
                        temp1TooLong = false
                    }}, label = stringResource(id = R.string.p_addable_prompt_name))
                if (temp1TooLong) {
                    Text(
                        text = stringResource(id = R.string.p_addable_prompt_name_2l),
                        fontSize = 12.sp,
                        color = Color.Red,
                    )
                }
                OutlineTF(value = temp2, onValueChange = {
                    if (it.length >= 50) {
                        temp2 = it
                        temp2TooLong = true
                    } else {
                        temp2 = it
                        temp2TooLong = false
                    }
                }, label = stringResource(id = R.string.p_addable_prompt_value))
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
                if (temp1.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.t_sd_empty_addable_name), Toast.LENGTH_SHORT).show()
                } else {
                    val addablePrompt = Constant.addableFirst + temp2 + Constant.addableSecond
                    if (!temp1TooLong && !temp2TooLong)
                        onConfirm(UserPrompt(id = userPrompt.id, name = temp1, alias = addablePrompt))
                }
            }) {
                Text(text = stringResource(id = R.string.p_btn_confirm))
            }
        }
    )
}
