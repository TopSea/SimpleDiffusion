package top.topsea.simplediffusion.data.param

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import top.topsea.simplediffusion.data.SerialConverter
import top.topsea.simplediffusion.data.SnapshotStateListConverter
import top.topsea.simplediffusion.data.StringConverter
import top.topsea.simplediffusion.ui.scripts.Script
import top.topsea.simplediffusion.ui.scripts.UltimateSDUpscale
import top.topsea.simplediffusion.ui.scripts.XYZ
import top.topsea.simplediffusion.util.Constant
import top.topsea.simplediffusion.util.TextUtil.script2String


@Immutable
@TypeConverters(SnapshotStateListConverter::class, StringConverter::class, SerialConverter::class)
@Entity
data class ImgParam(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    @ColumnInfo(name = "priority_order")
    override val priority_order: Int = 0,
    @ColumnInfo(name = "name")
    override val name: String = "Name",
    @ColumnInfo(name = "activate")
    override val activate: Boolean = false,

    @ColumnInfo(name = "image")
    val image: MutableState<String> = mutableStateOf(""),
    @ColumnInfo(name = "denoising_strength")
    val denoising_strength: Float = 0.75f,
    @ColumnInfo(name = "baseModel")
    override val baseModel: String = "",
    @ColumnInfo(name = "refinerModel")
    override val refinerModel: String = "",
    @ColumnInfo(name = "refinerAt")
    override val refinerAt: Float = 0f,
    @ColumnInfo(name = "defaultPrompt")
    override val defaultPrompt: String = "",
    @ColumnInfo(name = "defaultNegPrompt")
    override val defaultNegPrompt: String = "",

    @ColumnInfo(name = "width")
    override val width: Int = 512,
    @ColumnInfo(name = "height")
    override val height: Int = 512,
    @ColumnInfo(name = "steps")
    override val steps: Int = 28,
    @ColumnInfo(name = "cfgScale")
    override val cfgScale: Float = 7f,
    @ColumnInfo(name = "sampler_index")
    override val sampler_index: String = "Euler",
    @ColumnInfo(name = "resize_mode")
    val resize_mode: Int = 0,
    @ColumnInfo(name = "batch_size")
    override val batch_size: Int = 1,
    @ColumnInfo(name = "script_name")
    override val script_name: String = "",
    @ColumnInfo(name = "script_args")
    override val script_args: Script? = null,
    @ColumnInfo(name = "control_net")
    override val control_net: SnapshotStateList<Int> = mutableStateListOf(),
): BasicParam() {
    override fun toRequest(): String {
        val scriptName = if (script_name.isNotEmpty())
            "  \"script_name\": \"${script_name}\",\n"
        else
            ""

        val scriptArgs = if (script_name.isNotEmpty() && script_args != null)
            "  \"script_args\": \"${script2String(script_args)}\",\n"
        else
            ""

        return "{\n" +
                "  \"cfg_scale\": ${cfgScale},\n" +
                "  \"sampler_index\": ${sampler_index},\n" +
                "  \"prompt\": \"${defaultPrompt}\",\n" +
                "  \"width\": ${width},\n" +
                "  \"height\": ${height},\n" +
                "  \"resize_mode\": ${resize_mode},\n" +
                "  \"batch_size\": ${batch_size},\n" +
                "  \"negative_prompt\": \"${defaultNegPrompt}\",\n" +
                    scriptName +
                    scriptArgs +
                "  \"steps\": ${steps}\n" +
                "}"
    }

    fun toSavable(gson: Gson): SavableImgParam {
        val script = if (script_args != null) {
            when (script_args) {
                is XYZ ->
                    gson.toJson(script_args) + Constant.addableFirst + "XYZ" + Constant.addableSecond
                else ->
                    gson.toJson(script_args) + Constant.addableFirst + "UltimateSDUpscale" + Constant.addableSecond
            }
        } else ""
        return SavableImgParam(
            id = id,
            name = name,
            activate = activate,
            image = image.value,
            denoising_strength = denoising_strength,
            baseModel = baseModel,
            defaultPrompt = defaultPrompt,
            defaultNegPrompt = defaultNegPrompt,
            width = width,
            height = height,
            steps = steps,
            cfgScale = cfgScale,
            sampler_index = sampler_index,
            resize_mode = resize_mode,
            batch_size = batch_size,
            script_name = script_name,
            script_args = script,
            control_net = control_net,
        )
    }
}

@Keep
data class SavableImgParam(
    var id: Int = 0,
    val name: String = "Name",
    val activate: Boolean = false,

    val image: String = "",
    val denoising_strength: Float = 0.75f,
    val baseModel: String = "",
    val defaultPrompt: String = "",
    val defaultNegPrompt: String = "",

    val width: Int = 512,
    val height: Int = 512,
    val steps: Int = 28,
    val cfgScale: Float = 7f,
    val sampler_index: String = "Euler",
    val resize_mode: Int = 0,
    val batch_size: Int = 1,
    val script_name: String = "",
    val script_args: String = "",
    val control_net: SnapshotStateList<Int> = mutableStateListOf(),
){
    fun toImgParam(gson: Gson): ImgParam {
        val script = if (script_args.isNotEmpty()) {
            if (script_args.endsWith(Constant.addableFirst + "XYZ" + Constant.addableSecond)) {
                val trueStr = script_args.replace(Constant.addableFirst + "XYZ" + Constant.addableSecond, "")
                gson.fromJson(trueStr, XYZ::class.java)
            } else {
                val trueStr = script_args.replace(Constant.addableFirst + "UltimateSDUpscale" + Constant.addableSecond, "")
                gson.fromJson(trueStr, UltimateSDUpscale::class.java)
            }
        } else null
        return ImgParam(
            id = id,
            name = name,
            activate = activate,
            image = mutableStateOf(image),
            denoising_strength = denoising_strength,
            baseModel = baseModel,
            defaultPrompt = defaultPrompt,
            defaultNegPrompt = defaultNegPrompt,
            width = width,
            height = height,
            steps = steps,
            cfgScale = cfgScale,
            sampler_index = sampler_index,
            resize_mode = resize_mode,
            batch_size = batch_size,
            script_name = script_name,
            script_args = script,
            control_net = control_net,
        )
    }
}

@Dao
interface ImgParamDao {
//    @Query("SELECT * FROM ImgParam order by `id` desc limit 20")
    @Query("SELECT * FROM ImgParam ORDER BY priority_order DESC")
    fun getImgParams(): Flow<List<ImgParam>>
    @Query("SELECT * FROM ImgParam WHERE name LIKE '%' || :searchTxt || '%' ORDER BY priority_order DESC")
    fun getSearchParams(searchTxt: String): Flow<List<ImgParam>>

    @Update(ImgParam::class)
    suspend fun update(imgParam: ImgParam)
    @Update(ImgParam::class)
    suspend fun update(pa: ParamActivate)
    @Update(ImgParam::class)
    suspend fun update(pi: ParamImage)
    @Update(ImgParam::class)
    suspend fun update(pcn: ParamControlNet)
    @Insert
    suspend fun insert(vararg imgParam: ImgParam)
    @Delete(ImgParam::class)
    suspend fun delete(imgParam: ImgParam): Int
}

@TypeConverters(StringConverter::class)
data class ParamImage(
    val id: Int,
    val image: MutableState<String>,
)