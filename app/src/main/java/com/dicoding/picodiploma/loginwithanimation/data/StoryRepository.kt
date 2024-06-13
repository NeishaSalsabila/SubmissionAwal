package com.dicoding.picodiploma.loginwithanimation.data

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.dicoding.picodiploma.loginwithanimation.remote.data.ApiService
import com.dicoding.picodiploma.loginwithanimation.remote.data.AddNewStoriesResponse
import com.dicoding.picodiploma.loginwithanimation.remote.data.StoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService
) {
    suspend fun getStories(): StoryResponse {
        return apiService.getStories()
    }

    suspend fun uploadStory(description: String, imageUri: Uri, context: Context): AddNewStoriesResponse {
        val file = File(getRealPathFromURI(context, imageUri))
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        val descriptionRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), description)

        return withContext(Dispatchers.IO) {
            apiService.uploadStory(descriptionRequestBody, body)
        }
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                result = it.getString(columnIndex)
            }
        }
        cursor?.close()
        return result
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService).also { instance = it }
            }
        }
    }
}
