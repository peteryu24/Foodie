package com.sparta.tl3p.backend.common.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("Authentication is null");
            return Optional.empty();
        }

        if (!authentication.isAuthenticated()) {
            log.debug("Authentication is not authenticated");
            return Optional.empty();
        }

        if (authentication.getPrincipal().equals("anonymousUser")) {
            log.debug("Anonymous user");
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            try {
                return Optional.of(Long.valueOf(userDetails.getUsername()));
            } catch (NumberFormatException e) {
                log.error("Failed to parse user: {}", userDetails.getUsername(), e);
                return Optional.empty();
            }
        }

        log.error("Authentication is not UserDetails : {}",
                authentication.getPrincipal().getClass().getName());

        return Optional.empty();
    }
}
