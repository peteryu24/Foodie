package com.sparta.tl3p.backend.domain.ai.entity;

import com.sparta.tl3p.backend.common.audit.BaseEntity;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_ai_description")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AIDescription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ai_description_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "response", columnDefinition = "TEXT")
    private String response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // TODO: 연관관계 재설정

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "member_id", nullable = false)
    //    private Member member;

    //    @Builder
    //    public AIDescription(String prompt, String response, Item item, Member member) {
    //        this.prompt = prompt;
    //        this.response = response;
    //        this.item = item;
    //        this.member = member;
    //    }

    @Builder
    public AIDescription(String prompt, String response, Item item) {
        this.prompt = prompt;
        this.response = response;
        this.item = item;
    }

    public void updateDescription(String prompt, String response) {
        this.prompt = prompt;
        this.response = response;
    }
}