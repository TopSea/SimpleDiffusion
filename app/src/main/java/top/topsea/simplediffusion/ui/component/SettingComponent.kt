package top.topsea.simplediffusion.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.ui.screen.SettingTitle


@Composable
fun StepRowInt(
    name: String,
    value: Int,
    onMinus: () -> Unit,
    onAdd: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.s_normal_height)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingTitle(name = name)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.circle_minus),
                contentDescription = "",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onMinus()
                    },
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "$value",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(64.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.circle_plus),
                contentDescription = "",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onAdd()
                    },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SettingSwitch(
    modifier: Modifier,
    isOn: Boolean,
    tint: Color = MaterialTheme.colorScheme.primary,
    onSwitch: (Boolean) -> Unit,
) {
    Box(modifier = modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onSwitch(!isOn) }) {
        if (isOn) {
            Icon(painter = painterResource(id = R.drawable.toggle_on), contentDescription = "", modifier = Modifier.fillMaxSize(), tint = tint)
        } else {
            Icon(painter = painterResource(id = R.drawable.toggle_off), contentDescription = "", modifier = Modifier.fillMaxSize(), tint = tint)
        }
    }
}