package top.topsea.simplediffusion.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import top.topsea.simplediffusion.data.param.ImageData
import top.topsea.simplediffusion.data.state.GenerateState
import top.topsea.simplediffusion.data.state.ImgDataState
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.ImgDataViewModel
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.event.GenerateEvent
import top.topsea.simplediffusion.util.TaskQueue
import top.topsea.simplediffusion.data.param.TaskParam
import top.topsea.simplediffusion.data.viewmodel.BasicViewModel
import top.topsea.simplediffusion.util.getWidthDp


@Composable
fun DisplayInGrid(
    modifier: Modifier = Modifier,
    imageState: ImgDataState,
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

        itemsIndexed(
            items = images,
            key = { _, img ->
                img.index
            }
        ) { index, img ->
            ImageInGrid(
                modifier = Modifier
                    .height(wdp)
                    .clickable {
                        uiViewModel.onEvent(UIEvent.DisplayImg(index))
                    },
                imgName = img.imageName
            )
        }
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
    val context = LocalContext.current
    val wp = context.resources.displayMetrics.widthPixels
    val pdp = with(LocalDensity.current) { (wp * 0.08F).toDp() }

    val tasksState by taskQueue.tasksState.collectAsState()
    val genState by taskQueue.genState.collectAsState()
    val state = rememberLazyListState()

    LaunchedEffect(key1 = tasksState.tasks.size, key2 = images.size) {
        val image = if (images.lastIndex >= 0) images.lastIndex else 0
        val errorTask = if (tasksState.errorTasks.lastIndex > 0) tasksState.errorTasks.lastIndex else 0
        val task = if (tasksState.tasks.lastIndex > 0) tasksState.tasks.lastIndex else 1
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
                items = tasksState.tasks
            ) { index, task ->
                TaskInDisplay(true, gen = task, genState = genState, generateEvent = taskQueue::generateEvent, index = index)
            }
        }
    }
}