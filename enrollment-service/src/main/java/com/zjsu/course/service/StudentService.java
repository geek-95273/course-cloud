package com.zjsu.course.service;

import com.zjsu.course.exception.BusinessException;
import com.zjsu.course.exception.ResourceNotFoundException;
import com.zjsu.course.model.Student;
import com.zjsu.course.repository.StudentJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Student domain logic.
 */
@Service
public class StudentService {

    @Autowired
    private StudentJpaRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public Student createStudent(Student student) {
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new BusinessException("Student number is required");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new BusinessException("Name is required");
        }
        if (student.getMajor() == null || student.getMajor().trim().isEmpty()) {
            throw new BusinessException("Major is required");
        }
        if (student.getGrade() == null) {
            throw new BusinessException("Enrollment year is required");
        }
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new BusinessException("Email is required");
        }

        if (!student.getEmail().contains("@") || !student.getEmail().contains(".")) {
            throw new BusinessException("Invalid email format");
        }

        if (studentRepository.existsByStudentId(student.getStudentId())) {
            throw new BusinessException("Student ID already exists: " + student.getStudentId());
        }

        return studentRepository.save(student);
    }

    public Student updateStudent(String id, Student studentDetails) {
        Student student = getStudentById(id);

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
        // Ensure the student exists before deletion
        getStudentById(id);
        studentRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return studentRepository.existsById(id);
    }

    public boolean existsByStudentId(String studentId) {
        if (studentId == null) {
            return false;
        }
        return studentRepository.existsByStudentId(studentId.trim());
    }

    public Student getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with studentId: " + studentId));
    }
}
