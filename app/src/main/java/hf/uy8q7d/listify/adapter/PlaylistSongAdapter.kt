package hf.uy8q7d.listify.adapter

import hf.uy8q7d.listify.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import hf.uy8q7d.listify.data.Song
import java.util.*
import kotlin.collections.ArrayList

class PlaylistSongAdapter(private val listener: PlaylistSongItemClickListener) :
    RecyclerView.Adapter<PlaylistSongAdapter.SongViewHolder>(),
    Filterable {

    private val items = mutableListOf<Song>()
    private var itemsFull = mutableListOf<Song>()
    private var sortStatus = 0
    private val sortStatusMessages = arrayOf("In order of entry", "A to Z by title", "Z to A by title")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_playlist_song_list, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
        holder.artistTextView.text = item.artist
        holder.featuresTextView.text = if (item.features.isNullOrEmpty()) "" else " feat. ${item.features}"

        holder.item = item
    }

    fun update(songItems: List<Song>) {
        items.clear()
        items.addAll(songItems)
        itemsFull = items.toMutableList()
        notifyDataSetChanged()
    }

    fun deleteItem(item: Song) {
        val index = items.indexOf(item)
        items.removeAt(index)
        itemsFull.remove(item)
        notifyItemRemoved(index)
    }

    override fun getItemCount() = items.size

    fun getItemCountFull() = itemsFull.size

    fun sortItems(): String {
        sortStatus = (sortStatus + 1) % 3
        when(sortStatus) {
            1 -> items.sortBy { it.title }
            2 -> items.sortByDescending { it.title }
            else -> items.sortBy { it.songId }
        }
        notifyDataSetChanged()
        return sortStatusMessages[sortStatus]
    }

    interface PlaylistSongItemClickListener {
        fun onSongDeleted(item: Song)
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleTextView: TextView
        val artistTextView: TextView
        val featuresTextView: TextView
        val removeButton: ImageButton
        var item: Song? = null

        init {
            titleTextView = itemView.findViewById(R.id.tvPlaylistSongTitle)
            artistTextView = itemView.findViewById(R.id.tvPlaylistSongArtist)
            featuresTextView = itemView.findViewById(R.id.tvPlaylistSongFeatures)
            removeButton = itemView.findViewById(R.id.btnPlaylistRemoveSong)
            removeButton.setOnClickListener {
                item?.let {
                    val deletedItem = it.copy()
                    deleteItem(it)
                    listener.onSongDeleted(deletedItem)
                }
            }
        }
    }

    private val songFilter = object: Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<Song>()
            if (constraint.isNullOrEmpty()) {
                filteredList.addAll(itemsFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
                filteredList.addAll(itemsFull.filter { filterCondition(it, filterPattern) })
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.let {
                items.clear()
                items.addAll(results.values as ArrayList<Song>)
                notifyDataSetChanged()
            }
        }
    }

    override fun getFilter(): Filter {
        return songFilter
    }

    private fun filterCondition(song: Song, filterPattern: String): Boolean {
        if (song.title.toLowerCase(Locale.ROOT).contains(filterPattern) or
            song.artist.toLowerCase(Locale.ROOT).contains(filterPattern))
            return true
        song.features?.let {
            return it.toLowerCase(Locale.ROOT).contains(filterPattern)
        }
        return false
    }
}