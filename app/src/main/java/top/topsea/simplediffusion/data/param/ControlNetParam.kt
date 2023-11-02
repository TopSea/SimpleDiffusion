package top.topsea.simplediffusion.data.param

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.BooleanConverter
import top.topsea.simplediffusion.data.StringConverter

fun getCNControlMode(context: Context): List<Pair<Int, String>>{
    return listOf(
        0 to context.getString(R.string.rs_cn_control_m1),
        1 to context.getString(R.string.rs_cn_control_m2),
        2 to context.getString(R.string.rs_cn_control_m3),
    )
}
fun getCNResizeMode(context: Context): List<Pair<Int, String>>{
    return listOf(
        0 to context.getString(R.string.rs_cn_resize_m1),
        1 to context.getString(R.string.rs_cn_resize_m2),
        2 to context.getString(R.string.rs_cn_resize_m3),
    )
}

@Immutable
@TypeConverters(BooleanConverter::class, StringConverter::class)
@Entity
data class CNParam(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "cn_name")
    val cn_name: String = "Name",
    @ColumnInfo(name = "enabled")
    val enabled: MutableState<Boolean> = mutableStateOf(false),
    @ColumnInfo(name = "input_image")
    val input_image: MutableState<String> = mutableStateOf(""),
    @ColumnInfo(name = "mask")
    val mask: String = "",
    @ColumnInfo(name = "module")
    val module: String = "none",
    @ColumnInfo(name = "model")
    val model: String = "none",
    @ColumnInfo(name = "weight")
    val weight: Float = 1f,
    @ColumnInfo(name = "resize_mode")
    val resize_mode: Int = 1,   //0 for "Just Resize";    1 for "Scale to Fit (Inner Fit)";    2 or "Envelope (Outer Fit)" ;
    @ColumnInfo(name = "lowvram")
    val lowvram: Boolean = false,
    @ColumnInfo(name = "processor_res")
    val processor_res: Int = 512,
    @ColumnInfo(name = "threshold_a")
    val threshold_a: Int = 64,
    @ColumnInfo(name = "threshold_b")
    val threshold_b: Int = 64,
    @ColumnInfo(name = "guidance_start")
    val guidance_start: Float = 0f,
    @ColumnInfo(name = "guidance_end")
    val guidance_end: Float = 1f,
    @ColumnInfo(name = "control_mode")
    val control_mode: Int = 0,  // 0 for "Balanced";     1 for "My prompt is more important";     2 for "ControlNet is more important";
    @ColumnInfo(name = "pixel_perfect")
    val pixel_perfect: Boolean = false,

    // 使用图生图的图片
    @ColumnInfo(name = "use_imgImg")
    val use_imgImg: Boolean = false
) {
    override fun toString(): String {
        val processorRes = if (pixel_perfect)
            ""
        else
            "   \"processor_res\": ${processor_res},"

        return "{" +
                "   \"id\": \"${id}\"," +
                "   \"cn_name\": \"${cn_name}\"," +
                "   \"enabled\": \"${enabled.value}\"," +
                "   \"input_image\": \"${input_image.value.length}\"," +
//            "   \"mask\": \"${mask.value}\"," +
                "   \"module\": \"${module}\"," +
                "   \"model\": \"${model}\"," +
                "   \"resize_mode\": ${resize_mode}," +
                "   \"lowvram\": ${lowvram}," +
                "   $processorRes" +
                "   \"threshold_a\": $threshold_a," +
                "   \"threshold_b\": $threshold_b," +
                "   \"guidance_start\": ${guidance_start}," +
                "   \"guidance_end\": ${guidance_end}," +
                "   \"control_mode\": ${control_mode}," +
                "   \"pixel_perfect\": $pixel_perfect" +
                "}"
    }

    fun toRequest(): String {
        val processorRes = if (pixel_perfect)
            ""
        else
            "   \"processor_res\": ${processor_res},"

        return "{" +
                "   \"input_image\": \"${input_image.value}\"," +
//            "   \"mask\": \"${mask.value}\"," +
                "   \"module\": \"${module}\"," +
                "   \"model\": \"${model}\"," +
                "   \"resize_mode\": ${resize_mode}," +
                "   \"lowvram\": ${lowvram}," +
                "   $processorRes" +
                "   \"threshold_a\": $threshold_a," +
                "   \"threshold_b\": $threshold_b," +
                "   \"guidance_start\": ${guidance_start}," +
                "   \"guidance_end\": ${guidance_end}," +
                "   \"control_mode\": ${control_mode}," +
                "   \"pixel_perfect\": $pixel_perfect" +
                "}"
    }
}

@Dao
interface CNParamDao {
//    @Query("SELECT * FROM CNParam order by `id` desc limit 20")
    @Query("SELECT * FROM CNParam order by `id` desc")
    fun getParams(): Flow<List<CNParam>>

    @Query("SELECT * FROM CNParam WHERE `id` < (:before)  order by `id` desc")
    fun getSearchParams(before: Int): List<CNParam>

    @Update(CNParam::class)
    suspend fun update(cnParam: CNParam)
    @Insert
    suspend fun insert(vararg cnParam: CNParam)
    @Delete(CNParam::class)
    suspend fun delete(cnParam: CNParam): Int
}
