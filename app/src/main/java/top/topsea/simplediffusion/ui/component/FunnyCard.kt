package top.topsea.simplediffusion.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import top.topsea.simplediffusion.R

@Composable
fun <T> FunnyCard(
    modifier: Modifier = Modifier,
    name: String,
    offset: Dp,
    slidingCard: MutableState<Boolean>,
    expandingCard: MutableState<Boolean>,
    onOpenCard: () -> Unit,
    onAddParam: () -> Unit,
    params: List<T>,
    content: @Composable () (T) -> Unit
) {
    val context = LocalContext.current
    val wp = context.resources.displayMetrics.widthPixels
    val hp = context.resources.displayMetrics.heightPixels

    val hdp = with(LocalDensity.current) { hp.toDp() }
    val wdp = with(LocalDensity.current) { wp.toDp() - 32.dp }

    val slidCard by animateDpAsState(
        targetValue = if (slidingCard.value) 0.dp else wdp,
        // Configure the animation duration and easing.
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    )
    val expandCard by animateDpAsState(
        targetValue = if (expandingCard.value) hdp else 120.dp,
        // Configure the animation duration and easing.
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    )
    val offsetCard by animateDpAsState(
        targetValue = if (expandingCard.value) 0.dp else offset,
        // Configure the animation duration and easing.
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    )

    LaunchedEffect(key1 = slidingCard.value) {
        if (slidingCard.value) {
            delay(300)
            expandingCard.value = true
        }
    }
    LaunchedEffect(key1 = expandingCard.value) {
        if (!expandingCard.value) {
            delay(300)
            slidingCard.value = false
        }
    }

    val cardShape = RoundedCornerShape(
        bottomStart = if (expandingCard.value) 16.dp else 0.dp,
        bottomEnd = 16.dp,
        topEnd = 16.dp,
    )


    Surface(
        shadowElevation = 16.dp,
        tonalElevation = 16.dp,
        modifier = modifier
            .padding(top = 16.dp, end = 36.dp)
            .height(expandCard)
            .offset(x = slidCard, y = offsetCard),
        color = Color.Transparent
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .height(dimensionResource(id = R.dimen.param_card_pin))
                    .width(32.dp)
                    .clickable {
                        onOpenCard()
                    }
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    )
            ) {
                Icon(
                    imageVector = if (expandingCard.value) Icons.Default.KeyboardArrowRight else Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Open Card",
                    modifier = Modifier
                        .size(32.dp),
                    tint = Color.White
                )

                Text(text = name, textAlign = TextAlign.Start, color = Color.White, modifier = Modifier
                    .offset(y = 48.dp)
                    .requiredWidth(dimensionResource(id = R.dimen.param_card_pin))
                    .rotate(90f))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background, cardShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, cardShape)
                    .clip(cardShape)
            ) {
                params.forEach { param ->
                    item {
                        content(param)
                    }
                }
                item{ AddParam(onAddParam) }
            }
        }
    }
}

@Composable
fun <T> FunnyCardBottom(
    modifier: Modifier = Modifier,
    name: String = "Name",
    offset: Dp,
    slidingCard: MutableState<Boolean>,
    expandingCard: MutableState<Boolean>,
    onOpenCard: () -> Unit,
    onAddParam: () -> Unit,
    params: List<T>,
    content: @Composable () (T) -> Unit
) {
    val context = LocalContext.current
    val wp = context.resources.displayMetrics.widthPixels
    var hp by remember { mutableStateOf(10000f) }

    val hdp = with(LocalDensity.current) { hp.toDp() + 4.dp }
    val wdp = with(LocalDensity.current) { wp.toDp() }

    val slidCard by animateDpAsState(
        targetValue = if (slidingCard.value) 0.dp else hdp,
        // Configure the animation duration and easing.
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    )
    val expandCard by animateDpAsState(
        targetValue = if (expandingCard.value) wdp else dimensionResource(id = R.dimen.param_card_pin),
        // Configure the animation duration and easing.
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    )
    val offsetCard by animateDpAsState(
        targetValue = if (expandingCard.value) 0.dp else offset,
        // Configure the animation duration and easing.
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    )

    val cardShape = RoundedCornerShape(
        bottomStart = 16.dp,
        bottomEnd = 16.dp,
        topEnd = if (expandingCard.value) 16.dp else 0.dp,
    )

    LaunchedEffect(key1 = slidingCard.value) {
        if (slidingCard.value) {
            delay(300)
            expandingCard.value = true
        }
    }
    LaunchedEffect(key1 = expandingCard.value) {
        if (!expandingCard.value) {
            delay(300)
            slidingCard.value = false
        }
    }

    Surface(
        color = Color.Transparent,
        shadowElevation = 16.dp,
        modifier = modifier
            .padding(bottom = 36.dp, top = 8.dp)
            .width(expandCard)
            .fillMaxHeight()
            .offset(y = slidCard, x = offsetCard),
    ) {
        Canvas(modifier = Modifier
            .fillMaxHeight()
            .width(0.dp)) { hp = size.height}

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .width(dimensionResource(id = R.dimen.param_card_pin))
                    .height(32.dp)
                    .clickable {
                        onOpenCard()
                    }
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (expandingCard.value) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "Open Card",
                    modifier = Modifier
                        .size(32.dp),
                    tint = Color.White
                )

                Text(text = name, textAlign = TextAlign.Start, color = Color.White, modifier = Modifier.offset(x = (-6).dp))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(Color.White, cardShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, cardShape)
                    .clip(cardShape)
            ) {
                params.forEach { param ->
                    item {
                        content(param)
                    }
                }
                item{ AddParam(onAddParam) }
            }
        }
    }
}

@Composable
fun AddParam(
    onAddParam: () -> Unit
) {
    val stroke = Stroke(
        width = 5f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
    Box(
        modifier = Modifier
            .padding(16.dp)
            .height(72.dp)
            .fillMaxWidth()
            .clickable { onAddParam() }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(color = Color.Gray, style = stroke, cornerRadius = CornerRadius(50F, 50F))
        }
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add new txt2img request param",
            tint = Color.Gray,
            modifier = Modifier
                .size(32.dp)
                .align(
                    Alignment.Center
                )
        )
    }
}