# Bot Menu

Bot Menu adalah aplikasi Android **offline-first** untuk membuat pengalaman bot lokal tanpa API key.

## Fitur
- Dashboard gelap modern dengan status LOCAL.
- Chat bot lokal dengan auto-reply untuk `/start`, `/help`, dan `/info`.
- Command tambahan dapat dibuat dari dashboard.
- Riwayat chat dan command tersimpan permanen di SQLite perangkat.
- Generator template hook JSON yang bisa disalin ke clipboard.
- Tidak mengirim data ke internet dan tidak membutuhkan database online.

## Build
Buka repository ini di Android Studio atau AIDE, lakukan Gradle sync, lalu build aplikasi.

## Catatan keamanan
Generator hook hanya membuat payload contoh. Untuk webhook sungguhan, gunakan backend milik sendiri, validasi input, autentikasi, dan jangan menaruh token rahasia di aplikasi Android.
