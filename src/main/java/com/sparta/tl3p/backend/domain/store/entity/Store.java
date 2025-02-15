package com.sparta.tl3p.backend.domain.store.entity;

import com.sparta.tl3p.backend.common.audit.BaseEntity;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_store")
public class Store extends BaseEntity {
    @Id
    @Column(name = "store_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "content", length = 200)
    private String content;

    @Column(name = "address", nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StoreStatus status = StoreStatus.CREATED;

    @Builder
    public Store(String name, Member member, String content, String address) {
        this.name = name;
        this.member = member;
        this.content = content;
        this.address = address;
    }

    public void update(String name, String content, String address) {
        this.name = name;
        this.content = content;
        this.address = address;
        this.status = StoreStatus.CREATED;
    }

    @Override
    public void softDelete(Long deleteUserId) {
        super.softDelete(deleteUserId);
        this.status = StoreStatus.DELETED;
    }
}
