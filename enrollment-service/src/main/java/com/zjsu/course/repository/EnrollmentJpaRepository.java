package com.zjsu.course.repository;

import com.zjsu.course.model.Enrollment;
import com.zjsu.course.model.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentJpaRepository extends JpaRepository<Enrollment, String> {
    List<Enrollment> findByCourseId(String courseId);
    List<Enrollment> findByStudentId(String studentId);
    boolean existsByCourseIdAndStudentId(String courseId, String studentId);
    long countByCourseId(String courseId);
    long countByStudentId(String studentId);
    long countByCourseIdAndStatus(String courseId, EnrollmentStatus status);
}
