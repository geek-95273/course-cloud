package com.zjsu.course.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;

/**
 * 教师信息（嵌入对象）
 */
@Embeddable
public class Instructor {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("email")
    private String email;

    public Instructor() {}

    public Instructor(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
