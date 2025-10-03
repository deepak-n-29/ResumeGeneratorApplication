package com.example.ResumeGeneratorApplication.service;

import com.example.ResumeGeneratorApplication.dto.ResumeDto;
import com.example.ResumeGeneratorApplication.entity.Education;
import com.example.ResumeGeneratorApplication.entity.Experience;
import com.example.ResumeGeneratorApplication.entity.Project;
import com.example.ResumeGeneratorApplication.entity.Resume;
import com.example.ResumeGeneratorApplication.exception.ResourceNotFoundException;
import com.example.ResumeGeneratorApplication.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final ModelMapper modelMapper;
    private static final String CACHE_NAME = "resumes";
    private static final String ALL_KEY = "'all'";


    @Override
    @Cacheable(cacheNames = CACHE_NAME, key="#id")
    public ResumeDto getResume(Long id) {
        log.info("Fetching resume with ID: {}", id);
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Resume not found with ID: {}", id);
                    return new ResourceNotFoundException("Resume not found with ID: " + id);
                });
        log.info("Successfully fetched resume with ID: {}", id);
        return modelMapper.map(resume, ResumeDto.class);
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME, key= ALL_KEY)
    public List<ResumeDto> getAllResumes() {
        log.info("Fetching all resumes");
        List<Resume> resumes = resumeRepository.findAll();
        log.info("Successfully fetched {} resumes", resumes.size());
        return resumes.stream()
                .map(resume -> modelMapper.map(resume, ResumeDto.class))
                .toList();
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CACHE_NAME,key="#result.id")
    @CacheEvict(cacheNames = CACHE_NAME, key = ALL_KEY)
    public ResumeDto createResume(ResumeDto resumeDto) {
        log.info("Creating new resume for {} with title: {}", resumeDto.getFullName(), resumeDto.getTitle());
        Resume resume = modelMapper.map(resumeDto, Resume.class);

        // Set parent references
        resume.getEducationList().forEach(e -> e.setResume(resume));
        resume.getExperienceList().forEach(e -> e.setResume(resume));
        resume.getProjects().forEach(e -> e.setResume(resume));

        Resume saved = resumeRepository.save(resume);
        log.info("Successfully created resume with ID: {}", saved.getId());
        return modelMapper.map(saved, ResumeDto.class);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CACHE_NAME,key="#id")
    @CacheEvict(cacheNames = CACHE_NAME, key = ALL_KEY)
    public ResumeDto updateResume(Long id, ResumeDto resumeDto) {
        log.info("Updating resume with ID: {}", id);
        Resume existingResume = resumeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Resume not found with ID: {}", id);
                    return new ResourceNotFoundException("Resume not found with ID: " + id);
                });

        // Map simple fields
        existingResume.setFullName(resumeDto.getFullName());
        existingResume.setTitle(resumeDto.getTitle());
        existingResume.setEmail(resumeDto.getEmail());
        existingResume.setPhone(resumeDto.getPhone());
        existingResume.setAddress(resumeDto.getAddress());
        existingResume.setSummary(resumeDto.getSummary());
        existingResume.setSkills(resumeDto.getSkills());

        // Clear and re-add nested entities
        existingResume.getExperienceList().clear();
        resumeDto.getExperienceList().forEach(e -> {
            Experience exp = modelMapper.map(e, Experience.class);
            exp.setResume(existingResume);
            existingResume.getExperienceList().add(exp);
        });

        existingResume.getEducationList().clear();
        resumeDto.getEducationList().forEach(e -> {
            Education edu = modelMapper.map(e, Education.class);
            edu.setResume(existingResume);
            existingResume.getEducationList().add(edu);
        });

        existingResume.getProjects().clear();
        resumeDto.getProjects().forEach(p -> {
            Project project = modelMapper.map(p, Project.class);
            project.setResume(existingResume);
            existingResume.getProjects().add(project);
        });

        Resume saved = resumeRepository.save(existingResume);
        log.info("Successfully updated resume with ID: {}", id);
        return modelMapper.map(saved, ResumeDto.class);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME,key="#id")
    public void deleteResume(Long id) {
        log.info("Deleting resume with ID: {}", id);
        boolean exists = resumeRepository.existsById(id);
        if (!exists) {
            log.error("Resume not found with ID: {}", id);
            throw new ResourceNotFoundException("Resume not found with ID: " + id);
        }
        resumeRepository.deleteById(id);
        log.info("Successfully deleted resume with ID: {}", id);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
    public void deleteAllResumes() {
        log.warn("Deleting all resumes");
        resumeRepository.deleteAll();
        log.info("Successfully deleted all resumes");
    }
}
