package com.zjsu.course.repository;

import com.zjsu.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseJpaRepository extends JpaRepository<Course, String> {
    Optional<Course> findByCode(String code);

    // embedded instructor.id -> findByInstructorId
    List<Course> findByInstructorId(String instructorId);

    @Query("select c from Course c where c.capacity > c.enrolled")
    List<Course> findWithAvailableSeats();

    List<Course> findByTitleContaining(String keyword);
}
