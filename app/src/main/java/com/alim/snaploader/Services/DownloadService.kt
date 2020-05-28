package com.alim.snaploader.Services

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alim.snaploader.Config.AppConfig
import com.alim.snaploader.DownloadsActivity
import com.alim.snaploader.R
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class DownloadService : Service() {

    companion object {
        var ID = 101
        var running = false
    }
    val CHANNEL_ID = "DOWNLOAD"
    var show = true

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ID++
        createNotificationChannel()
        if (intent!!.extras != null) {

            //startService(Intent(this, ServiceStarter::class.java))
            val Url = intent.getStringExtra("LINK")
            val name = intent.getStringExtra("NAME")

            val pendingIntent = PendingIntent.getActivity(this,
                0, Intent(this, DownloadsActivity::class.java), 0)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(name)
                .setOngoing(true)
                .setSmallIcon(R.drawable.file_download_white)
                .setContentIntent(pendingIntent)

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            Thread {
                val NotificatioID = ID
                var input: InputStream? = null
                var output: OutputStream? = null
                var connection: HttpURLConnection? = null
                try {
                    val url = URL(Url)
                    val inten = Intent("Progress")
                    inten.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                    inten.component = ComponentName(
                        "com.alim.snaploader",
                        "com.alim.snaploader.Reciever.ProgressReceiver"
                    )
                    connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    val fileLength: Int = connection.contentLength
                    input = connection.inputStream
                    val file = File(AppConfig().downloadPath)
                    if (!file.exists()) file.mkdirs()
                    output = FileOutputStream(AppConfig().downloadPath+name)
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        total += count.toLong()
                        if (show) {
                            show = false
                            inten.putExtra("PROGRESS", ((total*100)/fileLength).toInt())
                            sendBroadcast(inten)
                            notification.setProgress(100, ((total*100)/fileLength).toInt(), false)
                            manager.notify(NotificatioID, notification.build())
                            Handler(Looper.getMainLooper()).postDelayed({
                                show = true
                            }, 500)
                        }
                        output.write(data, 0, count)
                    }
                    inten.putExtra("PROGRESS", 100)
                    sendBroadcast(inten)
                    notification.setSmallIcon(R.drawable.check_circle_white)
                    notification.setOngoing(false)
                    notification.setContentText("Download complete")
                        .setProgress(0, 0, false)
                    manager.notify(NotificatioID, notification.build())
                } catch (e: Exception) {
                    Log.println(Log.ASSERT,"ERROR", e.toString())
                    notification.setProgress(0, 0, false)
                    notification.setOngoing(false)
                    notification.setSmallIcon(R.drawable.ic_error_white)
                    notification.setContentTitle("Error : $e")
                    manager.notify(NotificatioID, notification.build())
                } finally {
                    running = false
                    try {
                        output?.close()
                        input?.close()
                        stopForeground(true)
                    } catch (e: IOException) {
                        Log.println(Log.ASSERT,"ERROR", e.toString())
                    }
                    connection?.disconnect()
                }
            }.start()

            if (!running) {
                running = true
                startForeground(ID, notification.build())
            }
        }
        return START_NOT_STICKY
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

    /*private fun isServiceRunningInForeground(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return service.foreground
            }
        }
        return false
    }*/
}
