package com.example.ResumeGeneratorApplication.controller;

import com.example.ResumeGeneratorApplication.dto.ResumeDto;
import com.example.ResumeGeneratorApplication.service.ResumeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResumeControllerTest {

    @Mock
    private ResumeService resumeService;

    @InjectMocks
    private ResumeController resumeController;

    private ResumeDto resumeDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resumeDto = ResumeDto.builder()
                .id(1L)
                .title("Backend Developer Resume")
                .fullName("John Doe")
                .email("john@example.com")
                .phone("1234567890")
                .address("New York, USA")
                .summary("Experienced backend developer.")
                .skills(new HashSet<>(List.of("Java", "Spring Boot", "PostgreSQL")))
                .build();
    }

    @Test
    void createResume() {
        when(resumeService.createResume(any(ResumeDto.class))).thenReturn(resumeDto);

        ResponseEntity<ResumeDto> response = resumeController.createResume(resumeDto);
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo(resumeDto);

        verify(resumeService, times(1)).createResume(resumeDto);

    }

    @Test
    void updateResume() {
        Long id= 1L;
        ResumeDto updatedDto = ResumeDto.builder()
                .fullName("John Doe Updated")
                .title("Backend Developer Resume1")
                .build();

        when(resumeService.updateResume(eq(id),any(ResumeDto.class))).thenReturn(updatedDto);

        ResponseEntity<ResumeDto> response = resumeController.updateResume(id, updatedDto);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(updatedDto);

        verify(resumeService, times(1)).updateResume(id, updatedDto);
    }

    @Test
    void getResume() {
        when(resumeService.getResume(1L)).thenReturn(resumeDto);
        ResponseEntity<ResumeDto> response = resumeController.getResume(1L);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(resumeDto);

        verify(resumeService).getResume(1L);
    }

    @Test
    void getAllResumes() {
        ResumeDto resumeDto1 = ResumeDto.builder()
                .id(1L)
                .fullName("John Doe")
                .build();
        ResumeDto resumeDto2 = ResumeDto.builder()
                .id(2L)
                .fullName("John Doe")
                .build();
        List<ResumeDto> allResumes = List.of(resumeDto1,resumeDto2);

        when(resumeService.getAllResumes()).thenReturn(allResumes);

        ResponseEntity<List<ResumeDto>> response = resumeController.getAllResumes();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(resumeDto1,resumeDto2);

        verify(resumeService,times(1)).getAllResumes();
    }

    @Test
    void deleteResume() {
        doNothing().when(resumeService).deleteResume(1L);
        ResponseEntity<Void> response = resumeController.deleteResume(1L);
        assertThat(response.getStatusCodeValue()).isEqualTo(204);

        verify(resumeService,times(1)).deleteResume(1L);
    }

    @Test
    void deleteAllResumes() {
        doNothing().when(resumeService).deleteAllResumes();
        ResponseEntity<Void> response = resumeController.deleteAllResumes();
        assertThat(response.getStatusCodeValue()).isEqualTo(204);

        verify(resumeService,times(1)).deleteAllResumes();
    }
}