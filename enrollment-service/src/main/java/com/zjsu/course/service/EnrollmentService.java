package com.zjsu.course.service;

import com.zjsu.course.exception.BusinessException;
import com.zjsu.course.exception.ResourceNotFoundException;
import com.zjsu.course.model.EnrollmentRecord;
import com.zjsu.course.repository.EnrollmentJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选课业务逻辑层（通过 HTTP 调用 catalog-service、user-service）
 */
@Service
public class EnrollmentService {

    private final EnrollmentJpaRepository enrollmentRepository;
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    @Value("${services.user-service.url:user-service}")
    private String userServiceBase;

    @Value("${services.catalog-service.url:catalog-service}")
    private String catalogServiceBase;

    public EnrollmentService(EnrollmentJpaRepository enrollmentRepository,
                             RestTemplate restTemplate,
                             DiscoveryClient discoveryClient) {
        this.enrollmentRepository = enrollmentRepository;
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    public List<EnrollmentRecord> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public EnrollmentRecord getEnrollmentById(String id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
    }

    @Transactional
    public EnrollmentRecord createEnrollment(EnrollmentRecord enrollment) {
        if (enrollment.getCourseId() == null || enrollment.getCourseId().trim().isEmpty()) {
            throw new BusinessException("课程ID不能为空");
        }
        if (enrollment.getStudentId() == null || enrollment.getStudentId().trim().isEmpty()) {
            throw new BusinessException("学生ID不能为空");
        }

        String courseId = enrollment.getCourseId().trim();
        String studentId = enrollment.getStudentId().trim();

        // 校验学生是否存在（服务发现 + 负载均衡调用 user-service）
        ensureServiceAvailable(resolveServiceName(userServiceBase), "user-service");
        String userUrl = buildServiceUrl(userServiceBase, "/api/students/" + studentId);
        try {
            restTemplate.getForObject(userUrl, Map.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        } catch (Exception e) {
            throw new BusinessException("Failed to call user service: " + e.getMessage());
        }

        // 调用 catalog-service 校验课程并获取容量
        ensureServiceAvailable(resolveServiceName(catalogServiceBase), "catalog-service");
        String url = buildServiceUrl(catalogServiceBase, "/api/courses/" + courseId);
        Map<String, Object> response;
        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        } catch (Exception e) {
            throw new BusinessException("Failed to call catalog service: " + e.getMessage());
        }

        Map<String, Object> courseData = extractDataOrSelf(response, "catalog service");
        Number capacityNum = (Number) courseData.get("capacity");
        Number enrolledNum = (Number) courseData.get("enrolled");
        int capacity = capacityNum == null ? 0 : capacityNum.intValue();
        int enrolledCount = enrolledNum == null ? 0 : enrolledNum.intValue();

        if (enrolledCount >= capacity) {
            throw new BusinessException("Course is full");
        }

        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new BusinessException("Already enrolled in this course");
        }

        enrollment.setCourseId(courseId);
        enrollment.setStudentId(studentId);

        EnrollmentRecord saved = enrollmentRepository.save(enrollment);

        updateCourseEnrolledCount(courseId, enrolledCount + 1);

        return saved;
    }

    @Transactional
    public void deleteEnrollment(String id) {
        EnrollmentRecord enrollment = getEnrollmentById(id);

        enrollmentRepository.deleteById(id);

        String courseId = enrollment.getCourseId();
        long remaining = enrollmentRepository.countByCourseId(courseId);
        updateCourseEnrolledCount(courseId, (int) remaining);
    }

    public List<EnrollmentRecord> getEnrollmentsByCourseId(String courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public List<EnrollmentRecord> getEnrollmentsByStudentId(String studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public boolean existsByCourseIdAndStudentId(String courseId, String studentId) {
        return enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId);
    }

    public long countByCourseId(String courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }

    private void updateCourseEnrolledCount(String courseId, int newCount) {
        String url = buildServiceUrl(catalogServiceBase, "/api/courses/" + courseId);
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("enrolled", newCount);
        try {
            restTemplate.put(url, updateData);
        } catch (Exception e) {
            System.err.println("Failed to update course enrolled count: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractDataOrSelf(Map<String, Object> response, String sourceName) {
        if (response == null) {
            throw new BusinessException("Invalid response from " + sourceName);
        }
        Object data = response.get("data");
        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        if (data == null && !response.isEmpty()) {
            return response;
        }
        throw new BusinessException("Invalid response from " + sourceName);
    }

    private String buildServiceUrl(String base, String path) {
        String normalized = base;
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "http://" + normalized;
        }
        if (path != null && !path.startsWith("/")) {
            normalized = normalized + "/";
        }
        return path == null ? normalized : normalized + path;
    }

    private String resolveServiceName(String base) {
        if (base == null || base.isEmpty()) {
            return null;
        }
        if (!base.startsWith("http://") && !base.startsWith("https://")) {
            return base;
        }
        try {
            java.net.URI uri = java.net.URI.create(base);
            return uri.getHost();
        } catch (Exception e) {
            return null;
        }
    }

    private void ensureServiceAvailable(String serviceName, String fallbackLabel) {
        if (serviceName == null || discoveryClient == null) {
            return;
        }
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances == null || instances.isEmpty()) {
            throw new BusinessException("Service not available: " + (fallbackLabel != null ? fallbackLabel : serviceName));
        }
    }
}
