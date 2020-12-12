package hf.uy8q7d.listify.adapter

import hf.uy8q7d.listify.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import hf.uy8q7d.listify.data.Song

class SongAdapter(private val listener: SongItemClickListener) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private val items = mutableListOf<Song>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_song_list, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
        holder.artistTextView.text = item.artist
        holder.featuresTextView.text = if (item.features.isNullOrEmpty()) "" else " feat. ${item.features}"

        holder.item = item
    }

    fun addItem(item: Song) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(songItems: List<Song>) {
        items.clear()
        items.addAll(songItems)
        notifyDataSetChanged()
    }

    fun deleteItem(item: Song) {
        val index = items.indexOf(item)
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface SongItemClickListener {
        fun onSongChanged(item: Song)
        fun onSongDeleted(item: Song)
        fun onSongSelected(item: Song)
        fun onSongAddToPlaylist(item: Song)
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleTextView: TextView
        val artistTextView: TextView
        val featuresTextView: TextView
        val addToPlaylistButton: ImageButton
        val removeButton: ImageButton
        var item: Song? = null

        init {
            titleTextView = itemView.findViewById(R.id.SongTitleTextView)
            artistTextView = itemView.findViewById(R.id.SongArtistTextView)
            featuresTextView = itemView.findViewById(R.id.SongFeaturesTextView)
            addToPlaylistButton = itemView.findViewById(R.id.SongAddToPlaylistButton)
            removeButton = itemView.findViewById(R.id.SongRemoveButton)
            removeButton.setOnClickListener {
                item?.let {
                    val deletedItem = it.copy()
                    deleteItem(it)
                    listener.onSongDeleted(deletedItem)
                }
            }
            addToPlaylistButton.setOnClickListener{
                item?.let {
                    listener.onSongAddToPlaylist(it)
                }
            }
            itemView.setOnClickListener {
                item?.let {
                    listener.onSongSelected(it)
                }
            }
        }
    }
}