package com.example.ResumeGeneratorApplication.controller;


import com.example.ResumeGeneratorApplication.dto.ResumeDto;
import com.example.ResumeGeneratorApplication.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    @PostMapping
    public ResponseEntity<ResumeDto> createResume(@RequestBody ResumeDto resumeDto){
        ResumeDto resume = resumeService.createResume(resumeDto);
        return new ResponseEntity<>(resume, HttpStatus.CREATED);
//        return ResponseEntity.status(HttpStatus.CREATED).body(resumeService.createResume(resumeDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResumeDto> updateResume(@PathVariable Long id, @RequestBody ResumeDto resumeDto){
        ResumeDto resume = resumeService.updateResume(id, resumeDto);
        return new ResponseEntity<>(resume, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeDto> getResume(@PathVariable Long id) {
        return ResponseEntity.ok(resumeService.getResume(id));
    }

    @GetMapping
    public ResponseEntity<List<ResumeDto>> getAllResumes() {
        return ResponseEntity.ok(resumeService.getAllResumes());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllResumes() {
        resumeService.deleteAllResumes();
        return ResponseEntity.noContent().build();
    }
}
