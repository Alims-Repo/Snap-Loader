package com.alim.snaploader.Reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.alim.snaploader.Interface.LinkInterface

open class LinkReceiver : BroadcastReceiver() {

    companion object {
        lateinit var broadcastInterface: LinkInterface
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.println(Log.ASSERT,"TAG", "${intent.getIntExtra("Size", 0)}")

        try { broadcastInterface.Cast(intent) }
        catch (e: Exception) { Log.println(Log.ASSERT,"Receiver Ex", "$e") }
    }

    fun register(cast: LinkInterface) {
        Log.println(Log.ASSERT,"REGISTER", "DONE")
        broadcastInterface = cast
    }
}
