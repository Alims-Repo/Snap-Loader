package com.alim.snaploader.Reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.alim.snaploader.Interface.ProgressInterface

class ProgressReceiver : BroadcastReceiver() {

    companion object {
        lateinit var progressInterface: ProgressInterface
    }

    override fun onReceive(context: Context, intent: Intent) {

        Log.println(Log.ASSERT,"PROGRESS", intent.getIntExtra("PROGRESS",0).toString())

        try { progressInterface.Progress(intent.getIntExtra("PROGRESS",0)) }
        catch (e: Exception) { Log.println(Log.ASSERT,"Receiver Ex", "$e") }
    }

    fun register(cast: ProgressInterface) {
        Log.println(Log.ASSERT,"REGISTER", "DONE")
        progressInterface = cast
    }
}