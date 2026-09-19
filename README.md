# Bot Menu

Bot Menu adalah dashboard bot Android yang berjalan **offline-first**. Command disimpan di SQLite lokal perangkat, jadi tidak memerlukan API key atau database eksternal.

## Fitur
- Dashboard gelap dengan status LOCAL.
- Tambah dan hapus command bot.
- Penyimpanan permanen menggunakan SQLite.
- Generator template hook JSON yang dapat disalin ke clipboard.

## Build
Buka folder ini di Android Studio atau AIDE, lalu jalankan Gradle sync dan build. Template hook hanya menghasilkan payload lokal; untuk menerima webhook sungguhan, sambungkan payload tersebut ke server milik sendiri dan tambahkan autentikasi.
