package com.zjsu.course.config;

import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class H2ConsoleConfig {

    @Bean
    public ServletRegistrationBean<WebServlet> h2ConsoleServletRegistration() {
        ServletRegistrationBean<WebServlet> registration = new ServletRegistrationBean<>(new WebServlet());
        registration.addUrlMappings("/h2-console/*");
        return registration;
    }
}
