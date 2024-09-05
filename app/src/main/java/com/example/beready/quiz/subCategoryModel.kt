// SubCategoryModel.kt
package com.example.beready.quiz

import android.os.Parcel
import android.os.Parcelable

data class SubCategoryModel(
    val setTitle: String = "",
    val questions: List<QuestionModel> = emptyList()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createTypedArrayList(QuestionModel.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(setTitle)
        parcel.writeTypedList(questions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubCategoryModel> {
        override fun createFromParcel(parcel: Parcel): SubCategoryModel {
            return SubCategoryModel(parcel)
        }

        override fun newArray(size: Int): Array<SubCategoryModel?> {
            return arrayOfNulls(size)
        }
    }
}
