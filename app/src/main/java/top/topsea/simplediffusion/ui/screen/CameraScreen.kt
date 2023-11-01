package top.topsea.simplediffusion.ui.screen

import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.param.ImageData
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.BasicViewModel
import top.topsea.simplediffusion.data.viewmodel.ImgDataViewModel
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.event.TaskListEvent
import top.topsea.simplediffusion.ui.component.DisplayImagesInCam
import top.topsea.simplediffusion.util.TaskQueue
import top.topsea.simplediffusion.util.FileUtil
import top.topsea.simplediffusion.util.TextUtil
import top.topsea.simplediffusion.util.getDpSize
import java.io.File
import java.sql.Date

val sizes by lazy {
    listOf(
        Size(512, 512),
        Size(512, 768),
        Size(512, 1024),
        Size(768, 1024),
        Size(1024, 1024),
        Size(720, 1280),
        Size(1080, 1920),
    )
}

@Composable
fun CameraScreen(
    imgViewModel: ImgDataViewModel,
    paramViewModel: BasicViewModel,
    normalViewModel: NormalViewModel,
    uiViewModel: UIViewModel,
    taskQueue: TaskQueue,
) {
    val context = LocalContext.current
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val cameraProvider = cameraProviderFuture.get()
    val currSize = remember { mutableStateOf(sizes[0]) }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = Color.Black,
        darkIcons = false
    )

    CameraView(
        modifier = Modifier,
        cameraProvider,
        currSize = currSize,
        imgViewModel = imgViewModel,
        paramViewModel = paramViewModel,
        normalViewModel = normalViewModel,
        uiViewModel = uiViewModel,
        taskQueue = taskQueue
    )

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider.unbindAll()
        }
    }
}

@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    cameraProvider: ProcessCameraProvider,
    focusOnTap: Boolean = true,
    currSize: MutableState<Size>,
    imgViewModel: ImgDataViewModel,
    paramViewModel: BasicViewModel,
    normalViewModel: NormalViewModel,
    uiViewModel: UIViewModel,
    taskQueue: TaskQueue,
) {
    val camIndex = remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageList: SnapshotStateList<ImageData> = remember { taskQueue.capGenImgList }

    val context = LocalContext.current

    val previewView = remember { PreviewView(context) }
    previewView.scaleType = PreviewView.ScaleType.FILL_END
    val lifecycleOwner = LocalLifecycleOwner.current

    val imageCapture = ImageCapture.Builder()
//        .setTargetRotation(previewView.display.rotation)
        .setTargetResolution(currSize.value)
        .build()

    val preview : androidx.camera.core.Preview = androidx.camera.core.Preview.Builder()
        .setTargetResolution(currSize.value)
        .build()

    val cameraSelector : CameraSelector = CameraSelector.Builder()
        .requireLensFacing(camIndex.value)
        .build()

    preview.setSurfaceProvider(previewView.surfaceProvider)

    val camera = cameraProvider.let {
            it.unbindAll()
            it.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                imageCapture,
                preview
            )
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (!uiViewModel.displaying) {
                AndroidView(
                    modifier = Modifier
                        .size(getDpSize(size = currSize.value))
                        .pointerInput(camera, focusOnTap) {
                            if (!focusOnTap) return@pointerInput
                            detectTapGestures {
                                val meteringPointFactory = SurfaceOrientedMeteringPointFactory(
                                    size.width.toFloat(),
                                    size.height.toFloat()
                                )

                                // 点击屏幕聚焦
                                val meteringAction = FocusMeteringAction
                                    .Builder(
                                        meteringPointFactory.createPoint(it.x, it.y),
                                        FocusMeteringAction.FLAG_AF
                                    )
                                    .disableAutoCancel()
                                    .build()

                                camera.cameraControl.startFocusAndMetering(meteringAction)
                            }
                        },
                    factory = {
                        previewView
                    },
                )
            } else {
                DisplayImagesInCam(
                    modifier = Modifier.fillMaxSize(),
                    images = imageList,
                    imgViewModel = imgViewModel,
                    normalViewModel = normalViewModel,
                    taskQueue = taskQueue,
                )
            }
        }

        CamBottomBar(
            currSize = currSize,
            imageCapture = imageCapture,
            camIndex = camIndex,
            paramViewModel = paramViewModel,
            uiViewModel = uiViewModel,
            taskQueue = taskQueue,
        )
    }
}

