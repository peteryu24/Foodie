package com.sparta.tl3p.backend.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.order.entity.QOrder;
import com.sparta.tl3p.backend.domain.order.entity.QOrderItem;
import com.sparta.tl3p.backend.domain.item.entity.QItem;
import com.sparta.tl3p.backend.domain.store.entity.QStore;
import jakarta.persistence.EntityManager;
import org.springframework.util.StringUtils;

import java.util.List;

public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Order> searchOrders(Long memberId, String storeName, String productName) {
        QOrder order = QOrder.order;
        QStore store = QStore.store;
        QOrderItem orderItem = QOrderItem.orderItem;
        QItem item = QItem.item;

        return queryFactory.selectFrom(order)
                .leftJoin(order.store, store).fetchJoin()
                .leftJoin(order.orderItems, orderItem).fetchJoin()
                .leftJoin(orderItem.item, item).fetchJoin()
                .where(
                        order.member.memberId.eq(memberId),
                        StringUtils.hasText(storeName) ? store.name.containsIgnoreCase(storeName) : null,
                        StringUtils.hasText(productName) ? item.name.containsIgnoreCase(productName) : null
                )
                .distinct()
                .fetch();
    }
}
