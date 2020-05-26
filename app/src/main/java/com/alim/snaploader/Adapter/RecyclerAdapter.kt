package com.alim.snaploader.Adapter

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.alim.snaploader.PlayerActivity
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.Model.YoutubeData
import com.alim.snaploader.R
import com.bumptech.glide.Glide
import java.lang.Exception

class RecyclerAdapter(context: Context,
                      youtubeData: ArrayList<YoutubeData> = ArrayList())
    : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    private val mContext = context
    private val data = youtubeData

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view.findViewById(R.id.layout)
        val title: TextView = view.findViewById(R.id.title)
        val thumb: ImageView = view.findViewById(R.id.thumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.youtube_thumb, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            Glide.with(mContext).load(data[position].thumbnail).centerCrop().into(holder.thumb)
            holder.title.text = data[position].title
        } catch (e: Exception) {
            Log.println(Log.ASSERT,"Ex Home Adapter","$e")
        }

        holder.layout.setOnClickListener {
            var intent = Intent()
            if (ApplicationData(mContext).externalPlayer) {
                if (extractorAvailable(mContext)) {
                    intent.action = Intent.ACTION_SEND
                    intent.type = "alim/code"
                    intent.component =
                        ComponentName("com.alim.extractor", "com.alim.extractor.PlayerActivity")
                    Toast.makeText(mContext, "External", Toast.LENGTH_SHORT).show()
                }
            }
            else
                intent = Intent(mContext, PlayerActivity::class.java)
            intent.putExtra(Intent.EXTRA_TEXT, "YOUTUBE")
            intent.putExtra("LINK", data[position].id)
            intent.putExtra("VIEWS", data[position].views)
            intent.putExtra("TITLE", data[position].title)
            intent.putExtra("LENGTH", data[position].length)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
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