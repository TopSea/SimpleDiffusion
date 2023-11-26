package top.topsea.simplediffusion.api.dto

import androidx.annotation.Keep
import com.google.gson.JsonObject
import top.topsea.simplediffusion.R


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
    "All" to R.string.rs_cn_control_type_1,
    "Canny" to R.string.rs_cn_control_type_2,
    "Depth" to R.string.rs_cn_control_type_3,
    "NormalMap" to R.string.rs_cn_control_type_4,
    "OpenPose" to R.string.rs_cn_control_type_5,
    "MLSD" to R.string.rs_cn_control_type_6,
    "Lineart" to R.string.rs_cn_control_type_7,
    "SoftEdge" to R.string.rs_cn_control_type_8,
    "Scribble/Sketch" to R.string.rs_cn_control_type_9,
    "Segmentation" to R.string.rs_cn_control_type_10,
    "Shuffle" to R.string.rs_cn_control_type_11,
    "Tile/Blur" to R.string.rs_cn_control_type_12,
    "Inpaint" to R.string.rs_cn_control_type_13,
    "InstructP2P" to R.string.rs_cn_control_type_14,
    "Reference" to R.string.rs_cn_control_type_15,
    "Recolor" to R.string.rs_cn_control_type_16,
    "Revision" to R.string.rs_cn_control_type_17,
    "T2I-Adapter" to R.string.rs_cn_control_type_18,
    "IP-Adapter" to R.string.rs_cn_control_type_19,
)