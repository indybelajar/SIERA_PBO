-- 1. Reset Database (Hapus yang lama, buat yang baru)
DROP DATABASE IF EXISTS pkkmb_db;
CREATE DATABASE pkkmb_db;
USE pkkmb_db;

-- 2. Groups Table (Master Kelompok)
CREATE TABLE groups (
    id INT PRIMARY KEY AUTO_INCREMENT,
    group_name VARCHAR(100) NOT NULL
);

-- 3. Users Table (Master Akun - Terhubung langsung ke group_id)
-- id menggunakan BIGINT tanpa AUTO_INCREMENT agar dapat menampung NIM 10 digit secara langsung
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('mentor', 'mentee') NOT NULL,
    group_id INT,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE SET NULL
);

-- 4. User Profiles Table (Data Tambahan Opsional dari UI Profile)
CREATE TABLE user_profiles (
    user_id BIGINT PRIMARY KEY,
    jurusan VARCHAR(100),
    fakultas VARCHAR(100),
    kontak VARCHAR(20),
    bio TEXT,
    linkedin_url VARCHAR(255),
    instagram_handle VARCHAR(100),
    tiktok_handle VARCHAR(100),
    x_handle VARCHAR(100),
    youtube_url VARCHAR(255),
    other_social VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. Tasks Table (Definisi Tugas dari Mentor)
CREATE TABLE tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    group_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    deadline DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);

-- 6. Task Submissions Table (Pengumpulan Tugas Mentee)
CREATE TABLE task_submissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    task_id INT NOT NULL,
    user_id BIGINT NOT NULL,
    submission_link VARCHAR(255),
    status ENUM('Pending', 'Submitted', 'Accepted', 'Rejected') DEFAULT 'Pending',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 7. Attendance Table (Absensi Mentee - 1 Tabel Praktis)
CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    agenda ENUM('mentoring 1', 'mentoring 2', 'mentoring 3', 'patribera day 1', 'patribera day 2') NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('hadir', 'sakit', 'izin', 'tanpa keterangan') NOT NULL,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 8. Announcements Table (Pengumuman Kelompok)
CREATE TABLE announcements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    group_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);

-- ==========================================
-- DUMMY DATA UNTUK TESTING UI
-- ==========================================

-- Insert Groups
INSERT INTO groups (group_name) VALUES
('Kelompok 1'),
('Kelompok 82');

-- Insert Users (NIM digunakan langsung sebagai primary key 'id')
INSERT INTO users (id, name, email, password, role, group_id) VALUES
(2410512120, 'Zulfi Alisya', '2410512120@mahasiswa.upnvj.ac.id', 'mentor123', 'mentor', 1),
(2410512106, 'Indy Agustin', '2410512106@mahasiswa.upnvj.ac.id', 'mentor123', 'mentor', 2),
(2410512130, 'Tasya Angellica Sugiharto', '2410512130@mahasiswa.upnvj.ac.id', 'mentee123', 'mentee', 2),
(2410512117, 'Naura Belva Qonita', '2410512117@mahasiawa.upnvj.ac.id', 'mentee123', 'mentee', 1);

-- Insert User Profiles (Jurusan & Fakultas diisi agar tidak kosong)
INSERT INTO user_profiles (user_id, jurusan, fakultas, kontak, bio) VALUES
(2410512120, 'S1 Sistem Informasi', 'Fakultas Ilmu Komputer', '081234567890', 'Halo, saya Zulfi, mentor Kelompok 1!'),
(2410512106, 'S1 Sistem Informasi', 'Fakultas Ilmu Komputer', '081234567891', 'Halo, saya Indy, mentor Kelompok 82!'),
(2410512130, 'S1 Sistem Informasi', 'Fakultas Ilmu Komputer', '081234567892', 'Halo, saya Tasya, mentee Kelompok 82!'),
(2410512117, 'S1 Sistem Informasi', 'Fakultas Ilmu Komputer', '081234567893', 'Halo, saya Naura, mentee Kelompok 1!');

-- Insert Tasks
INSERT INTO tasks (group_id, title, description, deadline) VALUES
(1, 'Tugas 1: Perkenalan', 'Buat video perkenalan singkat.', '2026-06-29 23:59:00'),
(1, 'Tugas 2: Resume Materi', 'Buat resume materi bela negara.', '2026-07-05 23:59:00'),
(2, 'Tugas 1: Perkenalan', 'Buat video perkenalan singkat.', '2026-06-29 23:59:00'),
(2, 'Tugas 2: Resume Materi', 'Buat resume materi bela negara.', '2026-07-05 23:59:00');

-- Insert Task Submissions (Menghubungkan ke NIM dummy)
INSERT INTO task_submissions (task_id, user_id, submission_link, status) VALUES
(1, 2410512117, 'drive.google.com/naura_video', 'Accepted'),
(2, 2410512117, NULL, 'Pending'),
(3, 2410512130, 'drive.google.com/tasya_video', 'Submitted'),
(4, 2410512130, NULL, 'Pending');

-- Insert Attendance (Absensi ke NIM dummy)
INSERT INTO attendance (user_id, agenda, attendance_date, status, notes) VALUES
(2410512117, 'mentoring 1', '2026-06-20', 'hadir', ''),
(2410512130, 'mentoring 1', '2026-06-20', 'izin', 'Izin acara keluarga');

-- Insert Announcements (Pengumuman Kelompok)
INSERT INTO announcements (group_id, title, content, created_at) VALUES
(2, 'Semangat untuk Hari Kedua', 'Terima kasih atas dedikasi kalian memantau kelompok hingga sore ini. Tetap semangat dan jangan lupa istirahat yang cukup!', '2026-06-24 16:30:00'),
(2, 'Cek Kondisi Kesehatan Kelompok', 'Mengingat cuaca yang cukup terik, pastikan mentee kalian cukup minum dan jaga kondisi kesehatan.', '2026-06-24 10:30:00'),
(1, 'Pantau Kelengkapan Atribut Mentee', 'Bapak ada pengecekan atribut untuk kegiatan esok hari. Pastikan semua mentee sudah lengkap.', '2026-06-24 20:00:00');