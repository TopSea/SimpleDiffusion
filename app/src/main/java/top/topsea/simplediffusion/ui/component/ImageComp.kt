package top.topsea.simplediffusion.ui.component

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.param.ImageData
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.param.TaskParam
import top.topsea.simplediffusion.data.state.GenerateState
import top.topsea.simplediffusion.data.viewmodel.BasicViewModel
import top.topsea.simplediffusion.event.GenerateEvent
import top.topsea.simplediffusion.event.ImageEvent
import top.topsea.simplediffusion.event.ParamEvent
import top.topsea.simplediffusion.ui.theme.LittleTrans
import top.topsea.simplediffusion.util.FileUtil
import top.topsea.simplediffusion.util.getWidthDp
import java.io.File


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageInDisplay(
    fromCamera: Boolean = false,
    image: ImageData,
    paramViewModel: BasicViewModel?,
    onImgEvent: (ImageEvent) -> Unit,
) {
    val context = LocalContext.current
    val wdp = getWidthDp().times(if (fromCamera) 1f else 0.9f)

    var showInfo by remember { mutableStateOf(false) }

    val permissionBQ = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    )
    val path = File(context.filesDir, image.imageName).absolutePath

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .width(wdp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(path)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.placeholder_32),
            contentDescription = "Display image.",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        )

        if (!fromCamera) {
            Box(modifier = Modifier.fillMaxSize()) {
                // 展示图片信息
                if (showInfo) {
                    Row(
                        modifier = Modifier
                            .padding(32.dp)
                            .background(LittleTrans, RoundedCornerShape(8.dp))
                            .align(Alignment.Center)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = image.info,
                            color = Color.White,
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(end = 16.dp, top = 8.dp)
                        .background(LittleTrans, RoundedCornerShape(8.dp))
                        .align(Alignment.TopEnd)
                ) {
                    IconButton(onClick = { showInfo = !showInfo }) {
                        Icon(
                            imageVector = Icons.Default.Info, contentDescription = "Image information",
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(LittleTrans, RoundedCornerShape(8.dp))
                        .align(Alignment.CenterEnd)
                ) {
                    ShowingIcon(
                        icon = Icons.Default.Delete,
                        onclick = {
                            onImgEvent(ImageEvent.DeleteImage(image, context))
                        }
                    )
                    ShowingIcon(
                        icon = painterResource(id = R.drawable.download_32),
                        onclick = {
                            if (!permissionBQ.allPermissionsGranted) {
                                permissionBQ.launchMultiplePermissionRequest()
                            } else {
                                onImgEvent(ImageEvent.DownloadImage(image, context))
                            }
                        })
                    ShowingIcon(
                        icon = Icons.Default.Favorite,
                        onclick = {
                            image.isFavorite.value = !image.isFavorite.value
                            onImgEvent(ImageEvent.LikeImage(image))
                        },
                        tint = if (image.isFavorite.value) Color.Red else Color.White
                    )
                    if (paramViewModel != null) { // 拍照展示图片时不显示
                        val paramState by paramViewModel.paramState.collectAsState()
                        val param = paramState.currParam
                        // 只在图生图时显示
                        if (param is ImgParam)
                            ShowingIcon(
                                icon = painterResource(id = R.drawable.photo_plus),
                                onclick = {
                                    paramViewModel.paramEvent(ParamEvent.AddImage(FileUtil.imageName2Base64(context, image.imageName)))
                                    Toast.makeText(context, context.getString(R.string.t_image_added), Toast.LENGTH_SHORT).show()
                                },
                            )
                    }
                    ShowingIcon(
                        icon = Icons.Default.Share,
                        onclick = { onImgEvent(ImageEvent.ShareImage(image, context)) })
                }
            }
        }
    }
}

@Composable
fun ShowingIcon(
    onclick: () -> Unit,
    icon: ImageVector,
    tint: Color = Color.White
) {
    IconButton(onClick = { onclick() }) {
        Icon(
            imageVector = icon, contentDescription = "", tint = tint, modifier = Modifier
                .size(32.dp)
        )
    }
}

@Composable
fun ShowingIcon(
    onclick: () -> Unit,
    icon: Painter,
    tint: Color = Color.White,
) {
    IconButton(onClick = { onclick() }) {
        Icon(
            painter = icon, contentDescription = "", tint = tint, modifier = Modifier
                .size(32.dp)
        )
    }
}

@Composable
fun TaskInDisplay(
    fromCamera: Boolean = false,
    gen: TaskParam,
    genState: GenerateState,
    generateEvent: (GenerateEvent) -> Unit,
    index: Int,
) {
    val context = LocalContext.current
    val wdp = getWidthDp().times(if (fromCamera) 1f else 0.9f)
    val boxColor = MaterialTheme.colorScheme.primary

    val img = if (gen.image != null) {
        File(context.filesDir, gen.image.imageName).absolutePath
    } else {
        ""
    }

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .width(wdp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(img)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder_32),
                contentDescription = "Display image.",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            )

            Box(
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp)
                    .size(88.dp)
                    .align(Alignment.Center)
                    .background(color = LittleTrans, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        generateEvent(GenerateEvent.RemoveTask(gen, index == 0))
                    },
                contentAlignment = Alignment.Center
            ) {
                // 生成进度
                CircularProgressIndicator(
                    progress = if (index == 0) genState.generatingProgress else 0f,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                    color = boxColor,
                    strokeWidth = 4.dp
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .size(36.dp),
                        imageVector = Icons.Default.Clear, contentDescription = "Stop",
                        tint = boxColor
                    )
                    Text(
                        text = "${"%.2f".format(if (index == 0) genState.generatingProgress * 100F else 0F)}%",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorTaskInDisplay(
    fromCamera: Boolean = false,
    task: TaskParam,
    generateEvent: (GenerateEvent) -> Unit,
) {
    val context = LocalContext.current
    val wdp = getWidthDp().times(if (fromCamera) 1f else 0.9f)

    val img = if (task.image != null) {
        File(context.filesDir, task.image.imageName).absolutePath
    } else {
        ""
    }

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .width(wdp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (img.isNotEmpty())
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(img)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.placeholder_32),
                    contentDescription = "Display image.",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                )
            else
                Image(
                    painter = painterResource(id = R.drawable.placeholder_32),
                    contentDescription = "Display image.",
                    modifier = Modifier.fillMaxSize()
                )


            Box(modifier = Modifier.fillMaxSize()) {
                // 展示图片错误生成的信息
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .background(LittleTrans, RoundedCornerShape(8.dp))
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "",
                        tint = Color.Red,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .size(32.dp)
                    )
                    Text(
                        text = task.genInfo,
                        color = Color.White,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }

                // 错误的处理：删除或者重新开始
                Column(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(LittleTrans, RoundedCornerShape(8.dp))
                        .align(Alignment.CenterEnd)
                ) {
                    ShowingIcon(
                        icon = Icons.Default.Delete,
                        onclick = {
                            generateEvent(GenerateEvent.RemoveTask(task))
                        }
                    )
                    ShowingIcon(
                        icon = painterResource(id = R.drawable.task_restart),
                        onclick = {
                            generateEvent(GenerateEvent.RefreshTask(task))
                        })
                }
            }
        }
    }
}

