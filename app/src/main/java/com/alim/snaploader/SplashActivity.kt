package com.alim.snaploader

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.alim.snaploader.BuildConfig
import com.alim.snaploader.Config.AppConfig
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.R
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class SplashActivity : AppCompatActivity() {

    private var show = true
    private val myPermission = 101
    private val myPermission2 = 102
    private lateinit var progressDialog: ProgressDialog

    private val threadCheck = Thread {
        try {
            val httpClient = DefaultHttpClient()
            val httpGet = HttpGet(AppConfig().updateCheckUrl)
            val response = httpClient.execute(httpGet)
            val httpEntity = response.entity
            val jsonArray = JSONArray(EntityUtils.toString(httpEntity))
            val jsonObject: JSONObject = jsonArray.getJSONObject(0)
            val versionName: String = jsonObject.getJSONObject("apkData").getString("versionName")
            val versioncode: Int = jsonObject.getJSONObject("apkData").getInt("versionCode")
            val versionCode = BuildConfig.VERSION_CODE
            when {
                versioncode < versionCode -> {
                    runOnUiThread {
                        Toast.makeText(this, "Server is under maintenance.", Toast.LENGTH_SHORT).show()
                    }
                }
                versioncode > versionCode -> {
                    Log.println(Log.ASSERT,"DATA","$versioncode and $versionCode")
                    runOnUiThread {
                        AlertDialog.Builder(this)
                            .setTitle("Update Snap Loader")
                            .setCancelable(false)
                            .setMessage("Current Version : ${BuildConfig.VERSION_NAME}\nUpdated version : $versionName")
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            .setPositiveButton("Update") { dialog, _ ->
                                dialog.dismiss()
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    myPermission
                                )
                            }
                            .show()
                    }
                }
                else -> {
                    Thread(threadCheckEx).start()
                }
            }
        } catch (e: Exception) {
            Log.println(Log.ASSERT,"UPDATE ERROR",e.toString())
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private val threadCheckEx = Thread {
        try {
            val httpClient = DefaultHttpClient()
            val httpGet = HttpGet(AppConfig().updateExCheckUrl)
            val response = httpClient.execute(httpGet)
            val httpEntity = response.entity
            val jsonArray = JSONArray(EntityUtils.toString(httpEntity))
            val jsonObject: JSONObject = jsonArray.getJSONObject(0)
            val versionName: String = jsonObject.getJSONObject("apkData").getString("versionName")
            val versioncode: Int = jsonObject.getJSONObject("apkData").getInt("versionCode")
            var versionCode = versioncode
            var versionname = versionName
            try {
                versionCode = packageManager.getPackageInfo(AppConfig().extensionPackageName, 0).versionCode
                versionname = packageManager.getPackageInfo(AppConfig().extensionPackageName, 0).versionName
            } catch (e: java.lang.Exception) {
                Log.println(Log.ASSERT,"SPLASH",e.toString())
            }
            when {
                versioncode < versionCode -> {
                    runOnUiThread {
                        Toast.makeText(this, "Server is under maintenance.", Toast.LENGTH_SHORT).show()
                    }
                }
                versioncode > versionCode -> {
                    Log.println(Log.ASSERT,"DATA","$versioncode and $versionCode")
                    runOnUiThread {
                        AlertDialog.Builder(this)
                            .setTitle("Update Extractor")
                            .setCancelable(false)
                            .setMessage("Current Version : $versionname\nUpdated version : $versionName")
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            .setPositiveButton("Update") { dialog, _ ->
                                dialog.dismiss()
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    myPermission2
                                )
                            }
                            .show()
                    }
                }
                else -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        } catch (e: Exception) {
            Log.println(Log.ASSERT,"ERROR",e.toString())
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private val threadDownload = Thread {
        val ID = 101
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this, "DOWNLOAD").apply {
            setContentTitle("Snap Loader")
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
                val url = URL(AppConfig().updateDownloadUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val fileLength: Int = connection.contentLength
                input = connection.inputStream
                val file = File("/sdcard/Android/data/com.alim.snaploader/Update/")
                if (!file.exists()) file.mkdirs()
                output = FileOutputStream(AppConfig().updatePath)
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
                install(AppConfig().updatePath)
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

    private val threadDownloadEx = Thread {
        val ID = 101
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this, "DOWNLOAD").apply {
            setContentTitle("Extractor")
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
                val file = File("/sdcard/Android/data/com.alim.snaploader/Update/")
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
                install(AppConfig().updateExPath)
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
        setContentView(R.layout.activity_splash)

        progressDialog = ProgressDialog(this)
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val screenWidth = metrics.widthPixels
        val logo: ImageView = findViewById(R.id.logo)
        val prog = findViewById<ProgressBar>(R.id.prog)
        val params_logo: ViewGroup.LayoutParams = logo.layoutParams
        val params_prog = prog.layoutParams
        params_logo.height = (screenWidth / 2)
        params_logo.width = (screenWidth / 2)
        params_prog.width = (screenWidth / 3)

        Thread(threadCheck).start()
    }

    private fun install(path: String) {
        var intent: Intent? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkUri: Uri = FileProvider.getUriForFile(
                    this, BuildConfig.APPLICATION_ID + ".provider",
                    File(path)
                )
                intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                intent.data = apkUri
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            } else {
                val apkUri: Uri = Uri.fromFile(File(AppConfig().updatePath))
                intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            //pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        } catch (e: java.lang.Exception) {
            Log.println(Log.ASSERT, "INSTALL", e.toString())
        }
        this.startActivity(intent)
        finish()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            myPermission -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Thread(threadDownload).start()
                }
                else { Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show() }
                return
            }
            myPermission2 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Thread(threadDownloadEx).start()
                }
                else { Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show() }
                return
            }
        }
    }
}