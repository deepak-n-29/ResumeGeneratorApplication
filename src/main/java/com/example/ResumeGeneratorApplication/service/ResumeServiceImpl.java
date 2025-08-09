package com.example.ResumeGeneratorApplication.service;

import com.example.ResumeGeneratorApplication.dto.ResumeDto;
import com.example.ResumeGeneratorApplication.entity.Education;
import com.example.ResumeGeneratorApplication.entity.Experience;
import com.example.ResumeGeneratorApplication.entity.Project;
import com.example.ResumeGeneratorApplication.entity.Resume;
import com.example.ResumeGeneratorApplication.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService{

    private final ResumeRepository resumeRepository;
    private final ModelMapper modelMapper;

    @Override
    public ResumeDto createResume(ResumeDto resumeDto) {
        log.info("Creating Resume for {} with Title: {}", resumeDto.getFullName(), resumeDto.getTitle());
        Resume resume = modelMapper.map(resumeDto, Resume.class);

        resume.getEducationList().forEach(e->e.setResume(resume));
        resume.getExperienceList().forEach(e->e.setResume(resume));
        resume.getProjects().forEach(e->e.setResume(resume));

        Resume saved = resumeRepository.save(resume);
        log.debug("Saved resume with ID: {} and Title: {}", saved.getId(), saved.getTitle());
        return modelMapper.map(saved, ResumeDto.class);
    }

    @Override
    public ResumeDto updateResume(Long id, ResumeDto resumeDto) {
        log.info("Updating resume with ID: {} and Title: {}", id, resumeDto.getTitle());
        Resume existingResume = resumeRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Resume not found"));

        existingResume.setTitle(resumeDto.getTitle());
        existingResume.setFullName(resumeDto.getFullName());
        existingResume.setEmail(resumeDto.getEmail());
        existingResume.setPhone(resumeDto.getPhone());
        existingResume.setAddress(resumeDto.getAddress());
        existingResume.setSummary(resumeDto.getSummary());
        existingResume.setSkills(resumeDto.getSkills());

        existingResume.getExperienceList().clear();
        resumeDto.getExperienceList().forEach(e-> {
            Experience exp = modelMapper.map(e, Experience.class);
            exp.setResume(existingResume);
            existingResume.getExperienceList().add(exp);
        });

        existingResume.getEducationList().clear();
        resumeDto.getEducationList().forEach(e-> {
            Education edu = modelMapper.map(e, Education.class);
            edu.setResume(existingResume);
            existingResume.getEducationList().add(edu);
        });

        existingResume.getProjects().clear();
        resumeDto.getProjects().forEach(e-> {
            Project edu = modelMapper.map(e, Project.class);
            edu.setResume(existingResume);
            existingResume.getProjects().add(edu);
        });

        Resume saved = resumeRepository.save(existingResume);
        log.debug("Resume updated and saved for ID: {} with Title: {}", saved.getId(), saved.getTitle());
        return modelMapper.map(saved, ResumeDto.class);
    }

    @Override
    public ResumeDto getResume(Long id) {
        log.info("Fetching resume with ID: {}", id);
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        return modelMapper.map(resume, ResumeDto.class);
    }

    @Override
    public List<ResumeDto> getAllResumes() {
        log.info("Fetching all resumes");
        return resumeRepository.findAll().stream().map(resume -> modelMapper.map(resume, ResumeDto.class))
                .toList();
    }

    @Override
    public void deleteResume(Long id) {
        log.warn("Deleting resume with ID: {}", id);
        resumeRepository.deleteById(id);
    }

    @Override
    public void deleteAllResumes() {
        log.warn("Deleting ALL resumes");
        resumeRepository.deleteAll();
    }
}
