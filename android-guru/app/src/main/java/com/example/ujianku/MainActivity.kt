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
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            errorText.visibility = View.GONE

            when {
                email.isBlank() -> {
                    errorText.text = "Email wajib diisi."
                    errorText.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                password.isBlank() -> {
                    errorText.text = "Password wajib diisi."
                    errorText.visibility = View.VISIBLE
                    return@setOnClickListener
                }
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
                    errorText.text =
                        e.message ?: "Login gagal. Periksa email dan password."
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

        emailText.text =
            "Login sebagai: ${supabase.auth.currentUserOrNull()?.email ?: "-"}"

        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    supabase.auth.signOut()
                } finally {
                    showLogin()
                }
            }
        }
    }
}


@Composable
fun DashboardScreen(
    email: String = "",
    onCreateExam: () -> Unit = {},
    onQuestionBank: () -> Unit = {},
    onExamList: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "UJIANKU",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Dashboard Guru",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (email.isNotBlank()) {
            Text(
                text = "Guru: $email",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        DashboardButton("＋ Buat Ujian", onCreateExam)
        Spacer(modifier = Modifier.height(12.dp))
        DashboardButton("📚 Bank Soal", onQuestionBank)
        Spacer(modifier = Modifier.height(12.dp))
        DashboardButton("📝 Daftar Ujian", onExamList)
        Spacer(modifier = Modifier.height(12.dp))
        DashboardButton("🚪 Logout", onLogout)
    }
}

@Composable
private fun DashboardButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label)
    }
}
