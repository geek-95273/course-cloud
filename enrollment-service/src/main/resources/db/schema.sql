-- Schema for course management

CREATE TABLE IF NOT EXISTS students (
  id VARCHAR(64) PRIMARY KEY,
  student_id VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(255),
  major VARCHAR(255),
  grade INT,
  email VARCHAR(255) NOT NULL UNIQUE,
  created_at DATETIME
);

CREATE TABLE IF NOT EXISTS courses (
  id VARCHAR(64) PRIMARY KEY,
  code VARCHAR(64) NOT NULL UNIQUE,
  title VARCHAR(255),
  capacity INT,
  enrolled INT,
  created_at DATETIME,
  -- embedded instructor and schedule stored as JSON/text or separate columns in simple schema
  instructor_id VARCHAR(64),
  instructor_name VARCHAR(255),
  instructor_email VARCHAR(255),
  schedule_day_of_week VARCHAR(32),
  schedule_start_time VARCHAR(16),
  schedule_end_time VARCHAR(16),
  schedule_expected_attendance INT
);

CREATE TABLE IF NOT EXISTS enrollments (
  id VARCHAR(64) PRIMARY KEY,
  course_id VARCHAR(64) NOT NULL,
  student_id VARCHAR(64) NOT NULL,
  enrolled_at DATETIME,
  status VARCHAR(32),
  CONSTRAINT fk_enroll_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
  CONSTRAINT fk_enroll_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  UNIQUE KEY uq_course_student (course_id, student_id)
);
