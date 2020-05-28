package com.alim.snaploader.Settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.R

class ContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (ApplicationData(this).theme)
            setTheme(R.style.AppThemeDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

    }
}
