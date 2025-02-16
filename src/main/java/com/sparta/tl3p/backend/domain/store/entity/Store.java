package com.sparta.tl3p.backend.domain.store.entity;

import com.sparta.tl3p.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.sparta.tl3p.backend.domain.store.enums.StoreStatus;
import com.sparta.tl3p.backend.domain.store.enums.CategoryType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "store_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", length = 20, nullable = false, unique = true)
    private String name;

    @Column(name = "content", length = 200)
    private String content;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StoreStatus status = StoreStatus.CREATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member owner;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StoreCategory> storeCategories = new HashSet<>();

    @Builder
    public Store(String name, String content, String address, Member owner, Long createdBy) {
        this.name = name;
        this.content = content;
        this.address = address;
        this.owner = owner;
        this.createdBy = createdBy;
    }

    public void addCategory(StoreCategory category) {
        this.storeCategories.add(category);
    }

    public void removeCategory(StoreCategory category) {
        this.storeCategories.remove(category);
    }
}
