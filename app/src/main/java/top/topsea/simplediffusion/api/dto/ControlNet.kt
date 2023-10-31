package top.topsea.simplediffusion.api.dto

import androidx.annotation.Keep
import com.google.gson.JsonObject


@Keep
data class CNVersion(
    val version: Int,
)

@Keep
data class CNSettings(
    val control_net_unit_count: Int,
)

@Keep
data class Types(
    val control_types: JsonObject
)

@Keep
data class ControlTypes(
    val module_list: List<String>,
    val model_list: List<String>,
    val default_option: String,
    val default_model: String,
)

@Keep
val listTypes = listOf<Pair<String, Int>>(
    "All" to 0,
    "Canny" to 0,
    "Depth" to 0,
    "NormalMap" to 0,
    "OpenPose" to 0,
    "MLSD" to 0,
    "Lineart" to 0,
    "SoftEdge" to 0,
    "Scribble/Sketch" to 0,
    "Segmentation" to 0,
    "Shuffle" to 0,
    "Tile" to 0,
    "Inpaint" to 0,
    "InstructP2P" to 0,
    "Reference" to 0,
    "Recolor" to 0,
    "Revision" to 0,
    "T2I-Adapter" to 0,
    "IP-Adapter" to 0,
)