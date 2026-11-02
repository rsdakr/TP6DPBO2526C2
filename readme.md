Saya Muhammad Rusydi Akramallah dengan NIM 2508255 mengerjakan TP 6 dalam mata kuliah Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin

---

## Desain Program

Program ini merupakan game *Flappy Bird* sederhana yang dirancang menggunakan pendekatan Pemrograman Berorientasi Objek (PBO) dengan pemisahan peran kelas:

* **`App`**: *Entry point* untuk menginisialisasi window dan menjalankan aplikasi.
* **`View`**: Menangani seluruh tampilan antarmuka (rendering elemen grafis player, pipa, skor, serta tombol menu).
* **`Logic`**: Pengendali logika utama permainan (pergerakan player & gravitasi, spawner pipa, deteksi *collision*, sistem skor, serta pemroses input keyboard).
* **`Player`**: Model data karakter utama (posisi, ukuran, dan kecepatan vertikal).
* **`Pipe`**: Model data rintangan pipa (posisi, ukuran, dan status *passed*).
* **`Sound`**: Model data sound effect.

### Fitur & Spesifikasi:
1. **Main Menu**: Tampilan awal dengan pilihan tombol *Start Game* dan *Exit*.
2. **Game Loop & Physics**: Pergerakan player berbasis gravitasi dan rintangan pipa acak yang bergerak ke kiri.
3. **Deteksi Collision & Game Over**: Permainan berhenti otomatis jika player menabrak pipa atau menyentuh batas bawah layar.
4. **Fitur Restart**: Menekan tombol **'R'** pada keyboard saat Game Over untuk mengulang permainan dari awal.
5. **Sistem Skor**: Skor bertambah (+1) setiap kali player berhasil melewati pipa dan ditampilkan pada layar.
6. **Audio & SFX**: Efek suara saat melompat, mendapat skor, atau menabrak.

---

## Penjelasan Alur Program

1. **Awal Permainan (Main Menu)**: Saat aplikasi dijalankan, sistem menampilkan menu utama. Pemain dapat menekan tombol *Start* untuk memulai atau *Exit* untuk keluar.
2. **Gameplay Loop**:
   * Pemain menekan **Spasi** untuk membuat karakter melompat ke atas.
   * Pipa dimunculkan secara berkala dengan celah acak dan bergerak mendekati player.
   * Setiap pipa yang berhasil dilewati akan menambah nilai skor.
3. **Game Over & Restart**:
   * Jika terjadi benturan dengan pipa atau tanah, *looping* permainan dihentikan dan status berubah menjadi Game Over.
   * Pemain dapat menekan tombol **'R'** untuk mereset posisi player, pipa, dan skor ke kondisi semula.

---

## Dokumentasi Pengujian

[demo.webm](https://github.com/user-attachments/assets/8015ee59-72bb-4648-954c-85c9a30be952)
