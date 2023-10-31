package top.topsea.simplediffusion.api.dto

import androidx.annotation.Keep

@Keep
data class ActivateModel(
    val activate_code: String,
    val device_id: String,
) {
    override fun toString(): String {
        return "{" +
                "\n\"activate_code\": \"$activate_code\"," +
                "\n\"device_id\": \"$device_id\"" +
                "\n}"
    }
}

/**
 * {
 *     "status": 402,
 *     "activate_info": "This activation code has been used.",
 *     "expire_date": "2023-08-18T06:32:44.199598+08:00"
 * }
 */

@Keep
data class ActResponse(
    val status: Int,
    val activate_info: String,
    val expire_date: String,
)

@Keep
data class VersionResponse(
    val status: Int,
    val apk_name: String,
    val apk_ver: String,
    val apk_size: String,
    val apk_hash: String,
)