package com.example.ujianku

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

@Composable
fun GuruDashboardScreen(
    email: String = "",
    onCreateExam: () -> Unit = {},
    onQuestionBank: () -> Unit = {},
    onExamList: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("UJIANKU", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text("Dashboard Guru", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))
        if (email.isNotBlank()) {
            Text("Guru: $email", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(20.dp))
        }
        DashboardAction("＋ Buat Ujian", onCreateExam)
        Spacer(Modifier.height(12.dp))
        DashboardAction("📚 Bank Soal", onQuestionBank)
        Spacer(Modifier.height(12.dp))
        DashboardAction("📝 Daftar Ujian", onExamList)
        Spacer(Modifier.height(12.dp))
        DashboardAction("🚪 Logout", onLogout)
    }
}

@Composable
private fun DashboardAction(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(label)
    }
}
