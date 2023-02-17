package  com.example.todofinal


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Database(entities = [ToDo::class], version = 1, exportSchema = false)
abstract class RoomSingleton : RoomDatabase() {
    abstract fun todoDao():TodoDAO

    companion object {
        private var INSTANCE: RoomSingleton? = null
        fun getInstance(context: Context): RoomSingleton {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    RoomSingleton::class.java,
                    "roomdb")
                    .build()
            }
            return INSTANCE as RoomSingleton
        }
    }
}
@Entity(tableName = "todoTBL")
class ToDo( @PrimaryKey
            var id:Long?,

            @ColumnInfo(name = "uuid")
            var fullName: String,

            @ColumnInfo(name = "notes")
            var notes:String) {

}
@Dao
interface TodoDAO {
    @Query("SELECT * FROM todoTBL ORDER BY id DESC")
    fun getTodos(): LiveData<MutableList<ToDo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo:ToDo)

    @Update
    suspend fun update(todo:ToDo)

    @Delete
    suspend fun delete(todo:ToDo)

    @Query("DELETE FROM todoTBL")
    suspend fun clear()
    // kishan
}