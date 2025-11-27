package com.zjsu.course.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 选课记录实体类
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "student_id"}))
public class Enrollment {
    @Id
    @Column(name = "id", length = 64)
    private String id;
    
    @JsonProperty("courseId")
    @Column(name = "course_id", nullable = false)
    private String courseId;
    
    @JsonProperty("studentId")
    @Column(name = "student_id", nullable = false)
    private String studentId;
    
    @JsonProperty("enrolledAt")
    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnrollmentStatus status;

    public Enrollment() {}

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = java.util.UUID.randomUUID().toString();
        if (this.enrolledAt == null) this.enrolledAt = LocalDateTime.now();
        if (this.status == null) this.status = EnrollmentStatus.ACTIVE;
    }

    public Enrollment(String id, String courseId, String studentId, LocalDateTime enrolledAt) {
        this.id = id;
        this.courseId = courseId;
        this.studentId = studentId;
        this.enrolledAt = enrolledAt;
        this.status = EnrollmentStatus.ACTIVE;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }
}

