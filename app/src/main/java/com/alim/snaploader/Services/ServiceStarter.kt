package com.alim.snaploader.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alim.snaploader.DownloadsActivity
import com.alim.snaploader.Interface.CastInterface
import com.alim.snaploader.R
import com.alim.snaploader.Reciever.CastReceiver
import com.alim.snaploader.Reciever.LinkReceiver

class ServiceStarter : Service() {

    var ID = 97
    val CHANNEL_ID = "BACKGROUND"

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ID++
        createNotificationChannel()

        val pendingIntent = PendingIntent.getActivity(this,
            0, Intent(this, DownloadsActivity::class.java), 0)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Name")
            .setSmallIcon(R.drawable.file_download_white)
            .setContentIntent(pendingIntent)

        //stopForeground(true)

        //val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startForeground(ID, notification.build())
        //stopForeground(true)
        Handler(Looper.getMainLooper()).postDelayed({
            stopSelf()
            Log.println(Log.ASSERT,"TAG","KILLED")
        },2000)

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Background"
            val descriptionText = "Background Notification Service"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
                .apply { description = descriptionText }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        stopForeground(true)
    }
}

