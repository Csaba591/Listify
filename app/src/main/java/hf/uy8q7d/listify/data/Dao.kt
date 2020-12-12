package hf.uy8q7d.listify.data

import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    @Transaction
    @Query("SELECT * FROM Playlist")
    fun getPlaylistsWithSongs(): List<PlaylistWithSongs>

    @Query("SELECT * FROM Playlist where playlistId = :playlistId")
    fun getPlaylistWithSongsById(playlistId: Long): PlaylistWithSongs

    @Query("SELECT * FROM Playlist")
    fun getAllPlaylists(): List<Playlist>

    @Query("SELECT COUNT(1) FROM Playlist")
    fun getPlaylistCount(): Int

    @Query("SELECT * FROM Song")
    fun getAllSongs(): List<Song>

    @Query("SELECT * FROM Song where songId = :songId")
    fun getSongById(songId: Long): Song

    @Query("SELECT COUNT(1) FROM PlaylistSongCrossRef where playlistId = :playlistId")
    fun getNumberOfSongsInPlaylist(playlistId: Long): Int

    @Insert
    fun insertSong(song: Song): Long

    @Insert
    fun insertPlaylist(playlist: Playlist): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef): Long

    @Delete
    fun deletePlaylist(playlist: Playlist)

    @Delete
    fun deleteSong(song: Song)

    @Delete
    fun deleteSongFromPlaylist(playlistSongCrossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM PlaylistSongCrossRef WHERE playlistId = :playlistId")
    fun deletePlaylistCrossReferences(playlistId: Long)
}