package com.example.ResumeGeneratorApplication.auth;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If authentication exists, return username; else return a default SYSTEM user
        return Optional.ofNullable(authentication != null ? authentication.getName() : "SYSTEM");
    }
}
