package hf.uy8q7d.listify.adapter

import hf.uy8q7d.listify.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import hf.uy8q7d.listify.data.Playlist

class PlaylistAdapter(private val listener: PlaylistItemClickListener) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private val items = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_playlist_list, parent, false)
        return PlaylistViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name

        holder.item = item
    }

    fun addItem(item: Playlist) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(playlistItems: List<Playlist>) {
        items.clear()
        items.addAll(playlistItems)
        notifyDataSetChanged()
    }

    fun deleteItem(item: Playlist) {
        val index = items.indexOf(item)
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface PlaylistItemClickListener {
        fun onPlaylistChanged(item: Playlist)
        fun onPlaylistDeleted(item: Playlist)
        fun onPlaylistSelected(item: Playlist)
    }

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView: TextView
        val removeButton: ImageButton
        var item: Playlist? = null

        init {
            nameTextView = itemView.findViewById(R.id.PlaylistNameTextView)
            removeButton = itemView.findViewById(R.id.PlaylistRemoveButton)
            removeButton.setOnClickListener {
                item?.let {
                    val deletedItem = it.copy()
                    deleteItem(it)
                    listener.onPlaylistDeleted(deletedItem)
                }
            }
            itemView.setOnClickListener {
                item?.let {
                    listener.onPlaylistSelected(it)
                }
            }
        }
    }
}