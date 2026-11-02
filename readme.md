Saya Muhammad Rusydi Akramallah dengan NIM 2508255 mengerjakan TP 6 dalam mata kuliah Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin

---

## Desain Program & Struktur Komponen

Permainan Flappy Bird ini dibangun menggunakan bahasa pemrograman Java dengan pustaka grafis **Java Swing** dan **AWT**. Arsitektur program dirancang dengan memisahkan komponen entitas model data, antarmuka tampilan (kanvas grafis & GUI), pengelola logika permainan (fisika & game loop), serta sistem pemutaran audio secara modular (pendekatan *Model-View-Controller* / MVC):

### 1. Titik Masuk Aplikasi (`App.java`)
Class `App` bertindak sebagai *entry point* program yang bertanggung jawab menginisialisasi jendela utama:
* **Pengaturan Window (`JFrame`)**: Menyiapkan jendela aplikasi berukuran tetap 360 x 640 piksel, judul `"FlappyBird"`, posisi di tengah layar (`setLocationRelativeTo(null)`), dan mode non-resizable.
* **Instansiasi & Pengikatan Dependensi**: Menginstansiasi objek `Logic` dan `View`, menghubungkan relasi keduanya via `logic.setView(view)`, mendaftarkan `View` ke dalam frame, serta memastikan fokus input keyboard tertuju pada kanvas tampilan (`view.requestFocus()`).

### 2. Model Objek Pemain (`Player.java`)
Class `Player` berfungsi sebagai representasi objek entitas burung dengan menerapkan prinsip enkapsulasi OOP:
* **Atribut Posisi & Dimensi**: `posX`, `posY` (koordinat posisi pada bidang 2D), serta `width` (34 px) dan `height` (24 px) sebagai batas ukuran *bounding box*.
* **Aset Gambar**: `image` bertipe `Image` untuk menyimpan *sprite* burung (`bird.png`).
* **Atribut Fisika**: `velocityY` (`int`) yang merepresentasikan vektor kecepatan gerak vertikal (dorongan lompatan ke atas maupun percepatan jatuh ke bawah akibat gravitasi).
* **Enkapsulasi**: Dilengkapi dengan method *getter* dan *setter* lengkap untuk mengontrol akses dan modifikasi data objek secara aman.

### 3. Model Objek Rintangan (`Pipe.java`)
Class `Pipe` merepresentasikan rintangan pipa bergerak pada permainan:
* **Atribut Posisi & Dimensi**: `posX`, `posY` (koordinat rintangan), serta `width` (64 px) dan `height` (512 px).
* **Aset Gambar**: `image` bertipe `Image` penampung gambar pipa atas (`upperPipe.png`) atau pipa bawah (`lowerPipe.png`).
* **Dinamika Pergerakan**: `velocityX` (`int`) untuk mengontrol laju pergeseran horizontal pipa ke arah kiri.
* **Status Penilaian**: `passed` (`boolean`) sebagai penanda bendera (*flag*) apakah burung telah melewati pipa terkait, guna mencegah penghitungan skor bertambah lebih dari satu kali.

### 4. Modul Efek Suara (`Sound.java`)
Class `Sound` mengelola pemutaran efek audio digital berbasis Java Sound API (`javax.sound.sampled`):
* **Penyimpanan Berbasis Memori (`byte[] audioData`)**: Membaca berkas audio `.wav` dari direktori aset dan menyimpannya langsung ke dalam array byte RAM saat inisialisasi. Pendekatan ini menjamin pemutaran audio bebas latensi disk I/O dan mencegah *stuttering* pada permainan.
* `play()`: Memutar audio secara *asynchronous* melalui `new Thread(...)` agar tidak memblokir siklus *Game Loop* utama, serta dilengkapi `LineListener` untuk menutup *clip* otomatis saat audio selesai.
* `loop()` & `stop()`: Menyediakan kapabilitas pemutaran audio berulang terus-menerus (`Clip.LOOP_CONTINUOUSLY`) dan penghentian suara.

### 5. Pengendali Logika & Siklus Permainan (`Logic.java`)
Class `Logic` bertindak sebagai otak permainan (*Controller & Physics Engine*) yang mengimplementasikan `ActionListener` dan `KeyListener`:
* **Sistem Finite State Machine (`enum State`)**:
  * `MENU`: Kondisi awal saat layar menampilkan menu utama.
  * `READY`: Kondisi bersiap setelah pemain menekan tombol start, menunggu instruksi lompatan pertama.
  * `PLAYING`: Siklus permainan berlangsung aktif.
  * `DYING`: Fase animasi transisi ketika tabrakan terjadi dan burung jatuh ke tanah.
  * `GAMEOVER`: Kondisi akhir permainan yang memunculkan skor dan opsi mengulang.
