package com.vms.entity;

import com.vms.enums.IdProofType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * JPA entity representing an external visitor to the organization.
 *
 * <p>Stores personal and identification details of individuals who visit
 * the premises. A visitor can be linked to one or more {@link VisitRequest}
 * records over time.</p>
 *
 * <p>An optional photo may be uploaded and stored on the filesystem;
 * the {@code photoPath} field holds the file system path to the image.</p>
 *
 * @see VisitRequest
 * @see IdProofType
 */
@Entity
@Table(name = "visitors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visitor_seq")
    @SequenceGenerator(name = "visitor_seq", sequenceName = "VISITOR_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    private String email;

    @Column(name = "company_name")
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "id_proof_type")
    private IdProofType idProofType;

    @Column(name = "id_proof_number")
    private String idProofNumber;

    @Column(name = "photo_path")
    private String photoPath;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
