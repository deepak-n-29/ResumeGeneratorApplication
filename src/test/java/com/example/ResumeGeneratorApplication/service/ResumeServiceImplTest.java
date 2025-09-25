package com.example.ResumeGeneratorApplication.service;

import com.example.ResumeGeneratorApplication.TestContainerConfiguration;
import com.example.ResumeGeneratorApplication.dto.EducationDto;
import com.example.ResumeGeneratorApplication.dto.ExperienceDto;
import com.example.ResumeGeneratorApplication.dto.ProjectDto;
import com.example.ResumeGeneratorApplication.dto.ResumeDto;
import com.example.ResumeGeneratorApplication.entity.Education;
import com.example.ResumeGeneratorApplication.entity.Experience;
import com.example.ResumeGeneratorApplication.entity.Project;
import com.example.ResumeGeneratorApplication.entity.Resume;
import com.example.ResumeGeneratorApplication.exception.ResourceNotFoundException;
import com.example.ResumeGeneratorApplication.repository.ResumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@ExtendWith(MockitoExtension.class)
class ResumeServiceImplTest {

    @Mock
    private ResumeRepository resumeRepository;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    @Spy
    private ModelMapper modelMapper;

    private ResumeDto resumeDto;
    private Resume resume;

    private EducationDto educationDto;
    private Education education;

    private ExperienceDto experienceDto;
    private Experience experience;

    private ProjectDto projectDto;
    private Project project;

    @Captor
    private ArgumentCaptor<Resume> resumeArgumentCaptor;

    @BeforeEach
    void setUp() {
        educationDto = EducationDto
                .builder()
                .institution("VVCE")
                .degree("b.e")
                .fieldOfStudy("cse")
                .grade("A")
                .startDate(LocalDate.of(2019,06,01))
                .endDate(LocalDate.of(2023,06,01))
                .build();
        education = modelMapper.map(educationDto, Education.class);

        experienceDto = ExperienceDto.builder()
                .jobTitle("Software Engineer")
                .companyName("Tech Solutions Pvt Ltd")
                .location("Bangalore, India")
                .startDate(LocalDate.of(2021, 7, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .currentJob(false)   // not current job since endDate exists
                .description("Worked on developing REST APIs using Spring Boot and microservices architecture. " +
                        "Collaborated with a team of 5 engineers and improved system performance by 30%.")
                .build();
        experience = modelMapper.map(experienceDto, Experience.class);

        projectDto = ProjectDto.builder()
                .title("Resume Generator Application")
                .description("A Spring Boot application that allows users to create, manage, and download resumes in PDF format. " +
                        "Supports multiple resumes per user with sections like education, experience, and projects.")
                .techStack(Set.of("Java", "Spring Boot", "PostgreSQL", "React", "Docker"))
                .url("https://github.com/deepak/resume-generator")
                .build();
        project = modelMapper.map(projectDto, Project.class);


        resumeDto = ResumeDto.builder()
                .id(1L)
                .title("Backend Developer Resume")
                .fullName("John Doe")
                .email("john@example.com")
                .phone("1234567890")
                .address("New York, USA")
                .summary("Experienced backend developer.")
                .educationList(List.of(educationDto))
                .experienceList(List.of(experienceDto))
                .projects(List.of(projectDto))
                .skills(new HashSet<>(List.of("Java", "Spring Boot", "PostgreSQL")))
                .build();
        resume = modelMapper.map(resumeDto,Resume.class);
        resume.setId(1L);
    }


    @Test
    void createResume() {
        //assign
        when(resumeRepository.save(any(Resume.class))).thenReturn(resume);
        when(modelMapper.map(resumeDto, Resume.class)).thenReturn(resume);

        when(modelMapper.map(resume, ResumeDto.class)).thenReturn(resumeDto);

        //act
        ResumeDto result = resumeService.createResume(resumeDto);



        verify(resumeRepository).save(resumeArgumentCaptor.capture());
        Resume savedResume = resumeArgumentCaptor.getValue();

        //assert
        assertThat(savedResume.getEducationList()).allMatch(education -> education.getResume()==savedResume);
        assertThat(savedResume.getExperienceList()).allMatch(experience -> experience.getResume()==savedResume);
        assertThat(savedResume.getProjects()).allMatch(project -> project.getResume()==savedResume);


        verify(resumeRepository).save(resume);
        assertThat(result.getFullName()).isEqualTo("John Doe");
    }


    @Test
    void updateResume() {
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));
        when(resumeRepository.save(any())).thenReturn(resume);

        when(modelMapper.map(educationDto, Education.class)).thenReturn(education);
        when(modelMapper.map(experienceDto, Experience.class)).thenReturn(experience);
        when(modelMapper.map(projectDto, Project.class)).thenReturn(project);


        when(modelMapper.map(resume, ResumeDto.class)).thenReturn(resumeDto);

        ResumeDto updatedDto = ResumeDto.builder()
                .title("Senior Developer")
                .fullName("John Updated")
                .email("john@example.com")  // Email unchanged
                .phone("9999999999")
                .address("New Address")
                .summary("Updated summary")
                .educationList(List.of(educationDto))
                .experienceList(List.of(experienceDto))
                .projects(List.of(projectDto))
                .skills(Set.of("Java", "Kubernetes"))
                .build();

        ResumeDto result = resumeService.updateResume(1L, updatedDto);

        verify(resumeRepository).save(resumeArgumentCaptor.capture());
        Resume saved = resumeArgumentCaptor.getValue();

        assertThat(saved.getTitle()).isEqualTo("Senior Developer");
        assertThat(saved.getExperienceList()).hasSize(1);
        assertThat(saved.getEducationList()).hasSize(1);
        assertThat(saved.getProjects()).hasSize(1);

        assertThat(result).isNotNull();
    }

    @Test
    void updateResume_shouldThrowIfNotFound() {
        when(resumeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.updateResume(1L, resumeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void getResume_shouldReturnDto() {
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));
        when(modelMapper.map(resume, ResumeDto.class)).thenReturn(resumeDto);

        ResumeDto result = resumeService.getResume(1L);

        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("John Doe");
    }

    @Test
    void getResume_shouldThrowIfNotFound() {
        when(resumeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.getResume(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void getAllResumes_shouldReturnList() {
        when(resumeRepository.findAll()).thenReturn(List.of(resume));
        when(modelMapper.map(resume, ResumeDto.class)).thenReturn(resumeDto);

        List<ResumeDto> resumes = resumeService.getAllResumes();

        assertThat(resumes).hasSize(1);
    }

    @Test
    void deleteResume_shouldDeleteIfExists() {
        when(resumeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(resumeRepository).deleteById(1L);

        assertThatCode(() -> resumeService.deleteResume(1L)).doesNotThrowAnyException();

        verify(resumeRepository).existsById(1L);
        verify(resumeRepository).deleteById(1L);
    }


    @Test
    void deleteResume_shouldThrowIfNotFound() {
        Long id = 1L;

        when(resumeRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> resumeService.deleteResume(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Resume with ID " + id + " not found");

        verify(resumeRepository, never()).deleteById(anyLong());
    }



    @Test
    void deleteAllResumes_shouldDeleteAll() {
        doNothing().when(resumeRepository).deleteAll();

        resumeService.deleteAllResumes();

        verify(resumeRepository).deleteAll();
    }
}