package com.example.ResumeGeneratorApplication.controller;

import com.example.ResumeGeneratorApplication.entity.*;
import com.example.ResumeGeneratorApplication.repository.ResumeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ResumeControllerIntegrationTest extends AbstractIntegrationTest {

    private Resume buildResumeAggregate(String title) {
        Resume resume = Resume.builder()
                .title(title)
                .fullName("Test User")      // must match authenticated user
                .email("test@example.com")  // must match authenticated user
                .phone("1234567890")
                .address("123 Main St")
                .summary("Profile summary text")
                .skills(new HashSet<>(Set.of("Java", "Spring Boot")))
                .build();

        Education edu = Education.builder()
                .institution("VVCE")
                .degree("B.E.")
                .fieldOfStudy("CSE")
                .startDate(LocalDate.of(2019, 6, 1))
                .endDate(LocalDate.of(2023, 6, 1))
                .resume(resume)
                .build();

        Experience exp = Experience.builder()
                .jobTitle("Software Engineer")
                .companyName("Tech Solutions Pvt Ltd")
                .location("Bangalore, India")
                .startDate(LocalDate.of(2021, 7, 1))
                .currentJob(false)
                .endDate(LocalDate.of(2023, 12, 31))
                .description("Developed REST APIs and improved performance by 30%.")
                .resume(resume)
                .build();

        Project prj = Project.builder()
                .title("Resume Generator App")
                .description("Spring Boot app that allows users to build and manage resumes.")
                .url("https://github.com/deepak/resume-generator")
                .techStack(new HashSet<>(Set.of("Spring Boot", "Docker")))
                .resume(resume)
                .build();

        resume.setEducationList(new ArrayList<>(List.of(edu)));
        resume.setExperienceList(new ArrayList<>(List.of(exp)));
        resume.setProjects(new ArrayList<>(List.of(prj)));

        return resume;
    }

    @Test
    void create_resume_shouldReturn201_withSecurity() {
        Resume resume = buildResumeAggregate("My First Resume");

        webTestClient.post()
                .uri("/resume")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resume)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.id").isNotEmpty()
                .jsonPath("$.data.title").isEqualTo("My First Resume");
    }

    @Test
    void get_resume_byId_shouldReturn200_withSecurity() {
        Resume saved = resumeRepository.save(buildResumeAggregate("Resume A"));

        webTestClient.get()
                .uri("/resume/" + saved.getId())
                .header("Authorization", bearerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo(saved.getId().intValue());
    }

    @Test
    void update_resume_shouldReturn200_withSecurity() {
        Resume saved = resumeRepository.save(buildResumeAggregate("Resume B"));

        saved.setSummary("Full Stack Developer");
        Set<String> newSkills = new HashSet<>(saved.getSkills());
        newSkills.add("Kubernetes");
        saved.setSkills(newSkills);

        webTestClient.put()
                .uri("/resume/" + saved.getId())
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(saved)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.summary").isEqualTo("Full Stack Developer")
                .jsonPath("$.data.skills.length()").isEqualTo(3);
    }

    @Test
    void delete_resume_shouldReturn204_withSecurity() {
        Resume saved = resumeRepository.save(buildResumeAggregate("Resume C"));

        webTestClient.delete()
                .uri("/resume/" + saved.getId())
                .header("Authorization", bearerToken)
                .exchange()
                .expectStatus().isNoContent();

        assertFalse(resumeRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void delete_all_resumes_shouldReturn204_withSecurity() {
        // Arrange: save multiple resumes
        resumeRepository.save(buildResumeAggregate("Resume X"));
        resumeRepository.save(buildResumeAggregate("Resume Y"));

        // Act: call DELETE /resume (bulk delete endpoint)
        webTestClient.delete()
                .uri("/resume") // <-- endpoint to delete all resumes
                .header("Authorization", bearerToken)
                .exchange()
                // Assert: expect 204 No Content
                .expectStatus().isNoContent();

        // Verify DB is empty
        assertFalse(resumeRepository.findAll().iterator().hasNext());
    }


    @Test
    void list_resumes_shouldReturn200_withSecurity() {
        resumeRepository.save(buildResumeAggregate("Resume 1"));
        resumeRepository.save(buildResumeAggregate("Resume 2"));

        webTestClient.get()
                .uri("/resume")
                .header("Authorization", bearerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.length()").isEqualTo(2);
    }
}
