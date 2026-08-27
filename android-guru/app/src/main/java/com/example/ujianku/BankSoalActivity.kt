package com.example.ujianku

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.TextView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ujianku.data.SupabaseClient
import com.example.ujianku.model.Question
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class BankSoalActivity : AppCompatActivity() {
    private val supabase = SupabaseClient.client
    private lateinit var listContainer: LinearLayout
    private lateinit var progress: ProgressBar
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_soal)

        listContainer = findViewById(R.id.questionList)
        progress = findViewById(R.id.questionProgress)
        emptyText = findViewById(R.id.emptyQuestions)
        findViewById<Button>(R.id.addQuestionButton).setOnClickListener { showQuestionDialog(null) }
        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
        loadQuestions()
    }

    private fun loadQuestions() {
        val teacherId = supabase.auth.currentUserOrNull()?.id
        if (teacherId == null) {
            finish()
            return
        }
        progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val questions = supabase.from("questions").select {
                    filter { eq("teacher_id", teacherId) }
                }.decodeList<Question>()
                renderQuestions(questions)
            } catch (e: Exception) {
                showError(e.message ?: "Gagal mengambil bank soal.")
            } finally {
                progress.visibility = View.GONE
            }
        }
    }

    private fun renderQuestions(questions: List<Question>) {
        listContainer.removeAllViews()
        emptyText.visibility = if (questions.isEmpty()) View.VISIBLE else View.GONE
        questions.forEachIndexed { index, item ->
            val box = layoutInflater.inflate(R.layout.item_soal, listContainer, false)
            box.findViewById<TextView>(R.id.questionText).text = "${index + 1}. ${item.question}"
            box.findViewById<TextView>(R.id.optionsText).text =
                "A. ${item.optionA}\nB. ${item.optionB}\nC. ${item.optionC}\nD. ${item.optionD}"
            box.findViewById<TextView>(R.id.correctAnswerText).text = "Jawaban: ${item.correctAnswer}"
            box.findViewById<Button>(R.id.editQuestionButton).setOnClickListener { showQuestionDialog(item) }
            box.findViewById<Button>(R.id.deleteQuestionButton).setOnClickListener { confirmDelete(item) }
            listContainer.addView(box)
        }
    }

    private fun showQuestionDialog(existing: Question?) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 8, 36, 0)
        }
        fun field(hint: String, value: String = "") = EditText(this).apply {
            this.hint = hint
            setText(value)
            minLines = 1
            setPadding(0, 12, 0, 12)
        }
        val question = field("Pertanyaan", existing?.question ?: "")
        val a = field("Pilihan A", existing?.optionA ?: "")
        val b = field("Pilihan B", existing?.optionB ?: "")
        val c = field("Pilihan C", existing?.optionC ?: "")
        val d = field("Pilihan D", existing?.optionD ?: "")
        val correct = Spinner(this)
        correct.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrayOf("A", "B", "C", "D"))
        existing?.let { correct.setSelection(listOf("A", "B", "C", "D").indexOf(it.correctAnswer).coerceAtLeast(0)) }
        root.addView(question); root.addView(a); root.addView(b); root.addView(c); root.addView(d)
        val label = TextView(this).apply { text = "Jawaban benar"; setPadding(0, 12, 0, 4) }
        root.addView(label); root.addView(correct)
        val scroll = ScrollView(this).apply { addView(root) }
        val dialog = AlertDialog.Builder(this)
            .setTitle(if (existing == null) "Tambah Soal" else "Edit Soal")
            .setView(scroll)
            .setNegativeButton("BATAL", null)
            .setPositiveButton("SIMPAN", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val q = question.text.toString().trim()
                val av = a.text.toString().trim(); val bv = b.text.toString().trim()
                val cv = c.text.toString().trim(); val dv = d.text.toString().trim()
                if (q.isBlank() || av.isBlank() || bv.isBlank() || cv.isBlank() || dv.isBlank()) {
                    showError("Pertanyaan dan semua pilihan wajib diisi.")
                    return@setOnClickListener
                }
                saveQuestion(existing, q, av, bv, cv, dv, correct.selectedItem.toString())
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun saveQuestion(existing: Question?, q: String, a: String, b: String, c: String, d: String, correct: String) {
        val teacherId = supabase.auth.currentUserOrNull()?.id ?: return
        progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                if (existing == null) {
                    supabase.from("questions").insert(Question(teacherId = teacherId, question = q, optionA = a, optionB = b, optionC = c, optionD = d, correctAnswer = correct))
                } else {
                    supabase.from("questions").update({
                        set("question", q)
                        set("option_a", a)
                        set("option_b", b)
                        set("option_c", c)
                        set("option_d", d)
                        set("correct_answer", correct)
                    }) { filter {
                            eq("id", existing.id!!)
                            eq("teacher_id", teacherId)
                        } }
                }
                loadQuestions()
            } catch (e: Exception) {
                showError(e.message ?: "Gagal menyimpan soal.")
            } finally { progress.visibility = View.GONE }
        }
    }

    private fun confirmDelete(item: Question) {
        AlertDialog.Builder(this)
            .setTitle("Hapus soal?")
            .setMessage("Soal ini akan dihapus dari bank soal.")
            .setNegativeButton("BATAL", null)
            .setPositiveButton("HAPUS") { _, _ -> deleteQuestion(item) }
            .show()
    }

    private fun deleteQuestion(item: Question) {
        val teacherId = supabase.auth.currentUserOrNull()?.id ?: return
        lifecycleScope.launch {
            try {
                supabase.from("questions").delete { filter {
                    eq("id", item.id!!)
                    eq("teacher_id", teacherId)
                } }
                loadQuestions()
            } catch (e: Exception) { showError(e.message ?: "Gagal menghapus soal.") }
        }
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this).setTitle("Bank Soal").setMessage(message).setPositiveButton("OK", null).show()
    }
}
