package com.alim.snaploader.Settings

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.alim.snaploader.BuildConfig
import com.alim.snaploader.Config.AppConfig
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.R
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class OthersActivity : AppCompatActivity() {

    private var show = true
    private val installCode = 11
    private val uninstallCode = 1
    private val myPermission = 101
    private lateinit var Extension: TextView
    private lateinit var progressDialog: ProgressDialog

    private val threadDownload = Thread {
        val ID = 101
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this, "DOWNLOAD").apply {
            setContentTitle("Extension")
            setOngoing(true)
            setContentText("Download in progress")
            setSmallIcon(R.drawable.file_download_white)
            priority = NotificationCompat.PRIORITY_LOW
        }

        runOnUiThread {
            progressDialog.setMessage("Downloading")
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.isIndeterminate = true
            progressDialog.isIndeterminate = false
            progressDialog.show()
        }

        NotificationManagerCompat.from(this).apply {
            builder.setProgress(100, 0, true)
            notify(ID, builder.build())
            var input: InputStream? = null
            var output: OutputStream? = null
            var connection: HttpURLConnection? = null
            try {
                val url = URL(AppConfig().updateExDownloadUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val fileLength: Int = connection.contentLength
                input = connection.inputStream
                val file = File(Environment.getExternalStorageDirectory().path+"/Android/data/com.alim.snaploader/Update/")
                if (!file.exists()) file.mkdirs()
                output = FileOutputStream(AppConfig().updateExPath)
                val data = ByteArray(4096)
                var total: Long = 0
                runOnUiThread {
                    progressDialog.max = fileLength
                }
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    if (show) {
                        show = false
                        builder.setProgress(100, ((total*100)/fileLength).toInt(), false)
                        notify(ID, builder.build())
                        Handler(Looper.getMainLooper()).postDelayed({
                            show = true
                        }, 750)
                    }
                    runOnUiThread {
                        progressDialog.progress = total.toInt()
                    }
                    output.write(data, 0, count)
                }
                builder.setSmallIcon(R.drawable.check_circle_white)
                builder.setOngoing(false)
                builder.setContentText("Download complete")
                    .setProgress(0, 0, false)
                notify(ID, builder.build())
                progressDialog.dismiss()
                install()
            } catch (e: Exception) {
                builder.setProgress(0, 0, false)
                builder.setOngoing(false)
                builder.setSmallIcon(R.drawable.ic_error_white)
                builder.setContentTitle("Error : $e")
                notify(ID, builder.build())
                progressDialog.dismiss()
            } finally {
                try {
                    output?.close()
                    input?.close()
                } catch (e: IOException) {
                    Log.println(Log.ASSERT,"ERROR", e.toString())
                }
                connection?.disconnect()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val applicationData = ApplicationData(this)
        if (applicationData.theme)
            setTheme(R.style.AppThemeDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others)
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        progressDialog = ProgressDialog(this)
        Extension = findViewById(R.id.ex_text)

        val ex = findViewById<FrameLayout>(R.id.extension)
        if (extractorAvailable(this))
            Extension.text = "Uninstall Extension"
        else
            Extension.text = "Install Extension"

        ex.setOnClickListener {
            if (extractorAvailable(this)) {
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.extension_dialog)
                dialog.findViewById<TextView>(R.id.title).text = "Uninstall Extension"
                dialog.findViewById<TextView>(R.id.des).text = resources.getString(R.string.uninstall_des)
                dialog.findViewById<Button>(R.id.dismiss).setOnClickListener { dialog.dismiss() }
                val uninstall = dialog.findViewById<Button>(R.id.download)
                uninstall.text = "Uninstall"
                uninstall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DELETE)
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
                    intent.data = Uri.parse("package:com.alim.extension")
                    startActivityForResult(intent, uninstallCode)
                    Log.println(Log.ASSERT,"TAG", "Touch")
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.extension_dialog)
                dialog.findViewById<TextView>(R.id.title).text = "Install Extension"
                dialog.findViewById<TextView>(R.id.des).text = resources.getString(R.string.install_des)
                dialog.findViewById<Button>(R.id.dismiss).setOnClickListener { dialog.dismiss() }
                val install = dialog.findViewById<Button>(R.id.download)
                install.text = "Install"
                install.setOnClickListener {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        myPermission
                    )
                    dialog.dismiss()
                }
                dialog.show()
            }
        }

        if (intent.getBooleanExtra("DIALOG", false))
            ex.callOnClick()
        else if (intent.getBooleanExtra("FIRST", false))
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                myPermission
            )
    }

    private fun install() {
        var intent: Intent? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkUri: Uri = FileProvider.getUriForFile(
                    this, BuildConfig.APPLICATION_ID + ".provider",
                    File(AppConfig().updateExPath)
                )
                intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                intent.data = apkUri
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            } else {
                val apkUri: Uri = Uri.fromFile(File(AppConfig().updateExPath))
                intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            //pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        } catch (e: java.lang.Exception) {
            Log.println(Log.ASSERT, "INSTALL", e.toString())
        }
        this.startActivityForResult(intent, installCode)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Updater"
            val descriptionText = "This notification channel will be used to show the updating progress"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("DOWNLOAD", name, importance)
                .apply { description = descriptionText }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == uninstallCode || requestCode == installCode) {
            if (extractorAvailable(this@OthersActivity))
                Extension.text = "Uninstall Extension"
            else
                Extension.text = "Install Extension"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            myPermission -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Thread(threadDownload).start()
                }
                else { Toast.makeText(this,"Permission Denied", Toast.LENGTH_LONG).show() }
                return
            }
        }
    }
}
