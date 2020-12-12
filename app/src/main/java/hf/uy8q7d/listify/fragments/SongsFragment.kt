package hf.uy8q7d.listify.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import hf.uy8q7d.listify.R
import hf.uy8q7d.listify.adapter.SongAdapter
import hf.uy8q7d.listify.data.ListifyDatabase
import hf.uy8q7d.listify.data.Playlist
import hf.uy8q7d.listify.data.PlaylistSongCrossRef
import hf.uy8q7d.listify.data.Song
import hf.uy8q7d.listify.view.PlaylistActivity
import hf.uy8q7d.listify.view.SongActivity
import kotlinx.android.synthetic.main.fragment_songs.*
import kotlin.concurrent.thread

class SongsFragment : Fragment(R.layout.fragment_songs),
    SongAdapter.SongItemClickListener,
    NewSongDialogFragment.NewSongDialogListener,
    PlaylistSelectDialogFragment.PlaylistSelectedListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SongAdapter
    private lateinit var database: ListifyDatabase

    companion object {
        val NAME = "Songs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.database = ListifyDatabase.getInstance(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        fabSongsAddNew.setOnClickListener {
            NewSongDialogFragment(this).show(
                requireFragmentManager(),
                NewSongDialogFragment.TAG
            )
        }
    }

    private fun initRecyclerView() {
        recyclerView = SongsRecyclerView
        adapter = SongAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.dao().getAllSongs()
            activity?.runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onSongChanged(item: Song) {
        TODO("Not yet implemented")
    }

    override fun onSongDeleted(item: Song) {
        thread {
            database.dao().deleteSong(item)
            Log.d("MainActivity", "Song delete was successful")
        }
    }

    override fun onSongSelected(item: Song) {
        val showSongIntent = Intent()
        showSongIntent.setClass(requireContext(), SongActivity::class.java)
        showSongIntent.putExtra(SongActivity.EXTRA_SONG_ID, item.songId)
        startActivity(showSongIntent)
        Log.d("SongsFragment","Song selected!")
    }

    override fun onSongAddToPlaylist(item: Song) {
        thread {
            val numberOfPlaylists = database.dao().getPlaylistCount()
            if (numberOfPlaylists == 0) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Add some playlists first!", Toast.LENGTH_SHORT).show()
                }
                return@thread
            }
            requireActivity().runOnUiThread {
                PlaylistSelectDialogFragment(this, item)
                    .show(requireFragmentManager(), PlaylistSelectDialogFragment.TAG)
            }
        }
    }

    override fun onSongCreated(newItem: Song) {
        thread {
            val newId = database.dao().insertSong(newItem)
            val newSong = newItem.copy(
                songId = newId
            )
            activity?.runOnUiThread {
                adapter.addItem(newSong)
            }
        }
    }

    override fun onPlaylistSelectedForSong(playlist: Playlist, song: Song) {
        try {
            val playlistId = playlist.playlistId!!
            val songId = song.songId!!
            thread {
                val playListSongCrossRef = PlaylistSongCrossRef(playlistId, songId)
                database.dao().insertPlaylistSongCrossRef(playListSongCrossRef)
                Log.d("SongsFragment", "CrossRef inserted into db!")
            }
        } catch (e: NullPointerException) {
            Log.d("SongsFragment", "Error inserting new crossreference! playlistId or songId was null.")
        }
    }

}