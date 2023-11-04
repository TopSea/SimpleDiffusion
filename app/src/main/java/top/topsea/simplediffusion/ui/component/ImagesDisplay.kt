package top.topsea.simplediffusion.ui.component

import android.icu.util.Calendar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.param.ImageData
import top.topsea.simplediffusion.data.param.TaskParam
import top.topsea.simplediffusion.data.state.GenerateState
import top.topsea.simplediffusion.data.state.ImgDataState
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.BasicViewModel
import top.topsea.simplediffusion.data.viewmodel.ImgDataViewModel
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.event.GenerateEvent
import top.topsea.simplediffusion.event.ImageEvent
import top.topsea.simplediffusion.util.TaskQueue
import top.topsea.simplediffusion.util.getWidthDp


@Composable
fun DisplayInGrid(
    modifier: Modifier = Modifier,
    imageState: ImgDataState,
    imageEvent: (ImageEvent) -> Unit,
    selectedID: List<Int>,
    uiViewModel: UIViewModel,
    errorTasks: List<TaskParam>,
    tasks: List<TaskParam>,
    genState: GenerateState,
    generateEvent: (GenerateEvent) -> Unit
) {
    val wdp = getWidthDp() / 4

    val listState = rememberLazyGridState()
    val images = imageState.images

    LazyVerticalGrid(
        modifier = modifier,
        state = listState,
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        // 生成任务
        item(
            span = {
                // 占据最大宽度
                GridItemSpan(maxLineSpan) }
        ) {
            if (errorTasks.isNotEmpty() || tasks.isNotEmpty())
                GridHeader(headStr = "Task")
        }

        itemsIndexed(items = errorTasks) { index, task ->
            ErrorTaskInGrid(
                modifier = Modifier
                    .height(wdp)
                    .clickable {
                        uiViewModel.onEvent(UIEvent.DisplayImg(index))
                    },
                task = task,
            )
        }
        itemsIndexed(items = tasks) { index, task ->
            TaskInGrid(
                modifier = Modifier
                    .height(wdp)
                    .clickable {
                        generateEvent(GenerateEvent.RemoveTask(task, index == 0))
                    },
                gen = task,
                genState = genState,
                isGenThis = index == 0,
            )
        }

        // 图片
        images.forEachIndexed { index, image ->
            if (index == 0)
                item(
                    span = {
                        // 占据最大宽度
                        GridItemSpan(maxLineSpan) }
                ) {
                    val toCalendar: Calendar = Calendar.getInstance()
                    toCalendar.time = images[0].genDate
                    toCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    toCalendar.set(Calendar.MINUTE, 0)
                    toCalendar.set(Calendar.SECOND, 0)
                    toCalendar.set(Calendar.MILLISECOND, 0)
                    FuncHeader(headStr = image.genDate.toString(), longPressed = uiViewModel.longPressImage, uiViewModel = uiViewModel) {
                        imageEvent(ImageEvent.SelectByDay(toCalendar.time.time / 1000) {
                            val label = image.genDate.toString()
                            if (it)
                                uiViewModel.fullSelected.remove(label)
                            else
                                uiViewModel.fullSelected.add(label)
                        })
                    }
                }
            else {
                val preDate = images[index - 1].genDate
                val nowDate = images[index].genDate

                val fromCalendar: Calendar = Calendar.getInstance()
                fromCalendar.time = preDate
                fromCalendar.set(Calendar.HOUR_OF_DAY, 0)
                fromCalendar.set(Calendar.MINUTE, 0)
                fromCalendar.set(Calendar.SECOND, 0)
                fromCalendar.set(Calendar.MILLISECOND, 0)
                val toCalendar: Calendar = Calendar.getInstance()
                toCalendar.time = nowDate
                toCalendar.set(Calendar.HOUR_OF_DAY, 0)
                toCalendar.set(Calendar.MINUTE, 0)
                toCalendar.set(Calendar.SECOND, 0)
                toCalendar.set(Calendar.MILLISECOND, 0)

                val days = (toCalendar.timeInMillis - fromCalendar.timeInMillis) / (86400000L)
                if (days < 0L) { // 不在同一天之内
                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        FuncHeader(headStr = image.genDate.toString(), longPressed = uiViewModel.longPressImage, uiViewModel = uiViewModel) {
                            imageEvent(ImageEvent.SelectByDay(toCalendar.time.time / 1000) {
                                val label = image.genDate.toString()
                                if (it)
                                    uiViewModel.fullSelected.remove(label)
                                else
                                    uiViewModel.fullSelected.add(label)
                            })
                        }
                    }
                }
            }

            item(key = image.index) {
                ImageInGrid(
                    modifier = Modifier
                        .height(wdp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    if (uiViewModel.longPressImage) {
                                        imageEvent(ImageEvent.Select(image.index, image.genDate) {
                                            val label = image.genDate.toString()
                                            if (it)
                                                uiViewModel.fullSelected.remove(label)
                                            else
                                                uiViewModel.fullSelected.add(label)
                                        })
                                    } else {
                                        uiViewModel.onEvent(UIEvent.DisplayImg(index))
                                    }
                                },
                                onDoubleTap = {
                                },
                                onPress = {
                                },
                                onLongPress = {
                                    if (!uiViewModel.longPressImage) {
                                        uiViewModel.onEvent(UIEvent.LongPressImage(true))
                                        imageEvent(ImageEvent.Select(image.index, image.genDate) {
                                            val label = image.genDate.toString()
                                            if (it)
                                                uiViewModel.fullSelected.remove(label)
                                            else
                                                uiViewModel.fullSelected.add(label)
                                        })
                                    }
                                }
                            )
                        },
                    imgName = image.imageName,
                    longPressed = uiViewModel.longPressImage,
                    selected = selectedID.contains(image.index)
                )
            }
        }
    }
}


