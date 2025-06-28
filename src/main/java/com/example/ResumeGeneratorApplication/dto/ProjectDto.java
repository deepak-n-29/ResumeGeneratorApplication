package com.example.ResumeGeneratorApplication.dto;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    private String title;
    private String description;
    private Set<String> techStack;
    private String url;
}
