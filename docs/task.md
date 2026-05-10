## **A. Spesifikasi Teknis Wajib (Must-Have Features)**

Aplikasi bebas berbentuk apa saja, **ASALKAN** mencakup implementasi teknis berikut:

1. **Tata Letak & Antarmuka (UI & Layouts):**

- Menggunakan kombinasi `ConstraintLayout` (untuk tata letak kompleks) dan `LinearLayout` secara proporsional.
- Mengimplementasikan _View Binding_ (`findViewById` dapat digunakan, namun _View Binding_ sangat direkomendasikan).

2. **Pengelolaan Koleksi Data Dinamis (RecyclerView):**

- Terdapat minimal satu halaman yang menampilkan daftar data (minimal 20 item).
- Wajib mengimplementasikan arsitektur standar: `LayoutManager`, custom `Adapter`, dan `ViewHolder`.

3. **Siklus Hidup & Pelestarian Data (Lifecycle & State Management):**

- Aplikasi harus _robust_ terhadap perubahan konfigurasi (_Device Rotation_ dari Portrait ke Landscape).
- Input teks yang sedang diketik atau data yang sedang diubah **tidak boleh hilang** saat layar diputar (Wajib menggunakan `onSaveInstanceState` atau `ViewModel`).

4. **Navigasi & Arsitektur (Intents & Fragments):**

- **Explicit Intent:** Harus ada perpindahan halaman (misal: klik item di RecyclerView masuk ke halaman Detail) dengan membawa/melempar data (menggunakan `putExtra`).
- **Implicit Intent:** Harus ada satu fitur yang mendelegasikan tugas ke aplikasi lain (misalnya tombol "Share" ke WhatsApp, atau tombol "Buka Peta").
- **Fragment:** Harus mengimplementasikan arsitektur _Single-Activity_ dengan minimal 2 Fragment (misalnya menggunakan _Bottom Navigation_ atau sekadar _swap_ antar Fragment).

---

## **B. Aturan Pengumpulan (Video Code Walkthrough)**

Anda **TIDAK** perlu mengumpulkan file APK atau Zip project. Evaluasi dilakukan melalui **Video Presentasi (Screen Recording)** berdurasi 7-15 menit. Video diunggah ke YouTube/Google Drive (pastikan akses publik) dan kumpulkan tautannya.

### **Agenda Wajib di dalam Video:**

1. **Demo Fungsionalitas (2-3 menit):**

- Jalankan aplikasi di Emulator/Device Asli.
- Tunjukkan fitur-fiturnya berjalan dengan baik.
- **Wajib didemokan:** Putar layar (rotasi) saat ada input data untuk membuktikan aplikasi tidak _crash_ dan data tidak hilang.

2. **Code Walkthrough & Alasan Teknis (5-12 menit):**

- Buka Android Studio Anda. Tunjukkan blok kode untuk setiap **Spesifikasi Teknis Wajib** di atas.
- **PENTING:** Anda harus menjelaskan **ALASAN TEKNIS (Technical Reasoning)**. Jangan sekadar membaca kode.
- _Contoh:_ "Di sini saya menggunakan `RecyclerView` dan bukan `ScrollView` biasa karena data saya berpotensi panjang, sehingga `ViewHolder` dapat menghemat konsumsi RAM."
- _Contoh:_ "Saya menyimpan variabel ini di dalam `viewModel` agar ketika Activity destroy akibat rotasi layar pada `onDestroy()`, datanya tetap bertahan di memori."
