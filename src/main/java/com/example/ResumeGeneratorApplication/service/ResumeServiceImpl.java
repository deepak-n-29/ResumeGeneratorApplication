package com.example.ResumeGeneratorApplication.service;

import com.example.ResumeGeneratorApplication.dto.ResumeDto;
import com.example.ResumeGeneratorApplication.entity.Resume;
import com.example.ResumeGeneratorApplication.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService{

    private final ResumeRepository resumeRepository;


    @Override
    public ResumeDto createResume(ResumeDto resumeDto) {
        return null;
    }

    @Override
    public ResumeDto updateResume(Long id, ResumeDto resumeDto) {
        return null;
    }

    @Override
    public ResumeDto getResume(Long id) {
        return null;
    }

    @Override
    public List<ResumeDto> getAllResumes() {
        return List.of();
    }

    @Override
    public void deleteResume(Long id) {

    }

    @Override
    public void deleteAllResumes() {

    }
}
