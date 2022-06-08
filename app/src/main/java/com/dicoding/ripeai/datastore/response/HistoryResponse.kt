package com.dicoding.ripeai.datastore.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class HistoryResponse (
    @field:SerializedName("data")
    val data: List<DataItem>,

    @field:SerializedName("success")
    val success: Boolean
) : Parcelable

@Parcelize
data class DataItem(

    @field:SerializedName("email_user")
    val emailUser: String? = null,

    @field:SerializedName("fruit")
    val fruit: String? = null,

    @field:SerializedName("ripeness")
    val ripeness: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
) : Parcelable