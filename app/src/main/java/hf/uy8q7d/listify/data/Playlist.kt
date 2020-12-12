package hf.uy8q7d.listify.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true) val playlistId: Long?,
    val name: String
) {
    override fun toString(): String {
        return name.toString()
    }
}