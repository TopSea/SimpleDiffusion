package top.topsea.simplediffusion

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import top.topsea.simplediffusion.api.impl.GenImgApiImp
import top.topsea.simplediffusion.api.impl.NormalApiImp
import top.topsea.simplediffusion.data.param.TaskParamDao
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.ImgDataViewModel
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.ParamViewModel
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.event.ControlNetEvent
import top.topsea.simplediffusion.event.ImageEvent
import top.topsea.simplediffusion.event.ParamEvent
import top.topsea.simplediffusion.ui.screen.AboutSDScreen
import top.topsea.simplediffusion.ui.screen.BaseBottomBar
import top.topsea.simplediffusion.ui.screen.BaseScreen
import top.topsea.simplediffusion.ui.screen.Bottom
import top.topsea.simplediffusion.ui.screen.CNParamEditScreen
import top.topsea.simplediffusion.ui.screen.CameraScreen
import top.topsea.simplediffusion.ui.screen.CameraSettingScreen
import top.topsea.simplediffusion.ui.screen.DesktopScreen
import top.topsea.simplediffusion.ui.screen.ParamEditScreen
import top.topsea.simplediffusion.ui.screen.SetParamEditScreen
import top.topsea.simplediffusion.ui.screen.SettingScreen
import top.topsea.simplediffusion.ui.theme.SimpleDiffusionTheme
import top.topsea.simplediffusion.util.DeleteImage
import top.topsea.simplediffusion.util.FileUtil
import top.topsea.simplediffusion.util.TaskQueue
import top.topsea.simplediffusion.util.TextUtil
import javax.inject.Inject


