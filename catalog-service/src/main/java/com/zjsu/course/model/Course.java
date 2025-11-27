package com.zjsu.course.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Entity
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
public class Course {
    @Id
    @Column(name = "id", length = 64)
    private String id;
    
    @JsonProperty("code")
    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @JsonProperty("title")
    @Column(name = "title")
    private String title;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "instructor_id")),
        @AttributeOverride(name = "name", column = @Column(name = "instructor_name")),
        @AttributeOverride(name = "email", column = @Column(name = "instructor_email"))
    })
    private Instructor instructor;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "dayOfWeek", column = @Column(name = "schedule_day_of_week")),
        @AttributeOverride(name = "startTime", column = @Column(name = "schedule_start_time")),
        @AttributeOverride(name = "endTime", column = @Column(name = "schedule_end_time")),
        @AttributeOverride(name = "expectedAttendance", column = @Column(name = "schedule_expected_attendance"))
    })
    private ScheduleSlot schedule;
    
    @JsonProperty("capacity")
    @Column(name = "capacity")
    private Integer capacity;
    
    @JsonProperty("enrolled")
    @Column(name = "enrolled")
    private Integer enrolled;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Course() {
        this.enrolled = 0;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.enrolled == null) this.enrolled = 0;
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public Course(String id, String code, String title, Instructor instructor, 
                  ScheduleSlot schedule, Integer capacity) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.instructor = instructor;
        this.schedule = schedule;
        this.capacity = capacity;
        this.enrolled = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public ScheduleSlot getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleSlot schedule) {
        this.schedule = schedule;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(Integer enrolled) {
        this.enrolled = enrolled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
