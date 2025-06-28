package com.example.ResumeGeneratorApplication.service;

import com.example.ResumeGeneratorApplication.dto.ResumeDto;

import java.util.List;

public interface ResumeService {

    ResumeDto createResume(ResumeDto resumeDto);

    ResumeDto updateResume(Long id, ResumeDto resumeDto);// Update an existing resume

    ResumeDto getResume(Long id); // Get resume by ID

    List<ResumeDto> getAllResumes();// Get all resumes

    void deleteResume(Long id); // Delete resume by ID

    void deleteAllResumes(); // Delete all resumes

}
