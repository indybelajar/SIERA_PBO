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
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('mentor', 'mentee') NOT NULL,
    group_id INT,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE SET NULL
);

-- 4. User Profiles Table (Data Tambahan Opsional dari UI Profile)
CREATE TABLE user_profiles (
    user_id INT PRIMARY KEY,
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
    user_id INT NOT NULL,
    submission_link VARCHAR(255),
    status ENUM('Pending', 'Submitted', 'Accepted', 'Rejected') DEFAULT 'Pending',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 7. Attendance Table (Absensi Mentee - 1 Tabel Praktis)
CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    agenda ENUM('mentoring 1', 'mentoring 2', 'mentoring 3', 'patribera day 1', 'patribera day 2') NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('hadir', 'sakit', 'izin', 'tanpa keterangan') NOT NULL,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ==========================================
-- DUMMY DATA UNTUK TESTING UI
-- ==========================================

-- Insert Groups
INSERT INTO groups (group_name) VALUES
('Kelompok 82'),
('Kelompok 83');

-- Insert Users (Password: mentor123 / mentee123)
INSERT INTO users (name, email, password, role, group_id) VALUES
('Zulfi Alisya', 'mentor@pkkmb.com', 'mentor123', 'mentor', 1),
('John Mentee', 'mentee1@pkkmb.com', 'mentee123', 'mentee', 1),
('Jane Mentee', 'mentee2@pkkmb.com', 'mentee123', 'mentee', 1);

-- Insert User Profiles (Mengisi beberapa field sebagai sampel)
INSERT INTO user_profiles (user_id, jurusan, fakultas, kontak, x_handle) VALUES
(1, 'S1 Sistem Informasi', 'Fakultas Ilmu Komputer', '081234567890', '@zulfi_x'),
(2, 'S1 Informatika', 'Fakultas Ilmu Komputer', '081987654321', '@john_x'),
(3, 'S1 Sains Data', 'Fakultas Ilmu Komputer', '081112223333', '@jane_x');

-- Insert Tasks (Mentor memberi tugas ke Kelompok 82)
INSERT INTO tasks (group_id, title, description, deadline) VALUES
(1, 'Tugas 1: Perkenalan', 'Buat video perkenalan singkat.', '2026-05-29 23:59:00'),
(1, 'Tugas 2: Resume Materi', 'Buat resume materi bela negara.', '2026-06-05 23:59:00');

-- Insert Task Submissions (Mentee mengumpulkan tugas)
INSERT INTO task_submissions (task_id, user_id, submission_link, status) VALUES
(1, 2, 'drive.google.com/john_video', 'Accepted'),
(1, 3, 'drive.google.com/jane_video', 'Submitted'),
(2, 2, NULL, 'Pending');

-- Insert Attendance (Absensi untuk Mentoring 1)
INSERT INTO attendance (user_id, agenda, attendance_date, status, notes) VALUES
(2, 'mentoring 1', '2026-05-29', 'hadir', ''),
(3, 'mentoring 1', '2026-05-29', 'izin', 'Sakit demam berdarah');