package com.alim.snaploader.Interface

interface VideoDownloader {
    fun createDirectory(): String?
    fun getVideoId(link: String?): String?
    fun DownloadVideo()
}