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
import hf.uy8q7d.listify.adapter.PlaylistAdapter
import hf.uy8q7d.listify.data.ListifyDatabase
import hf.uy8q7d.listify.data.Playlist
import hf.uy8q7d.listify.view.PlaylistActivity
import hf.uy8q7d.listify.view.SongActivity
import kotlinx.android.synthetic.main.fragment_playlists.*
import kotlin.concurrent.thread

class PlaylistsFragment : Fragment(R.layout.fragment_playlists),
    NewPlaylistDialogFragment.NewPlaylistDialogListener,
    PlaylistAdapter.PlaylistItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaylistAdapter
    private lateinit var database: ListifyDatabase

    companion object {
        val NAME = "Playlists"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.database = ListifyDatabase.getInstance(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        fabPlaylistAddNew.setOnClickListener {
            NewPlaylistDialogFragment(this).show(
                requireFragmentManager(),
                NewPlaylistDialogFragment.TAG
            )
        }
    }

    private fun initRecyclerView() {
        recyclerView = PlaylistsRecyclerView
        adapter = PlaylistAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.dao().getAllPlaylists()
            activity?.runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onPlaylistChanged(item: Playlist) {
        TODO("Not yet implemented")
    }

    override fun onPlaylistDeleted(item: Playlist) {
        thread {
            item.playlistId?.let {
                database.dao().deletePlaylist(item)
                database.dao().deletePlaylistCrossReferences(item.playlistId)
                Log.d("MainActivity", "Playlist delete was successful")
            }
        }
    }

    override fun onPlaylistSelected(item: Playlist) {
        val showSongsIntent = Intent()
        showSongsIntent.setClass(requireContext(), PlaylistActivity::class.java)
        showSongsIntent.putExtra(PlaylistActivity.EXTRA_PLAYLIST_ID, item.playlistId)
        showSongsIntent.putExtra(PlaylistActivity.EXTRA_PLAYLIST_NAME, item.name)
        startActivity(showSongsIntent)
        Log.d("PlaylistsFragment","Playlist selected!")
    }

    override fun onPlaylistCreated(newItem: Playlist) {
        thread {
            val newId = database.dao().insertPlaylist(newItem)
            val newPlaylist = newItem.copy(
                playlistId = newId
            )
            activity?.runOnUiThread {
                adapter.addItem(newPlaylist)
            }
        }
    }
}