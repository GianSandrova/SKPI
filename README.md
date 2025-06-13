# Spring Boot 3 Boilerplate untuk Pelatihan Mahasiswa

Selamat datang di boilerplate aplikasi Java Spring Boot! Proyek ini dirancang sebagai titik awal untuk mahasiswa yang ingin belajar dan mengembangkan aplikasi web menggunakan Spring Boot 3, Java 17, dan MySQL.

## Tujuan Boilerplate

*   Menyediakan struktur proyek yang umum digunakan dan mudah dipahami.
*   Mempercepat proses setup awal proyek Spring Boot.
*   Mengenalkan konsep-konsep dasar seperti Controller, Service, Repository, dan Entity.
*   Menunjukkan konfigurasi dasar untuk koneksi database MySQL.
*   Menjadi dasar untuk berbagai modul pelatihan atau proyek mahasiswa.

## Teknologi yang Digunakan

*   **Java 17**: Versi LTS terbaru dari Java.
*   **Spring Boot 3.x.x**: Framework utama untuk membangun aplikasi.
    *   Spring Web: Untuk membangun RESTful API.
    *   Spring Data JPA: Untuk interaksi dengan database.
    *   Spring Boot DevTools: Untuk pengembangan yang lebih cepat (auto-restart, live reload).
*   **MySQL**: Sistem manajemen database relasional.
*   **Maven**: Alat manajemen dependensi dan build proyek.
*   **(Opsional) Lombok**: Untuk mengurangi boilerplate code pada model/entity.

## Prasyarat

Sebelum memulai, pastikan Anda telah menginstal perangkat lunak berikut:

1.  **Java Development Kit (JDK) 17** atau yang lebih baru.
2.  **Apache Maven 3.6.x** atau yang lebih baru.
3.  **MySQL Server 8.x** atau yang kompatibel.
4.  **IDE (Integrated Development Environment)** seperti:
    *   IntelliJ IDEA (Community atau Ultimate)
    *   Eclipse IDE for Java EE Developers
    *   Visual Studio Code dengan ekstensi Java
5.  **Git** (untuk cloning repository).

## Memulai Proyek

1.  **Konfigurasi Database:**
    *   Buat database baru di MySQL. Misalnya, `skpi_jtk`.
    *   Buka file `src/main/resources/application.properties`.
    *   Sesuaikan konfigurasi koneksi database berikut dengan pengaturan MySQL Anda:
        ```properties
        # MySQL Datasource Configuration
        spring.datasource.url=jdbc:mysql://localhost:3306/pelatihan_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
        spring.datasource.username=root # Ganti dengan username MySQL Anda
        spring.datasource.password=password # Ganti dengan password MySQL Anda
        spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

        # JPA Properties
        spring.jpa.hibernate.ddl-auto=update # 'create' untuk membuat tabel saat startup, 'update' untuk memperbarui, 'validate' untuk validasi, 'none' untuk tidak melakukan apa-apa
        spring.jpa.show-sql=true # Tampilkan query SQL di console
        spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
        spring.jpa.properties.hibernate.format_sql=true # Format SQL agar mudah dibaca
        ```
    *   **Catatan:** `createDatabaseIfNotExist=true` akan mencoba membuat database jika belum ada. `ddl-auto=update` berguna selama pengembangan, namun pertimbangkan `validate` atau `none` untuk produksi.

2.  **Build Proyek :**
    ```bash
    mvn clean install
    ```

3.  **Jalankan Aplikasi:**
    *   **Melalui IDE:** Cari kelas utama `SpringBootBoilerplateApplication.java` dan jalankan sebagai Java Application.
    *   **Melalui Maven:**
        ```bash
        mvn spring-boot:run
        ```
    Aplikasi akan berjalan secara default di `http://localhost:8080`.

## Fitur Utama Boilerplate

*   Struktur proyek yang terorganisir.
*   Konfigurasi dasar untuk Spring Boot 3 dan Java 17.
*   Integrasi dengan MySQL menggunakan Spring Data JPA.
*   Contoh dasar untuk layer Controller, Service, dan Repository.
*   (Opsional) Penanganan error global sederhana.

## Modifikasi dan Pengembangan Lanjutan

Mahasiswa didorong untuk:

1.  **Menambahkan Entitas (Model) Baru**: Definisikan entitas JPA baru di package `entity`.
2.  **Membuat Repository**: Buat interface repository yang extends `JpaRepository` untuk setiap entitas di package `repository`.
3.  **Mengembangkan Service**: Implementasikan logika bisnis di package `service`.
4.  **Membuat Controller**: Buat REST controller baru di package `controller` untuk mengekspos API.
5.  **Menggunakan DTO (Data Transfer Object)**: Untuk transfer data antara layer, terutama antara controller dan service, atau untuk request/response API.
6.  **Menambahkan Validasi**: Gunakan anotasi Bean Validation (misalnya `@NotNull`, `@Size`) pada DTO atau Entitas.
7.  **Implementasi Keamanan**: Pelajari dan implementasikan Spring Security untuk otentikasi dan otorisasi.
8.  **Menulis Tes**: Buat unit test untuk service dan integration test untuk controller.
9.  **Logging**: Manfaatkan SLF4J (atau Logback/Log4j2) untuk logging yang lebih baik.

## Tips untuk Mahasiswa

*   Pahami alur request dari Controller -> Service -> Repository -> Database.
*   Gunakan debugger IDE Anda untuk melacak alur eksekusi dan memahami variabel.
*   Baca dokumentasi resmi Spring Boot dan Spring Data JPA.
*   Jangan takut untuk bereksperimen dan membuat kesalahan. Itu bagian dari proses belajar!
*   Bertanyalah jika ada kesulitan.

## Lisensi

Proyek ini dilisensikan di bawah [LISENSI_ANDA, mis. MIT License]. Lihat file `LICENSE` untuk detailnya. (Jika tidak ada file LICENSE, Anda bisa menghapus bagian ini atau memilih lisensi standar seperti MIT).

---

Semoga berhasil dengan pelatihan Anda!