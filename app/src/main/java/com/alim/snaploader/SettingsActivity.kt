package com.alim.snaploader

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.R
import com.alim.snaploader.Settings.*
import java.util.*

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        val applicationData = ApplicationData(this)
        if (applicationData.theme)
            setTheme(R.style.AppThemeDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        findViewById<FrameLayout>(R.id.content).setOnClickListener {
            startActivity(Intent(this, ContentActivity::class.java))
        }

        findViewById<FrameLayout>(R.id.theme).setOnClickListener {
            val intent = Intent(this, ThemeActivity::class.java)
            startActivityForResult(intent, 1)
        }

        findViewById<FrameLayout>(R.id.video).setOnClickListener {
            startActivity(Intent(this, VideoActivity::class.java))
        }

        findViewById<FrameLayout>(R.id.other_frame).setOnClickListener {
            startActivity(Intent(this, OthersActivity::class.java))
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data!!.getStringExtra("THEME") == "CHANGE")
            reCreate()
    }

    private fun reCreate() {
        Handler().postDelayed({
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            Objects.requireNonNull(overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            ))
            val goBack = Intent()
            goBack.putExtra("THEME","CHANGE")
            setResult(Activity.RESULT_OK, goBack)
            finish()
        },250)
    }
}
