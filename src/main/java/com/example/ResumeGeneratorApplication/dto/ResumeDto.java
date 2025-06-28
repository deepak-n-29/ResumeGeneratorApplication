package com.example.ResumeGeneratorApplication.dto;

import lombok.*;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeDto {
    private Long id;
    private String title;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String summary;

    private List<EducationDto> educationList;
    private List<ExperienceDto> experienceList;
    private List<ProjectDto> projects;
    private Set<String> skills;
}
