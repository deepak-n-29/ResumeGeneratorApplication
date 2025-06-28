package com.example.ResumeGeneratorApplication.repository;

import com.example.ResumeGeneratorApplication.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByTitle(String title);
}
