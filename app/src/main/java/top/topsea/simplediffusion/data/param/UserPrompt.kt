package top.topsea.simplediffusion.data.param

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Immutable
@Entity
data class UserPrompt (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "prompt_name")
    var name: String = "Name",
    @ColumnInfo(name = "prompt_value")
    var alias: String = "",
    @ColumnInfo(name = "prompt_path")
    val path: String = "Simple",
)

@Dao
interface UserPromptDao {
    @Query("SELECT * FROM UserPrompt order by `id` desc")
    fun getAllPrompt(): Flow<List<UserPrompt>>

    @Upsert(UserPrompt::class)
    suspend fun update(prompt: UserPrompt)
    @Insert
    suspend fun insert(vararg prompt: UserPrompt)
    @Delete(UserPrompt::class)
    suspend fun delete(prompt: UserPrompt): Int
}