var currentScreen: SimpleDestination by mutableStateOf(BaseScreen)
val pickedImgUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)             // 图片的的 Uri
val pickingImg: MutableState<Int> = mutableStateOf(-2)             // -2: 未在选择图片；-1：图生图在选择图片；>=0：各个ControlNet在选择图片

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Registers a photo picker activity launcher in single-select mode.
    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            TextUtil.topsea("PhotoPicker Selected URI: $uri")
            pickedImgUri.value = uri
        } else {
            TextUtil.topsea("PhotoPicker no media selected")
            pickingImg.value = -2
        }
    }
    @Inject lateinit var genImgApi: GenImgApiImp
    @Inject lateinit var normalApi: NormalApiImp
    @Inject lateinit var taskParamDao: TaskParamDao

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskQueue = TaskQueue(
            genImgApi = genImgApi,
            normalApi = normalApi,
            dao = taskParamDao,
            context = this
        )

        setContent {
            TextUtil.topsea("setContent...", Log.ERROR)
            val navController = rememberNavController()
            val context = LocalContext.current

            val imgDataViewModel: ImgDataViewModel = hiltViewModel()
            val uiViewModel: UIViewModel = hiltViewModel()
            val normalViewModel: NormalViewModel = hiltViewModel()
            val cnState by normalViewModel.cnState.collectAsState()
            taskQueue.cancelGenerate = normalViewModel::cancelGenerate
            taskQueue.imgViewModel = imgDataViewModel
            taskQueue.cnState = cnState
            taskQueue.uiViewModel = uiViewModel

            val taskState by taskQueue.tasksState.collectAsState()
            val generateState by taskQueue.genState.collectAsState()

            val paramViewModel = hiltViewModel<ParamViewModel>()
            val paramState by paramViewModel.paramState.collectAsState()

            LaunchedEffect(key1 = uiViewModel.serverConnected) {
                if (uiViewModel.serverConnected) {
                    Toast.makeText(context, context.getText(R.string.t_sd_connected), Toast.LENGTH_SHORT).show()
                    // 生成任务循环开始
                    taskQueue.trueOp()
                    val vae = uiViewModel.currentVae
                    if (vae.model_name.isNotEmpty())
                        uiViewModel.onEvent(UIEvent.UpdateVae(vae, {}){})
                } else {
                    Toast.makeText(context, context.getText(R.string.t_sd_not_connected), Toast.LENGTH_SHORT).show()
                }
            }

            LaunchedEffect(key1 = pickingImg.value) {
                when (pickingImg.value) {
                    in -1..Int.MAX_VALUE -> {
                        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    }

                    -3 -> {
                        paramViewModel.paramEvent(ParamEvent.CloseImage)
                    }

                    in Int.MIN_VALUE..-4 -> {
                        normalViewModel.cnEvent(ControlNetEvent.CloseImage(-4 - pickingImg.value))
                    }

                    else -> {   // -2
                        pickedImgUri.value = Uri.EMPTY
                    }
                }
            }

            val loraModelState = normalViewModel.loraState.collectAsState()

            val resolver = context.contentResolver

            LaunchedEffect(key1 = pickedImgUri.value) {
                if (pickedImgUri.value != Uri.EMPTY) {
                    resolver.openFileDescriptor(pickedImgUri.value, "r").use { pfd ->
                        // Perform operations on "pfd".
                        pfd?.let {
                            val bitmap = BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
                            when (pickingImg.value) {
                                -1 -> {
                                    paramViewModel.paramEvent(ParamEvent.AddImage(FileUtil.bitmap2Base64(bitmap)))
                                }
                                in 0..Int.MAX_VALUE-> {
                                    normalViewModel.cnEvent(ControlNetEvent.AddImage(FileUtil.bitmap2Base64(bitmap), pickingImg.value))
                                }
                            }
                            pickingImg.value = -2
                        }
                    }
                }
            }

            val selectedItem = remember { mutableStateOf(Bottom.PHOTO) }

            SimpleDiffusionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold (
                        topBar = { currentScreen.topBar(uiViewModel, navController) },
                        bottomBar = {
                            if (currentScreen is BaseScreen)
                                BaseBottomBar(
                                    selectedItem,
                                    navController,
                                    imgDataViewModel = imgDataViewModel,
                                    uiViewModel = uiViewModel,
                                    paramState = paramState,
                                    paramEvent = paramViewModel::paramEvent,
                                    tasks = taskQueue.tasks,
                                    taskListEvent = taskQueue::genListEvent
                                )
                        },
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = BaseScreen.route,
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable(BaseScreen.route) {
                                BaseScreen(
                                    navController = navController,
                                    uiViewModel = uiViewModel,
                                    selectedItem = selectedItem,
                                    normalViewModel = normalViewModel,
                                    imgDataViewModel = imgDataViewModel,
                                    paramViewModel = paramViewModel,
                                    tasks = taskQueue.tasks,
                                    errorTasks = taskState.errorTasks,
                                    genState = generateState,
                                    generateEvent = taskQueue::generateEvent
                                )
                            }

                            composable(EditScreen.route) {
                                ParamEditScreen(
                                    navController = navController,
                                    cardColor = Color.Gray,
                                    paramState = paramState,
                                    uiViewModel = uiViewModel,
                                    paramEvent = paramViewModel::paramEvent,
                                    normalViewModel = normalViewModel,
                                )
                            }
                            composable(EditCNScreen.route) {
                                CNParamEditScreen(
                                    navController = navController,
                                    cardColor = Color.Gray,
                                    cnState = cnState,
                                    uiEvent = uiViewModel::onEvent,
                                    cnEvent = normalViewModel::cnEvent,
                                )
                            }
                            composable(SettingScreen.route) {
                                SettingScreen(
                                    navController = navController,
                                    uiViewModel = uiViewModel,
                                    normalViewModel = normalViewModel,
                                    tasks = taskQueue.tasks,
                                )
                            }
                            composable(SetParamEditScreen.route) {
                                SetParamEditScreen(
                                    navController = navController,
                                    uiViewModel = uiViewModel,
                                    normalViewModel = normalViewModel,
                                )
                            }
                            composable(AboutScreen.route) {
                                AboutSDScreen(uiViewModel = uiViewModel)
                            }
                            composable(CameraScreen.route) {
                                CameraScreen(
                                    imgViewModel = imgDataViewModel,
                                    paramViewModel = paramViewModel,
                                    normalViewModel = normalViewModel,
                                    uiViewModel = uiViewModel,
                                    taskQueue = taskQueue,
                                )
                            }
                            composable(CameraSettingScreen.route) {
                                CameraSettingScreen(
                                    navController = navController,
                                    paramViewModel = paramViewModel,
                                    normalViewModel = normalViewModel,
                                    uiViewModel = uiViewModel,
                                    tasks = taskQueue.tasks,
                                )
                            }
                            composable(DesktopScreen.route) {
                                DesktopScreen(
                                    uiViewModel = uiViewModel,
                                )
                            }
                        }

                        BackHandler(enabled = true) {
                            TextUtil.topsea("BackHandler.")
                            if (uiViewModel.longPressImage) {
                                uiViewModel.onEvent(UIEvent.LongPressImage(false))
                                uiViewModel.fullSelected.clear()
                                imgDataViewModel.selectedID.clear()
                                return@BackHandler
                            }
                            if (uiViewModel.displaying) {
                                uiViewModel.onEvent(UIEvent.Display(false))
                                return@BackHandler
                            }
                            if (!navController.popBackStack())
                                this.finish()
                            else
                                updateCurrScreen(navController.currentDestination!!.route!!)
                        }
                    }
                }
            }
        }
    }
}
