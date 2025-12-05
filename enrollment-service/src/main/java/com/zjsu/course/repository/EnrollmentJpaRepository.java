package com.zjsu.course.repository;

import com.zjsu.course.model.EnrollmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentRecord, String> {
    List<EnrollmentRecord> findByCourseId(String courseId);
    List<EnrollmentRecord> findByStudentId(String studentId);
    boolean existsByCourseIdAndStudentId(String courseId, String studentId);
    long countByCourseId(String courseId);
    long countByStudentId(String studentId);
}
