package com.alim.snaploader.Settings

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.R
import com.google.android.material.switchmaterial.SwitchMaterial

class VideoActivity : AppCompatActivity() {

    private lateinit var applicationData: ApplicationData

    override fun onCreate(savedInstanceState: Bundle?) {
        applicationData = ApplicationData(this)
        if (applicationData.theme)
            setTheme(R.style.AppThemeDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        val switchPlayer = findViewById<SwitchMaterial>(R.id.external_switch)

        if (extractorAvailable(this))
            switchPlayer.isChecked = applicationData.externalPlayer
        else
            switchPlayer.isClickable = false

        switchPlayer.setOnClickListener {
            applicationData.externalPlayer = switchPlayer.isChecked
        }
    }

    private fun extractorAvailable(context: Context): Boolean {
        return try {
            context.packageManager.getApplicationInfo("com.alim.extractor", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
