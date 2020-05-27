package com.alim.snaploader.Reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.alim.snaploader.Interface.BroadcastInterface

open class LinkReceiver : BroadcastReceiver() {

    companion object {
        lateinit var broadcastInterface: BroadcastInterface
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.println(Log.ASSERT,"TAG", "${intent.getIntExtra("Size", 0)}")
        broadcastInterface.Cast(intent)
    }

    fun register(cast: BroadcastInterface) {
        Log.println(Log.ASSERT,"REGISTER", "DONE")
        broadcastInterface = cast
    }
}
