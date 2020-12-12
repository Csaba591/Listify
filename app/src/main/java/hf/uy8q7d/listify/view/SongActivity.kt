package hf.uy8q7d.listify.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import hf.uy8q7d.listify.R
import hf.uy8q7d.listify.data.*
import kotlinx.android.synthetic.main.activity_song.*
import java.net.URLEncoder
import kotlin.concurrent.thread

class SongActivity : AppCompatActivity() {
    private lateinit var database: ListifyDatabase
    private lateinit var song: Song
    private lateinit var queue: RequestQueue

    companion object {
        private const val TAG = "SongActivity"
        const val EXTRA_SONG_ID = "extra.songId"
        const val SERVICE_URL_LYRICS = "https://orion.apiseeds.com/api/music/lyric/"
        const val API_KEY_LYRICS = "ZTbRbPUTKrn4mzGAiBCl6KNScPL1gI2idZfe03VvzzgUXohFnt3ynZyOgmH5iBQK"
        const val ACCESS_TOKEN_GENIUS = "UPdgKwhXBnCrI1p2x01gM4lpXJVpFcONAFKOR81IDK7ztLivNsOmv9SVIJKxhLZW"
        const val SERVICE_URL_GENIUS = "https://api.genius.com/search"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)
        supportActionBar?.title = "Song details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.database = ListifyDatabase.getInstance(this)
        this.queue = Volley.newRequestQueue(this)
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread {
            val songId = intent.getLongExtra(EXTRA_SONG_ID, -1)
            song = database.dao().getSongById(songId)
            runOnUiThread {
                tvSongTitle.text = song.title
                tvSongArtist.text = song.artist
                tvSongFeatures.text = song.features?: ""
            }
            fetchLyricsFromApi()
            fetchCoverArtFromApi()
        }
    }

    private fun getLyricsRequestUrl(): String {
        val artist = URLEncoder.encode(song.artist, "utf-8")
        val title = URLEncoder.encode(song.title, "utf-8")
        val encodedUrl = "${SERVICE_URL_LYRICS}${artist}/${title}?apikey=${API_KEY_LYRICS}"
        Log.d("SongActivity", "Sending request to $encodedUrl")
        return encodedUrl
    }

    private fun fetchLyricsFromApi() {
        val lyricsRequest = JsonObjectRequest(Request.Method.GET, getLyricsRequestUrl(), null,
            Response.Listener { response ->
                tvSongLyrics.text = response
                    .getJSONObject("result")
                    .getJSONObject("track")
                    .getString("text")
            },
            Response.ErrorListener { error ->
                tvSongLyrics.text = getString(R.string.lyrics_api_error)
            })
        queue.add(lyricsRequest)
    }

    private fun getCoverRequestUrl(): String {
        val params = URLEncoder.encode("${song.artist} ${song.title}", "utf-8")
        val encodedUrl = "${SERVICE_URL_GENIUS}?access_token=${ACCESS_TOKEN_GENIUS}&q=$params"
        Log.d("SongActivity", "Sending request to $encodedUrl")
        return encodedUrl
    }

    private fun fetchCoverArtFromApi() {
        val coverRequest = JsonObjectRequest(Request.Method.GET, getCoverRequestUrl(), null,
            Response.Listener { response ->
                val imageUrl = response
                    .getJSONObject("response")
                    .getJSONArray("hits")
                    .getJSONObject(0)
                    .getJSONObject("result")
                    .getString("song_art_image_url")
                Picasso.get().load(imageUrl).into(ivSongCover)
                ivSongCover.visibility = View.VISIBLE
            },
            Response.ErrorListener { error ->
                ivSongCover.visibility = View.GONE
                Log.d("SongActivity", "Error fetching cover :(")
            })
        queue.add(coverRequest)
    }
}