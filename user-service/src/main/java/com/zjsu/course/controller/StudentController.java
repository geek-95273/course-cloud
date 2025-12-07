package com.zjsu.course.controller;

import com.zjsu.course.common.ApiResponse;
import com.zjsu.course.model.Student;
import com.zjsu.course.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生管理API控制器
 */
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {
    
    @Autowired
    private StudentService studentService;

    /**
     * 创建学生
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Student>> createStudent(@RequestBody Student student) {
        Student createdStudent = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdStudent));
    }

    /**
     * 查询所有学生
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    /**
     * 根据ID查询学生
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> getStudentById(@PathVariable String id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success(student));
    }

    /**
     * 更新学生信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> updateStudent(@PathVariable String id, 
                                                             @RequestBody Student studentDetails) {
        Student updatedStudent = studentService.updateStudent(id, studentDetails);
        return ResponseEntity.ok(ApiResponse.success(updatedStudent));
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }
}
