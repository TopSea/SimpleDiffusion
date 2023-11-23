package top.topsea.simplediffusion.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.api.dto.BaseModel
import top.topsea.simplediffusion.api.dto.LoraModel
import top.topsea.simplediffusion.api.dto.Sampler
import top.topsea.simplediffusion.data.param.AddablePrompt
import top.topsea.simplediffusion.data.param.CNParam
import top.topsea.simplediffusion.data.param.UserPrompt
import top.topsea.simplediffusion.pickingImg
import top.topsea.simplediffusion.ui.theme.Pink80
import top.topsea.simplediffusion.util.FileUtil
import top.topsea.simplediffusion.util.TextUtil
import top.topsea.simplediffusion.util.getWidthDp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRequest(
    onSearch: (String) -> Unit,
) {
    var sInput by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = sInput,
            onValueChange = { sInput = it },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search, contentDescription = "",
                    modifier = Modifier.clickable(enabled = sInput.isNotEmpty()) { onSearch(sInput) }
                )
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
        )
    }
}

@Composable
fun StepRowInt(
    boldTitle: Boolean = true,
    name: String,
    int: MutableState<Int>,
    max: Int = 100,
    min: Int = 0,
    step: Int = 1
) {
    val scope = rememberCoroutineScope()
    var longMinus = false
    var longPlus = false
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(start = 4.dp)
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.param_pad_item_height)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ParamTitle(modifier = Modifier.weight(1f), boldTitle = boldTitle, title = name, isPad = true)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.circle_minus),
                contentDescription = "",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        val value = int.value - step
                        if (value >= min) {
                            int.value = value
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDrag = { _, _ -> },
                            onDragStart = {
                                longMinus = true
                                var value = int.value - step
                                scope.launch {
                                    while (value >= min && longMinus) {
                                        delay(100)
                                        int.value = value
                                        value = int.value - step
                                    }
                                }
                            },
                            onDragEnd = { longMinus = false },
                            onDragCancel = { longMinus = false }
                        )
                    },
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${int.value}",
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
                        val value = int.value + step
                        if (value <= max) {
                            int.value = value
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDrag = { _, _ -> },
                            onDragStart = {
                                longPlus = true
                                var value = int.value + step
                                scope.launch {
                                    while (value <= max && longPlus) {
                                        delay(100)
                                        int.value = value
                                        value = int.value + step
                                    }
                                }
                            },
                            onDragEnd = { longPlus = false },
                            onDragCancel = { longPlus = false }
                        )
                    },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * @param ff: float print format.
 */
