package com.example.ResumeGeneratorApplication.controller;


import com.example.ResumeGeneratorApplication.entity.Resume;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @GetMapping("/resume/{resumeId}")
    List<Resume> getResumeRevision(@PathVariable Long resumeId){
        AuditReader reader = AuditReaderFactory.get(entityManagerFactory.createEntityManager());
        List<Number> revisions = reader.getRevisions(Resume.class,resumeId);
        return revisions.stream().map(revisionNumber -> reader.find(Resume.class,resumeId,revisionNumber))
                .collect(Collectors.toList());

    }
}
