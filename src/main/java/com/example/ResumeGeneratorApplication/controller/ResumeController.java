package com.example.ResumeGeneratorApplication.controller;


import com.example.ResumeGeneratorApplication.aspects.MyLogging;
import com.example.ResumeGeneratorApplication.dto.ResumeDto;
import com.example.ResumeGeneratorApplication.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {
    private final ResumeService resumeService;

    @PostMapping
    @Operation(
            summary = "Create a new resume",
            description = "Creates a new resume along with personal details, education, experience, and projects"
    )
    public ResponseEntity<ResumeDto> createResume(@RequestBody ResumeDto resumeDto){
        ResumeDto resume = resumeService.createResume(resumeDto);
        log.info("Received request to create resume for {}", resumeDto.getFullName());
//        return ResponseEntity.ok(resumeService.createResume(resume));
        return new ResponseEntity<>(resume, HttpStatus.CREATED);
//        return ResponseEntity.status(HttpStatus.CREATED).body(resumeService.createResume(resumeDto));
    }

    @Operation(
            summary = "Update an existing resume",
            description = "Updates an existing resume with the given ID and replaces all fields"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ResumeDto> updateResume(@PathVariable Long id, @RequestBody ResumeDto resumeDto){
        log.info("Received request to update resume with ID: {}", id);
        ResumeDto resume = resumeService.updateResume(id, resumeDto);
        return new ResponseEntity<>(resume, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get a resume by ID",
            description = "Returns the resume corresponding to the given ID"
    )
    @MyLogging
    public ResponseEntity<ResumeDto> getResume(@PathVariable Long id) {
        log.info("Fetching resume with ID: {}", id);
        return ResponseEntity.ok(resumeService.getResume(id));
    }


    @GetMapping
    @Operation(
            summary = "Get all resumes",
            description = "Returns a list of all available resumes"
    )
    public ResponseEntity<List<ResumeDto>> getAllResumes() {
        log.info("Fetching all resumes");
        return ResponseEntity.ok(resumeService.getAllResumes());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a resume by ID",
            description = "Deletes a specific resume based on the provided ID"
    )
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        log.warn("Deleting resume with ID: {}", id);
        resumeService.deleteResume(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(
            summary = "Delete all resumes",
            description = "Deletes all resumes in the system"
    )
    public ResponseEntity<Void> deleteAllResumes() {
        log.warn("Deleting all resumes");
        resumeService.deleteAllResumes();
        return ResponseEntity.noContent().build();
    }
}
