package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("MainActivity", "Binding and layout set")

        viewModel.getSession().observe(this) { user ->
            Log.d("MainActivity", "Session observed: isLogin = ${user.isLogin}")
            if (!user.isLogin) {
                Log.d("MainActivity", "User not logged in, redirecting to WelcomeActivity")
                navigateToWelcomeActivity()
                finish()
            } else {
                Log.d("MainActivity", "User logged in, setting email to TextView")
                binding.nameTextView.text = user.email
            }
        }

        setupView()
        setupAction()
        Log.d("MainActivity", "onCreate completed")
    }

    private fun setupView() {
        // Implementasi setupView
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            Log.d("MainActivity", "Logout button clicked")
            showLogoutConfirmationDialog()
        }
    }

    private fun logoutUser() {
        viewModel.logout {
            Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show()
            navigateToWelcomeActivity()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                logoutUser()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finishAffinity() // Finish all activities in the task stack
    }
}
