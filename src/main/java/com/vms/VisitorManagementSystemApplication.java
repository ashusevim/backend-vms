package com.vms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Visitor Management System (VMS) application.
 *
 * <p>This Spring Boot application provides a comprehensive visitor management
 * solution with features including visit request creation, approval workflows,
 * QR code-based check-in/check-out, notifications, and analytics dashboards.</p>
 *
 * <p>Scheduling is enabled to support automated tasks such as auto-closing
 * expired visits.</p>
 *
 * @author VMS Team
 * @version 1.0
 */
@SpringBootApplication
@EnableScheduling
public class VisitorManagementSystemApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(VisitorManagementSystemApplication.class, args);
    }
}
