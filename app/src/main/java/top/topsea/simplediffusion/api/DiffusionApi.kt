package top.topsea.simplediffusion.api

import androidx.annotation.Keep
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming


@Keep
data class ImgResponse(
    var images: Array<String>,
    var parameters: JsonObject,
    var info: String
)

@Keep
data class QueueTask(
    var task_id: String,
)

@Keep
data class AgentResponse(
    var success: Boolean,
    var message: String
)

@Keep
data class QueueImgResponse(
    var success: Boolean,
    var data: Array<QueueData>,
)

@Keep
data class QueueData(
    var image: String,
    var infotext: String,
)

