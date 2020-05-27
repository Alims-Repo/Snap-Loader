package com.alim.snaploader.Settings

import android.content.*
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.Interface.BroadcastInterface
import com.alim.snaploader.R
import com.alim.snaploader.Reciever.LinkReceiver

class ContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (ApplicationData(this).theme)
            setTheme(R.style.AppThemeDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

    }
}
