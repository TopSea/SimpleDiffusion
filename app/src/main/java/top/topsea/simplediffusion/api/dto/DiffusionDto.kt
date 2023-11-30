package top.topsea.simplediffusion.api.dto

import androidx.annotation.Keep
import com.google.gson.JsonObject


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

