-- Sample data for development

INSERT INTO students (id, student_id, name, major, grade, email, created_at) VALUES
('S-1', 'S001', '张三', '计算机科学', 2024, 'zhangsan@student.zjsu.edu.cn', NOW()),
('S-2', 'S002', '李四', '软件工程', 2024, 'lisi@student.zjsu.edu.cn', NOW());

INSERT INTO courses (id, code, title, capacity, enrolled, created_at, instructor_id, instructor_name, instructor_email, schedule_day_of_week, schedule_start_time, schedule_end_time, schedule_expected_attendance) VALUES
('C-1', 'CS101', '计算机科学导论', 30, 0, NOW(), 'T001', '张教授', 'zhang@zjsu.edu.cn', 'Monday', '08:00', '10:00', 30),
('C-2', 'MATH201', '高等数学', 40, 0, NOW(), 'T002', '李教授', 'li@zjsu.edu.cn', 'Wednesday', '10:00', '12:00', 40);

-- No enrollments initially
