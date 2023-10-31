package top.topsea.simplediffusion.data.param

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import top.topsea.simplediffusion.data.SerialConverter
import top.topsea.simplediffusion.data.SnapshotStateListConverter
import top.topsea.simplediffusion.ui.scripts.Script
import top.topsea.simplediffusion.util.TextUtil


@Immutable
@TypeConverters(SnapshotStateListConverter::class, SerialConverter::class)
@Entity
data class TxtParam(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    @ColumnInfo(name = "name")
    override val name: String = "Name",
    @ColumnInfo(name = "activate")
    override val activate: Boolean = false,

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
            "  \"script_args\": \"${TextUtil.script2String(script_args)}\",\n"
        else
            ""

        return "{\n" +
                "  \"cfg_scale\": ${cfgScale},\n" +
                "  \"sampler_index\": ${sampler_index},\n" +
                "  \"prompt\": \"${defaultPrompt}\",\n" +
                "  \"width\": ${width},\n" +
                "  \"height\": ${height},\n" +
                "  \"batch_size\": ${batch_size},\n" +
                "  \"negative_prompt\": \"${defaultNegPrompt}\",\n" +
                    scriptName +
                    scriptArgs +
                "  \"steps\": ${steps}\n" +
                "}"
    }

    fun toSavable(): SavableTxtParam {
        return SavableTxtParam(
            id = id,
            name = name,
            activate = activate,
            baseModel = baseModel,
            defaultPrompt = defaultPrompt,
            defaultNegPrompt = defaultNegPrompt,
            width = width,
            height = height,
            steps = steps,
            cfgScale = cfgScale,
            sampler_index = sampler_index,
            batch_size = batch_size,
            script_name = script_name,
            script_args = script_args,
            control_net = control_net,
        )
    }
}

@Keep
data class SavableTxtParam(
    var id: Int = 0,
    val name: String = "Name",
    val activate: Boolean = false,

    val baseModel: String = "",
    val defaultPrompt: String = "",
    val defaultNegPrompt: String = "",
    val width: Int = 512,
    val height: Int = 512,
    val steps: Int = 28,
    val cfgScale: Float = 7f,
    val sampler_index: String = "Euler",
    val batch_size: Int = 1,
    val script_name: String = "",
    val script_args: Script? = null,
    val control_net: SnapshotStateList<Int> = mutableStateListOf(),
) {
    fun toTxtParam(): TxtParam {
        return TxtParam(
            id = id,
            name = name,
            activate = activate,
            baseModel = baseModel,
            defaultPrompt = defaultPrompt,
            defaultNegPrompt = defaultNegPrompt,
            width = width,
            height = height,
            steps = steps,
            cfgScale = cfgScale,
            sampler_index = sampler_index,
            batch_size = batch_size,
            script_name = script_name,
            script_args = script_args,
            control_net = control_net,
        )
    }
}

@Dao
interface TxtParamDao {
//    @Query("SELECT * FROM TxtParam order by `id` desc limit 20")
    @Query("SELECT * FROM TxtParam order by `id` desc")
    fun getLatest20(): Flow<List<TxtParam>>
    @Query("SELECT * FROM TxtParam WHERE name LIKE '%' || :searchTxt || '%' order by `id` desc limit 20")
    fun getALikeLatest20(searchTxt: String): Flow<List<TxtParam>>

    @Query("SELECT * FROM TxtParam WHERE `id` < (:before)  order by `id` desc limit 10")
    fun get10More(before: Int): List<TxtParam>

    @Update(TxtParam::class)
    suspend fun update(txtParam: TxtParam)
    @Update(TxtParam::class)
    suspend fun update(pa: ParamActivate)
    @Update(ImgParam::class)
    suspend fun update(pcn: ParamControlNet)
    @Insert
    suspend fun insert(vararg txtParam: TxtParam)
    @Delete(TxtParam::class)
    suspend fun delete(txtParam: TxtParam): Int
}