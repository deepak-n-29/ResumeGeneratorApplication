package com.example.ResumeGeneratorApplication.repository;

import com.example.ResumeGeneratorApplication.TestContainerConfiguration;
import com.example.ResumeGeneratorApplication.entity.Resume;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@Import(TestContainerConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //prevents spring from using H2
class ResumeRepositoryTestUsingDataJpaTest {

    @Autowired
    private ResumeRepository resumeRepository;

    private Resume resume;

    @BeforeEach
    void setUp() {
        resume = Resume.builder()
//                .id(1L)
                .title("Backend Developer Resume")
                .fullName("John Doe")
                .email("john@example.com")
                .phone("1234567890")
                .address("New York, USA")
                .summary("Experienced backend developer.")
                .skills(new HashSet<>(List.of("Java", "Spring Boot", "PostgreSQL")))
                .build();

        resumeRepository.save(resume);


    }

    @AfterEach
    void tearDown() {
        resumeRepository.deleteAll();
    }

    @Test
    void findByTitle() {
        Optional<Resume> found = resumeRepository.findByTitle("Backend Developer Resume");

        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("John Doe");
    }
}