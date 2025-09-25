package com.example.ResumeGeneratorApplication.controller;

import com.example.ResumeGeneratorApplication.TestContainerConfiguration;
import com.example.ResumeGeneratorApplication.dto.LoginDto;
import com.example.ResumeGeneratorApplication.dto.SignUpRequestDto;
import com.example.ResumeGeneratorApplication.entity.User;
import com.example.ResumeGeneratorApplication.repository.ResumeRepository;
import com.example.ResumeGeneratorApplication.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

@AutoConfigureWebTestClient(timeout = "15000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestContainerConfiguration.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected ResumeRepository resumeRepository;

    @Autowired
    protected UserRepo appUserRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected String bearerToken;

    @BeforeEach
    void authenticate() {

        resumeRepository.deleteAll();
        appUserRepository.deleteAll();

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(java.util.Set.of(com.example.ResumeGeneratorApplication.entity.enums.Role.ADMIN)); // ensure authorities
        appUserRepository.save(user); // [file:249]

        LoginDto login = new LoginDto();
        login.setEmail("test@example.com");
        login.setPassword("password");

        Map<String, Object> envelope = webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(login)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody(); // { timeStamp, data: {accessToken: ...}, apiError } [file:249]

        if (envelope == null || envelope.get("data") == null) {
            throw new IllegalStateException("Login failed: response body missing 'data'");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) envelope.get("data");

        Object accessToken = data.get("accessToken");
        if (accessToken == null) {
            throw new IllegalStateException("Login failed: accessToken not found under 'data'");
        }

        this.bearerToken = "Bearer " + accessToken.toString(); // JwtAuthFilter will accept [file:249]
    }

}
