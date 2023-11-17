package top.topsea.simplediffusion.ui.scripts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DividerOfScript() {
    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Gray,
        thickness = .8.dp
    )
}