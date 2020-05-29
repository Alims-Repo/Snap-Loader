package com.alim.snaploader.Config

import android.os.Environment

class AppConfig {

    val extensionPackageName = "com.alim.extension"
    val YOUTUBE_API_KEY = "AIzaSyBGmwob2aK2oQ15p6doEdw5D_TCKs1K0qY"
    val downloadPath = Environment.getExternalStorageDirectory().path+"/Snap Loader/"

    /**Share Link*/
    val  shareUrl = Environment.getExternalStorageDirectory().path+"/Android/data/com.alim.snaploader/Update/SnapLoader.apk"

    /**Main Application Data*/
    val updateDownloadUrl = "https://github.com/Alims-Repo/Snap-Loader/raw/master/app/release/app-release.apk"
    val updateCheckUrl = "https://raw.githubusercontent.com/Alims-Repo/Snap-Loader/master/app/release/output.json"
    val updatePath = Environment.getExternalStorageDirectory().path+"/Android/data/com.alim.snaploader/Update/SnapLoader.apk"

    /**Extension Data*/
    val updateExDownloadUrl = "https://github.com/Alims-Repo/Extension/raw/master/app/release/app-release.apk"
    val updateExCheckUrl = "https://raw.githubusercontent.com/Alims-Repo/Extension/master/app/release/output.json"
    val updateExPath = Environment.getExternalStorageDirectory().path+"/Android/data/com.alim.snaploader/Update/Extension.apk"
}