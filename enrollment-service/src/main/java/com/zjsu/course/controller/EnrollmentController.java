package com.zjsu.course.controller;

import com.zjsu.course.common.ApiResponse;
import com.zjsu.course.model.EnrollmentRecord;
import com.zjsu.course.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 选课管理API控制器
 */
@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {
    
    @Autowired
    private EnrollmentService enrollmentService;

    /**
     * 学生选课
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentRecord>> createEnrollment(@RequestBody EnrollmentRecord enrollment) {
        EnrollmentRecord createdEnrollment = enrollmentService.createEnrollment(enrollment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdEnrollment));
    }

    /**
     * 学生退课
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteEnrollment(@PathVariable String id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.ok(ApiResponse.success("Enrollment deleted successfully", null));
    }

    /**
     * 查询选课记录
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EnrollmentRecord>>> getAllEnrollments() {
        List<EnrollmentRecord> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(ApiResponse.success(enrollments));
    }

    /**
     * 按课程查询选课记录
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<EnrollmentRecord>>> getEnrollmentsByCourseId(@PathVariable String courseId) {
        List<EnrollmentRecord> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success(enrollments));
    }

    /**
     * 按学生查询选课记录
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<EnrollmentRecord>>> getEnrollmentsByStudentId(@PathVariable String studentId) {
        List<EnrollmentRecord> enrollments = enrollmentService.getEnrollmentsByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success(enrollments));
    }
}
