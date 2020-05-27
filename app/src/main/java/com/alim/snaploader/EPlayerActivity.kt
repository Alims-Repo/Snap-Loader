package com.alim.snaploader

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.Interface.BroadcastInterface
import com.alim.snaploader.Reciever.LinkReceiver
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_e_player.*

class EPlayerActivity : AppCompatActivity() {

    companion object { var run = true
        var uri: Uri = Uri.parse("https://www.radiantmediaplayer.com/media/bbb-360p.mp4") }

    var O8 = false
    var T2 = false

    var LT8 = ""
    var LS2 = ""
    var LT6 = ""
    var LAD = ""

    var first = true
    var fullscreen = false
    private var currentWindow = 0
    private var playWhenReady = true
    private var playbackPosition: Long = 0
    lateinit var fullscreenButton: ImageView
    private var player: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        if (ApplicationData(this).theme)
            setTheme(R.style.AppThemeDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_e_player)
        val frameLayout = findViewById<FrameLayout>(R.id.player_frame)
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val screenHeight = metrics.heightPixels
        frameLayout.minimumHeight = (screenHeight / 4)

        playerView = findViewById(R.id.video_view)
        fullscreenButton = video_view.findViewById(R.id.exo_fullscreen_icon)
        fullscreenButton.setOnClickListener { fullScreen() }

        LinkReceiver().register(object : BroadcastInterface {
            override fun Cast(inte: Intent) {
                if (inte.getStringExtra("ERROR")!=null) finish()
                else youTube(inte)
            }
        })

        val intentExternal = Intent()
        intentExternal.action = Intent.ACTION_MAIN
        intentExternal.component = ComponentName(
            "com.alim.extension",
            "com.alim.extension.ExtractService")
        intentExternal.putExtra(Intent.EXTRA_TEXT, intent.getStringExtra("LINK")!!)
        if (SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intentExternal)
        else
            startService(intentExternal)
    }

    private fun initializePlayer() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        player = ExoPlayerFactory.newSimpleInstance(this)
        playerView.player = player
        val mediaSource = buildMediaSource(uri)
        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)
        player!!.prepare(mediaSource, false, false)
        player!!.addListener(object : Player.EventListener{
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (fullscreen) {
                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                if (playerView.visibility != View.VISIBLE) {
                    findViewById<ProgressBar>(R.id.loading_prog).visibility = View.GONE
                    playerView.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun buildMediaSource(uri: Uri): MediaSource? {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(this, "exoplayer-codelab")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun fullScreen() {
        if (fullscreen) {
            fullscreenButton.background = ContextCompat.getDrawable(
                this, R.drawable.ic_fullscreen_black_24dp)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            if (supportActionBar != null) {
                supportActionBar!!.show()
            }
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            val params: FrameLayout.LayoutParams =
                video_view.layoutParams as FrameLayout.LayoutParams
            params.width = FrameLayout.LayoutParams.MATCH_PARENT
            params.height = FrameLayout.LayoutParams.WRAP_CONTENT
            video_view.layoutParams = params
            fullscreen = false
        } else {
            fullscreenButton.background = ContextCompat.getDrawable(
                this, R.drawable.ic_fullscreen_exit_black_24dp)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            if (supportActionBar != null) {
                supportActionBar!!.hide()
            }
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            val params: FrameLayout.LayoutParams =
                video_view.layoutParams as FrameLayout.LayoutParams

            params.width = FrameLayout.LayoutParams.MATCH_PARENT
            params.height = FrameLayout.LayoutParams.MATCH_PARENT
            video_view.layoutParams = params
            fullscreen = true
        }
    }

    private fun youTube(data: Intent) {
        Log.println(Log.ASSERT, "Done", "")
        //name = data.getStringExtra("NAME")!!
        for (i in 0 until data.getIntExtra("Size", 0)) {
            try {
                val Tag = data.getIntExtra("Tag : $i", 0)
                val Url = data.getStringExtra("Url : $i")!!

                if (Tag == 18) {
                    O8 = true
                    LT6 = Url }
                else if (Tag == 22) {
                    T2 = true
                    LS2 = Url }
                else if (Tag == 134 && !O8) { LT6 = Url }
                else if (Tag == 136 && !T2) { LS2 = Url }
                else if (Tag == 137) { LT8 = Url }
                else if (Tag == 140) { LAD = Url }
            } catch (e: Exception) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        uri = Uri.parse(LT6)
        initializePlayer()
    }

    override fun onBackPressed() {
        if (fullscreen) fullscreenButton.callOnClick()
        else super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        if (!first) initializePlayer()
    }

    override fun onStop() {
        if (SDK_INT >= 24) releasePlayer()
        run = false
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        if (SDK_INT < 24)
            releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}