* **Pengatur Waktu (*Timer*)**:
  * `gameLoop`: `javax.swing.Timer` dengan interval ~16.6 ms (setara 60 FPS) untuk memperbarui fisika dan memicu proses rendering ulang.
  * `pipesCooldown`: Timer interval 1000 ms untuk memunculkan sepasang pipa baru secara berkala.
* **Variabel Fisika & Skor**: Mengatur gravitasi (`gravity = 1`), daya angkat lompatan (`-10`), kecepatan pipa (`pipeVelocityX = -3`), pergeseran latar (`backgroundSpeed = -1`), dan akumulasi skor (`score`).

### 6. Antarmuka & Renderer Grafis (`View.java`)
Class `View` merupakan turunan dari `JPanel` yang bertindak sebagai antarmuka visual sekaligus kanvas rendering grafis:
* **Komponen GUI Interaktif**:
  * `titleLabel`, `startButton`, `exitButton`: Komponen menu utama dengan pewarnaan kustom dan penataan *absolute layout*.
  * `instructionLabel`: Label petunjuk kendali (*"press [SPACE] to start"*).
  * `scoreLabel`: Label HUD yang menampilkan perolehan skor secara *real-time* di bagian atas.
  * `gameOverLabel` & `restartLabel`: Label penanda kekalahan dan instruksi pengulangan (*"press [R] to restart"*).
* **Tipografi Kustom**: Memuat berkas font retro TrueType (`flappy-font.ttf`).
* **Rendering Kustom (`paintComponent` & `draw`)**:
  * *Infinite Parallax Scrolling*: Menggambar dua gambar latar belakang bersebelahan untuk menghasilkan ilusi pergerakan latar secara berkelanjutan tanpa jeda.
  * *Sprite Rotation Rendering*: Menggunakan transformasi matriks 2D (`Graphics2D.rotate`) untuk memutar sudut kemiringan burung secara proporsional sesuai kecepatan vertikal (`velocityY`), mulai dari menengadah -30° saat melompat hingga menukik 90° saat jatuh bebas.

---

## Penjelasan Alur Kerja & Logika Program

### 1. Inisialisasi Aplikasi & Navigasi Menu Utama (`MENU`)
* Saat aplikasi dijalankan melalui `App.main()`, seluruh aset gambar (`bird.png`, `upperPipe.png`, `lowerPipe.png`, `background.png`), aset audio (`sfx_wing.wav`, `sfx_hit.wav`, `sfx_point.wav`, `sfx_die.wav`), dan aset font dimuat ke dalam memori.
* Aplikasi berada pada status `State.MENU`. Layar menampilkan judul *"Flappy bird"* beserta dua tombol interaktif:
  * **Tombol Start**: Menyembunyikan menu utama, menampilkan HUD skor dan teks instruksi, mengalihkan status ke `State.READY`, serta mengarahkan fokus input ke kanvas permainan.
  * **Tombol Exit**: Menghentikan eksekusi aplikasi secara langsung (`System.exit(0)`).

### 2. Fase Siaga & Pemicuan Terbang (`READY` ke `PLAYING`)
* Pada status `READY`, burung melayang diam di posisi awal tanpa terpengaruh gravitasi, sementara latar belakang mulai bergerak perlahan ke arah kiri.
* Ketika pemain menekan tombol **SPACE** atau **Panah Atas (`VK_UP`)**:
  * Status permainan berubah menjadi `State.PLAYING`.
  * Label instruksi disembunyikan melalui `view.hideInstruction()`.
  * Timer pemunculan pipa (`pipesCooldown`) mulai dijalankan.
  * Burung diberi kecepatan vertikal awal `velocityY = -10` dan efek suara sayap melompat (`jumpSound.play()`) dibunyikan.

