package com.dicoding.picodiploma.loginwithanimation.view.login

import LoginViewModel
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.lifecycle.Observer
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.remote.data.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import androidx.lifecycle.asLiveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var userPreference: UserPreference

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingIndicator = binding.loadingIndicator

        userPreference = UserPreference.getInstance(dataStore)

        setupView()
        setupAction()
        populateEmailFromIntent()

        viewModel.loading.observe(this, Observer { isLoading ->
            loadingIndicator.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
        })

        viewModel.loginResult.observe(this, Observer { result ->
            result.onSuccess { response: LoginResponse ->
                if (!response.error!!) {
                    response.loginResult?.let { loginResult ->
                        val userModel = UserModel(
                            email = binding.emailEditText.text.toString(),
                            token = loginResult.token ?: "",
                            isLogin = true
                        )
                        viewModel.saveSession(userModel)
                        showSuccessDialog()
                    }
                } else {
                    Toast.makeText(this, response.message ?: "Login gagal", Toast.LENGTH_SHORT).show()
                }
            }
            result.onFailure { error ->
                Toast.makeText(this, error.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
            }
        })

        userPreference.getSession().asLiveData().observe(this, Observer { user ->
            user?.let {
                if (it.isLogin) {
                    navigateToStoryActivity()
                }
            }
        })
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateEmailFromIntent() {
        val email = intent.getStringExtra("email")
        email?.let {
            binding.emailEditText.setText(it)
        }
    }

    private fun navigateToStoryActivity() {
        val intent = Intent(this@LoginActivity, StoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Success")
            setPositiveButton("Lanjut") { _, _ ->
                navigateToStoryActivity()
            }
            create()
            show()
        }
    }
}