@Composable
fun CamBottomBar(
    currSize: MutableState<Size>,
    imageCapture: ImageCapture,
    camIndex: MutableState<Int>,
    paramViewModel: BasicViewModel,
    uiViewModel: UIViewModel,
    taskQueue: TaskQueue,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var path by remember { mutableStateOf("") }

    val imageList: SnapshotStateList<ImageData> = remember { taskQueue.capGenImgList }

    val tasksState by taskQueue.tasksState.collectAsState()
    val paramState by paramViewModel.paramState.collectAsState()
    val param = paramState.currParam
    val currParam = param as ImgParam

    Column(modifier = Modifier.fillMaxWidth()) {
        Divider(color = Color.LightGray, thickness = 0.3.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Black)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            sizes.forEachIndexed { _, size ->
                Row(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .width(dimensionResource(id = R.dimen.c_size_width)),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currSize.value = size
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${size.width}x${size.height}",
                            color = if (currSize.value == size) Color.White else Color.LightGray,
                            fontWeight = FontWeight.Bold
                        )
                        if (currSize.value == size)
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .size(8.dp)
                                    .background(
                                        Color.White,
                                        CircleShape
                                    )
                            )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .height(110.dp)
                .fillMaxWidth()
                .background(Color.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                modifier = Modifier.size(54.dp)
            ) {
                // 缩略图
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(path)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.placeholder_32),
                    contentDescription = "Display image.",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color.Black)
                        .clickable {
                            if (imageList.isNotEmpty() || taskQueue.tasks.isNotEmpty())
                                uiViewModel.onEvent(UIEvent.DisplayImg(-1))
                        },
                )
                // 待生成的图片
                Text(
                    text = "${taskQueue.tasks.size}",
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.BottomEnd),
                    color = Color.Red
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable {
                        // 在展示图片时不允许拍照
                        if (!uiViewModel.displaying) {
                            val curr = "${System.currentTimeMillis()}.png"
                            val tmpImg = File(context.filesDir, curr)

                            // 拍摄后添加到生成队列
                            val outputFileOptions = ImageCapture.OutputFileOptions
                                .Builder(tmpImg)
                                .build()
                            imageCapture.takePicture(
                                outputFileOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onError(error: ImageCaptureException) {
                                        TextUtil.topsea("onError: ${error.printStackTrace()}")
                                    }

                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        FileUtil.rotateImage(
                                            FileUtil.readPictureDegree(tmpImg),
                                            context,
                                            curr,
                                            camIndex.value == CameraSelector.LENS_FACING_FRONT
                                        )
                                        TextUtil.topsea(
                                            "onImageSaved: ${outputFileResults.savedUri}",
                                            Log.ERROR
                                        )
                                        val capImg = ImageData(
                                            imageName = curr,
                                            isFavorite = mutableStateOf(false), info = "",
                                            genDate = Date(System.currentTimeMillis())
                                        )
                                        path = File(context.filesDir, capImg.imageName).absolutePath

                                        val trueParam = ImgParam(
                                            image = mutableStateOf(
                                                FileUtil.imageName2Base64(
                                                    context,
                                                    curr
                                                )
                                            ),
                                            denoising_strength = currParam.denoising_strength,
                                            defaultPrompt = currParam.defaultPrompt,
                                            defaultNegPrompt = currParam.defaultNegPrompt,
                                            width = currSize.value.width,
                                            height = currSize.value.height,
                                            steps = currParam.steps,
                                            cfgScale = currParam.cfgScale,
                                            sampler_index = currParam.sampler_index,
                                            resize_mode = currParam.resize_mode,
                                            batch_size = currParam.batch_size,
                                            script_name = currParam.script_name,
                                            script_args = currParam.script_args,
                                            control_net = currParam.control_net
                                        )
                                        taskQueue.genListEvent(TaskListEvent.AddTaskImage(capImg to trueParam){
                                            Toast.makeText(context, context.getString(R.string.t_too_many_gen), Toast.LENGTH_SHORT).show()
                                        })

                                        if (uiViewModel.taskQueueSize == 1 && uiViewModel.showGenOn1) {
                                            uiViewModel.onEvent(UIEvent.Display(true))
                                        }
                                    }
                                })
                        } else {
                            uiViewModel.onEvent(UIEvent.Display(false))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (!uiViewModel.displaying) MaterialTheme.colorScheme.inversePrimary else Color.LightGray,
                            CircleShape
                        )
                )
            }

            IconButton(
                onClick = {
                    if (uiViewModel.displaying) {
                        uiViewModel.onEvent(UIEvent.DisplayImg(-1))
                    }
                    camIndex.value = if (camIndex.value != CameraSelector.LENS_FACING_FRONT)
                        CameraSelector.LENS_FACING_FRONT
                    else
                        CameraSelector.LENS_FACING_BACK
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.inversePrimary, CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.flip_camera_32),
                    contentDescription = "Change camera facing.",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}