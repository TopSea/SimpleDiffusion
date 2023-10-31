package top.topsea.simplediffusion.api.dto

import androidx.annotation.Keep
import com.google.gson.JsonObject

// [{"title":"abyssorangemix3AOM3_aom3a1b.safetensors [5493a0ec49]","model_name":"abyssorangemix3AOM3_aom3a1b",
// "hash":"5493a0ec49","sha256":"5493a0ec491f5961dbdc1c861404088a6ae9bd4007f6a3a7c5dee8789cdc1361",
// "filename":"E:\\WebUI\\stable-diffusion-webui\\models\\Stable-diffusion\\abyssorangemix3AOM3_aom3a1b.safetensors","config":null}]

@Keep
data class BaseModel(
    val title: String,
    val model_name: String,
    val hash: String,
    val sha256: String,
    val filename: String,
    val config: Any? = null
)

@Keep
data class Sampler(
    val name: String,
    val aliases: List<String>,
    val options: JsonObject,
)

@Keep
data class SDPrompt(
    val name: String,
    val prompt: String,
    val negative_prompt: String,
)