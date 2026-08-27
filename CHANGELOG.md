# Changelog

## UJIANKU V1.0-A-FIX10 — Bank Soal Import Excel
- Menambahkan tombol IMPORT EXCEL pada halaman Bank Soal.
- Menambahkan pembaca XLSX ringan tanpa Apache POI.
- Mendukung shared strings dan inline strings pada worksheet pertama.
- Validasi header: No, Pertanyaan, Pilihan A, Pilihan B, Pilihan C, Pilihan D, Jawaban.
- Validasi setiap baris soal dan melewati baris yang tidak valid tanpa menghentikan seluruh import.
- Import ke Supabase dilakukan per batch 100 soal.
- Menambahkan template Excel `templates/Bank_Soal_Template.xlsx`.
- Tidak mengubah struktur Dashboard Guru, Login, atau fitur input manual Bank Soal.

## UJIANKU V1.0-A-FIX9 — Bank Soal
- Menambahkan tombol Bank Soal pada Dashboard Guru.
- Menambahkan halaman Bank Soal berbasis XML/AppCompat (tanpa Jetpack Compose).
- Guru dapat melihat, menambah, mengedit, dan menghapus soal pilihan ganda A-D.
- Soal disimpan di Supabase PostgreSQL dan dibatasi berdasarkan `teacher_id`.
- Menambahkan RLS policy untuk memastikan guru hanya dapat mengakses soal miliknya.
- Menambahkan dependency Supabase PostgREST.
