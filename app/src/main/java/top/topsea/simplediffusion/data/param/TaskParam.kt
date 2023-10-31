package top.topsea.simplediffusion.data.param

import androidx.compose.runtime.Immutable
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
import top.topsea.simplediffusion.data.BasicParamConverter
import top.topsea.simplediffusion.data.ImageDataConverter

@Immutable
@TypeConverters(ImageDataConverter::class, BasicParamConverter::class)
@Entity
class TaskParam(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "image")
    val image: ImageData?,
    @ColumnInfo(name = "param")
    val param: BasicParam,
    @ColumnInfo(name = "taskID")
    var taskID: String = "",
    @ColumnInfo(name = "genInfo")
    var genInfo: String = "",
) {
    override fun toString(): String {
        return "" +
                "taskID: $taskID" +
                "genInfo: $genInfo" +
                ""
    }
}

@Dao
interface TaskParamDao {
    //    @Query("SELECT * FROM TxtParam order by `id` desc limit 20")
    @Query("SELECT * FROM TaskParam WHERE length(genInfo)<=0 order by `id` ")
    fun getTaskParam(): Flow<List<TaskParam>>
    @Query("SELECT * FROM TaskParam WHERE length(genInfo)>0 order by `id` ")
    fun getErrorTask(): Flow<List<TaskParam>>

    @Update(TaskParam::class)
    suspend fun update(taskParam: TaskParam)
    @Insert
    suspend fun insert(vararg taskParam: TaskParam)
    @Delete(TaskParam::class)
    suspend fun delete(taskParam: TaskParam): Int
}