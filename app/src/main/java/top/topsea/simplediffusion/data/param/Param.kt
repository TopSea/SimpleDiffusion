package top.topsea.simplediffusion.data.param

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.TypeConverters
import top.topsea.simplediffusion.data.SnapshotStateListConverter
import top.topsea.simplediffusion.ui.scripts.Script

val scripts by lazy {
    listOf(
        "X/Y/Z plot",
//        "SD upscale",
//        "Prompts from file or textbox",
    )
}

data class ParamActivate(
    val id: Int,
    val activate: Boolean,
)

@TypeConverters(SnapshotStateListConverter::class)
data class ParamControlNet(
    val id: Int,
    val control_net: SnapshotStateList<Int>,
)

abstract class BasicParam() {
    abstract var id: Int
    abstract val name: String
    abstract val activate: Boolean
    abstract val baseModel: String
    abstract val refinerModel: String
    abstract val refinerAt: Float
    abstract val defaultPrompt: String
    abstract val defaultNegPrompt: String
    abstract val width: Int
    abstract val height: Int
    abstract val steps: Int
    abstract val cfgScale: Float
    abstract val sampler_index: String
    abstract val batch_size: Int
    abstract val script_name: String
    abstract val script_args: Script?
    abstract val control_net: SnapshotStateList<Int>

    abstract fun toRequest(): String
}
