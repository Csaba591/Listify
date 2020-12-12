package hf.uy8q7d.listify.fragments

import hf.uy8q7d.listify.R

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hf.uy8q7d.listify.data.Playlist

class NewPlaylistDialogFragment(private val listener: NewPlaylistDialogListener) : DialogFragment() {
    private lateinit var nameEditText: EditText

    interface NewPlaylistDialogListener {
        fun onPlaylistCreated(newItem: Playlist)
    }

    private fun getContentView(): View {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.dialog_new_playlist, null)
        nameEditText = contentView.findViewById(R.id.PlaylistNameEditText)
        return contentView
    }

    private fun isValid() = nameEditText.text.isNotEmpty()

    private fun getPlaylist() = Playlist(
        playlistId = null,
        name = nameEditText.text.toString()
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_playlist)
            .setView(getContentView())
            .setPositiveButton(R.string.add) { dialogInterface, i ->
                if (isValid()) {
                    listener.onPlaylistCreated(getPlaylist());
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    companion object {
        const val TAG = "NewPlaylistDialogFragment"
    }
}