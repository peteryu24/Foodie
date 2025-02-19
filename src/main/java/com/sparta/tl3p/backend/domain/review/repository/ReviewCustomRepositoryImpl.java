package com.sparta.tl3p.backend.domain.review.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.tl3p.backend.domain.item.entity.QItem;
import com.sparta.tl3p.backend.domain.member.entity.QMember;
import com.sparta.tl3p.backend.domain.order.entity.QOrder;
import com.sparta.tl3p.backend.domain.order.entity.QOrderItem;
import com.sparta.tl3p.backend.domain.review.entity.QReview;
import com.sparta.tl3p.backend.domain.review.entity.Review;
import com.sparta.tl3p.backend.domain.review.entity.ReviewStatus;
import com.sparta.tl3p.backend.domain.store.entity.QStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Review> searchReviews(UUID storeId, String query) {
        QReview qReview = QReview.review;
        QStore qStore = QStore.store;
        QOrder qOrder = QOrder.order;
        QOrderItem qOrderItem = QOrderItem.orderItem;
        QItem qItem = QItem.item;

        return queryFactory
                .selectDistinct(qReview)
                .from(qReview)
                .join(qReview.order, qOrder).fetchJoin()
                .join(qOrder.orderItems, qOrderItem).fetchJoin()
                .join(qOrderItem.item, qItem)
                .join(qReview.store, qStore)
                .where(
                        qReview.status.notIn(ReviewStatus.DELETED),
                        storeId != null ? qStore.storeId.eq(storeId) : null,
                        query != null ? qItem.name.contains(query) : null
                )
                .orderBy(qReview.createdAt.asc())
                .fetch();
    }

    @Override
    public List<Review> searchOwnerReviews(UUID storeId, Long memberId) {
        QReview qReview = QReview.review;
        QStore qStore = QStore.store;
        QOrder qOrder = QOrder.order;
        QOrderItem qOrderItem = QOrderItem.orderItem;
        QItem qItem = QItem.item;
        QMember qMember = QMember.member;

        return queryFactory
                .selectDistinct(qReview)
                .from(qReview)
                .join(qReview.order, qOrder).fetchJoin()  // Review -> Order
                .join(qOrder.orderItems, qOrderItem).fetchJoin()  // Order -> OrderItems
                .join(qOrderItem.item, qItem)  // OrderItem -> Item
                .join(qOrder.member, qMember)  // Order -> Member
                .join(qReview.store, qStore)  // Review -> Store
                .where(
                        qReview.status.notIn(ReviewStatus.DELETED),
                        storeId != null ? qStore.storeId.eq(storeId) : null,
                        qMember.memberId.eq(memberId)
                )
                .orderBy(qReview.createdAt.asc())
                .fetch();

    }

}
