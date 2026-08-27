# UJIANKU V1.0-A

Fondasi Android Guru + Supabase Auth.

## Scope
- Login Guru email/password
- Supabase Auth
- Dashboard sederhana
- Logout
- GitHub Actions debug APK

Belum termasuk Data Siswa, Ujian, Soal, Web Siswa, Email, Penilaian, dan Export Excel.

## Konfigurasi
Gunakan `SUPABASE_URL` dan `SUPABASE_PUBLISHABLE_KEY` sebagai Gradle properties lokal atau GitHub Secrets.
Jangan masukkan database password atau `sb_secret_...` ke APK/source code.

## GitHub Secrets
- SUPABASE_URL
- SUPABASE_PUBLISHABLE_KEY

## Baseline
UJIANKU-V1.0-A


## Latest fix
V1.0-A-FIX1: compileSdk raised to 36 to satisfy AndroidX AAR metadata requirements; targetSdk remains 35.


## Current Fix
`UJIANKU-V1.0-A-FIX2` — perbaikan Supabase Auth pada MainActivity.


Current fix: `UJIANKU-V1.0-A-FIX3`.


### V1.0-A-FIX4
- Memperbaiki konfigurasi URL/key Supabase agar tidak jatuh ke localhost saat Gradle property kosong.
- GitHub Actions memvalidasi `SUPABASE_URL` dan `SUPABASE_PUBLISHABLE_KEY` sebelum build.
- Tidak mengubah UI dan tidak menambahkan fitur di luar V1.0-A.


Current fix: `UJIANKU-V1.0-A-FIX5` — Supabase URL is fixed in BuildConfig; only publishable key is required as a GitHub Secret.


Current fix: `UJIANKU-V1.0-A-FIX6` — GitHub Actions no longer requires `SUPABASE_URL` secret.


Current fix: `UJIANKU-V1.0-A-FIX8` — GitHub Actions builds from `android-guru` using Gradle 8.13 without a Gradle wrapper.

## Bank Soal — V1.0-A-FIX9

Bank Soal Guru menggunakan Supabase PostgreSQL. Sebelum membuka fitur Bank Soal, jalankan file `supabase/001_questions.sql` di Supabase SQL Editor satu kali. RLS membatasi setiap guru agar hanya dapat membaca, menambah, mengubah, dan menghapus soal miliknya sendiri.

Fitur saat ini:
- Lihat daftar soal
- Tambah soal pilihan ganda A-D
- Edit soal
- Hapus soal
- Pilih jawaban benar

## Bank Soal — Import Excel

Halaman Bank Soal mendukung import banyak soal sekaligus dari file `.xlsx`.
Gunakan sheet pertama dengan header:

`No | Pertanyaan | Pilihan A | Pilihan B | Pilihan C | Pilihan D | Jawaban`

Nilai Jawaban hanya `A`, `B`, `C`, atau `D`. Template siap pakai tersedia di `templates/Bank_Soal_Template.xlsx`.
