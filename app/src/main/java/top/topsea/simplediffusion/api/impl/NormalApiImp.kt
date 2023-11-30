package top.topsea.simplediffusion.api.impl

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import top.topsea.simplediffusion.api.NormalApi
import top.topsea.simplediffusion.api.dto.ControlTypes
import top.topsea.simplediffusion.api.dto.SDPrompt
import top.topsea.simplediffusion.api.dto.Sampler
import top.topsea.simplediffusion.api.dto.SimpleSdConfig
import top.topsea.simplediffusion.api.dto.TaskPgRequest
import top.topsea.simplediffusion.api.dto.VaeModel
import top.topsea.simplediffusion.api.dto.listTypes
import top.topsea.simplediffusion.data.param.UserPrompt
import top.topsea.simplediffusion.event.ExecuteState
import top.topsea.simplediffusion.util.TextUtil


class NormalApiImp(
    private val normalApi: NormalApi
) {
    suspend fun getProgress(): Flow<Float> {
        return flow {
            val result = kotlin.runCatching {
                normalApi.generatingProgress().execute()
            }
            val response = result.getOrNull()
            var progress = 0f

            response?.let {
                if (response.isSuccessful) {
                    response.body()?.let { generateProgress ->
                        progress = generateProgress.progress
                        emit(progress)
                    }
                } else {
                    emit(progress)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun taskProgress(task: TaskPgRequest): Flow<Float> {
        return flow {
            val requestJson = task.toRequest()
            val json = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toRequestBody(json)

            val result = kotlin.runCatching {
                normalApi.taskProgress(requestBody).execute()
            }
            val response = result.getOrNull()
            var progress = 0f

            response?.let {
                if (response.isSuccessful) {
                    response.body()?.let { taskProgress ->
                        progress = if (taskProgress.completed || taskProgress.progress == null)
                            1f
                        else taskProgress.progress
                        emit(progress)
                    }
                } else {
                    emit(progress)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getSDSamplers(): Flow<List<Sampler>> {
        return flow {
            val result = kotlin.runCatching {
                normalApi.getSDSamplers().execute()
            }
            val response = result.getOrNull()
            val samplers = ArrayList<Sampler>()

            response?.let {
                if (response.isSuccessful) {
                    response.body()?.let { samplerResponse ->
                        val gs = GsonBuilder().disableHtmlEscaping().create()
                        val parser = JsonParser()
                        val array: JsonArray = parser.parse(samplerResponse.string()).asJsonArray
                        array.forEach {
                            val sampler = gs.fromJson(it, Sampler::class.java)
                            samplers.add(sampler)
                        }
                    }
                } else {
                }
            }
            emit(samplers)
        }.flowOn(Dispatchers.IO)
    }

    fun getSDPrompts(): Flow<List<SDPrompt>> {
        return flow {
            val result = kotlin.runCatching {
                normalApi.getSDPrompts().execute()
            }
            val response = result.getOrNull()
            val samplers = arrayListOf(SDPrompt("None","", ""))

            response?.let {
                if (response.isSuccessful) {
                    response.body()?.let { samplerResponse ->
                        val gs = GsonBuilder().disableHtmlEscaping().create()
                        val parser = JsonParser()
                        val array: JsonArray = parser.parse(samplerResponse.string()).asJsonArray
                        array.forEach {
                            val sampler = gs.fromJson(it, SDPrompt::class.java)
                            samplers.add(sampler)
                        }
                    }
                } else {
                }
            }
            emit(samplers)
        }.flowOn(Dispatchers.IO)
    }

    fun <T> getModels(classOfT: Class<T>): Flow<List<T>> {
        return flow {
            val result = kotlin.runCatching {
                when (classOfT) {
                    UserPrompt::class.java -> {
                        normalApi.getLoras().execute()
                    }

                    VaeModel::class.java -> {
                        normalApi.getVaes().execute()
                    }

                    else -> {
                        normalApi.getSdModels().execute()
                    }
                }
            }
            val response = result.getOrNull()
            val models = ArrayList<T>()
            response?.let {
                if (it.isSuccessful) {
                    it.body()?.let { array ->
                        array.forEach { json ->
                            val model = Gson().fromJson(json, classOfT)
                            models.add(model)
                        }
                    }
                } else {
                }
            }
            emit(models)
        }.flowOn(Dispatchers.IO)
    }

    fun getCNTypes(): Flow<List<Pair<String, ControlTypes>>> {
        return flow {
            val result = kotlin.runCatching {
                normalApi.getCNTypes().execute()
            }
            val response = result.getOrNull()
            val models = ArrayList<Pair<String, ControlTypes>>()
            response?.let { it ->
                if (it.isSuccessful) {
                    TextUtil.topsea(it.body().toString(), Log.ERROR)
                    it.body()?.let { types ->
                        val gs = GsonBuilder().disableHtmlEscaping().create()
                        TextUtil.topsea("ControlNet types: ${types.control_types}")
                        listTypes.forEach { pair ->
                            val type = types.control_types.get(pair.first)
                            val trueType = gs.fromJson(type, ControlTypes::class.java)
                            models.add(pair.first to trueType)
                        }
                    }
                } else {
                }
            }
            emit(models)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCNSettings(): Int {
        return withContext(Dispatchers.IO) {
            val result = kotlin.runCatching {
                normalApi.getCNSettings().execute()
            }
            var maxModelsNum = -1
            val response = result.getOrNull()
            response?.let {
                if (it.isSuccessful) {
                    it.body()?.let { settings ->
                        maxModelsNum =  settings.control_net_unit_count
                    }
                } else {
                }
            }
            maxModelsNum
        }
    }

    suspend fun getCNVersion(): Int {
        return withContext(Dispatchers.IO) {
            val result = kotlin.runCatching {
                normalApi.getCNVersion().execute()
            }
            var ver = -1
            val response = result.getOrNull()
            response?.let {
                if (it.isSuccessful) {
                    it.body()?.let { res ->
                        ver = res.version
                    }
                } else {
                }
            }
            ver
        }
    }

    suspend fun skipGenerate(taskID: String) {
        return withContext(Dispatchers.IO) {
            if (taskID.isEmpty()) {
                val result = kotlin.runCatching {
                    normalApi.skipGenerate().execute()
                }
                val response = result.getOrNull()
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                        }
                    } else {
                    }
                }
            } else {
                val result = kotlin.runCatching {
                    normalApi.deleteAgentTask(taskID).execute()
                }
                val response = result.getOrNull()
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            TextUtil.topsea("Task skipGenerate: ${res.string()}", Log.ERROR)
                        }
                    } else {
                    }
                }
            }
        }
    }

    suspend fun <T> refreshModels(classOfT: Class<T>): List<T> {
        return withContext(Dispatchers.IO) {
            val result = kotlin.runCatching {
                when (classOfT) {
                    UserPrompt::class.java -> {
                        normalApi.getLoras().execute()
                    }

                    VaeModel::class.java -> {
                        normalApi.getVaes().execute()
                    }

                    else -> {
                        normalApi.getSdModels().execute()
                    }
                }
            }
            val response = result.getOrNull()
            val models = ArrayList<T>()
            response?.let {
                if (it.isSuccessful) {
                    it.body()?.let { array ->
                        array.forEach { json ->
                            val model = Gson().fromJson(json, classOfT)
                            models.add(model)
                        }
                    }
                }
            }
            models
        }
    }

    suspend fun checkSDConnect(checkConnect: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            val json = "application/json; charset=utf-8".toMediaType()
            val requestBody = "{ }".toRequestBody(json)

            val connect = kotlin.runCatching {
                normalApi.checkSDConnect(requestBody).execute()
            }
            val responseBody = connect.getOrNull()
            if (responseBody == null) {
                checkConnect(false)
            } else {
                checkConnect(responseBody.isSuccessful)
            }
        }
    }

    suspend fun checkAgentScheduler(checkSuccess: () -> Unit, checkFailed: suspend () -> Unit, ) {
        withContext(Dispatchers.IO) {
            val connect = kotlin.runCatching {
                normalApi.checkAgentScheduler().execute()
            }
            val response = connect.getOrNull()!!

            if (response.isSuccessful) {
                checkSuccess()
            } else {
                checkFailed()
            }
        }
    }

    suspend fun <T> updateSdConfig (simpleSdConfig: SimpleSdConfig<T>): Flow<ExecuteState> {
        val json = "application/json; charset=utf-8".toMediaType()
        val requestBody = simpleSdConfig.toString().toRequestBody(json)

        return flow {
            val result = kotlin.runCatching {
                normalApi.updateSdConfig(requestBody).execute()
            }
            val response = result.getOrNull()
            response?.let {
                if (it.isSuccessful) {
                    emit(ExecuteState.ExecuteSuccess(true))
                } else {
                    emit(ExecuteState.ExecuteSuccess(false))
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}