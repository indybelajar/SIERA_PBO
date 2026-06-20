-- Create database
CREATE DATABASE IF NOT EXISTS pkkmb_db;
USE pkkmb_db;

-- Users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Groups table
CREATE TABLE groups (
    id INT PRIMARY KEY AUTO_INCREMENT,
    group_name VARCHAR(100) NOT NULL
);

-- Group members table
CREATE TABLE group_members (
    id INT PRIMARY KEY AUTO_INCREMENT,
    group_id INT NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tasks table
CREATE TABLE tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    deadline DATE,
    mentor_id INT,
    FOREIGN KEY (mentor_id) REFERENCES users(id)
);

-- Submissions table
CREATE TABLE submissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    task_id INT NOT NULL,
    user_id INT NOT NULL,
    submission_text TEXT,
    submitted_at DATETIME,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Attendance table
CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert sample data
INSERT INTO users (name, email, password, role) VALUES
('Admin Mentor', 'mentor@pkkmb.com', 'mentor123', 'mentor'),
('John Mentee', 'mentee1@pkkmb.com', 'mentee123', 'mentee'),
('Jane Mentee', 'mentee2@pkkmb.com', 'mentee123', 'mentee');

INSERT INTO groups (group_name) VALUES
('Group A'),
('Group B');

INSERT INTO group_members (group_id, user_id) VALUES
(1, 2),
(1, 3);

INSERT INTO tasks (title, description, deadline, mentor_id) VALUES
('Introduction to PKKMB', 'Write a brief introduction about yourself', '2026-07-01', 1),
('Team Building Exercise', 'Complete the team building assignment', '2026-07-15', 1);

INSERT INTO attendance (user_id, attendance_date, status) VALUES
(2, '2026-06-20', 'Hadir'),
(3, '2026-06-20', 'Izin');