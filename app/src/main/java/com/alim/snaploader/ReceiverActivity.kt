package com.alim.snaploader

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.Interface.LinkInterface
import com.alim.snaploader.Reciever.LinkReceiver
import com.alim.snaploader.Services.DownloadService
import com.google.android.material.radiobutton.MaterialRadioButton

class ReceiverActivity : Activity() {

    var O8 = false
    var T2 = false

    var LT8 = ""
    var LS2 = ""
    var LT6 = ""
    var LAD = ""

    var name = ""

    private val myPermission = 105
    lateinit var radioGroup: RadioGroup
    lateinit var T8: MaterialRadioButton
    lateinit var S2: MaterialRadioButton
    lateinit var T6: MaterialRadioButton
    lateinit var AD: MaterialRadioButton

    lateinit var backgroundPlay: Button
    lateinit var download: Button
    lateinit var cancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        val applicationData = ApplicationData(this)
        if (applicationData.theme)
            setTheme(R.style.ReceiverThemeDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reciever)

        T8 = findViewById(R.id.t8)
        S2 = findViewById(R.id.s2)
        T6 = findViewById(R.id.t6)
        AD = findViewById(R.id.ad)
        radioGroup = findViewById(R.id.quality)

        cancel = findViewById(R.id.cancel)
        download = findViewById(R.id.download)
        backgroundPlay = findViewById(R.id.play_background)

        cancel.setOnClickListener {
            finish()
        }

        Log.println(Log.ASSERT,"Trigger","True")

        LinkReceiver().register(object : LinkInterface {
            override fun Cast(inte: Intent) {
                findViewById<ProgressBar>(R.id.loading).visibility = View.GONE
                if (inte.getStringExtra("ERROR")!=null) {
                    val er = findViewById<TextView>(R.id.error)
                    er.text = inte.getStringExtra("ERROR")
                    er.visibility = View.VISIBLE
                } else youTube(inte)
            }
        })

        download.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                myPermission
            )
        }

        if (extractorAvailable(this)) {
            if (savedInstanceState == null && Intent.ACTION_SEND == intent.action && intent.type != null && "text/plain" == intent.type) {
                val Link = intent.getStringExtra(Intent.EXTRA_TEXT)
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.type = "alim/code"
                intent.component = ComponentName(
                    "com.alim.extractor",
                    "com.alim.extractor.ExtractorActivity"
                )
                if (Link != null && (Link.contains("://youtu.be/") || Link.contains("youtube.com/watch?v="))) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_MAIN
                    //intent.type = "alim/code"
                    intent.component = ComponentName(
                        "com.alim.extension",
                        "com.alim.extension.ExtractService"
                    )
                    intent.putExtra(Intent.EXTRA_TEXT, Link)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        startForegroundService(intent)
                    else
                        startService(intent)
                    Log.println(Log.ASSERT,"Launch","True")
                } else if (Link != null && Link.contains("://www.facebook.com/")) {
                    intent.putExtra(Intent.EXTRA_TEXT, "FACEBOOK")
                    intent.putExtra("LINK", Link)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Unsupported Link", Toast.LENGTH_LONG).show()
                    finish()
                }
            } else {
                finish()
            }
        } else {
            Toast.makeText(this, "Extension required.",Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun download(url: String, ex: String) {
        Log.println(Log.ASSERT, "Link", url)
        Log.println(Log.ASSERT, "Name", name+ex)
        val intent = Intent(this, DownloadService::class.java)
        intent.putExtra(Intent.EXTRA_TEXT, "DOWNLOAD")
        intent.putExtra("LINK", url)
        intent.putExtra("NAME", name+ex)
        startService(intent)
        finish()
    }

    private fun youTube(data: Intent) {
        Log.println(Log.ASSERT, "Done", "")
        name = data.getStringExtra("Name")!!
        for (i in 0 until data.getIntExtra("Size", 0)) {
            try {
                val Tag = data.getIntExtra("Tag : $i", 0)
                val Url = data.getStringExtra("Url : $i")!!

                if (Tag == 18) {
                    O8 = true
                    LT6 = Url
                    //T6.text = "\tLow (360p)$Siz"
                    T6.visibility = View.VISIBLE }
                else if (Tag == 22) {
                    T2 = true
                    LS2 = Url
                    S2.visibility = View.VISIBLE }
                else if (Tag == 134 && !O8) { LT6 = Url
                    T6.visibility = View.VISIBLE }
                else if (Tag == 136 && !T2) { LS2 = Url
                    S2.visibility = View.VISIBLE }
                else if (Tag == 137) { LT8 = Url
                    T8.visibility = View.VISIBLE }
                else if (Tag == 140) { LAD = Url
                    AD.visibility = View.VISIBLE }
            } catch (e: Exception) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun extractorAvailable(context: Context): Boolean {
        return try {
            context.packageManager.getApplicationInfo("com.alim.extension", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            myPermission -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    when(radioGroup.checkedRadioButtonId) {
                        R.id.t8 ->  download(LT8, ".mp4")
                        R.id.s2 -> download(LS2, ".mp4")
                        R.id.t6 ->  download(LT6, ".mp4")
                        R.id.ad -> download(LAD, ".mp3")
                    }
                }
                else { Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show() }
                return
            }
        }
    }
}