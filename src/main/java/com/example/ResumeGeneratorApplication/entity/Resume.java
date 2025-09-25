package com.example.ResumeGeneratorApplication.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;
    private String summary;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name= "education_order")
    @JsonManagedReference
    private List<Education> educationList; // Ordered education (usually chronological)

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name= "experience_order")
    @JsonManagedReference
    private List<Experience> experienceList; // Ordered experience (most recent to oldest)

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name= "project_order") // Ordered project list
    @JsonManagedReference
    private List<Project> projects;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "resume_skills", joinColumns = @JoinColumn(name = "resume_id"))
    private Set<String> skills;  // Unordered, unique skill set

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}
