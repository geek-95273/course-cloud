package com.zjsu.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/db")
    public ResponseEntity<String> db() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                return ResponseEntity.ok("OK");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("DB_ERROR: " + e.getMessage());
        }
        return ResponseEntity.status(500).body("DB_ERROR");
    }
}
