package top.topsea.simplediffusion.api.dto

import androidx.annotation.Keep
import com.google.gson.JsonArray

/**
 * {
 *     "error": "HTTPException",
 *     "detail": "Init image not found",
 *     "body": "",
 *     "errors": ""
 * }
 */

@Keep
data class ErrorResponse(
    val error: String,
    val detail: String,
    val body: String,
    val errors: String,
)

@Keep
data class RequestErrorDetail(
    val loc: Array<Any>,
    val msg: String,
    val type: String,
)
