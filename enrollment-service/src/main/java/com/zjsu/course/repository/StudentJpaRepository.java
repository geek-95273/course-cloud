package com.zjsu.course.repository;

import com.zjsu.course.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentJpaRepository extends JpaRepository<Student, String> {
    Optional<Student> findByStudentId(String studentId);
    boolean existsByStudentId(String studentId);

    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);

    List<Student> findByMajor(String major);
    List<Student> findByGrade(Integer grade);
}
