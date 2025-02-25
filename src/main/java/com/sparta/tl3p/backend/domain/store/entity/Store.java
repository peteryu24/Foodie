package com.sparta.tl3p.backend.domain.store.entity;

import com.sparta.tl3p.backend.common.audit.BaseEntity;
import com.sparta.tl3p.backend.common.type.Address;
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
    private UUID storeId;

    @Column(name = "name", length = 20, unique = true, nullable = false)
    private String name;

    @Column(name = "content", length = 200)
    private String content;

    @Embedded
    @Column(name = "address", nullable = false)
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StoreStatus status = StoreStatus.CREATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StoreCategory> storeCategories = new HashSet<>();

    @Builder
    public Store(String name, String content, Address address, Member member) {
        this.name = name;
        this.content = content;
        this.address = address;
        this.member = member;
    }

    public void updateStore(String name, String content, Address address, StoreStatus status) {
        this.name = name;
        this.content = content;
        this.address = address;
        this.status = status;
    }

    public void hideStore() {
        this.status = StoreStatus.DELETED;
    }
}