@Composable
fun StepRowFloat(
    boldTitle: Boolean = true,
    name: String,
    float: MutableState<Float>,
    max: Float = 100f,
    min: Float = 0f,
    ff: String = "%.1f",
    step: Float = 0.5f
) {
    val scope = rememberCoroutineScope()
    var longMinus = false
    var longPlus = false
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(start = 4.dp)
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.param_pad_item_height)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ParamTitle(modifier = Modifier.weight(1f), boldTitle = boldTitle, title = name, isPad = true)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.circle_minus),
                contentDescription = "",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        val value = float.value - step
                        if (value >= min) {
                            float.value = value
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDrag = { _, _ -> },
                            onDragStart = {
                                longMinus = true
                                var value = float.value - step
                                scope.launch {
                                    while (value >= min && longMinus) {
                                        delay(100)
                                        float.value = value
                                        value = float.value - step
                                    }
                                }
                            },
                            onDragEnd = { longMinus = false },
                            onDragCancel = { longMinus = false }
                        )
                    },
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = ff.format(float.value),
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
                        val value = float.value + step
                        if (value <= max) {
                            float.value = value
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDrag = { _, _ -> },
                            onDragStart = {
                                longPlus = true
                                var value = float.value + step
                                scope.launch {
                                    while (value <= max && longPlus) {
                                        delay(100)
                                        float.value = value
                                        value = float.value + step
                                    }
                                }
                            },
                            onDragEnd = { longPlus = false },
                            onDragCancel = { longPlus = false }
                        )
                    },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SwipeInt(
    boldTitle: Boolean = true,
    modifier: Modifier = Modifier,
    name: String,
    int: MutableState<Int>,
    max: Int = 100,
    min: Int = 0,
    step: Int = 1,
) {
    val scope = rememberCoroutineScope()
    var longMinus = false
    var longPlus = false

    val wdp = getWidthDp()
    val width = if (wdp > 420.dp) wdp / 2 else wdp / 3
    val sizePx = with(LocalDensity.current) { width.toPx() }
    var offsetX by remember { mutableStateOf(int.value / max.toFloat() * sizePx) }
    val trueStep = step / max.toFloat() * sizePx
    val percentage = offsetX / sizePx
    val num = (percentage * max).toInt()
    int.value = num

    val bmChangingColors = arrayOf(
        percentage to MaterialTheme.colorScheme.primary,
        percentage + 0.001F to MaterialTheme.colorScheme.inverseOnSurface,
        1f to MaterialTheme.colorScheme.inverseOnSurface
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(start = 4.dp)
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.param_pad_item_height)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ParamTitle(boldTitle = boldTitle, title = name, isPad = true)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.circle_minus),
                contentDescription = "",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        val value = offsetX - trueStep
                        if (value >= min) {
                            offsetX = value
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDrag = { _, _ -> },
                            onDragStart = {
                                longMinus = true
                                var value = offsetX - trueStep
                                scope.launch {
                                    while (value >= min && longMinus) {
                                        delay(100)
                                        offsetX = value
                                        value = offsetX - trueStep
                                    }
                                }
                            },
                            onDragEnd = { longMinus = false },
                            onDragCancel = { longMinus = false }
                        )
                    },
                tint = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxHeight()
                    .width(width)
                    .background(
                        brush = Brush.horizontalGradient(colorStops = bmChangingColors),
                        RoundedCornerShape(16.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .offset { IntOffset(offsetX.roundToInt(), 0) }
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(8.dp)
                        )
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val x = offsetX + dragAmount.x
                                offsetX = if (x < sizePx) {
                                    if (x < 0)
                                        0f
                                    else
                                        x
                                } else
                                    sizePx
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "$num", color =  Color.White, modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
            Text(text = "         ", color =  Color.White, modifier = Modifier.padding(horizontal = 8.dp))
            Icon(
                painter = painterResource(id = R.drawable.circle_plus),
                contentDescription = "",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        val value = offsetX + trueStep
                        if (value <= max) {
                            offsetX = value
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDrag = { _, _ -> },
                            onDragStart = {
                                longPlus = true
                                var value = offsetX + trueStep
                                scope.launch {
                                    while (value <= max && longPlus) {
                                        delay(100)
                                        offsetX = value
                                        value = offsetX + trueStep
                                    }
                                }
                            },
                            onDragEnd = { longPlus = false },
                            onDragCancel = { longPlus = false }
                        )
                    },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun <T> ParamRowChoice(
    name: String,
    currChoice: MutableState<String>,
    content: List<T>,
    onChangeItem: (T) -> Unit,
    text: @Composable () (T) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    FitScreen(modifier = Modifier,
        titleComp = { ParamTitle(title = name, isPad = it) }) { modifier ->
        Row(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1F)
            ) {

                Row(
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen.param_drop_menu))
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        .clickable { menuExpanded = true },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currChoice.value,
                        modifier = Modifier
                            .widthIn(
                                min = dimensionResource(id = R.dimen.param_menu_length_min),
                                max = dimensionResource(id = R.dimen.param_menu_length_max)
                            )
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(
                        imageVector = if (menuExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded, onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.6F)
                ) {
                    content.forEach {
                        DropdownMenuItem(text = {
                            text(it)
                        }, onClick = {
                            menuExpanded = false
                            onChangeItem(it)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ParamRowChoice(
    name: String,
    currChoice: MutableState<String>,
    content: List<BaseModel>,
    onRefresh: suspend () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    FitScreen(modifier = Modifier,
        titleComp = { ParamTitle(title = name, isPad = it) }) { modifier ->
        Row(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1F)
            ) {

                Row(
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen.param_drop_menu))
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        .clickable { menuExpanded = true },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currChoice.value,
                        modifier = Modifier
                            .widthIn(
                                min = dimensionResource(id = R.dimen.param_menu_length_min),
                                max = dimensionResource(id = R.dimen.param_menu_length_max)
                            )
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(
                        imageVector = if (menuExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded, onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.6F)
                ) {
                    content.forEach {
                        DropdownMenuItem(text = {
                            Text(
                                text = it.title,
                                modifier = Modifier
                                    .widthIn(min = 160.dp),
                                maxLines = 1
                            )
                        }, onClick = {
                            currChoice.value = it.title
                            menuExpanded = false
                        })
                    }
                }
            }

            IconButton(onClick = { scope.launch { onRefresh() } }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Choices",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ParamRowChoice(
    name: String,
    currChoice: MutableState<String>,
    content: List<String>,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(start = 4.dp)
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.param_pad_item_height)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ParamTitle(title = name, isPad = true)
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .weight(1F)
            ) {

                Row(
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen.param_drop_menu))
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        .clickable { menuExpanded = true },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currChoice.value,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                        fontSize = 18.sp,
                        maxLines = 1
                    )
                    Icon(
                        imageVector = if (menuExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded, onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.6F)
                ) {
                    content.forEach {
                        DropdownMenuItem(text = {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .widthIn(min = 240.dp),
                                maxLines = 1
                            )
                        }, onClick = {
                            currChoice.value = it
                            menuExpanded = false
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ParamRowChooseSampler(
    name: String,
    currChoice: MutableState<String>,
    content: List<Sampler>,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(start = 4.dp)
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.param_pad_item_height)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ParamTitle(title = name, isPad = true)
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .weight(1F)
            ) {

                Row(
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen.param_drop_menu))
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        .clickable { menuExpanded = true },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currChoice.value,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                        fontSize = 18.sp,
                        maxLines = 1
                    )
                    Icon(
                        imageVector = if (menuExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded, onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.6F)
                ) {
                    content.forEach {
                        DropdownMenuItem(text = {
                            Text(
                                text = it.name,
                                modifier = Modifier
                                    .widthIn(min = 240.dp),
                                maxLines = 1
                            )
                        }, onClick = {
                            currChoice.value = it.name
                            menuExpanded = false
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ParamRowChangeName(
    title: String,
    name: MutableState<String>,
) {
    Row(
        modifier = Modifier
            .padding(start = 4.dp, end = 8.dp)
            .height(dimensionResource(id = R.dimen.param_pad_item_height))
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ParamTitle(title = title, isPad = true)

            StringInput(
                modifier = Modifier
                    .height(28.dp),
                value = name,
                textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
        }
    }
}

@Composable
fun ParamRowImgChoose(
    base64Str: MutableState<String>,
    picking: Int = -1,
) {
//    val uiState by uiViewModel.uiState.collectAsState()
    val stroke = Stroke(
        width = 8f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )

    Box(
        modifier = Modifier
            .padding(start = 8.dp, top = 4.dp, end = 8.dp)
            .fillMaxWidth()
            .height(420.dp)
            .clickable {
                pickingImg.value = picking
            }
    ) {
        if (base64Str.value.isNotEmpty()) {
            Image(
                bitmap = FileUtil.getIBFromBase64(base64Str.value)!!,
                contentDescription = "Selected image.",
                modifier = Modifier.align(
                    Alignment.Center
                )
            )

            IconButton(
                onClick = {
                    if (picking == -1) {
                        pickingImg.value = -3
                    } else {
                        pickingImg.value = -4 - picking
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close image.", tint = Color.White)
            }
        } else {
            Canvas(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                drawRoundRect(
                    color = Color.Gray,
                    style = stroke,
                    cornerRadius = CornerRadius(30F, 30F)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(
                        Alignment.Center
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    modifier = Modifier
                        .size(48.dp),
                    tint = Color.Gray
                )
                Text(
                    text = stringResource(id = R.string.r_img_choose),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
    }
}
@Composable
fun ParamRowImgChoose(
    base64Str: String,
    picking: Int = -1,
) {
    val stroke = Stroke(
        width = 8f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )

    Box(
        modifier = Modifier
            .padding(start = 8.dp, top = 4.dp, end = 8.dp)
            .fillMaxWidth()
            .height(420.dp)
            .clickable {
                pickingImg.value = picking
            }
    ) {
        if (base64Str.isNotEmpty()) {
            Image(
                bitmap = FileUtil.getIBFromBase64(base64Str)!!,
                contentDescription = "Selected image.",
                modifier = Modifier.align(
                    Alignment.Center
                )
            )

            IconButton(
                onClick = {
                    if (picking == -1) {
                        pickingImg.value = -3
                    } else {
                        pickingImg.value = -4 - picking
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close image.", tint = Color.White)
            }
        } else {
            Canvas(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                drawRoundRect(
                    color = Color.Gray,
                    style = stroke,
                    cornerRadius = CornerRadius(30F, 30F)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(
                        Alignment.Center
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    modifier = Modifier
                        .size(48.dp),
                    tint = Color.Gray
                )
                Text(
                    text = stringResource(id = R.string.r_img_choose),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ParamRowPrompt(
    name: String,
    models: List<Pair<String, MutableList<AddablePrompt>>>,
    prompts: List<AddablePrompt>,
    prompt: MutableState<String>,
    onRefresh: suspend () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val onEditPrompt = remember { mutableStateOf(false) }
    var currLoraDir by remember { mutableStateOf("Simple") }

    val focusRequester = remember { FocusRequester() } //焦点
    val softKeyboard = LocalSoftwareKeyboardController.current //软键盘

    val scope = rememberCoroutineScope()

    val onAddLora = { value: String ->
        prompt.value += value
    }
    val onClickItem: (Int, Int) -> Unit = { start: Int, end: Int ->
        TextUtil.topsea("DiffusionRowPrompt: start--$start, end--$end ")
        prompt.value = prompt.value.removeRange(start, end + 1)
    }

    LaunchedEffect(key1 = onEditPrompt.value) {
        if (onEditPrompt.value) {
            delay(100) //延迟操作(关键点)
            focusRequester.requestFocus()
            softKeyboard?.show()
        }
    }

    val editingColor = if (onEditPrompt.value) {
        Pink80
    } else {
        MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        FitScreen(modifier = Modifier,
            titleComp = { ParamTitle(title = name, isPad = it) }) { modifier ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {

                LazyColumn(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp)
                        .weight(1F)
                        .height(dimensionResource(id = R.dimen.param_prompt_height))
                        .border(2.dp, editingColor, RoundedCornerShape(8.dp))
                ) {
                    item {
                        if (onEditPrompt.value) {
                            BasicTextField(
                                value = prompt.value,
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                onValueChange = { prompt.value = it },
                                textStyle = TextStyle(
                                    fontSize = TextUnit(
                                        value = 16F,
                                        type = TextUnitType.Sp
                                    )
                                ),
                                
                            )
                        } else {
                            ClickableMessage(
                                prompt = prompt,
                                onEditPrompt = onEditPrompt,
                                onClickItem = onClickItem
                            )
                        }
                    }
                }

                Column {
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        onClick = { onEditPrompt.value = false },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.r_btn_confirm))
                    }
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        onClick = { prompt.value = "" },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.r_btn_clear))
                    }
                }
            }
        }
        Column {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp, start = 8.dp)
                    .width(dimensionResource(id = R.dimen.param_title_width_max))
            ) {
                Row(
                    modifier = Modifier
                        .width(dimensionResource(id = R.dimen.param_title_width_max))
                        .height(dimensionResource(id = R.dimen.param_drop_menu))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        .clickable { menuExpanded = true },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currLoraDir,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .widthIn(max = 64.dp)
                            .align(Alignment.CenterVertically),
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = if (menuExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded, onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.6F)
                ) {
                    DropdownMenuItem(text = {
                        Text(
                            text = "Simple",
                            modifier = Modifier
                                .widthIn(min = 72.dp)
                        )
                    }, onClick = {
                        currLoraDir = "Simple"
                        menuExpanded = false
                    })
                    models.forEach {
                        DropdownMenuItem(text = {
                            Text(
                                text = it.first,
                                modifier = Modifier
                                    .widthIn(min = 72.dp)
                            )
                        }, onClick = {
                            currLoraDir = it.first
                            menuExpanded = false
                        })
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoraField(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                        .weight(1F)
                        .height(dimensionResource(id = R.dimen.param_prompt_height))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                    onClickAdd = onAddLora,
                    models = if (currLoraDir == "Simple") prompts else models.find { it.first == currLoraDir }!!.second
                )
                IconButton(
                    onClick = { scope.launch { onRefresh() } },
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Choices",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ParamRowNegPrompt(
    name: String,
    prompt: MutableState<String>,
    onRefresh: suspend () -> Unit,
) {
    val onEditPrompt = remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() } //焦点
    val softKeyboard = LocalSoftwareKeyboardController.current //软键盘

    LaunchedEffect(key1 = onEditPrompt.value) {
        if (onEditPrompt.value) {
            delay(100) //延迟操作(关键点)
            focusRequester.requestFocus()
            softKeyboard?.show()
        }
    }

    val editingColor = if (onEditPrompt.value) {
        Pink80
    } else {
        MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        FitScreen(modifier = Modifier,
            titleComp = { ParamTitle(title = name, isPad = it) }) { modifier ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                        .weight(1F)
                        .height(dimensionResource(id = R.dimen.param_prompt_height))
                        .border(2.dp, editingColor, RoundedCornerShape(8.dp))
                ) {
                    item {
                        BasicTextField(
                            value = prompt.value,
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .padding(8.dp)
                                .fillMaxSize(),
                            onValueChange = { prompt.value = it },
                            textStyle = TextStyle(
                                fontSize = TextUnit(
                                    value = 16F,
                                    type = TextUnitType.Sp
                                )
                            )
                        )
                    }
                }

                Button(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    onClick = { onEditPrompt.value = false },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(id = R.string.r_btn_confirm))
                }
            }
        }
    }
}

@Composable
fun ParamRowControlNet(
    name: String,
    cnModels: List<CNParam>,
    controlNets: SnapshotStateList<Int>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        FitScreen(modifier = Modifier,
            titleComp = { ParamTitle(title = name, isPad = it) }) { modifier ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 48.dp)
                        .weight(1F)
                        .height(dimensionResource(id = R.dimen.param_prompt_height))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                ) {
                    item {
                        Spacer(modifier = Modifier.size(4.dp))
                    }
                    items(cnModels.size) {
                        ControlNetItem(
                            cnModel = cnModels[it],
                            controlNets = controlNets,
                            )
                    }
                    item {
                        Spacer(modifier = Modifier.size(4.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoraField(
    modifier: Modifier = Modifier,
    onClickAdd: (value: String) -> Unit,
    models: List<AddablePrompt>
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            FlowRow(modifier = Modifier
                .padding(bottom = 4.dp, end = 4.dp)
                .fillMaxWidth()) {
                models.forEach {
                    if (it is LoraModel)
                        LoraItem(lora = it, onClickAdd)
                    else
                        PromptItem(
                            userPrompt = it as UserPrompt,
                            onClickAdd = onClickAdd,
                            onDelete = {},
                            onUpdate = {}
                        )
                }
            }
        }
    }

}

@Composable
fun LoraItem(
    lora: AddablePrompt,
    onClickAdd: (value: String) -> Unit,
) {
    val max = if (getWidthDp() > 400.dp) 320.dp else 240.dp
    val tmax = if (getWidthDp() > 400.dp) 170.dp else 190.dp
    if (lora is LoraModel) {
        Row(
            modifier = Modifier
                .padding(start = 4.dp, top = 4.dp)
                .widthIn(max = max)
                .height(dimensionResource(id = R.dimen.param_model_item))
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
        ) {
            Text(
                text = lora.name,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .widthIn(max = tmax)
                    .align(Alignment.CenterVertically),
                fontSize = TextUnit(value = 16F, type = TextUnitType.Sp),
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.5.dp)
                    .background(Color.Gray)
            )
            Box(modifier = Modifier
                .size(dimensionResource(id = R.dimen.param_model_item))
                .background(
                    color = MaterialTheme.colorScheme.inversePrimary,
                    shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                )
                .clickable {
                    onClickAdd("<lora:${lora.alias}:1>,")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add a Lora to prompt.",
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PromptField(
    modifier: Modifier = Modifier,
    models: List<AddablePrompt>,
    onDelete: (UserPrompt) -> Unit,
    onUpdate: (UserPrompt) -> Unit,
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            FlowRow(modifier = Modifier
                .padding(bottom = 4.dp, end = 4.dp)
                .fillMaxWidth()) {

                models.forEach {
                    PromptItem(userPrompt = it as UserPrompt, inSettings = true, onClickAdd = {}, onDelete, onUpdate)
                }
            }
        }
    }
}

@Composable
fun PromptItem(
    userPrompt: UserPrompt,
    inSettings: Boolean = false,
    onClickAdd: (value: String) -> Unit,
    onDelete: (UserPrompt) -> Unit,
    onUpdate: (UserPrompt) -> Unit,
) {
    val max = if (getWidthDp() > 400.dp) 320.dp else 240.dp
    val tmax = if (getWidthDp() > 400.dp) 170.dp else 190.dp
    val stmax = if (inSettings) tmax - 35.dp else tmax
    Row(
        modifier = Modifier
            .padding(start = 4.dp, top = 4.dp)
            .widthIn(max = max)
            .height(dimensionResource(id = R.dimen.param_model_item))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
    ) {
        Text(
            text = userPrompt.name,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .widthIn(max = stmax)
                .align(Alignment.CenterVertically),
            fontSize = TextUnit(value = 16F, type = TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.5.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
        if (inSettings) {
            Box(modifier = Modifier
                .size(dimensionResource(id = R.dimen.param_model_item))
                .background(
                    color = MaterialTheme.colorScheme.inversePrimary,
                    shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                )
                .clickable {
                    onDelete(userPrompt)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.5.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(modifier = Modifier
                .size(dimensionResource(id = R.dimen.param_model_item))
                .background(
                    color = MaterialTheme.colorScheme.inversePrimary,
                    shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                )
                .clickable {
                    onUpdate(userPrompt)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )
            }
        } else {
            Box(modifier = Modifier
                .size(dimensionResource(id = R.dimen.param_model_item))
                .background(
                    color = MaterialTheme.colorScheme.inversePrimary,
                    shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                )
                .clickable {
                    onClickAdd(userPrompt.alias)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add a Lora to prompt.",
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ControlNetItem(
    cnModel: CNParam,
    controlNets: SnapshotStateList<Int>,
) {
    val max = if (getWidthDp() > 400.dp) 320.dp else 280.dp

    val checkState = remember {
        mutableStateOf(controlNets.contains(cnModel.id))
    }
    TextUtil.topsea("ControlNetItem: ${controlNets.toIntArray().contentToString()}")

    Row(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .width(max)
            .height(dimensionResource(id = R.dimen.param_model_item))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
    ) {
        Text(
            text = cnModel.cn_name,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .weight(1f)
                .align(Alignment.CenterVertically),
            fontSize = TextUnit(value = 16F, type = TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
        Box(modifier = Modifier
            .size(dimensionResource(id = R.dimen.param_model_item))
            .background(
                color = if (checkState.value) MaterialTheme.colorScheme.inversePrimary else Color.LightGray,
                shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
            )
        ) {
            Checkbox(
                checked = checkState.value,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center),
                onCheckedChange = {
                    checkState.value = it
                    if (it)
                        controlNets.add(cnModel.id)
                    else
                        controlNets.remove(cnModel.id)
                },
                colors = CheckboxDefaults.colors(uncheckedColor = Color.White, checkedColor = MaterialTheme.colorScheme.inversePrimary),
                interactionSource = remember { MutableInteractionSource() }
            )
        }
    }
}

@Composable
fun ParamTitle(
    modifier: Modifier = Modifier,
    boldTitle: Boolean = true,
    title: String,
    isPad: Boolean,
) {
    Text(
        text = title,
        modifier = modifier
            .padding(start = 4.dp),
        fontWeight = if (boldTitle) FontWeight.Bold else FontWeight.Thin,
        fontSize = 16.sp,
        maxLines = 2
    )
}

@Composable
fun FitScreen(
    modifier: Modifier = Modifier,
    titleComp: @Composable() ((isPad: Boolean) -> Unit),
    contentComp: @Composable() ((Modifier) -> Unit),
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (maxWidth < 400.dp) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .heightIn(min = dimensionResource(id = R.dimen.param_phone_item_height))
                    .fillMaxWidth()
            ) {
                titleComp(false)
                contentComp(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(min = dimensionResource(id = R.dimen.param_pad_item_height))
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                titleComp(true)
                contentComp(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun ParamRowRadio(
    name: String,
    curerChoice: MutableState<Int>,
    radioList: List<Pair<Int, String>>
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val max = maxWidth
        Row(
            modifier = Modifier
                .padding(start = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ParamTitle(title = name, isPad = true)
            if (max < 400.dp) {
                Column(modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f), horizontalAlignment = Alignment.Start) {
                    radioList.forEach {
                        Row(
                            modifier = Modifier.height(28.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = curerChoice.value == it.first,
                                onClick = { curerChoice.value = it.first })
                            Text(text = it.second, modifier = Modifier.offset(x = (-4).dp))
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen.param_pad_item_height))
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    radioList.forEach {
                        Row(
                            modifier = Modifier.height(28.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = curerChoice.value == it.first,
                                onClick = { curerChoice.value = it.first })
                            Text(text = it.second, modifier = Modifier.offset(x = (-4).dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParamRowCheck(
    name: String,
    check: MutableState<Boolean>,
) {
    Row(
        modifier = Modifier
            .padding(start = 4.dp, end = 16.dp)
            .height(dimensionResource(id = R.dimen.param_pad_item_height))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ParamTitle(title = name, isPad = true)
        RoundedCheck(Modifier.size(42.dp), check)
    }
}