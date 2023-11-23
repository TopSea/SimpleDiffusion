package top.topsea.simplediffusion.ui.scripts

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.ui.component.FitScreen
import top.topsea.simplediffusion.ui.component.StepRowFloat
import top.topsea.simplediffusion.ui.component.ParamTitle
import top.topsea.simplediffusion.ui.component.StepRowInt
import top.topsea.simplediffusion.ui.component.SwipeInt
import top.topsea.simplediffusion.ui.component.UpscalersChoose
import top.topsea.simplediffusion.util.TextUtil

//def run(self, p, _, tile_width, tile_height, mask_blur, padding, seams_fix_width, seams_fix_denoise, seams_fix_padding,
//upscaler_index, save_upscaled_image, redraw_mode, save_seams_fix_image, seams_fix_mask_blur,
//seams_fix_type, target_size_type, custom_width, custom_height, custom_scale):
//"script_args": [0, 512, 512, 8, 16, 64, 0.35, 16, 3, true, 1, false, 4, 0, 1, 1024, 1024, 2]

val target_size_types  by lazy {
    listOf(
        R.string.upscale_choose_1_1,
        R.string.upscale_choose_1_2,
        R.string.upscale_choose_1_3,
    )
}
val upscalers  by lazy {
    listOf(
        R.string.upscalers_1,
        R.string.upscalers_2,
        R.string.upscalers_3,
        R.string.upscalers_4,
        R.string.upscalers_5,
        R.string.upscalers_6,
        R.string.upscalers_7,
        R.string.upscalers_8,
        R.string.upscalers_9,
        R.string.upscalers_10,
    )
}
val upscale_funcs by lazy {
    listOf(
        R.string.upscale_func_1,
        R.string.upscale_func_2,
        R.string.upscale_func_3,
    )
}
val seams_fix_types  by lazy {
    listOf(
        R.string.seams_fix_type_1,
        R.string.seams_fix_type_2,
        R.string.seams_fix_type_3,
        R.string.seams_fix_type_4,
    )
}

data class UltimateSDUpscale(
    val tile_width: Int = 512,
    val tile_height: Int = 512,
    val mask_blur: Int = 8,
    val padding: Int = 16,
    val seams_fix_width: Int = 64,
    val seams_fix_denoise: Float = 0.35f,
    val seams_fix_padding: Int = 16,
    val upscaler_index: Int = 3,
    val save_upscaled_image: Boolean = true,
    val redraw_mode: Int = 1,
    val save_seams_fix_image: Boolean = false,
    val seams_fix_mask_blur: Int = 4,
    val seams_fix_type: Int = 0,
    val target_size_type: Int = 0,
    val custom_width: Int = 1024,
    val custom_height: Int = 1024,
    val custom_scale: Float = 2f,
) : Script()

