package top.topsea.simplediffusion.api.dto

import androidx.annotation.Keep
import com.google.gson.JsonObject


@Keep
data class GenerateProgress(
    val progress: Float,
    val eta_relative: Float,
    val state: JsonObject,
    val current_image: String?,
    val textinfo: String?,
)

@Keep
data class TaskPgRequest(
    val id_task: String,
    val id_live_preview: Int = -1,
    val live_preview: Boolean = false,
) {
    fun toRequest(): String {
        return "{" +
                "\"id_task\": \"$id_task\"," +
                "\"id_live_preview\": $id_live_preview," +
                "\"live_preview\": $live_preview" +
                "}"
    }
}

@Keep
data class TaskProgress(
    val active: Boolean,
    val queued: Boolean,
    val completed: Boolean,
    val progress: Float?,
    val eta: Float?,
    val live_preview: String?,
    val id_live_preview: Int,
    val textinfo: String?,
)

