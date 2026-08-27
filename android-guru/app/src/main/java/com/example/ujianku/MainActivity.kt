package com.example.ujianku

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ujianku.data.SupabaseClient
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val supabase = SupabaseClient.client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLogin()
    }

    private fun showLogin() {
        setContentView(R.layout.activity_main)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val errorText = findViewById<TextView>(R.id.errorText)

        lifecycleScope.launch {
            try {
                supabase.auth.awaitInitialization()
                if (supabase.auth.currentSessionOrNull() != null) showDashboard()
            } catch (_: Exception) {}
        }

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            errorText.visibility = View.GONE

            if (email.isBlank()) {
                errorText.text = "Email wajib diisi."
                errorText.visibility = View.VISIBLE
                return@setOnClickListener
            }
            if (password.isBlank()) {
                errorText.text = "Password wajib diisi."
                errorText.visibility = View.VISIBLE
                return@setOnClickListener
            }

            loginButton.isEnabled = false
            progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                try {
                    supabase.auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }
                    showDashboard()
                } catch (e: Exception) {
                    errorText.text = e.message ?: "Login gagal. Periksa email dan password."
                    errorText.visibility = View.VISIBLE
                } finally {
                    loginButton.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun showDashboard() {
        setContentView(R.layout.activity_dashboard)
        val emailText = findViewById<TextView>(R.id.userEmailText)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        emailText.text = "Login sebagai: ${supabase.auth.currentUserOrNull()?.email ?: "-"}"

        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                try { supabase.auth.signOut() } finally { showLogin() }
            }
        }
    }
}
