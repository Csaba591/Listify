package hf.uy8q7d.listify.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.room.Room
import hf.uy8q7d.listify.R
import hf.uy8q7d.listify.data.ListifyDatabase
import hf.uy8q7d.listify.data.Playlist
import hf.uy8q7d.listify.data.Song
import kotlin.concurrent.thread

class PlaylistSelectDialogFragment(private val listener: PlaylistSelectedListener, private val forSong: Song) : DialogFragment() {
    private lateinit var spinner: Spinner
    private lateinit var database: ListifyDatabase
    private lateinit var playlists: List<Playlist>
    private lateinit var adapter: ArrayAdapter<Playlist>

    companion object {
        const val TAG = "PlaylistSelectDialogFragment"
    }

    interface PlaylistSelectedListener {
        fun onPlaylistSelectedForSong(playlist: Playlist, song: Song)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        database = ListifyDatabase.getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        loadPlaylists()
    }

    private fun loadPlaylists() {
        thread {
            playlists = database.dao().getAllPlaylists()
            adapter.addAll(playlists)
        }
    }

    private fun getContentView(): View {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_select_playlist_for_song, null)
        spinner = contentView.findViewById(R.id.PlaylistSelectSpinner)
        spinner.adapter = adapter
        if (adapter.isEmpty)
            spinner.visibility = View.INVISIBLE
        return contentView
    }

    private fun getSelectedPlaylist(): Playlist {
        return playlists[spinner.selectedItemPosition]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_playlist)
            .setView(getContentView())
            .setPositiveButton(R.string.add) { dialogInterface, i ->
                listener.onPlaylistSelectedForSong(getSelectedPlaylist(), forSong);
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }
}