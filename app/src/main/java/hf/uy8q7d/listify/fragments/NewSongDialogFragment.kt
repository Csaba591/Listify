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
import hf.uy8q7d.listify.data.Song

class NewSongDialogFragment(private val listener: NewSongDialogListener) : DialogFragment() {
    private lateinit var titleEditText: EditText
    private lateinit var artistEditText: EditText
    private lateinit var featuresEditText: EditText

    interface NewSongDialogListener {
        fun onSongCreated(newItem: Song)
    }

    private fun getContentView(): View {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.dialog_new_song, null)
        titleEditText = contentView.findViewById(R.id.SongTitleEditText)
        artistEditText = contentView.findViewById(R.id.SongArtistEditText)
        featuresEditText = contentView.findViewById(R.id.SongFeaturesEditText)
        return contentView
    }

    private fun isValid(): Boolean {
        return titleEditText.text.isNotEmpty() and artistEditText.text.isNotEmpty()
    }

    private fun getSong() = Song(
        songId = null,
        title = titleEditText.text.toString(),
        artist = artistEditText.text.toString(),
        features = if (featuresEditText.text.isNotEmpty()) featuresEditText.text.toString() else null,
        durationInSeconds = null
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_song)
            .setView(getContentView())
            .setPositiveButton(R.string.add) { dialogInterface, i ->
                if (isValid()) {
                    listener.onSongCreated(getSong());
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    companion object {
        const val TAG = "NewSongDialogFragment"
    }
}