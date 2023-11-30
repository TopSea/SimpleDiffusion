package top.topsea.simplediffusion.api.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import top.topsea.simplediffusion.api.dto.AgentResponse
import top.topsea.simplediffusion.api.GenImgApi
import top.topsea.simplediffusion.api.dto.QueueData
import top.topsea.simplediffusion.api.dto.QueueImgResponse
import top.topsea.simplediffusion.api.dto.ErrorResponse
import top.topsea.simplediffusion.api.dto.RequestErrorDetail
import top.topsea.simplediffusion.event.RequestState
import top.topsea.simplediffusion.util.TextUtil

class GenImgApiImp(
    private val genImgApi: GenImgApi
) {
    suspend fun txt2Img(param: String, onImageReceived: suspend (Array<String>?, String?) -> Unit): Flow<RequestState<String>> {
        return flow<RequestState<String>> {
            val json = "application/json; charset=utf-8".toMediaType()
            val requestBody = param.toRequestBody(json)

            val result = kotlin.runCatching {
                genImgApi.txt2Img(requestBody).execute()
            }
            val response = result.getOrNull()
            if (response == null) {
                emit(RequestState.OnAppError(result.toString()))
            } else {
                if (response.isSuccessful) {
                    val images = response.body()?.images
                    val info = response.body()?.info
                    onImageReceived(images, info)
                } else {
                    response.errorBody()?.let { error ->
                        emit(RequestState.OnRequestFailure(getErrorDetail(error.string(), response.code())))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun img2Img(
        param: String,
        onImageReceived: suspend (Array<String>?, String?) -> Unit
    ): Flow<RequestState<String>> {
        return flow<RequestState<String>> {
            val json = "application/json; charset=utf-8".toMediaType()
            val requestBody = param.toRequestBody(json)

            val result = kotlin.runCatching {
                genImgApi.img2Img(requestBody).execute()
            }
            val response = result.getOrNull()
            if (response == null) {
                emit(RequestState.OnAppError(result.toString()))
            } else {
                if (response.isSuccessful) {
                    val images = response.body()?.images
                    val info = response.body()?.info
                    onImageReceived(images, info)
                } else {
                    response.errorBody()?.let { error ->
                        emit(RequestState.OnRequestFailure(getErrorDetail(error.string(), response.code())))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }
    suspend fun queueTxt2Img(
        param: String,
        ): Flow<RequestState<String>> {
        return flow<RequestState<String>> {
            val json = "application/json; charset=utf-8".toMediaType()
            val requestBody = param.toRequestBody(json)

            val result = kotlin.runCatching {
                genImgApi.queueTxt2Img(requestBody).execute()
            }
            val response = result.getOrNull()
            if (response == null) {
                emit(RequestState.OnAppError(result.toString()))
            } else {
                if (response.isSuccessful) {
                    val task = response.body()
                    if (task == null) {
                        emit(RequestState.OnRequestFailure(""))
                    } else {
                        emit(RequestState.OnRequestSuccess(task.task_id))
                    }
                } else {
                    response.errorBody()?.let { error ->
                        emit(RequestState.OnRequestFailure(getErrorDetail(error.string(), response.code())))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun queueImg2Img(
        param: String,
    ): Flow<RequestState<String>> {
        return flow<RequestState<String>> {
            val json = "application/json; charset=utf-8".toMediaType()
            val requestBody = param.toRequestBody(json)

            val result = kotlin.runCatching {
                genImgApi.queueImg2Img(requestBody).execute()
            }
            val response = result.getOrNull()
            if (response == null) {
                emit(RequestState.OnAppError(result.toString()))
            } else {
                if (response.isSuccessful) {
                    val task = response.body()
                    if (task == null) {
                        emit(RequestState.OnRequestFailure(""))
                    } else {
                        emit(RequestState.OnRequestSuccess(task.task_id))
                    }
                } else {
                    response.errorBody()?.let { error ->
                        emit(RequestState.OnRequestFailure(getErrorDetail(error.string(), response.code())))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun queueResult(
        taskID: String,
        onImageReceived: suspend (Array<QueueData>) -> Unit
    ): Flow<RequestState<String>> {
        return flow<RequestState<String>> {

            val result = kotlin.runCatching {
                genImgApi.dQueueResults(taskID).execute()
            }
            val response = result.getOrNull()
            if (response == null) {
                emit(RequestState.OnAppError(result.toString()))
            } else {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val gson = Gson()
                        val str = responseBody.string()
                        if (str.startsWith("{\"success\":false")) {
                            val error = gson.fromJson(str, AgentResponse::class.java)
                            emit(RequestState.OnRequestFailure(error.message))
                        } else {
                            val data = gson.fromJson(str, QueueImgResponse::class.java)
                            onImageReceived(data.data)
                        }
                    } else {
                        emit(RequestState.OnRequestFailure("Task not found"))
                    }
                } else {
                    response.errorBody()?.let { error ->
                        emit(RequestState.OnRequestFailure(getErrorDetail(error.string(), response.code())))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}

fun getErrorDetail(str: String, code: Int): String {
    TextUtil.topsea("Error Code: $code, Error Content: $str")
    val gson = GsonBuilder().disableHtmlEscaping().create()
    val parser = JsonParser()
    when (code) {
        422 -> {
            val array = parser.parse(str).asJsonObject
            array.get("detail")
            val errorDetail = gson.fromJson(array.get("detail"), RequestErrorDetail::class.java)
            TextUtil.topsea(errorDetail.toString())
            return errorDetail.msg
        }
        500 -> {
            val errorResponse = gson.fromJson(str, ErrorResponse::class.java)
            return errorResponse.detail
        }
        else ->
            return ""
    }
}