### 3. Siklus Game Loop & Simulasi Fisika (`PLAYING`)
Setiap tick timer `gameLoop` (60 FPS), method `actionPerformed()` memanggil `move()` dan memicu penggambaran ulang kanvas via `view.repaint()`:
* **Efek Latar Belakang Bergerak (*Scrolling Background*)**: Nilai koordinat `backgroundX` berkurang sebesar -1 px per frame. Saat nilai mencapai batas negatif lebar layar (`-frameWidth`), koordinat di-reset ke 0 untuk menciptakan efek pengulangan tanpa henti (*infinite seamless loop*).
* **Simulasi Gravitasi & Kendali Lompat**: Nilai `player.velocityY` ditambah dengan konstanta `gravity` (+1) setiap frame sehingga burung tertarik ke bawah. Saat tombol lompat ditekan, `velocityY` di-reset seketika ke `-10`. Posisi Y burung dibatasi agar tidak melampaui batas atas layar (`Math.max(0, player.getPosY())`).
* **Pembangkitan & Pergerakan Rintangan Pipa**:
  * Method `placePipes()` dipicu setiap 1 detik. Posisi vertikal pipa atas ditentukan secara acak (`Math.random()`), sedangkan pipa bawah ditempatkan dengan celah vertikal sebesar 160 piksel (`openingSpace = frameHeight / 4`).
  * Setiap pasang pipa bergerak ke arah kiri dengan kecepatan horizontal -3 px per frame.
  * Timer pembersih otomatis (`cleanupTimer`) berdurasi 3,5 detik menghapus pipa yang sudah keluar dari layar dari `ArrayList<Pipe>` guna mencegah kebocoran memori (*memory leak*).
* **Perhitungan Skor (*Scoring Mechanism*)**:
  * Program mengecek posisi horizontal burung terhadap pipa atas yang belum ditandai (`!pipe.isPassed()`).
  * Ketika sisi kiri burung telah melewati posisi X pipa (`player.getPosX() > pipe.getPosX()`), status pipa diubah menjadi `pipe.setPassed(true)`.
  * Variabel `score` bertambah 1, efek suara koin/poin (`scoreSound.play()`) dibunyikan, dan teks `scoreLabel` diperbarui.
* **Deteksi Tabrakan (*Collision Detection*)**:
  * **Tabrakan dengan Tanah**: Jika koordinat Y burung mencapai batas bawah tanah (`player.getPosY() > 550`), method `death()` dipanggil.
  * **Tabrakan dengan Pipa (AABB Collision)**: Program mengiterasi seluruh pipa aktif dan memeriksa irisan koordinat sumbu X dan Y:
    * Burung menabrak secara horizontal: koordinat horizontal burung tumpang tindih dengan pipa.
    * Burung menabrak secara vertikal: untuk pipa atas ketika posisi Y burung lebih tinggi dari ujung bawah pipa atas, atau untuk pipa bawah ketika posisi bawah burung lebih rendah dari ujung atas pipa bawah.
    * Jika terjadi tabrakan, fungsi `death()` segera dieksekusi.

### 4. Penanganan Kekalahan & Transisi Game Over (`DYING` ke `GAMEOVER`)
* Saat fungsi `death()` dipicu:
  * Status langsung beralih ke `State.DYING`.
  * Timer pemunculan pipa (`pipesCooldown.stop()`) dihentikan.
  * Efek suara benturan (`hitSound.play()`) dibunyikan seketika.
  * Burung diberikan efek sentakan ke atas (`player.setVelocityY(-5)`) sebelum ditarik gravitasi jatuh bebas ke dasar layar.
  * Timer independen (350 ms) membunyikan efek suara kematian (`dieSound.play()`).
  * Timer jeda kedua (800 ms) mengalihkan status ke `State.GAMEOVER`, menghentikan siklus `gameLoop.stop()`, serta menampilkan teks *"game over"* dan *"press [R] to restart"*.

### 5. Mekanisme Pengulangan Permainan (*Restart*)
* Pada status `State.GAMEOVER`, menekan tombol keyboard **R** (`VK_R`) memicu method `resetGame()`:
  * Posisi vertikal burung dikembalikan ke koordinat awal dan nilai `velocityY` di-reset ke 0.
  * Seluruh data pipa dalam `ArrayList<Pipe>` dibersihkan (`pipes.clear()`).
  * Nilai `score` dan posisi `backgroundX` diatur ulang ke 0.
  * Status permainan diubah kembali ke `State.READY`.
  * Komponen antarmuka di-reset melalui `view.resetUI()`.
  * Siklus `gameLoop` diaktifkan kembali (`gameLoop.start()`) untuk menyambut sesi permainan berikutnya.

---

## Dokumentasi Saat Program Dijalankan

Berikut merupakan dokumentasi rekaman demonstrasi jalannya permainan Flappy Bird:

<video src="Dokumentasi/dokumentasi.webm" width="100%" controls></video>
