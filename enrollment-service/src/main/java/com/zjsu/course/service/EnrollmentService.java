package com.zjsu.course.service;

import com.zjsu.course.exception.BusinessException;
import com.zjsu.course.exception.ResourceNotFoundException;
import com.zjsu.course.model.Enrollment;
import com.zjsu.course.model.EnrollmentStatus;
import com.zjsu.course.repository.EnrollmentJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 选课业务逻辑层（通过 HTTP 调用 catalog-service 验证课程）
 */
@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentJpaRepository enrollmentRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${catalog-service.url:http://localhost:8081}")
    private String catalogServiceUrl;

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Enrollment getEnrollmentById(String id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
    }

    @Transactional
    public Enrollment createEnrollment(Enrollment enrollment) {
        // 验证必填
        if (enrollment.getCourseId() == null || enrollment.getCourseId().trim().isEmpty()) {
            throw new BusinessException("课程ID不能为空");
        }
        if (enrollment.getStudentId() == null || enrollment.getStudentId().trim().isEmpty()) {
            throw new BusinessException("学生ID不能为空");
        }

        String courseId = enrollment.getCourseId().trim();
        String studentId = enrollment.getStudentId().trim();

        // 验证学生是否存在（本服务）
        if (!studentService.existsByStudentId(studentId)) {
            throw new ResourceNotFoundException("Student not found with studentId: " + studentId);
        }

        // 调用 catalog-service 验证课程并获取容量信息
        String url = catalogServiceUrl + "/api/courses/" + courseId;
        Map<String, Object> response;
        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        } catch (Exception e) {
            throw new BusinessException("Failed to call catalog service: " + e.getMessage());
        }

        if (response == null || !response.containsKey("data")) {
            throw new BusinessException("Invalid response from catalog service");
        }

        Map<String, Object> courseData = (Map<String, Object>) response.get("data");
        Number capacityNum = (Number) courseData.get("capacity");
        Number enrolledNum = (Number) courseData.get("enrolled");
        int capacity = capacityNum == null ? 0 : capacityNum.intValue();
        int enrolledCount = enrolledNum == null ? 0 : enrolledNum.intValue();

        // 检查容量
        if (enrolledCount >= capacity) {
            throw new BusinessException("Course is full");
        }

        // 检查重复选课
        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new BusinessException("Already enrolled in this course");
        }

        // 创建选课记录
        enrollment.setId(UUID.randomUUID().toString());
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setCourseId(courseId);
        enrollment.setStudentId(studentId);

        Enrollment saved = enrollmentRepository.save(enrollment);

        // 异步/容错地更新课程已选人数（调用 catalog-service）
        updateCourseEnrolledCount(courseId, enrolledCount + 1);

        return saved;
    }

    @Transactional
    public void deleteEnrollment(String id) {
        Enrollment enrollment = getEnrollmentById(id);

        // 删除选课记录
        enrollmentRepository.deleteById(id);

        // 尝试更新课程已选人数（可容错）
        String courseId = enrollment.getCourseId();
        long remaining = enrollmentRepository.countByCourseId(courseId);
        updateCourseEnrolledCount(courseId, (int) remaining);
    }

    public List<Enrollment> getEnrollmentsByCourseId(String courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public List<Enrollment> getEnrollmentsByStudentId(String studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public boolean existsByCourseIdAndStudentId(String courseId, String studentId) {
        return enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId);
    }

    public long countByCourseId(String courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }

    private void updateCourseEnrolledCount(String courseId, int newCount) {
        String url = catalogServiceUrl + "/api/courses/" + courseId;
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("enrolled", newCount);
        try {
            restTemplate.put(url, updateData);
        } catch (Exception e) {
            // 记录但不回滚主流程
            System.err.println("Failed to update course enrolled count: " + e.getMessage());
        }
    }
}
