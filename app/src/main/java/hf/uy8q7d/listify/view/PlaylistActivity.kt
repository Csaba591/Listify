package hf.uy8q7d.listify.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hf.uy8q7d.listify.R
import hf.uy8q7d.listify.adapter.PlaylistSongAdapter
import hf.uy8q7d.listify.data.*
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlin.concurrent.thread

class PlaylistActivity : AppCompatActivity(),
    PlaylistSongAdapter.PlaylistSongItemClickListener, SearchView.OnQueryTextListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaylistSongAdapter
    private lateinit var database: ListifyDatabase
    private lateinit var playlistWithSongs: PlaylistWithSongs
    private var btSort: MenuItem? = null

    companion object {
        private const val TAG = "PlaylistActivity"
        const val EXTRA_PLAYLIST_ID = "extra.playlistId"
        const val EXTRA_PLAYLIST_NAME = "extra.name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PlaylistActivity", "PlaylistActivity created")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)
        val playlistName = intent.getStringExtra(EXTRA_PLAYLIST_NAME)
        supportActionBar?.title = getString(R.string.songs_of, playlistName)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.database = ListifyDatabase.getInstance(this)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = rvPlaylistSongs
        adapter = PlaylistSongAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadItemsInBackground() {
        thread {
            val playlistId = intent.getLongExtra(EXTRA_PLAYLIST_ID, -1)
            val noPlaylistId: Long = -1
            if (playlistId == noPlaylistId) {
                Log.e("PlaylistActivity", "playlistId in intent was null")
                return@thread
            }
            playlistWithSongs = database.dao().getPlaylistWithSongsById(playlistId)
            Log.d("PlaylistActivity", "playlistWithSongs.size = ${playlistWithSongs.songs.size}")
            runOnUiThread {
                adapter.update(playlistWithSongs.songs)
                updateVisibility()
            }
        }
    }

    private fun updateVisibility() {
        runOnUiThread {
            if (adapter.getItemCountFull() == 0) {
                rvPlaylistSongs.visibility = View.INVISIBLE
                tvPlaylistEmpty.visibility = View.VISIBLE
            } else {
                rvPlaylistSongs.visibility = View.VISIBLE
                tvPlaylistEmpty.visibility = View.INVISIBLE
            }
        }
    }

    override fun onSongDeleted(item: Song) {
        thread {
            try {
                val songId = item.songId!!
                val playlistId = playlistWithSongs.playlist.playlistId!!
                val playlistSongCrossRef = PlaylistSongCrossRef(playlistId, songId)
                database.dao().deleteSongFromPlaylist(playlistSongCrossRef)
                updateVisibility()
                Log.d("MainActivity", "Song delete from playlist was successful")
            } catch (e: NullPointerException) {
                Log.e("PlaylistActivity", "playlistId or songId was null when trying to delete crossreference")
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_playlist, menu)
        val searchView = menu?.findItem(R.id.searchPlaylistSongs)?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return true
    }

    fun sortPlaylistsByName(item: MenuItem) {
        if (adapter.itemCount == 0) {
            Toast.makeText(this, "The playlist is still empty!", Toast.LENGTH_SHORT).show()
            return
        }
        val sortOrder = adapter.sortItems()
        Toast.makeText(this, sortOrder, Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            adapter.filter.filter(newText)
        }
        return false
    }
}