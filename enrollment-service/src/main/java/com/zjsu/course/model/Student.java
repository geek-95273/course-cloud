package com.zjsu.course.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 学生实体类
 */
@Entity
@Table(name = "students", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class Student {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Id
    @Column(name = "id", length = 64)
    private String id;
    
    @JsonProperty("studentId")
    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId;
    
    @JsonProperty("name")
    @Column(name = "name")
    private String name;
    
    @JsonProperty("major")
    @Column(name = "major")
    private String major;
    
    @JsonProperty("grade")
    @Column(name = "grade")
    private Integer grade;
    
    @JsonProperty("email")
    @Column(name = "email", nullable = false, unique = true)
    private String email;



    public Student() {}

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = java.util.UUID.randomUUID().toString();
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public Student(String id, String studentId, String name, String major, 
                   Integer grade, String email, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.major = major;
        this.grade = grade;
        this.email = email;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
