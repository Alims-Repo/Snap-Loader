package com.alim.snaploader.Model

import android.os.Parcel
import android.os.Parcelable

class YoutubeData() : Parcelable {

    var id = ""
    var date = ""
    var title = ""
    var channelI = ""
    var channelN = ""
    var thumbnail = ""
    var description = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()!!
        date = parcel.readString()!!
        title = parcel.readString()!!
        channelI = parcel.readString()!!
        channelN = parcel.readString()!!
        thumbnail = parcel.readString()!!
        description = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(date)
        parcel.writeString(title)
        parcel.writeString(channelI)
        parcel.writeString(channelN)
        parcel.writeString(thumbnail)
        parcel.writeString(description)
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