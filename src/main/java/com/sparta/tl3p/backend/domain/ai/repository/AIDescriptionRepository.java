package com.sparta.tl3p.backend.domain.ai.repository;

import com.sparta.tl3p.backend.domain.ai.entity.AIDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AIDescriptionRepository extends JpaRepository<AIDescription, UUID> {
}