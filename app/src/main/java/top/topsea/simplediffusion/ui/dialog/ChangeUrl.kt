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
fun ChangeUrl(
    serverIP: MutableState<String>,
    onDismiss: () -> Unit,
) {
    val kv = MMKV.defaultMMKV()
    val ku = kv.decodeString(stringResource(id = R.string.api_username), "")!!
    val kp = kv.decodeString(stringResource(id = R.string.api_password), "")!!
    var username by remember { mutableStateOf(ku) }
    var password by remember { mutableStateOf(kp) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = {
            serverIP.value = kv.decodeString(context.getString(R.string.server_ip), "http://192.168.0.107:7860")!!
            onDismiss()
        },
        title = {
            Text(
                text = stringResource(id = R.string.p_sd_change_url),
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
                OutlineTF(value = serverIP.value, onValueChange = { serverIP.value = it}, label = stringResource(id = R.string.p_sd_url))
                OutlineTF(value = username, onValueChange = { username = it}, label = stringResource(id = R.string.p_sd_api_username))
                OutlineTF(value = password, isSecret = true, onValueChange = { password = it}, label = stringResource(id = R.string.p_sd_api_password))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                serverIP.value = kv.decodeString(context.getString(R.string.server_ip), "http://192.168.0.107:7860")!!
                onDismiss()
            }) {
                Text(text = stringResource(id = R.string.p_btn_dismiss))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (serverIP.value.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.t_sd_empty_url), Toast.LENGTH_SHORT).show()
                } else {
                    kv.encode(context.getString(R.string.server_ip), serverIP.value)
                    kv.encode(context.getString(R.string.api_username), username)
                    kv.encode(context.getString(R.string.api_password), password)
                    onDismiss()
                }
            }) {
                Text(text = stringResource(id = R.string.p_btn_confirm))
            }
        }
    )
}

@Composable
fun OutlineTF(
    value: String,
    isSecret: Boolean = false,
    onValueChange: (String) -> Unit,
    label: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        maxLines = 2,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        },
        visualTransformation = if (isSecret) PasswordVisualTransformation() else VisualTransformation.None
    )
}