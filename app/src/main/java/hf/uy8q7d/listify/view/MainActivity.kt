package hf.uy8q7d.listify.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.google.android.material.tabs.TabLayoutMediator
import hf.uy8q7d.listify.R
import hf.uy8q7d.listify.adapter.ViewPagerAdapter
import hf.uy8q7d.listify.data.ListifyDatabase
import hf.uy8q7d.listify.data.Playlist
import hf.uy8q7d.listify.data.Song
import hf.uy8q7d.listify.fragments.NewPlaylistDialogFragment
import hf.uy8q7d.listify.fragments.NewSongDialogFragment
import hf.uy8q7d.listify.fragments.PlaylistsFragment
import hf.uy8q7d.listify.fragments.SongsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var database: ListifyDatabase
    private lateinit var songsFragment: SongsFragment
    private lateinit var playlistsFragment: PlaylistsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.database = ListifyDatabase.getInstance(this)

        songsFragment = SongsFragment()
        playlistsFragment = PlaylistsFragment()
        val fragments = arrayListOf(songsFragment, playlistsFragment)
        viewPager.adapter = ViewPagerAdapter(this, fragments)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> SongsFragment.NAME
                else -> PlaylistsFragment.NAME
            }
        }.attach()
    }
}