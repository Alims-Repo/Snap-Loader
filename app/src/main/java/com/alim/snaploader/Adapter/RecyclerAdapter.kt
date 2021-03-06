package com.alim.snaploader.Adapter

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.RecyclerView
import com.alim.snaploader.YPlayerActivity
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.EPlayerActivity
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
        val channel: TextView = view.findViewById(R.id.channel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.youtube_thumb, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            Glide.with(mContext).load(data[position].thumbnail).centerCrop().into(holder.thumb)
            holder.channel.text = data[position].channelN
            holder.title.text = data[position].title
        } catch (e: Exception) {
            Log.println(Log.ASSERT,"Ex Home Adapter","$e")
        }

        holder.layout.setOnClickListener {
            var intent = Intent()
            intent = if (ApplicationData(mContext).externalPlayer)
                Intent(mContext, EPlayerActivity::class.java)
            else
                Intent(mContext, YPlayerActivity::class.java)
            intent.putExtra(Intent.EXTRA_TEXT, "YOUTUBE")
            intent.putExtra("LINK", data[position].id)
            intent.putExtra("TITLE", data[position].title)
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