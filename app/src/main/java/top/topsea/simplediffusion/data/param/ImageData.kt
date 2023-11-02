package top.topsea.simplediffusion.data.param

import androidx.annotation.Keep
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
import top.topsea.simplediffusion.data.BooleanConverter
import top.topsea.simplediffusion.data.DateConverter
import java.sql.Date

@Immutable
@TypeConverters(DateConverter::class, BooleanConverter::class)
@Entity
data class ImageData (
    @PrimaryKey(autoGenerate = true)
    val index: Int = 0,
    @ColumnInfo(name = "imageName")
    val imageName: String,
    @ColumnInfo(name = "isFavorite")
    var isFavorite: MutableState<Boolean> = mutableStateOf(false),
    @ColumnInfo(name = "info")
    val info: String,
    @ColumnInfo(name = "gen_date")
    val genDate: Date,
) {
    fun toSavable(): SavableImage {
        return SavableImage(
            index = index,
            imageName = imageName,
            isFavorite = isFavorite.value,
            info = info,
            genDate = genDate,
        )
    }
}

@Keep
data class SavableImage (
    val index: Int = 0,
    val imageName: String,
    var isFavorite: Boolean = false,
    val info: String,
    val genDate: Date,
) {
    fun toImageData(): ImageData {
        return ImageData(
            index = index,
            imageName = imageName,
            isFavorite = mutableStateOf(isFavorite),
            info = info,
            genDate = genDate,
        )
    }
}

@Dao
interface ImageDataDao {
    @Query("SELECT * FROM ImageData order by `index` desc")
    fun getAllImages(): Flow<List<ImageData>>

    @Query("SELECT * FROM ImageData WHERE `index` < (:before)  order by `index` desc limit 16")
    fun get16More(before: Int): List<ImageData>

    @Update(ImageData::class)
    suspend fun likeImage(image: ImageData)

    @Insert
    suspend fun insertAll(vararg images: ImageData)
    @Insert
    suspend fun insert(vararg images: ImageData)
    @Delete(ImageData::class)
    suspend fun delete(image: ImageData): Int

    // SQLite不能直接保存毫秒级时间戳，所以要除以1000
    @Query("DELETE FROM ImageData WHERE julianday('now') - julianday(gen_date / 1000, 'unixepoch') >= (:days)")
    suspend fun delete(days: Int)
    @Query("SELECT imageName FROM ImageData WHERE julianday('now') - julianday(gen_date / 1000, 'unixepoch') >= (:days)")
    fun getDeleteImages(days: Int): List<String>

}