package com.sparta.tl3p.backend.domain.order.repository;

import com.sparta.tl3p.backend.domain.order.entity.Order;
import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> searchOrders(Long memberId, String storeName, String productName);
}
