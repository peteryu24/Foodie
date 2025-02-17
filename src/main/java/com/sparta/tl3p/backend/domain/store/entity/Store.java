package com.sparta.tl3p.backend.domain.store.entity;

import com.sparta.tl3p.backend.common.audit.BaseEntity;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.store.enums.StoreStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_store")
public class Store extends BaseEntity {

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

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StoreCategory> storeCategories = new HashSet<>();

    @Builder
    public Store(String name, String content, String address, Member owner) {
        this.name = name;
        this.content = content;
        this.address = address;
        this.owner = owner;
    }

    public void updateStore(String name, String content, String address, StoreStatus status) {
        this.name = name;
        this.content = content;
        this.address = address;
        this.status = status;
    }

    public void hideStore() {
        this.status = StoreStatus.DELETED;
    }
}
