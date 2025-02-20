package com.sparta.tl3p.backend.domain.order.repository;

import com.sparta.tl3p.backend.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, OrderRepositoryCustom {
    // 회원별 주문 조회: Order 엔티티의 member.memberId를 기준으로 검색
    List<Order> findByMemberMemberId(Long memberId);

    // 가게별 주문 조회: Order 엔티티의 store.storeId를 기준으로 검색
    List<Order> findByStoreStoreId(UUID storeId);
}
