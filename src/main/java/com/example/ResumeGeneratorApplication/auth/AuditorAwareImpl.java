package com.example.ResumeGeneratorApplication.auth;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional getCurrentAuditor() {
//        return Optional.of("Deepak");
        return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
