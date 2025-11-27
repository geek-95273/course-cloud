package com.zjsu.course.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;

/**
 * 课程时间安排（嵌入对象）
 */
@Embeddable
public class ScheduleSlot {
    @JsonProperty("dayOfWeek")
    private String dayOfWeek;
    
    @JsonProperty("startTime")
    private String startTime;
    
    @JsonProperty("endTime")
    private String endTime;
    
    @JsonProperty("expectedAttendance")
    private Integer expectedAttendance;

    public ScheduleSlot() {}

    public ScheduleSlot(String dayOfWeek, String startTime, String endTime, Integer expectedAttendance) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.expectedAttendance = expectedAttendance;
    }

    // Getters and Setters
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getExpectedAttendance() {
        return expectedAttendance;
    }

    public void setExpectedAttendance(Integer expectedAttendance) {
        this.expectedAttendance = expectedAttendance;
    }
}
