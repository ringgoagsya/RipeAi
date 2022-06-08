package com.dicoding.ripeai.datastore.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserResponse(

	@field:SerializedName("data")
	val data: List<DataUser>,

	@field:SerializedName("success")
	val success: Boolean? = null
) : Parcelable

@Parcelize
data class DataUser(

	@field:SerializedName("firstname")
	val firstname: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("lastname")
	val lastname: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable
