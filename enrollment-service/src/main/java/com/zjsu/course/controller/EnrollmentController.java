package com.zjsu.course.controller;

import com.zjsu.course.common.ApiResponse;
import com.zjsu.course.model.EnrollmentRecord;
import com.zjsu.course.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选课管理API控制器
 */
@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Value("${server.port}")
    private String serverPort;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

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

    /**
     * 负载均衡/故障转移测试端点，返回当前容器端口
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testInstance() {
        Map<String, Object> body = new HashMap<>();
        body.put("instance", "enrollment-service");
        body.put("port", serverPort);
        body.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(ApiResponse.success(body));
    }
}