@Composable
fun ImageInGrid(
    modifier: Modifier,
    imgName: String,
    longPressed: Boolean = false,
    selected: Boolean = false,
) {
    val context = LocalContext.current
    val path = File(context.filesDir, imgName).absolutePath

    Box(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(path)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.placeholder_32),
            contentDescription = "Display image.",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (longPressed)
            Icon(
                painter = if (selected) painterResource(id = R.drawable.rounded_check) else painterResource(
                    id = R.drawable.rounded_uncheck),
                contentDescription = "",
                modifier = Modifier.padding(8.dp).align(Alignment.BottomEnd),
                tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun TaskInGrid(
    modifier: Modifier,
    gen: TaskParam,
    genState: GenerateState,
    isGenThis: Boolean,
) {
    val context = LocalContext.current
    val img = if (gen.image != null) {
        File(context.filesDir, gen.image.imageName).absolutePath
    } else {
        ""
    }
    val boxColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
    ) {
        // 拍摄的图片或者占位图
        if (img.isNotEmpty())
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(img)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder_32),
                contentDescription = "Display image.",
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        else
            Image(
                painter = painterResource(id = R.drawable.placeholder_32),
                contentDescription = "Display image.",
                modifier = Modifier.fillMaxSize()
            )
        // 生成进度
        CircularProgressIndicator(
            progress = if (isGenThis) genState.generatingProgress else 0f,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            color = boxColor,
            strokeWidth = 4.dp
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(36.dp),
                imageVector = Icons.Default.Clear, contentDescription = "Stop",
                tint = boxColor
            )
            Text(text = "${"%.2f".format(if (isGenThis) genState.generatingProgress * 100F else 0F)}%", fontSize = 14.sp)
        }
    }
}

@Composable
fun ErrorTaskInGrid(
    modifier: Modifier,
    task: TaskParam,
) {
    val context = LocalContext.current
    val img = if (task.image != null) {
        File(context.filesDir, task.image.imageName).absolutePath
    } else {
        ""
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 拍摄的图片或者占位图
        if (img.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(img)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder_32),
                contentDescription = "Display image.",
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
        else {
            Image(
                painter = painterResource(id = R.drawable.placeholder_32),
                contentDescription = "Display image.",
                modifier = Modifier.fillMaxSize()
            )
        }
        // 错误图标
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "",
            tint = Color.Red,
            modifier = Modifier.size(42.dp)
        )
    }
}
