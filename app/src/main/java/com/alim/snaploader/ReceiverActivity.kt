package com.alim.snaploader

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import com.alim.snaploader.R
import com.google.android.material.radiobutton.MaterialRadioButton

class ReceiverActivity : Activity() {

    var O8 = false
    var T2 = false

    var LT8 = ""
    var LS2 = ""
    var LT6 = ""
    var LAD = ""

    var name = ""

    lateinit var radioGroup: RadioGroup
    lateinit var T8: MaterialRadioButton
    lateinit var S2: MaterialRadioButton
    lateinit var T6: MaterialRadioButton
    lateinit var AD: MaterialRadioButton

    lateinit var backgroundPlay: Button
    lateinit var download: Button
    lateinit var cancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
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

        download.setOnClickListener {
            when(radioGroup.checkedRadioButtonId) {
                R.id.t8 ->  download(LT8, ".mp4")
                R.id.s2 -> download(LS2, ".mp4")
                R.id.t6 ->  download(LT6, ".mp4")
                R.id.ad -> download(LAD, ".mp3")
            }
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
                    intent.putExtra(Intent.EXTRA_TEXT, "YOUTUBE")
                    intent.putExtra("LINK", Link)
                    startActivityForResult(intent, 1)
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
            Toast.makeText(this, "Extractor Extension required.",Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun download(url: String, ex: String) {
        Log.println(Log.ASSERT, "LINK", url)
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "alim/code"
        intent.component = ComponentName(
            "com.alim.extractor",
            "com.alim.extractor.ExtractorActivity"
        )
        intent.putExtra(Intent.EXTRA_TEXT, "DOWNLOAD")
        intent.putExtra("LINK", url)
        intent.putExtra("NAME", name+ex)
        startActivity(intent)
        finish()
    }

    private fun youTube(data: Intent) {
        Log.println(Log.ASSERT, "Done", "")
        name = data.getStringExtra("NAME")!!
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1)
            if (resultCode == RESULT_OK && data != null) youTube(data) else finish()
        super.onActivityResult(requestCode, resultCode, data)
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