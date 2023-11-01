package top.topsea.simplediffusion.util

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import top.topsea.simplediffusion.api.QueueData
import top.topsea.simplediffusion.api.dto.Img2Img
import top.topsea.simplediffusion.api.dto.TaskPgRequest
import top.topsea.simplediffusion.api.dto.Txt2Img
import top.topsea.simplediffusion.api.impl.GenImgApiImp
import top.topsea.simplediffusion.api.impl.NormalApiImp
import top.topsea.simplediffusion.data.param.BasicParam
import top.topsea.simplediffusion.data.param.ImageData
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.param.TaskParam
import top.topsea.simplediffusion.data.param.TaskParamDao
import top.topsea.simplediffusion.data.param.TxtParam
import top.topsea.simplediffusion.data.state.ControlNetState
import top.topsea.simplediffusion.data.state.GenerateState
import top.topsea.simplediffusion.data.state.TaskState
import top.topsea.simplediffusion.data.viewmodel.ImgDataViewModel
import top.topsea.simplediffusion.data.viewmodel.NormalViewModel
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.event.TaskListEvent
import top.topsea.simplediffusion.event.GenerateEvent
import top.topsea.simplediffusion.event.ImageEvent
import top.topsea.simplediffusion.event.RequestState

class TaskQueue(
    val cancelGenerate: (String) -> Unit,
    val cnState: ControlNetState,
    val imgViewModel: ImgDataViewModel,
    val uiViewModel: UIViewModel,
    val genImgApi: GenImgApiImp,
    val normalApi: NormalApiImp,
    val dao: TaskParamDao,
    val context: Context
) {
    // 图像生成状态
    private val _genState = MutableStateFlow(GenerateState())
    val genState: StateFlow<GenerateState> = _genState

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow(TaskState())
    private val _tasks = dao.getTaskParam()
        .stateIn(scope, SharingStarted.WhileSubscribed(), emptyList())
    private val _error_tasks = dao.getErrorTask()
        .stateIn(scope, SharingStarted.WhileSubscribed(), emptyList())
    val tasksState = combine(_state, _tasks, _error_tasks) { state, tasks, errorTasks ->
        state.copy(
            tasks = tasks,
            errorTasks = errorTasks,
        )
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), TaskState())

    // 创建队列
    val capGenImgList: SnapshotStateList<ImageData> = mutableStateListOf()

    private var started = false

    fun genListEvent(event: TaskListEvent) {
        when(event) {
            is TaskListEvent.AddTaskImage -> {
                addGenImage(event.gen, event.onAddFailure)
            }
            is TaskListEvent.RemoveTask -> {}
        }
    }

    /**
     * 判断加入队列还是直接执行
     */
    private fun addGenImage(gen: Pair<ImageData?, BasicParam>, onAddFailure: () -> Unit) {
        if (tasksState.value.tasks.size >= uiViewModel.taskQueueSize) {
            onAddFailure()
        } else {
            val task = TaskParam(image = gen.first, param = gen.second)
            if (uiViewModel.exAgentScheduler) {
                getRealTask(task)
            } else {
                addTask(task)
            }
        }
    }

    private fun addTask(task: TaskParam) {
        scope.launch {
            TextUtil.topsea("Add task: $task", Log.ERROR)
            dao.insert(task)
        }
    }
    private fun updateTask(task: TaskParam) {
        scope.launch {
            TextUtil.topsea("Update task: $task", Log.ERROR)
            dao.update(task)
        }
    }
    private suspend fun removeTask(task: TaskParam) {
        TextUtil.topsea("Delete task: $task", Log.ERROR)

        // 不 delay 一下可能会造成无尽的请求
        delay(100)
        dao.delete(task)
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun getRealTask(task: TaskParam) {
        GlobalScope.launch {
            val param = task.param
            when (param) {
                is ImgParam -> {
                    genImgApi.queueImg2Img(getImgParam(param)).collect{
                        dealTaskState(it, task)
                    }
                }
                is TxtParam -> {
                    genImgApi.queueTxt2Img(getTxtParam(param)).collect{
                        dealTaskState(it, task)
                    }
                }
            }
        }
    }

    fun trueOp() {
        if (!started) {
            started = true
            scope.launch {
                while (true) {
                    if (tasksState.value.tasks.isEmpty()) {
                        delay(1000)
                    } else {
                        val task = tasksState.value.tasks[0]
                        if (uiViewModel.exAgentScheduler) {
                            requestTaskProgress(task = task)
                        } else {
                            startGenOp(task = task)
                        }
                        // 删除需要一点时间
                        delay(100)
                    }
                }
            }
        }
    }

    private suspend fun startGenOp(task: TaskParam) {
        generateEvent(GenerateEvent.IsGeneratingImages(true))
        val param = task.param
        when (param) {
            is ImgParam -> {
                genImgApi.img2Img(getImgParam(param)){ imgs, info ->
                    val images = FileUtil.saveBase64Images(imgs, context, info)
                    saveImages(images, task.image, param.batch_size)
                    removeTask(task)
                }.collect{
                    dealRequestState(it, task)
                }
            }
            is TxtParam -> {
                genImgApi.txt2Img(getTxtParam(param)){ imgs, info ->
                    val images = FileUtil.saveBase64Images(imgs, context, info)
                    saveImages(images, task.image, param.batch_size)
                    removeTask(task)
                }.collect{
                    dealRequestState(it, task)
                }
            }
        }
        generateEvent(GenerateEvent.IsGeneratingImages(false))
    }

    private suspend fun requestTaskProgress(task: TaskParam) {
        val requestTask = TaskPgRequest(id_task = task.taskID)
        loop@while (true) {
            var temp = 0f
            normalApi.taskProgress(requestTask).collect() { progress ->
                temp = if (progress == 1f) { // 已经生成完成
                    1f
                } else {
                    progress
                }
            }
            _genState.update {
                it.copy(
                    generatingProgress = temp
                )
            }
            if (temp == 1f)
                break@loop

            // 每 0.1s 请求一次
            delay(100)
        }
        // 等待图片就绪
        delay(300)

        // 请求结果
        queueResults(task)
    }

    private suspend fun queueResults(task: TaskParam) {
        val param = task.param
        genImgApi.queueResult(task.taskID) { data: Array<QueueData> ->
            val batchSize = param.batch_size
            val images: MutableList<ImageData?> = mutableListOf()

            data.forEachIndexed { index, queueData ->
                if (index < batchSize) {
                    val image = FileUtil.saveQueueImage(queueData, context)
                    images.add(image)
                }
            }
            saveImages(images, task.image, batchSize = batchSize)

            removeTask(task)
        }.collect{ state ->
            when (state) {
                is RequestState.OnAppError -> {
                    task.genInfo = state.errorStr
                    updateTask(task)
                }
                is RequestState.OnRequestError -> {
                    task.genInfo = state.error
                    updateTask(task)
                }
                is RequestState.OnRequestFailure -> {
                    task.genInfo = state.error
                    updateTask(task)
                }
                is RequestState.OnRequestSuccess -> {  }
            }
        }
    }

    fun generateEvent(event: GenerateEvent) {
        when (event) {
            is GenerateEvent.GeneratingProgress -> {
                requestGenProgress()
            }
            is GenerateEvent.RemoveTask -> {
                val task = event.task
                if (event.isGenThis) {
                    if (!uiViewModel.exAgentScheduler) {
                        cancelGenerate("")
                    } else {
                        val taskID = task.taskID
                        cancelGenerate(taskID)
                    }
                    _genState.update {
                        it.copy(
                            isGeneratingImages = false
                        )
                    }
                } else {
                    if (uiViewModel.exAgentScheduler) {
                        val taskID = task.taskID
                        cancelGenerate(taskID)
                    }
                    scope.launch {
                        removeTask(task)
                    }
                }
            }
            is GenerateEvent.RefreshTask -> {
                val task = event.task
                task.genInfo = ""
                updateTask(task)
            }
            is GenerateEvent.IsGeneratingImages -> {
                if (event.isGeneratingImages) {
                    _genState.update {
                        it.copy(
                            isGeneratingImages = true
                        )
                    }
                    if (event.taskID == "")
                        generateEvent(GenerateEvent.GeneratingProgress)
                    else {
                    }
                } else {
                    _genState.update {
                        it.copy(
                            isGeneratingImages = false,
                            generatingProgress = 0f
                        )
                    }
                }
            }
        }
    }

    private fun saveImages(images: List<ImageData?>?, capImg: ImageData?, batchSize: Int) {
        if (!images.isNullOrEmpty()) {
            // 先添加拍摄的照片
            if (capImg != null) {
                capGenImgList.add(capImg)
                if (uiViewModel.saveCapImage) {
                    imgViewModel.onEvent(ImageEvent.AddImages(capImg))
                }
            }
            // 添加生成的图片
            if (uiViewModel.saveControlNet) {
                images.forEach { image ->
                    image?.let {
                        imgViewModel.onEvent(ImageEvent.AddImages(it))
                        capGenImgList.add(it)
                    }
                }
            } else {
                // AgentScheduler 的图片顺序不同
                if (uiViewModel.exAgentScheduler) {
                    val imageSubList = if (batchSize < images.lastIndex) {
                        images.subList(1, batchSize + 1)
                    } else {
                        images.subList(0, batchSize)
                    }
                    imageSubList.forEach { image ->
                        image?.let {
                            imgViewModel.onEvent(ImageEvent.AddImages(it))
                            capGenImgList.add(it)
                        }
                    }
                } else {
                    val imageSubList = images.subList(0, batchSize)
                    imageSubList.forEach { image ->
                        image?.let {
                            imgViewModel.onEvent(ImageEvent.AddImages(it))
                            capGenImgList.add(it)
                        }
                    }
                }
            }
            generateEvent(GenerateEvent.IsGeneratingImages(false))
        }
    }

    private fun dealRequestState(state: RequestState<String>, task: TaskParam) {
        when (state) {
            is RequestState.OnAppError -> {
                task.genInfo = state.errorStr
                updateTask(task)
            }
            is RequestState.OnRequestError -> {
                task.genInfo = state.error
                updateTask(task)
            }
            is RequestState.OnRequestFailure -> {
                task.genInfo = state.error
                updateTask(task)
            }
            is RequestState.OnRequestSuccess -> {  }
        }
    }

    private fun dealTaskState(state: RequestState<String>, task: TaskParam) {
        when (state) {
            is RequestState.OnAppError -> {
                task.genInfo = state.errorStr
                addTask(task)
            }
            is RequestState.OnRequestError -> {
                task.genInfo = state.error
                addTask(task)
            }
            is RequestState.OnRequestFailure -> {
                task.genInfo = state.error
                addTask(task)
            }
            is RequestState.OnRequestSuccess -> {
                task.taskID = state.success
                addTask(task)
            }
        }
    }

    private fun requestGenProgress() {
        scope.launch {
            while (genState.value.isGeneratingImages) {
                normalApi.getProgress().collect() { progress ->
                    _genState.update {
                        it.copy(
                            generatingProgress = progress
                        )
                    }
                }

                // 每 0.1s 请求一次
                delay(100)
            }
        }
    }

    private fun getImgParam(param: ImgParam): String {
        val img2Img =  Img2Img(
            init_images = arrayOf(param.image.value),
            denoising_strength = param.denoising_strength,
            prompt = param.defaultPrompt,
            refiner_checkpoint = param.refinerModel,
            refiner_switch_at = param.refinerAt,
            negative_prompt = param.defaultNegPrompt,
            width = param.width,
            height = param.height,
            steps = param.steps,
            cfg_scale = param.cfgScale,
            sampler_name = param.sampler_index,
            resize_mode = param.resize_mode,
            batch_size = param.batch_size,
            script_name = param.script_name,
            script_args = param.script_args,
        )

        val controlNets = cnState.cnParams.filter {
            param.control_net.contains(it.id)
        }
        controlNets.forEach {
            TextUtil.topsea("ControlNets: ${it}", Log.ERROR)
            if (it.use_imgImg) {
                it.input_image.value = img2Img.init_images[0]
            }
        }

        img2Img.save_images = uiViewModel.saveOnServer

        return img2Img.requestWithCN(controlNets)
    }

    private fun getTxtParam(param: TxtParam): String {
        val txt2Img =  Txt2Img(
            prompt = param.defaultPrompt,
            refiner_checkpoint = param.refinerModel,
            refiner_switch_at = param.refinerAt,
            negative_prompt = param.defaultNegPrompt,
            width = param.width,
            height = param.height,
            steps = param.steps,
            cfg_scale = param.cfgScale,
            sampler_name = param.sampler_index,
            script_name = param.script_name,
            script_args = param.script_args,
            batch_size = param.batch_size
        )
        val controlNets = cnState.cnParams.filter {
            TextUtil.topsea("ControlNets: ${it}", Log.ERROR)
            param.control_net.contains(it.id)
        }

        txt2Img.save_images = uiViewModel.saveOnServer
        return txt2Img.requestWithCN(controlNets)
    }
}