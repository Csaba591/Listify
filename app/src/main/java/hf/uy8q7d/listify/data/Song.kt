package hf.uy8q7d.listify.data
import androidx.room.Entity;
import androidx.room.PrimaryKey

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true) val songId: Long?,
    var title: String,
    var artist: String,
    var features: String?,
    var durationInSeconds: Int?
)