package com.alim.snaploader.Config

import android.os.Environment

class AppConfig {

    val YOUTUBE_API_KEY = "AIzaSyCvIIJmyv8fVIu-VIVOBkYaaXYo-AU8CcE"

    /**Share Link*/
    val  shareUrl = Environment.getExternalStorageDirectory().path+"/Android/data/com.alim.snaploader/Update/SnapLoader.apk"

    /**Main Application Data*/
    val updateDownloadUrl = "https://github.com/Alims-Repo/SnapLoader/raw/master/app/release/app-release.apk"
    val updateCheckUrl = "https://raw.githubusercontent.com/Alims-Repo/SnapLoader/master/app/release/output.json"
    val updatePath = Environment.getExternalStorageDirectory().path+"/Android/data/com.alim.snaploader/Update/SnapLoader.apk"

    /**Extension Data*/
    val updateExDownloadUrl = "https://github.com/Alims-Repo/Extractor/raw/master/app/release/app-release.apk"
    val updateExCheckUrl = "https://raw.githubusercontent.com/Alims-Repo/Extractor/master/app/release/output.json"
    val updateExPath = Environment.getExternalStorageDirectory().path+"/Android/data/com.alim.snaploader/Update/Extractor.apk"
}