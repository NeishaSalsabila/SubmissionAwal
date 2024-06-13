package com.dicoding.picodiploma.loginwithanimation.view.story

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.remote.data.AddNewStoriesResponse
import kotlinx.coroutines.launch

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<AddNewStoriesResponse>()
    val uploadResult: LiveData<AddNewStoriesResponse> = _uploadResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun uploadStory(description: String, imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.uploadStory(description, imageUri, context)
                _uploadResult.value = response
            } catch (e: Exception) {
                _uploadResult.value = AddNewStoriesResponse(true, e.message ?: "Upload Failed")
            } finally {
                _loading.value = false
            }
        }
    }
}
