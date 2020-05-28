package com.alim.snaploader.Reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.alim.snaploader.Interface.CastInterface
import com.alim.snaploader.Interface.ProgressInterface
import java.lang.Exception

class CastReceiver : BroadcastReceiver() {

    companion object {
        lateinit var castInterface: CastInterface
    }
    override fun onReceive(context: Context, intent: Intent) {

        Toast.makeText(context, "Cast Received", Toast.LENGTH_SHORT).show()

        try { castInterface.cast() }
        catch (e: Exception) { Log.println(Log.ASSERT,"REGISTER", e.toString()) }
    }

    fun register(cast: CastInterface) {
        Log.println(Log.ASSERT,"REGISTER", "DONE")
        castInterface = cast
    }
}
