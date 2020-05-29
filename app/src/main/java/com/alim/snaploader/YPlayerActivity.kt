package com.alim.snaploader

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.alim.snaploader.Config.AppConfig
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.Provider
import com.google.android.youtube.player.YouTubePlayerView

class YPlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    private val RECOVERY_REQUEST = 1
    lateinit var title: TextView
    lateinit var view: TextView
    lateinit var youTubeView: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_y_player)
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        view = findViewById(R.id.views)
        title = findViewById(R.id.title)
        youTubeView =  findViewById(R.id.youtube_view)
        youTubeView.initialize(AppConfig().YOUTUBE_API_KEY, this)

        title.text = intent.getStringExtra("TITLE")
    }

    override fun onInitializationSuccess(p0: Provider?, p1: YouTubePlayer?, p2: Boolean) {
        if (!p2) {
            p1!!.cueVideo(intent.getStringExtra("LINK")) // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
            p1.setOnFullscreenListener {

            }
            p1.play()
        }
    }

    override fun onInitializationFailure(p0: Provider?, p1: YouTubeInitializationResult?) {
        if (p1!!.isUserRecoverableError) {
            p1.getErrorDialog(this, RECOVERY_REQUEST).show()
        } else {
            val error =
                String.format(getString(R.string.player_error), p1.toString())
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }
}
