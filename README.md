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


Current milestone: `UJIANKU-V1.0-B` — Guru Dashboard skeleton.


Current fix: `UJIANKU-V1.0-B-FIX1`.
