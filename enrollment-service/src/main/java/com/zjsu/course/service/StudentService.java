package com.zjsu.course.service;

import com.zjsu.course.exception.BusinessException;
import com.zjsu.course.exception.ResourceNotFoundException;
import com.zjsu.course.model.Student;
import com.zjsu.course.repository.EnrollmentJpaRepository;
import com.zjsu.course.repository.StudentJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 学生业务逻辑层
 */
@Service
public class StudentService {
    
    @Autowired
    private StudentJpaRepository studentRepository;
    
    @Autowired
    private EnrollmentJpaRepository enrollmentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public Student createStudent(Student student) {
        // 手动验证必填字段
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new BusinessException("学号不能为空");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new BusinessException("姓名不能为空");
        }
        if (student.getMajor() == null || student.getMajor().trim().isEmpty()) {
            throw new BusinessException("专业不能为空");
        }
        if (student.getGrade() == null) {
            throw new BusinessException("入学年份不能为空");
        }
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new BusinessException("邮箱不能为空");
        }
        
        // 验证邮箱格式
        if (!student.getEmail().contains("@") || !student.getEmail().contains(".")) {
            throw new BusinessException("邮箱格式不正确");
        }
        
        // 检查学号是否已存在
        if (studentRepository.existsByStudentId(student.getStudentId())) {
            throw new BusinessException("Student ID already exists: " + student.getStudentId());
        }
        
        // 生成唯一ID和创建时间
        student.setId(UUID.randomUUID().toString());
        student.setCreatedAt(LocalDateTime.now());
        
        return studentRepository.save(student);
    }

    public Student updateStudent(String id, Student studentDetails) {
        Student student = getStudentById(id);
        
        // 检查新的学号是否与其他学生冲突
        if (!student.getStudentId().equals(studentDetails.getStudentId())) {
            if (studentRepository.existsByStudentId(studentDetails.getStudentId())) {
                throw new BusinessException("Student ID already exists: " + studentDetails.getStudentId());
            }
        }
        
        student.setStudentId(studentDetails.getStudentId());
        student.setName(studentDetails.getName());
        student.setMajor(studentDetails.getMajor());
        student.setGrade(studentDetails.getGrade());
        student.setEmail(studentDetails.getEmail());
        
        return studentRepository.save(student);
    }

    public void deleteStudent(String id) {
        Student student = getStudentById(id);
        
            // 检查该学生（按外部 studentId，即学号）是否有选课记录，防止误删
            String externalStudentId = student.getStudentId();
            if (externalStudentId != null && enrollmentRepository.countByStudentId(externalStudentId) > 0) {
                throw new BusinessException("无法删除：该学生存在选课记录");
            }
        
        studentRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return studentRepository.existsById(id);
    }

    public boolean existsByStudentId(String studentId) {
        if (studentId == null) return false;
        return studentRepository.existsByStudentId(studentId.trim());
    }

    public Student getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with studentId: " + studentId));
    }
}
