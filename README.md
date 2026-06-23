# 📚 PKKMB Information System (SIERA_PBO)

Aplikasi desktop berbasis Java Swing untuk membantu pengelolaan kegiatan PKKMB antara Mentor dan Mentee.

## 📋 Fitur

### 👨‍🏫 Mentor
- Login
- Dashboard dengan statistik
- Melihat daftar kelompok & anggota
- Membuat tugas
- Menginput kehadiran mentee
- Melihat profil

### 👨‍🎓 Mentee
- Login
- Dashboard dengan statistik
- Melihat tugas
- Mengumpulkan tugas (simulasi)
- Melihat riwayat kehadiran
- Melihat profil

## 🛠️ Teknologi

- **Java 17+** - Bahasa pemrograman
- **Java Swing** - GUI framework
- **MySQL** - Database
- **JDBC** - Koneksi database
- **FlatLaf** - UI modern (opsional)

## 📁 Struktur Proyek

```
SIERA_PBO/
├── src/
│   ├── model/          # Model classes (User, Mentor, Mentee, dll)
│   ├── dao/            # Data Access Object
│   ├── database/       # Database connection
│   ├── view/           # GUI Views
│   └── Main.java       # Entry point
├── lib/                # Library JAR files
│   ├── mysql-connector-j-9.7.0.jar
│   └── flatlaf-3.4.1.jar (opsional)
├── database/
│   └── schema.sql      # Database schema
├── bin/                # Compiled classes (otomatis dibuat)
└── README.md
```

## 🚀 Cara Menjalankan

### ✅ Prasyarat

1. **Java JDK 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
2. **MySQL Server** - [Download](https://dev.mysql.com/downloads/mysql/)
3. **Git** - [Download](https://git-scm.com/)

### 📖 Langkah-Langkah Instalasi

#### 1. Clone Repository

```bash
git clone https://github.com/indybelajar/SIERA_PBO.git
cd SIERA_PBO
```

#### 2. Setup Database MySQL

Buka terminal dan login ke MySQL:

```bash
mysql -u root -p
```

Kemudian jalankan schema:

```bash
source database/schema.sql;
```

**Alternatif:** Buka file `database/schema.sql` dan copy-paste isinya ke MySQL Workbench atau phpMyAdmin.

#### 3. Konfigurasi Database

Buka file `src/database/DBConnection.java` dan sesuaikan:

```java
private static final String URL = "jdbc:mysql://localhost:3306/pkkmb_db";
private static final String USERNAME = "root";        // Ganti dengan username MySQL Anda
private static final String PASSWORD = "";            // Ganti dengan password MySQL Anda
```

#### 4. Compile Project

**Untuk Windows:**

```bash
javac -cp "lib/mysql-connector-j-9.7.0.jar;lib/flatlaf-3.4.1.jar" -d bin src/model/*.java src/dao/*.java src/database/*.java src/view/*.java src/Main.java
```

**Untuk Mac/Linux:**

```bash
javac -cp "lib/mysql-connector-j-9.7.0.jar:lib/flatlaf-3.4.1.jar" -d bin src/model/*.java src/dao/*.java src/database/*.java src/view/*.java src/Main.java
```

#### 5. Jalankan Aplikasi

**Untuk Windows:**

```bash
java -cp "bin;lib/mysql-connector-j-9.7.0.jar;lib/flatlaf-3.4.1.jar" Main
```

**Untuk Mac/Linux:**

```bash
java -cp "bin:lib/mysql-connector-j-9.7.0.jar:lib/flatlaf-3.4.1.jar" Main
```

## 🔑 Akun Demo

| Role | Email | Password |
|------|-------|----------|
| Mentor | mentor@pkkmb.com | mentor123 |
| Mentee 1 | mentee1@pkkmb.com | mentee123 |
| Mentee 2 | mentee2@pkkmb.com | mentee123 |

## 📦 Cara Export ke JAR (Untuk Teman)

### Membuat Executable JAR

1. Buat file `manifest.txt` di root folder:

```
Main-Class: Main
Class-Path: lib/mysql-connector-j-9.7.0.jar lib/flatlaf-3.4.1.jar
```

2. Buat JAR file:

```bash
jar cvfm SIERA_PBO.jar manifest.txt -C bin .
```

3. Jalankan JAR:

```bash
java -jar SIERA_PBO.jar
```

## ⚡ Troubleshooting

| Masalah | Solusi |
|---------|--------|
| `git: command not found` | Install Git dari [git-scm.com](https://git-scm.com/) |
| `javac: command not found` | Install Java JDK 17+ |
| Database connection error | Pastikan MySQL running & kredensial di `DBConnection.java` benar |
| `ClassNotFoundException` | Pastikan compile ulang dengan command compile yang benar |
| Port 3306 sudah dipakai | Hentikan MySQL service lain atau ganti port di `DBConnection.java` |

## 📞 Bantuan

Jika ada masalah:
1. Cek apakah Java, MySQL, dan Git sudah terinstall
2. Pastikan MySQL service sedang running
3. Verifikasi kredensial database di `DBConnection.java`
4. Coba compile ulang dari awal