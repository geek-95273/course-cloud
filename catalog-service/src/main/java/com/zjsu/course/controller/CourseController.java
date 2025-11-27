package com.zjsu.course.controller;

import com.zjsu.course.common.ApiResponse;
import com.zjsu.course.exception.BusinessException;
import com.zjsu.course.model.Course;
import com.zjsu.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程管理API控制器
 */
@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {
    
    @Autowired
    private CourseService courseService;

    /**
     * 查询所有课程
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    /**
     * 查询单个课程
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> getCourseById(@PathVariable String id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success(course));
    }

    /**
     * 创建课程
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Course>> createCourse(@RequestBody Course course) {
        Course createdCourse = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdCourse));
    }

    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(@PathVariable String id,
                                                           @RequestBody java.util.Map<String, Object> updates) {
        Course updatedCourse = courseService.updateCourseFromMap(id, updates);
        return ResponseEntity.ok(ApiResponse.success(updatedCourse));
    }

    /**
     * 按课程代码查询
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<Course>> getCourseByCode(@PathVariable String code) {
        Course course = courseService.getCourseByCode(code);
        return ResponseEntity.ok(ApiResponse.success(course));
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully", null));
    }
}
