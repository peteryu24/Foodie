package com.sparta.tl3p.backend.common.config;

import com.sparta.tl3p.backend.common.audit.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
