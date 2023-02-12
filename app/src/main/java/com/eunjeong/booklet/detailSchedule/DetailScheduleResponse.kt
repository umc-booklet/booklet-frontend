package com.eunjeong.booklet.detailSchedule

import android.os.Parcel
import android.os.Parcelable

data class DetailScheduleResponse(
    val isSuccess :Boolean,
    val code :Int,
    val message :String,
    val result :Detail
)

data class Detail constructor(val id :Int,
                              val name: String?,
                              val text: String?,
                              val color: String?,
                              val startYear: Int,
                              val startMonth: Int,
                              val startDay: Int,
                              val startHour: Int,
                              val startMinute: Int,
                              val endYear: Int,
                              val endMonth: Int,
                              val endDay: Int,
                              val endHour: Int,
                              val endMinute: Int): Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
        dest.writeString(text)
        dest.writeString(color)
        dest.writeInt(startYear)
        dest.writeInt(startMonth)
        dest.writeInt(startDay)
        dest.writeInt(startHour)
        dest.writeInt(startMinute)
        dest.writeInt(endYear)
        dest.writeInt(endMonth)
        dest.writeInt(endDay)
        dest.writeInt(endHour)
        dest.writeInt(endMinute)
    }

    companion object CREATOR : Parcelable.Creator<Detail> {
        override fun createFromParcel(parcel: Parcel): Detail {
            return Detail(parcel)
        }

        override fun newArray(size: Int): Array<Detail?> {
            return arrayOfNulls(size)
        }
    }

}