package com.alim.snaploader.Settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.R
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*

class ThemeActivity : AppCompatActivity() {

    private lateinit var applicationData: ApplicationData

    override fun onCreate(savedInstanceState: Bundle?) {
        applicationData = ApplicationData(this)
        if (applicationData.theme)
            setTheme(R.style.AppThemeDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        val switchTheme = findViewById<SwitchMaterial>(R.id.theme_switch)

        switchTheme.isChecked = applicationData.theme

        switchTheme.setOnClickListener {
            applicationData.theme = switchTheme.isChecked
            reCreate()
        }
    }

    private fun reCreate() {
        Handler().postDelayed({
            val intent = Intent(this, ThemeActivity::class.java)
            intent.putExtra("SESSION", true)
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
