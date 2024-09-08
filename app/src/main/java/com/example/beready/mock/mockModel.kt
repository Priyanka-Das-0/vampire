// QuizModel.kt
package com.example.beready.mock

import android.os.Parcel
import android.os.Parcelable
import com.example.beready.mock.subTopic
data class mockModel(
    val id: Int = 0,
    val title: String = "",
    val time: Int = 0,
    val subCategories: List<subTopic> = emptyList()

) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt()?:0,
        parcel.readString() ?: "",
        parcel.readInt()?:0,
        parcel.createTypedArrayList(subTopic.CREATOR) ?: emptyList(),


    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeInt(time)
        parcel.writeTypedList(subCategories)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<mockModel> {
        override fun createFromParcel(parcel: Parcel): mockModel {
            return mockModel(parcel)
        }

        override fun newArray(size: Int): Array<mockModel?> {
            return arrayOfNulls(size)
        }
    }
}
