package com.zjsu.course.controller;

import com.zjsu.course.common.ApiResponse;
import com.zjsu.course.exception.BusinessException;
import com.zjsu.course.model.Course;
import com.zjsu.course.model.Enrollment;
import com.zjsu.course.service.CourseService;
import com.zjsu.course.service.EnrollmentService;
import com.zjsu.course.service.StudentService;
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
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private StudentService studentService;

    /**
     * 学生选课
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Enrollment>> createEnrollment(@RequestBody Enrollment enrollment) {
        // 检查课程容量
        Course course = courseService.getCourseById(enrollment.getCourseId());
        long currentEnrollments = enrollmentService.countByCourseId(enrollment.getCourseId());
        
        if (currentEnrollments >= course.getCapacity()) {
            throw new BusinessException("Course capacity exceeded");
        }
        
        Enrollment createdEnrollment = enrollmentService.createEnrollment(enrollment);
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
    public ResponseEntity<ApiResponse<List<Enrollment>>> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(ApiResponse.success(enrollments));
    }

    /**
     * 按课程查询选课记录
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Enrollment>>> getEnrollmentsByCourseId(@PathVariable String courseId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success(enrollments));
    }

    /**
     * 按学生查询选课记录
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Enrollment>>> getEnrollmentsByStudentId(@PathVariable String studentId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success(enrollments));
    }
}
