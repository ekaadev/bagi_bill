# 📱 Skenario Blackbox Testing Manual - Bagi Bill App

## 📋 Daftar Isi

1. [HomeScreen](#1-homescreen)
2. [CameraScreen](#2-camerascreen)
3. [PreviewScreen](#3-previewscreen)
4. [RincianScreen](#4-rincianscreen)
5. [UbahRincianScreen](#5-ubahrincianscreen)
6. [SelectMemberScreen](#6-selectmemberscreen)
7. [ReplacePayerScreen](#7-replacepayerscreen)
8. [HistoryScreen](#8-historyscreen)

---

## 1. HomeScreen

### Fitur 1.1: Navigasi ke Fitur Scan Struk

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-HS-001 | ✅ Positif | Navigasi ke kamera via tombol "Scan Sekarang" | 1. Buka aplikasi<br>2. Pada HomeScreen, tekan tombol "Scan Sekarang" di bagian bawah | Aplikasi berpindah ke CameraScreen untuk memulai scan struk | ⬜ |
| TC-HS-002 | ✅ Positif | Navigasi ke kamera via card "Hitung otomatis pake struk" | 1. Buka aplikasi<br>2. Pada bagian "Bikin baru", tekan opsi "Hitung otomatis pake struk" | Aplikasi berpindah ke CameraScreen | ⬜ |
| TC-HS-003 | ❌ Negatif | Akses scan struk di platform Web | 1. Buka aplikasi via browser web<br>2. Tekan tombol "Scan Sekarang" atau opsi "Hitung otomatis pake struk" | Muncul snackbar "Fitur Scan OCR hanya tersedia di aplikasi mobile" dan opsi scan di-disable | ⬜ |

### Fitur 1.2: Menampilkan Riwayat Split Bill Terbaru

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-HS-004 | ✅ Positif | Menampilkan daftar riwayat split bill | 1. Buka aplikasi dengan data riwayat yang tersedia<br>2. Scroll ke bagian "Yang terakhir kamu buat" | Daftar split bill terbaru ditampilkan dengan nama, peserta, total, dan status pembayaran | ⬜ |
| TC-HS-005 | ✅ Positif | Navigasi ke halaman riwayat lengkap | 1. Pada HomeScreen, tekan tombol "Lihat riwayat selengkapnya" | Aplikasi berpindah ke HistoryScreen | ⬜ |
| TC-HS-006 | ❌ Negatif | Menampilkan state kosong ketika tidak ada riwayat | 1. Buka aplikasi dengan kondisi tidak ada data riwayat | Menampilkan pesan "Belum ada riwayat split bill" | ⬜ |

### Fitur 1.3: Fitur Draft dan Bantuan

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-HS-007 | ✅ Positif | Menampilkan jumlah draft | 1. Buka aplikasi<br>2. Lihat badge "Draft" di pojok kanan atas | Jumlah draft ditampilkan dalam format "Draft (X)" | ⬜ |
| TC-HS-008 | ✅ Positif | Tekan tombol Draft | 1. Pada HomeScreen, tekan tombol "Draft" di pojok kanan atas | Muncul snackbar "Fitur Draft belum tersedia" | ⬜ |
| TC-HS-009 | ❌ Negatif | Tekan tombol Bantuan | 1. Pada HomeScreen, tekan icon bantuan (?) di pojok kanan atas | Muncul snackbar "Fungsi Bantuan belum tersedia" | ⬜ |

---

## 2. CameraScreen

### Fitur 2.1: Akses Kamera

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-CS-001 | ✅ Positif | Memberikan izin akses kamera | 1. Buka CameraScreen untuk pertama kali<br>2. Saat muncul permintaan izin, tekan "Izinkan Kamera" | Kamera aktif dan menampilkan preview, tombol shutter dapat digunakan | ⬜ |
| TC-CS-002 | ✅ Positif | Kamera sudah memiliki izin sebelumnya | 1. Buka CameraScreen setelah sebelumnya sudah memberikan izin | Preview kamera langsung aktif tanpa permintaan izin lagi | ⬜ |
| TC-CS-003 | ❌ Negatif | Menolak izin akses kamera | 1. Buka CameraScreen<br>2. Tolak permintaan izin kamera | Muncul layar permintaan izin dengan pesan "Bolehkah kami akses kameramu?" dan tombol "Izinkan Kamera" | ⬜ |

### Fitur 2.2: Mengambil Foto Struk

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-CS-004 | ✅ Positif | Mengambil foto dengan menekan tombol shutter | 1. Pada CameraScreen dengan preview aktif<br>2. Arahkan kamera ke struk<br>3. Tekan tombol shutter (lingkaran putih) | Foto diambil dan berpindah ke PreviewScreen menampilkan hasil foto | ⬜ |
| TC-CS-005 | ✅ Positif | Mengaktifkan/menonaktifkan flash | 1. Pada CameraScreen<br>2. Tekan tombol flash | Icon berubah antara FlashOn/FlashOff dan lampu flash aktif/nonaktif | ⬜ |
| TC-CS-006 | ❌ Negatif | Mencoba foto tanpa izin kamera | 1. Pada CameraScreen tanpa izin kamera diberikan<br>2. Tekan tombol shutter | Tombol shutter tidak berfungsi (disabled dengan warna abu-abu) | ⬜ |

### Fitur 2.3: Memilih Foto dari Galeri

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-CS-007 | ✅ Positif | Memilih foto struk dari galeri | 1. Pada CameraScreen, tekan icon galeri (gambar)<br>2. Pilih foto struk dari galeri | Foto yang dipilih ditampilkan di PreviewScreen | ⬜ |
| TC-CS-008 | ✅ Positif | Membatalkan pemilihan galeri | 1. Pada CameraScreen, tekan icon galeri<br>2. Tekan tombol back/cancel tanpa memilih foto | Kembali ke CameraScreen tanpa perubahan | ⬜ |
| TC-CS-009 | ❌ Negatif | Tidak ada foto yang dipilih dari galeri | 1. Pada CameraScreen, tekan icon galeri<br>2. Tutup picker galeri tanpa memilih foto (swipe down/back) | Tetap di CameraScreen, tidak ada error | ⬜ |

---

## 3. PreviewScreen

### Fitur 3.1: Preview Foto yang Diambil

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-PS-001 | ✅ Positif | Menampilkan preview foto dari kamera | 1. Ambil foto dari CameraScreen | Foto ditampilkan di PreviewScreen dengan mode fit to screen, background hitam | ⬜ |
| TC-PS-002 | ✅ Positif | Menampilkan preview foto dari galeri | 1. Pilih foto dari galeri di CameraScreen | Foto ditampilkan di PreviewScreen (tanpa tombol "Foto ulang", hanya "Pakai foto ini") | ⬜ |
| TC-PS-003 | ❌ Negatif | Gagal memuat gambar | 1. Simulasi kondisi gagal decode gambar | Muncul pesan "Gagal memuat gambar" berwarna putih | ⬜ |

### Fitur 3.2: Konfirmasi atau Foto Ulang

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-PS-004 | ✅ Positif | Konfirmasi penggunaan foto | 1. Pada PreviewScreen, tekan tombol "Pakai foto ini" | Foto dikonfirmasi dan dilanjutkan ke proses OCR/RincianScreen | ⬜ |
| TC-PS-005 | ✅ Positif | Foto ulang dari kamera | 1. Pada PreviewScreen (foto dari kamera), tekan tombol "Foto ulang" | Kembali ke CameraScreen untuk mengambil foto baru | ⬜ |
| TC-PS-006 | ❌ Negatif | Kembali melalui tombol back arrow | 1. Pada PreviewScreen, tekan icon panah kembali di pojok kiri atas | Kembali ke CameraScreen, foto preview dihapus | ⬜ |

---

## 4. RincianScreen

### Fitur 4.1: Menampilkan Hasil Scan OCR

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-RS-001 | ✅ Positif | Menampilkan daftar item dari struk | 1. Setelah foto struk di-scan<br>2. Lihat card "Rincian Item" | Daftar item ditampilkan dengan nama, quantity (x), dan harga | ⬜ |
| TC-RS-002 | ✅ Positif | Menampilkan summary pembayaran | 1. Pada RincianScreen, scroll ke bagian bawah daftar item | Summary ditampilkan: Subtotal, Pajak, Servis, Diskon, Lainnya, dan Jumlah Total | ⬜ |
| TC-RS-003 | ❌ Negatif | Struk tidak terbaca dengan baik | 1. Scan foto struk yang buram atau tidak jelas | Menampilkan hasil OCR seadanya dengan opsi "Ubah rincian" untuk koreksi manual | ⬜ |

### Fitur 4.2: Input Nama Split Bill

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-RS-004 | ✅ Positif | Mengisi nama split bill | 1. Pada card pertama, ketik nama di field "Nama Split Bill"<br>2. Contoh: "Makan Siang Kantor" | Nama tersimpan dan ditampilkan sebagai judul split bill | ⬜ |
| TC-RS-005 | ✅ Positif | Nama split bill otomatis dari OCR | 1. Lihat field nama split bill setelah scan | Nama toko/merchant dari struk otomatis terisi sebagai nama default | ⬜ |
| TC-RS-006 | ❌ Negatif | Nama split bill kosong | 1. Hapus semua teks di field nama split bill<br>2. Kosongkan field | Field menampilkan placeholder "Kasih nama split bill disini" | ⬜ |

### Fitur 4.3: Info Tambahan (Catatan)

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-RS-007 | ✅ Positif | Menambahkan catatan tambahan | 1. Tekan tombol "Masukkin info tambahan di sini"<br>2. Isi catatan di bottom sheet<br>3. Tekan "Selesai" | Catatan tersimpan dan preview ditampilkan di button (max 35 karakter + "..") | ⬜ |
| TC-RS-008 | ✅ Positif | Menghapus catatan | 1. Buka bottom sheet catatan<br>2. Tekan tombol "Hapus"<br>3. Tekan "Selesai" | Catatan dihapus, button kembali menampilkan placeholder | ⬜ |
| TC-RS-009 | ❌ Negatif | Menutup bottom sheet tanpa menyimpan | 1. Buka bottom sheet catatan<br>2. Isi catatan<br>3. Swipe down atau tekan X untuk menutup tanpa tekan "Selesai" | Perubahan tidak tersimpan, catatan kembali ke nilai sebelumnya | ⬜ |

### Fitur 4.4: Thumbnail Foto Struk

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-RS-010 | ✅ Positif | Menampilkan thumbnail foto struk | 1. Pada card "Struk berhasil di-scan", lihat thumbnail | Thumbnail foto struk ditampilkan 80x80dp dengan rounded corner | ⬜ |
| TC-RS-011 | ✅ Positif | Foto ulang struk | 1. Tekan tombol "Foto ulang" di sebelah kanan thumbnail | Kembali ke CameraScreen untuk mengambil foto baru | ⬜ |
| TC-RS-012 | ❌ Negatif | Thumbnail tidak tersedia | 1. Kondisi imageBytes null | Menampilkan placeholder icon kamera dengan background abu-abu | ⬜ |

### Fitur 4.5: Konfirmasi Rincian

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-RS-013 | ✅ Positif | Konfirmasi rincian split bill | 1. Review semua data rincian<br>2. Tekan tombol "Konfirmasi" di bottom bar | Lanjut ke proses selanjutnya (SelectMemberScreen) | ⬜ |
| TC-RS-014 | ✅ Positif | Ubah rincian sebelum konfirmasi | 1. Tekan tombol "Ubah rincian" | Berpindah ke UbahRincianScreen untuk edit manual | ⬜ |
| TC-RS-015 | ❌ Negatif | Kembali tanpa konfirmasi | 1. Tekan tombol back arrow di pojok kiri atas | Kembali ke screen sebelumnya, data rincian tidak tersimpan | ⬜ |

---

## 5. UbahRincianScreen

### Fitur 5.1: Edit Item Pesanan

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-UR-001 | ✅ Positif | Mengubah nama pesanan | 1. Pada item row, ketik nama baru di field nama<br>2. Contoh: ubah "NASI GORENG" menjadi "Nasi Goreng Special" | Nama pesanan berubah sesuai input | ⬜ |
| TC-UR-002 | ✅ Positif | Mengubah quantity pesanan | 1. Pada item row, ubah angka di kotak quantity<br>2. Contoh: ubah "1" menjadi "2" | Quantity berubah dan subtotal ter-update otomatis | ⬜ |
| TC-UR-003 | ❌ Negatif | Nama pesanan kosong | 1. Hapus semua teks di field nama pesanan | Placeholder "Nama pesanan" ditampilkan, tombol konfirmasi disabled | ⬜ |

### Fitur 5.2: Edit Harga Pesanan

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-UR-004 | ✅ Positif | Mengubah harga pesanan | 1. Pada item row, ketik harga baru di field harga<br>2. Contoh: ubah "25000" menjadi "30000" | Harga berubah dan subtotal/total ter-update otomatis | ⬜ |
| TC-UR-005 | ✅ Positif | Clear "0" saat focus | 1. Klik pada field harga yang bernilai "0" | Nilai "0" otomatis ter-clear, siap diinput angka baru | ⬜ |
| TC-UR-006 | ❌ Negatif | Harga pesanan 0 atau kosong | 1. Kosongkan field harga atau isi 0<br>2. Coba tekan tombol konfirmasi | Tombol "Konfirmasi" menjadi disabled (abu-abu) | ⬜ |

### Fitur 5.3: Tambah/Hapus Pesanan

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-UR-007 | ✅ Positif | Menambah item pesanan baru | 1. Tekan tombol "Tambah pesanan"<br>2. Isi nama dan harga item baru | Item baru ditambahkan di list, subtotal ter-update | ⬜ |
| TC-UR-008 | ✅ Positif | Menghapus item pesanan | 1. Tekan icon menu (3 titik) pada item<br>2. Pilih "Hapus pesanan ini" di bottom sheet | Item dihapus dari list, subtotal ter-update | ⬜ |
| TC-UR-009 | ❌ Negatif | Menghapus item terakhir | 1. Jika hanya ada 1 item, tekan menu hapus | Item tidak terhapus (minimal harus ada 1 item) | ⬜ |

### Fitur 5.4: Edit Summary (Pajak, Servis, Diskon)

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-UR-010 | ✅ Positif | Mengubah nilai pajak | 1. Pada bagian summary, ubah field "Pajak"<br>2. Contoh: input "5000" | Total ter-update dengan penambahan pajak | ⬜ |
| TC-UR-011 | ✅ Positif | Mengubah nilai diskon | 1. Pada bagian summary, ubah field "Diskon"<br>2. Contoh: input "10000" | Total ter-update dengan pengurangan diskon | ⬜ |
| TC-UR-012 | ❌ Negatif | Total manual lebih kecil dari subtotal | 1. Edit field "Jumlah total" manual menjadi lebih kecil dari subtotal<br>2. Lihat nilai "Lainnya" | "Lainnya" menampilkan nilai negatif dengan warna merah | ⬜ |

### Fitur 5.5: Validasi dan Konfirmasi

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-UR-013 | ✅ Positif | Konfirmasi perubahan rincian | 1. Edit beberapa item dan summary<br>2. Tekan tombol "Konfirmasi" | Data terupdate dikembalikan ke RincianScreen dengan nilai baru | ⬜ |
| TC-UR-014 | ✅ Positif | Auto-calculate total | 1. Ubah item atau summary<br>2. Lihat field "Jumlah total" | Total otomatis ter-calculate: Subtotal + Pajak + Servis - Diskon | ⬜ |
| TC-UR-015 | ❌ Negatif | Validasi item incomplete | 1. Tambah item baru tanpa mengisi nama atau harga > 0<br>2. Coba konfirmasi | Tombol "Konfirmasi" disabled dengan warna abu-abu | ⬜ |

---

## 6. SelectMemberScreen

### Fitur 6.1: Pembayar (Payer)

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-SM-001 | ✅ Positif | Menampilkan pembayar default | 1. Buka SelectMemberScreen | Menampilkan "Bayar ke" dengan info pembayar default (Saya) beserta wallet | ⬜ |
| TC-SM-002 | ✅ Positif | Ganti pembayar | 1. Tekan tombol "Ganti" di sebelah info pembayar<br>2. Pilih member lain sebagai pembayar | Berpindah ke ReplacePayerScreen | ⬜ |
| TC-SM-003 | ❌ Negatif | Pembayar tanpa informasi wallet | 1. Lihat pembayar yang tidak memiliki wallet | Field wallet tidak ditampilkan (hanya nama) | ⬜ |

### Fitur 6.2: Pemilihan Anggota dari Kontak

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-SM-004 | ✅ Positif | Memilih anggota dari daftar kontak | 1. Berikan izin akses kontak<br>2. Checklist kontak yang ingin dijadikan anggota | Kontak yang dipilih muncul sebagai avatar di bagian "Anggota" | ⬜ |
| TC-SM-005 | ✅ Positif | Mencari kontak | 1. Ketik nama atau nomor di field "Cari kontak..."<br>2. Contoh: ketik "Budi" | Daftar kontak terfilter sesuai query pencarian | ⬜ |
| TC-SM-006 | ❌ Negatif | Mencari kontak tidak ditemukan | 1. Ketik nama yang tidak ada di kontak<br>2. Contoh: "xyz123" | Muncul pesan 'Tidak ditemukan "xyz123"' | ⬜ |

### Fitur 6.3: Izin Akses Kontak

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-SM-007 | ✅ Positif | Memberikan izin akses kontak | 1. Pada bagian kontak, tekan "Izinkan akses kontak"<br>2. Izinkan di dialog sistem | Daftar kontak ditampilkan | ⬜ |
| TC-SM-008 | ✅ Positif | Daftar kontak loading | 1. Setelah izin diberikan, tunggu loading | CircularProgressIndicator ditampilkan selama loading | ⬜ |
| TC-SM-009 | ❌ Negatif | Tidak ada izin kontak | 1. Tolak izin akses kontak<br>2. Lihat bagian kontak | Menampilkan card "Izinkan akses kontak" dengan icon dan keterangan | ⬜ |

### Fitur 6.4: Tambah Anggota Manual

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-SM-010 | ✅ Positif | Menambah anggota di luar kontak | 1. Tekan tombol "Di luar kontak"<br>2. Isi nama dan (opsional) nomor HP di bottom sheet<br>3. Tekan "Tambah" | Anggota baru ditambahkan dan muncul di avatar row | ⬜ |
| TC-SM-011 | ✅ Positif | Menghapus anggota yang sudah dipilih | 1. Tekan icon X merah pada avatar anggota | Anggota dihapus dari daftar | ⬜ |
| TC-SM-012 | ❌ Negatif | Menambah anggota tanpa nama | 1. Buka bottom sheet tambah anggota<br>2. Kosongkan field nama<br>3. Tekan "Tambah" | Tombol "Tambah" disabled atau validasi error | ⬜ |

### Fitur 6.5: Konfirmasi Anggota

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-SM-013 | ✅ Positif | Konfirmasi dengan anggota terpilih | 1. Pilih minimal 1 anggota<br>2. Tekan tombol "Konfirmasi" | Data split bill dengan daftar anggota dikembalikan | ⬜ |
| TC-SM-014 | ✅ Positif | Menampilkan jumlah anggota | 1. Pilih beberapa anggota<br>2. Lihat header "Anggota (X)" | X menunjukkan total anggota termasuk pembayar | ⬜ |
| TC-SM-015 | ❌ Negatif | Konfirmasi tanpa anggota | 1. Jangan pilih anggota manapun<br>2. Tekan tombol "Konfirmasi" | Tombol "Konfirmasi" disabled (abu-abu) | ⬜ |

---

## 7. ReplacePayerScreen

### Fitur 7.1: Menampilkan Daftar Kandidat Pembayar

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-RP-001 | ✅ Positif | Menampilkan semua kandidat pembayar | 1. Buka ReplacePayerScreen | Semua anggota (termasuk current payer) ditampilkan dengan avatar, nama, wallet, dan nomor HP | ⬜ |
| TC-RP-002 | ✅ Positif | Menandai pembayar saat ini | 1. Lihat daftar kandidat pembayar | Pembayar saat ini memiliki badge "Saat ini" dan icon checked | ⬜ |
| TC-RP-003 | ❌ Negatif | Tidak ada kandidat pembayar eligible | 1. Kondisi dimana tidak ada member dengan wallet/phone | Muncul pesan "Tidak ada anggota yang bisa menerima pembayaran" dengan saran untuk melengkapi data | ⬜ |

### Fitur 7.2: Memilih Pembayar Baru

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-RP-004 | ✅ Positif | Memilih pembayar baru | 1. Tekan salah satu anggota dari daftar<br>2. Lihat visual state | Item yang dipilih memiliki background primaryContainer dan icon check | ⬜ |
| TC-RP-005 | ✅ Positif | Konfirmasi penggantian pembayar | 1. Pilih anggota baru sebagai pembayar<br>2. Tekan tombol "Konfirmasi" | Kembali ke SelectMemberScreen dengan pembayar baru, pembayar lama menjadi anggota | ⬜ |
| TC-RP-006 | ❌ Negatif | Kembali tanpa mengubah pembayar | 1. Tekan tombol back arrow tanpa mengubah pilihan | Kembali ke SelectMemberScreen dengan pembayar tetap sama | ⬜ |

### Fitur 7.3: Swap Pembayar dan Anggota

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-RP-007 | ✅ Positif | Pembayar lama menjadi anggota | 1. Ganti pembayar dari A ke B<br>2. Tekan Konfirmasi<br>3. Kembali ke SelectMemberScreen | A muncul di daftar anggota, B menjadi pembayar | ⬜ |
| TC-RP-008 | ✅ Positif | Memilih pembayar yang sama | 1. Pilih pembayar yang sudah aktif (yang sama)<br>2. Tekan Konfirmasi | Tidak ada perubahan, kembali ke SelectMemberScreen | ⬜ |
| TC-RP-009 | ❌ Negatif | Anggota dari members dipindah ke payer | 1. Pilih anggota X sebagai pembayar baru<br>2. Konfirmasi | X dihapus dari members list dan menjadi payer baru | ⬜ |

---

## 8. HistoryScreen

### Fitur 8.1: Menampilkan Riwayat Transaksi

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-HS2-001 | ✅ Positif | Menampilkan daftar riwayat per tanggal | 1. Buka HistoryScreen dengan data riwayat | Bill dikelompokkan berdasarkan tanggal dengan header format "Senin, 28 Jan 2025" | ⬜ |
| TC-HS2-002 | ✅ Positif | Menampilkan detail setiap bill | 1. Lihat item bill di list | Menampilkan: nama bill, peserta, total amount, dan status bayar (X dari Y udah bayar) | ⬜ |
| TC-HS2-003 | ❌ Negatif | Riwayat kosong | 1. Buka HistoryScreen tanpa data riwayat | Muncul pesan "Tidak ada riwayat transaksi" di tengah layar | ⬜ |

### Fitur 8.2: Filter Berdasarkan Tanggal

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-HS2-004 | ✅ Positif | Membuka filter tanggal | 1. Tekan chip filter tanggal (contoh: "7 hari terakhir") | Bottom sheet muncul dengan opsi: 7 hari, 30 hari, 90 hari terakhir | ⬜ |
| TC-HS2-005 | ✅ Positif | Menerapkan filter | 1. Buka filter, pilih "30 hari terakhir"<br>2. Tekan "Pasang filter" | List terupdate menampilkan transaksi 30 hari terakhir, chip berubah | ⬜ |
| TC-HS2-006 | ❌ Negatif | Tidak ada transaksi dalam rentang filter | 1. Pilih filter dengan rentang waktu tanpa transaksi<br>2. Pasang filter | Muncul pesan "Tidak ada riwayat transaksi" | ⬜ |

### Fitur 8.3: Status Pembayaran

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-HS2-007 | ✅ Positif | Bill yang sudah lunas | 1. Lihat bill dengan semua anggota sudah bayar | Status hijau dengan icon CheckCircle, text "X dari X udah bayar" | ⬜ |
| TC-HS2-008 | ✅ Positif | Bill sebagian dibayar | 1. Lihat bill dengan sebagian anggota sudah bayar | Status oranye dengan icon AccessTime | ⬜ |
| TC-HS2-009 | ❌ Negatif | Bill belum ada yang bayar | 1. Lihat bill dengan 0 anggota sudah bayar | Status merah dengan icon AccessTime, text "0 dari X udah bayar" | ⬜ |

### Fitur 8.4: Navigasi dan Error State

| No | Jenis | Skenario | Langkah Pengujian | Hasil yang Diharapkan | Status |
|----|-------|----------|-------------------|----------------------|--------|
| TC-HS2-010 | ✅ Positif | Kembali ke HomeScreen | 1. Tekan tombol back arrow di pojok kiri atas | Kembali ke HomeScreen | ⬜ |
| TC-HS2-011 | ✅ Positif | Loading state | 1. Buka HistoryScreen saat data sedang dimuat | CircularProgressIndicator ditampilkan di tengah layar | ⬜ |
| TC-HS2-012 | ❌ Negatif | Error saat memuat data | 1. Simulasi error fetch data (offline/server error) | Muncul pesan error berwarna merah di tengah layar | ⬜ |

---

## 📊 Ringkasan Test Cases

| Screen | Total Test Cases | Positif | Negatif |
|--------|-----------------|---------|---------|
| HomeScreen | 9 | 6 | 3 |
| CameraScreen | 9 | 6 | 3 |
| PreviewScreen | 6 | 4 | 2 |
| RincianScreen | 15 | 10 | 5 |
| UbahRincianScreen | 15 | 10 | 5 |
| SelectMemberScreen | 15 | 10 | 5 |
| ReplacePayerScreen | 9 | 6 | 3 |
| HistoryScreen | 12 | 8 | 4 |
| **TOTAL** | **90** | **60** | **30** |

---

## ✅ Petunjuk Penggunaan

### Status Pengujian
- ⬜ = Belum diuji
- ✅ = Lulus
- ❌ = Gagal

### Langkah Pelaksanaan
1. Baca skenario dan langkah pengujian dengan teliti
2. Lakukan setiap langkah secara berurutan
3. Bandingkan hasil aktual dengan hasil yang diharapkan
4. Tandai status sesuai hasil pengujian
5. Dokumentasikan bug/issue yang ditemukan

### Format Laporan Bug
```
**Test Case ID:** TC-XX-XXX
**Severity:** Critical/Major/Minor
**Deskripsi Bug:** [Penjelasan bug]
**Langkah Reproduksi:** [Step by step]
**Hasil Aktual:** [Apa yang terjadi]
**Hasil Diharapkan:** [Apa yang seharusnya terjadi]
**Screenshot:** [Jika ada]
```

---

**Dokumen dibuat:** 28 Januari 2026  
**Versi:** 1.0  
**Aplikasi:** Bagi Bill - Split Bill App
