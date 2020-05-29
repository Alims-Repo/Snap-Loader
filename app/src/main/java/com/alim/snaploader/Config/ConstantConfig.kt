package com.alim.snaploader.Config

import android.content.Context
import com.alim.snaploader.Database.SettingsData

class ConstantConfig(context : Context) {
    //PRIMARY CONSTANT
    val REGION = SettingsData(context).region
    val START = "https://www.googleapis.com/youtube/v3/videos?part=snippet&maxResults=20&videoCategoryId"
    val API = "&chart=mostPopular&key="+AppConfig().YOUTUBE_API_KEY

    fun getContent(type: String): String {
        return when(type) {
            "home" -> "$START=0&regionCode=$REGION$API"
            "music" -> "$START=10&regionCode=$REGION$API"
            "sports" -> "$START=17&regionCode=$REGION$API"
            "gaming" -> "$START=20&regionCode=$REGION$API"
            "news" -> "$START=25&regionCode=$REGION$API"
            else -> "$START=0&regionCode=$REGION$API"
        }
    }
}