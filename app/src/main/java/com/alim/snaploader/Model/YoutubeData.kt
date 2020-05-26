package com.alim.snaploader.Model

import android.os.Parcel
import android.os.Parcelable

class YoutubeData() : Parcelable {

    var id = ""
    var title = ""
    var views = ""
    var length = ""
    var thumbnail = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()!!
        title = parcel.readString()!!
        views = parcel.readString()!!
        length = parcel.readString()!!
        thumbnail = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(views)
        parcel.writeString(length)
        parcel.writeString(thumbnail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<YoutubeData> {
        override fun createFromParcel(parcel: Parcel): YoutubeData {
            return YoutubeData(parcel)
        }

        override fun newArray(size: Int): Array<YoutubeData?> {
            return arrayOfNulls(size)
        }
    }
}