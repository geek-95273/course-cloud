package com.zjsu.course.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Teacher user subtype.
 */
@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends User {

    @Column(name = "teacher_id")
    private String teacherId;

    private String name;
    private String department;

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
