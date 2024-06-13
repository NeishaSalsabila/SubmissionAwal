package com.dicoding.picodiploma.loginwithanimation.remote.data

import com.google.gson.annotations.SerializedName

data class AddNewStoriesResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String? = null
)