@Composable
fun USDUpscaleScript(
    script_args: MutableState<Script?>
) {
    TextUtil.topsea("Script UltimateSDUpscale:${script_args.value is UltimateSDUpscale}", Log.ERROR)
    if (script_args.value !is UltimateSDUpscale) {
        script_args.value = UltimateSDUpscale()
    }
    val script = script_args.value as UltimateSDUpscale
    TextUtil.topsea("Script UltimateSDUpscale:$script", Log.ERROR)

    val custom_width = remember { mutableStateOf(script.custom_width) }
    val custom_scale = remember { mutableStateOf(script.custom_scale) }
    val custom_height = remember { mutableStateOf(script.custom_height) }
    val target_size_type = remember { mutableStateOf(script.target_size_type) }
    val upscaler_index = remember { mutableStateOf(script.upscaler_index) }
    val redraw_mode = remember { mutableStateOf(script.redraw_mode) }
    val tile_width = remember { mutableStateOf(script.tile_width) }
    val tile_height = remember { mutableStateOf(script.tile_height) }
    val mask_blur = remember { mutableStateOf(script.mask_blur) }
    val padding = remember { mutableStateOf(script.padding) }
    val seams_fix_type = remember { mutableStateOf(script.seams_fix_type) }
    val seams_fix_denoise = remember { mutableStateOf(script.seams_fix_denoise) }
    val seams_fix_width = remember { mutableStateOf(script.seams_fix_width) }
    val seams_fix_padding = remember { mutableStateOf(script.seams_fix_padding) }

    LaunchedEffect(target_size_type.value, custom_scale.value, custom_width.value, custom_height.value,
        upscaler_index.value, redraw_mode.value, tile_width.value, tile_height.value,
        padding.value, mask_blur.value, seams_fix_type.value,
        seams_fix_denoise.value, seams_fix_width.value, seams_fix_padding.value,) {
        val upscale = UltimateSDUpscale(
            target_size_type = target_size_type.value,
            custom_scale = custom_scale.value,
            custom_width = custom_width.value,
            custom_height = custom_height.value,
            upscaler_index = upscaler_index.value,
            redraw_mode = redraw_mode.value,
            tile_width = tile_width.value,
            tile_height = tile_height.value,
            mask_blur = mask_blur.value,
            padding = padding.value,
            seams_fix_type = seams_fix_type.value,
            seams_fix_denoise = seams_fix_denoise.value,
            seams_fix_width = seams_fix_width.value,
            seams_fix_padding = seams_fix_padding.value,
        )
        TextUtil.topsea("changing script... ${upscale}", Log.ERROR)
        script_args.value = upscale
    }

    var targetSizeType by remember { mutableStateOf(false) }
    var redrawMenu by remember { mutableStateOf(false) }
    var seamsMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    ) {
        FitScreen(
            titleComp = {
                ParamTitle(
                    boldTitle = false,
                    title = stringResource(id = R.string.upscale_choose_1),
                    isPad = it)
            }
        ) { modifier ->

            Box(
                modifier = modifier
                    .padding(start = 8.dp, top = 8.dp, end = 48.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.param_drop_menu))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        .clickable { targetSizeType = true },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(target_size_types[target_size_type.value]),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = if (targetSizeType) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Open menu.",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                    )
                }

                DropdownMenu(
                    expanded = targetSizeType, onDismissRequest = { targetSizeType = false },
                    modifier = Modifier
                        .fillMaxWidth(.7f)
                ) {
                    target_size_types.forEachIndexed { index, it ->
                        DropdownMenuItem(text = {
                            Text(
                                text = stringResource(id = it),
                                modifier = Modifier
                                    .widthIn(min = 72.dp),
                                maxLines = 1,
                            )
                        }, onClick = {
                            target_size_type.value = index
                            targetSizeType = false
                        })
                    }
                }
            }
        }
        DividerOfScript()
        when (target_size_type.value) {
            0 -> { }
            1 -> {
                SwipeInt(
                    boldTitle = false,
                    name = stringResource(id = R.string.custom_width),
                    int = custom_width,
                    max = 8192
                )
                DividerOfScript()
                SwipeInt(
                    boldTitle = false,
                    name = stringResource(id = R.string.custom_height),
                    int = custom_height,
                    max = 8192
                )
                DividerOfScript()
            }
            2 -> {
                StepRowFloat(
                    boldTitle = false,
                    name = stringResource(id = R.string.upscale_times),
                    float = custom_scale,
                    step = 0.01f,
                    max = 16f
                )
                DividerOfScript()
            }
        }
        
        Text(text = stringResource(id = R.string.upscalers_title), modifier = Modifier.padding(start = 8.dp))
        UpscalersChoose(chosen = upscaler_index.value, choices = upscalers) {
            upscaler_index.value = it
        }
        DividerOfScript()
        FitScreen(
            titleComp = {
                ParamTitle(
                    boldTitle = false,
                    title = stringResource(id = R.string.upscale_func),
                    isPad = it)
            }
        ) { modifier ->
            Box(
                modifier = modifier
                    .padding(start = 8.dp, top = 8.dp, end = 48.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.param_drop_menu))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        .clickable { redrawMenu = true },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(upscale_funcs[redraw_mode.value]),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = if (redrawMenu) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Open menu.",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                    )
                }

                DropdownMenu(
                    expanded = redrawMenu, onDismissRequest = { redrawMenu = false },
                    modifier = Modifier
                        .fillMaxWidth(.7f)
                ) {
                    upscale_funcs.forEachIndexed { index, it ->
                        DropdownMenuItem(text = {
                            Text(
                                text = stringResource(id = it),
                                modifier = Modifier
                                    .widthIn(min = 72.dp),
                                maxLines = 1,
                            )
                        }, onClick = {
                            redraw_mode.value = index
                            redrawMenu = false
                        })
                    }
                }
            }
        }
        DividerOfScript()
        SwipeInt(
            boldTitle = false,
            name = stringResource(id = R.string.upscale_func_width),
            int = tile_width,
            max = 2048
        )
        DividerOfScript()
        SwipeInt(
            boldTitle = false,
            name = stringResource(id = R.string.upscale_func_height),
            int = tile_height,
            max = 2048
        )
        DividerOfScript()
        StepRowInt(
            boldTitle = false,
            name = stringResource(id = R.string.upscale_func_blur),
            int = mask_blur,
            max = 64
        )
        DividerOfScript()
        StepRowInt(
            boldTitle = false,
            name = stringResource(id = R.string.upscale_func_padding),
            int = padding,
            max = 128
        )
        DividerOfScript()
        FitScreen(
            titleComp = {
                ParamTitle(
                    boldTitle = false,
                    title = stringResource(id = R.string.seams_func_title),
                    isPad = it)
            }
        ) { modifier ->

            Box(
                modifier = modifier
                    .padding(start = 8.dp, top = 8.dp, end = 48.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.param_drop_menu))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        .clickable { seamsMenu = true },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(seams_fix_types[seams_fix_type.value]),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = if (seamsMenu) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Open menu.",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                    )
                }

                DropdownMenu(
                    expanded = seamsMenu, onDismissRequest = { seamsMenu = false },
                    modifier = Modifier
                        .fillMaxWidth(.7f)
                ) {
                    seams_fix_types.forEachIndexed { index, it ->
                        DropdownMenuItem(text = {
                            Text(
                                text = stringResource(id = it),
                                modifier = Modifier
                                    .widthIn(min = 72.dp),
                                maxLines = 1,
                            )
                        }, onClick = {
                            seams_fix_type.value = index
                            seamsMenu = false
                        })
                    }
                }
            }
        }
        if (seams_fix_type.value != 0) {
            DividerOfScript()
            StepRowFloat(
                boldTitle = false,
                name = stringResource(id = R.string.seams_func_denoise),
                float = seams_fix_denoise,
                step = 0.01f,
                max = 16f
            )
            DividerOfScript()
            StepRowInt(
                boldTitle = false,
                name = stringResource(id = R.string.seams_func_blur),
                int = seams_fix_width,
                max = 64
            )
            DividerOfScript()
            StepRowInt(
                boldTitle = false,
                name = stringResource(id = R.string.seams_func_padding),
                int = seams_fix_padding,
                max = 64
            )
        }
    }
}