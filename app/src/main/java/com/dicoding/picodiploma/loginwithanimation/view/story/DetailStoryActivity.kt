package com.dicoding.picodiploma.loginwithanimation.view.story

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
        const val EXTRA_STORY_NAME = "extra_story_name"
        const val EXTRA_STORY_PHOTO_URL = "extra_story_photo_url"
        const val EXTRA_STORY_DESCRIPTION = "extra_story_description"
    }

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val storyName = intent.getStringExtra(EXTRA_STORY_NAME)
        val storyPhotoUrl = intent.getStringExtra(EXTRA_STORY_PHOTO_URL)
        val storyDescription = intent.getStringExtra(EXTRA_STORY_DESCRIPTION)

        supportActionBar?.title = storyName

        binding.tvDetailName.text = storyName
        binding.tvDetailDescription.text = storyDescription

        Glide.with(this)
            .load(storyPhotoUrl)
            .into(binding.ivDetailPhoto)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
