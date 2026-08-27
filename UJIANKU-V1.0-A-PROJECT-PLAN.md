# UJIANKU V1.0-A — PROJECT BASELINE

## Status
Tahap: V1.0-A
Status: Fondasi project
Tujuan: Android Guru + Supabase + Login Guru

## Aturan Project
1. UJIANKU adalah project baru dan berdiri sendiri.
2. Jangan mengambil source code, dependency, database, atau konfigurasi dari Android Cargo Manifest App.
3. Cargo Manifest V6-FIX3 tidak boleh disentuh dalam project ini.
4. Jangan mengubah UI yang sudah disepakati tanpa persetujuan pengguna.
5. Setiap perubahan besar harus menaikkan versi/fix dan dicatat di CHANGELOG.
6. Jangan menambahkan fitur V1.0-B/C/D sebelum tahap sebelumnya stabil.
7. Secret/API key tidak boleh ditanam langsung di source code atau APK.
8. Gunakan Supabase untuk backend sesuai arsitektur yang telah disepakati.

## Arsitektur V1.0-A

Guru
  ↓
Android Guru
  ↓ HTTPS
Supabase Auth
  ↓
Dashboard Guru

Komponen:
- Android Guru: Kotlin
- Backend: Supabase
- Authentication: Supabase Auth
- Database: Supabase PostgreSQL
- Siswa: belum dikerjakan pada V1.0-A
- Web Siswa: belum dikerjakan pada V1.0-A
- Email: belum dikerjakan pada V1.0-A

## Scope V1.0-A

### Wajib
- Project Android baru
- Package: `com.example.ujianku`
- Build dapat berjalan
- Halaman Login Guru
- Login menggunakan email/password
- Koneksi Supabase
- Dashboard sederhana setelah login
- Logout
- Error message dasar
- Loading state dasar

### Belum dikerjakan
- Data siswa
- Import Excel
- Pembuatan ujian
- Bank soal
- Peserta ujian
- Token ujian
- Web siswa
- Timer
- Penyimpanan jawaban
- Penilaian
- Email
- Export Excel

## Struktur Project

```text
UJIANKU-V1/
├── android-guru/
├── web-siswa/          # disiapkan nanti
├── supabase/            # disiapkan bertahap
├── .github/
│   └── workflows/
└── README.md
```

## Struktur Android V1.0-A

```text
android-guru/
└── app/
    └── src/main/
        └── java/com/example/ujianku/
            ├── MainActivity.kt
            ├── auth/
            │   └── LoginActivity.kt
            ├── dashboard/
            │   └── DashboardActivity.kt
            └── data/
                └── SupabaseClient.kt
```

Struktur aktual boleh berbeda jika implementasi Android yang dipilih membutuhkan pendekatan lain, tetapi fungsi V1.0-A harus tetap sama.

## Database V1.0-A

Pada tahap ini database minimal hanya membutuhkan sistem Auth guru.

Tabel bisnis berikut belum diaktifkan sebagai fitur:
- teachers
- students
- exams
- questions
- participants
- answers
- results
- email_logs

Struktur lengkap database akan dibuat pada tahap berikutnya setelah fondasi login stabil.

## Security Rules

- Jangan commit password.
- Jangan commit secret key Supabase.
- Gunakan konfigurasi lokal/Secrets untuk nilai sensitif.
- Anon/publishable key hanya digunakan sesuai rekomendasi Supabase.
- Service-role/secret key hanya boleh digunakan server-side dan tidak boleh masuk APK.
- Jangan menonaktifkan security policy hanya untuk mengatasi error build.

## Git / Versioning

Baseline:
`UJIANKU-V1.0-A`

Perbaikan kecil:
`UJIANKU-V1.0-A-FIX1`

Perbaikan berikutnya:
`UJIANKU-V1.0-A-FIX2`

Setelah V1.0-A dinyatakan stabil:
`UJIANKU-V1.0-B`

## Definition of Done — V1.0-A

V1.0-A dianggap selesai hanya jika:
- [ ] Project berhasil build
- [ ] APK berhasil dibuat
- [ ] APK dapat dipasang di Android
- [ ] Halaman login tampil
- [ ] Login guru berhasil dengan akun Supabase
- [ ] Login gagal menampilkan pesan yang jelas
- [ ] Dashboard tampil setelah login
- [ ] Logout berhasil
- [ ] Tidak ada secret key yang tertanam di source code
- [ ] Tidak ada perubahan pada project Cargo Manifest

## Changelog

### V1.0-A
- Project baru UJIANKU ditetapkan.
- Arsitektur Android Guru + Supabase ditetapkan.
- Scope fondasi login ditetapkan.
- Tahap fitur lain ditunda sampai fondasi stabil.

## Next Step

Setelah V1.0-A stabil:
1. V1.0-B — Data Siswa
2. V1.0-C — Ujian dan Bank Soal
3. V1.0-D — Web Siswa, Token, dan Timer
4. V1.0-E — Jawaban dan Penilaian
5. V1.0-F — Email dan Export Excel