@Composable
fun GridHeader(headStr: String) {
    Row(
        modifier = Modifier
            .height(42.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GridHeaderLine()
        Text(
            text = headStr,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        GridHeaderLine()
    }
}
@Composable
fun FuncHeader(
    headStr: String,
    longPressed: Boolean = false,
    uiViewModel: UIViewModel,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(42.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GridHeaderLine()
        Text(
            text = headStr,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        if (longPressed) FuncHeaderLine(headStr, uiViewModel, onClick) else GridHeaderLine()
    }
}

@Composable
private fun RowScope.GridHeaderLine() {
    Divider(
        modifier = Modifier
            .weight(1f),
        color = MaterialTheme.colorScheme.primary,
        thickness = 1.6.dp
    )
}

@Composable
private fun RowScope.FuncHeaderLine(
    label: String,
    uiViewModel: UIViewModel,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .weight(1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier
                .weight(1f),
            color = MaterialTheme.colorScheme.primary,
            thickness = 1.6.dp
        )

        ShowingIcon(
            icon = painterResource(id = if (uiViewModel.fullSelected.contains(label)) R.drawable.rounded_check else R.drawable.rounded_uncheck),
            onclick = {
                onClick()
            },
            tint = MaterialTheme.colorScheme.primary
        )
        Divider(
            modifier = Modifier
                .width(16.dp),
            color = MaterialTheme.colorScheme.primary,
            thickness = 1.6.dp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayImages(
    modifier: Modifier,
    uiViewModel: UIViewModel,
    imgDataViewModel: ImgDataViewModel,
    paramViewModel: BasicViewModel,

    tasks: List<TaskParam>,
    errorTasks: List<TaskParam>,
    genState: GenerateState,
    generateEvent: (GenerateEvent) -> Unit
) {
    val context = LocalContext.current
    val wp = context.resources.displayMetrics.widthPixels
    val pdp = with(LocalDensity.current) { (wp * 0.08F).toDp() }
    val imageState by imgDataViewModel.state.collectAsState()
    val images = imageState.images

    val state = rememberLazyListState(initialFirstVisibleItemIndex = uiViewModel.displayingImg)

// If you'd like to customize either the snap behavior or the layout provider
    val snappingLayout = remember(state) { SnapLayoutInfoProvider(state) }
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

    Box(modifier = modifier) {
        LazyRow(
            modifier = Modifier
                .fillMaxHeight(0.95F)
                .align(Alignment.Center),
            state = state,
            flingBehavior = flingBehavior
        ) {
            items(
                items = errorTasks
            ) { task ->
                ErrorTaskInDisplay(task = task, generateEvent = generateEvent)
            }
            itemsIndexed(
                items = tasks
            ) { index, task ->
                TaskInDisplay(gen = task, genState = genState, generateEvent = generateEvent, index = index)
            }
            items(
                items = images,
                key = { img ->
                    img.index
                }
            ) {img ->
                ImageInDisplay(false, img, paramViewModel, onImgEvent = imgDataViewModel::onEvent)
            }
            item { Spacer(modifier = Modifier.size(pdp)) }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayImagesInCam(
    modifier: Modifier,
    images: List<ImageData>,
    imgViewModel: ImgDataViewModel,
    normalViewModel: NormalViewModel,
    taskQueue: TaskQueue,
) {
    val tasksState by taskQueue.tasksState.collectAsState()
    val genState by taskQueue.genState.collectAsState()
    val state = rememberLazyListState()

    LaunchedEffect(key1 = taskQueue.tasks.size, key2 = images.size) {
        val image = if (images.lastIndex >= 0) images.lastIndex else 0
        val errorTask = if (tasksState.errorTasks.lastIndex > 0) tasksState.errorTasks.lastIndex else 0
        val task = if (taskQueue.tasks.lastIndex > 0) taskQueue.tasks.lastIndex else 1
        state.animateScrollToItem(image + errorTask + task)
    }

// If you'd like to customize either the snap behavior or the layout provider
    val snappingLayout = remember(state) { SnapLayoutInfoProvider(state) }
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

    Box(
        modifier = modifier.padding(1.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxHeight(0.95F)
                .align(Alignment.Center),
            state = state,
            flingBehavior = flingBehavior,
        ) {
            items(
                items = images,
                key = { img ->
                    img.imageName
                }
            ) {img ->
                ImageInDisplay(true, img, null) {}
            }
            itemsIndexed(
                items = tasksState.errorTasks
            ) { _, task ->
                ErrorTaskInDisplay(true, task = task, generateEvent = taskQueue::generateEvent)
            }
            itemsIndexed(
                items = taskQueue.tasks
            ) { index, task ->
                TaskInDisplay(true, gen = task, genState = genState, generateEvent = taskQueue::generateEvent, index = index)
            }
        }
    }
}