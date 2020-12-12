package hf.uy8q7d.listify.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Playlist::class, Song::class, PlaylistSongCrossRef::class], version = 1)
abstract class ListifyDatabase : RoomDatabase() {
    abstract fun dao(): Dao

    companion object {
        @Volatile private var instance: ListifyDatabase? = null

        fun getInstance(context: Context): ListifyDatabase {
            return instance?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context): ListifyDatabase {
            return Room.databaseBuilder(context, ListifyDatabase::class.java, "listifydb").build()
        }
    }
}