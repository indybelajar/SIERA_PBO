# 📚 PKKMB Information System

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

PKKMB-Information-System/
├── src/
│ ├── model/ # Model classes (User, Mentor, Mentee, dll)
│ ├── dao/ # Data Access Object
│ ├── database/ # Database connection
│ ├── view/ # GUI Views
│ └── Main.java # Entry point
├── lib/ # Library JAR files
│ ├── mysql-connector-j-9.7.0.jar
│ └── flatlaf-3.4.1.jar (opsional)
├── database/
│ └── schema.sql # Database schema
└── README.md

text

## 🚀 Cara Menjalankan

### Prasyarat

1. **Java JDK 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
2. **MySQL Server** - [Download](https://dev.mysql.com/downloads/mysql/)
3. **VS Code** (opsional) - [Download](https://code.visualstudio.com/)

### Langkah-langkah

#### 1. Clone Repository

```bash
git clone https://github.com/username/PKKMB-Information-System.git
cd PKKMB-Information-System
2. Setup Database
bash
# Login ke MySQL
mysql -u root -p

# Jalankan schema.sql
source database/schema.sql
Atau copy-paste isi database/schema.sql ke MySQL Workbench/phpMyAdmin.

3. Konfigurasi Database
Buka src/database/DBConnection.java dan sesuaikan:

java
private static final String URL = "jdbc:mysql://localhost:3306/pkkmb_db";
private static final String USERNAME = "root";   // Ganti dengan username MySQL Anda
private static final String PASSWORD = "";       // Ganti dengan password MySQL Anda
4. Compile
bash
# Windows
javac -cp "lib/mysql-connector-j-9.7.0.jar;lib/flatlaf-3.4.1.jar" -d bin src/model/*.java src/dao/*.java src/database/*.java src/view/*.java src/Main.java

# Mac/Linux
javac -cp "lib/mysql-connector-j-9.7.0.jar:lib/flatlaf-3.4.1.jar" -d bin src/model/*.java src/dao/*.java src/database/*.java src/view/*.java src/Main.java
5. Run
bash
# Windows
java -cp "bin;lib/mysql-connector-j-9.7.0.jar;lib/flatlaf-3.4.1.jar" Main

# Mac/Linux
java -cp "bin:lib/mysql-connector-j-9.7.0.jar:lib/flatlaf-3.4.1.jar" Main
🔑 Akun Demo
Role	Email	Password
Mentor	mentor@pkkmb.com	mentor123
Mentee 1	mentee1@pkkmb.com	mentee123
Mentee 2	mentee2@pkkmb.com	mentee123
📦 Cara Export ke JAR (Untuk Teman)
Membuat Executable JAR
Buat file manifest.txt:

text
Main-Class: Main
Class-Path: lib/mysql-connector-j-9.7.0.jar lib/flatlaf-3.4.1.jar
Buat JAR:

bash
jar cvfm PKKMB-System.jar manifest.txt -C bin .
Jalankan JAR:

bash
java -jar PKKMB-System.jar
```
