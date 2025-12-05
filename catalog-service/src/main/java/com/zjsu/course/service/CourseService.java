package com.zjsu.course.service;

import com.zjsu.course.exception.BusinessException;
import com.zjsu.course.exception.ResourceNotFoundException;
import com.zjsu.course.model.Course;
import com.zjsu.course.repository.CourseJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;

/**
 * 课程业务逻辑层
 */
@Service
public class CourseService {
    
    @Autowired
    private CourseJpaRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(String id) {
    // 尝试按内部 id 查找，找不到时再尝试按 code 查找，兼容客户端传入 code 或 id 的情况
    if (id == null) {
        throw new ResourceNotFoundException("Course not found with id: " + id);
    }
    String key = id.trim();
    return courseRepository.findById(key)
        .orElseGet(() -> courseRepository.findByCode(key)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id or code: " + id)));
    }

    public Course createCourse(Course course) {
        // 检查课程代码是否已存在
        if (courseRepository.findByCode(course.getCode()).isPresent()) {
            throw new BusinessException("Course code already exists: " + course.getCode());
        }
        
        return courseRepository.save(course);
    }

    public Course updateCourse(String id, Course courseDetails) {
        Course course = getCourseById(id);
        
        // 检查新的课程代码是否与其他课程冲突
        if (!course.getCode().equals(courseDetails.getCode())) {
            if (courseRepository.findByCode(courseDetails.getCode()).isPresent()) {
                throw new BusinessException("Course code already exists: " + courseDetails.getCode());
            }
        }
        
        course.setCode(courseDetails.getCode());
        course.setTitle(courseDetails.getTitle());
        course.setInstructor(courseDetails.getInstructor());
        course.setSchedule(courseDetails.getSchedule());
        course.setCapacity(courseDetails.getCapacity());
        
        return courseRepository.save(course);
    }

    /**
     * 支持部分更新：当传入 map 时只更新存在的字段（用于更新 enrolled 等数值）
     */
    public Course updateCourseFromMap(String id, java.util.Map<String, Object> updates) {
        Course course = getCourseById(id);
        if (updates.containsKey("code")) {
            String newCode = (String) updates.get("code");
            if (!course.getCode().equals(newCode) && courseRepository.findByCode(newCode).isPresent()) {
                throw new BusinessException("Course code already exists: " + newCode);
            }
            course.setCode(newCode);
        }
        if (updates.containsKey("title")) {
            course.setTitle((String) updates.get("title"));
        }
        if (updates.containsKey("instructor")) {
            // keep existing behavior: expect full instructor object -> rely on Jackson mapping when controller passes Map
            Object ins = updates.get("instructor");
            // naive handling: if it's a map, convert via simple construction
            if (ins instanceof java.util.Map) {
                java.util.Map m = (java.util.Map) ins;
                com.zjsu.course.model.Instructor instructor = new com.zjsu.course.model.Instructor();
                instructor.setId((String) m.get("id"));
                instructor.setName((String) m.get("name"));
                instructor.setEmail((String) m.get("email"));
                course.setInstructor(instructor);
            }
        }
        if (updates.containsKey("schedule")) {
            Object sch = updates.get("schedule");
            if (sch instanceof java.util.Map) {
                java.util.Map m = (java.util.Map) sch;
                com.zjsu.course.model.ScheduleSlot s = new com.zjsu.course.model.ScheduleSlot();
                Object dow = m.get("dayOfWeek");
                if (dow instanceof String) {
                    String day = ((String) dow).trim().toUpperCase();
                    if (!day.isEmpty()) {
                        try {
                            s.setDayOfWeek(DayOfWeek.valueOf(day));
                        } catch (IllegalArgumentException ignored) {
                            // keep null if not parsable
                        }
                    }
                } else if (dow instanceof DayOfWeek) {
                    s.setDayOfWeek((DayOfWeek) dow);
                }
                s.setStartTime((String) m.get("startTime"));
                s.setEndTime((String) m.get("endTime"));
                course.setSchedule(s);
            }
        }
        if (updates.containsKey("capacity")) {
            Object cap = updates.get("capacity");
            if (cap instanceof Number) course.setCapacity(((Number) cap).intValue());
        }
        if (updates.containsKey("enrolled")) {
            Object en = updates.get("enrolled");
            if (en instanceof Number) course.setEnrolled(((Number) en).intValue());
        }

        return courseRepository.save(course);
    }

    public Course getCourseByCode(String code) {
        return courseRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with code: " + code));
    }

    public void deleteCourse(String id) {
        Course course = getCourseById(id);
        courseRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        if (id == null) return false;
        String key = id.trim();
        // 既支持按内部 id，也支持按 course code
        return courseRepository.existsById(key) || courseRepository.findByCode(key).isPresent();
    }

    public void incrementEnrolled(String courseId) {
        Course course = getCourseById(courseId);
        course.setEnrolled(course.getEnrolled() + 1);
        courseRepository.save(course);
    }

    public void decrementEnrolled(String courseId) {
        Course course = getCourseById(courseId);
        if (course.getEnrolled() > 0) {
            course.setEnrolled(course.getEnrolled() - 1);
            courseRepository.save(course);
        }
    }
}
