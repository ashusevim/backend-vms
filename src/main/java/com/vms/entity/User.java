package com.vms.entity;

import com.vms.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * JPA entity representing a system user in the Visitor Management System.
 *
 * <p>Users can have one of three roles: {@link Role#ADMIN}, {@link Role#ASSOCIATE},
 * or {@link Role#SECURITY}. Each role grants different levels of access and
 * functionality within the system.</p>
 *
 * <ul>
 *   <li><strong>ADMIN</strong> — manages visit requests, users, and views dashboards</li>
 *   <li><strong>ASSOCIATE</strong> — creates visit requests and receives visitor notifications</li>
 *   <li><strong>SECURITY</strong> — handles visitor check-in and check-out at the gate</li>
 * </ul>
 *
 * @see Role
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "mobile_number")
    private String mobileNumber;

    private String department;

    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
