package com.example.beready.mock

import android.os.Parcel
import android.os.Parcelable
import com.example.beready.quiz.QuestionModel

 data class subTopic(
     val setTitle: String = "",
     val questions: List<QuestionModel> = emptyList(),
     val documentationLink:String=""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createTypedArrayList(QuestionModel.CREATOR) ?: emptyList(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(setTitle)
        parcel.writeTypedList(questions)
        parcel.writeString(documentationLink)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<subTopic> {
        override fun createFromParcel(parcel: Parcel): subTopic {
            return subTopic(parcel)
        }

        override fun newArray(size: Int): Array<subTopic?> {
            return arrayOfNulls(size)
        }
    }
}
