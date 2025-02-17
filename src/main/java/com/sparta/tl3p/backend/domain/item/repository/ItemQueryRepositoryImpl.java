package com.sparta.tl3p.backend.domain.item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.tl3p.backend.domain.item.dto.ItemSearchRequestDto;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.entity.QItem;
import com.sparta.tl3p.backend.domain.item.enums.ItemSortOption;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class ItemQueryRepositoryImpl implements ItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    QItem item = QItem.item;

    @Override
    public Page<Item> findAllWithStore(ItemSearchRequestDto request) {

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.getItemName())) {
            builder.and(item.name.containsIgnoreCase(request.getItemName()));
        }

        //        if (request.getStoreId() != null) {
        //            builder.and(item.store.id.eq(request.getStoreId()));
        //        }

        if (request.getMinPrice() != null) {
            builder.and(item.price.goe(request.getMinPrice()));
        }

        if (request.getMaxPrice() != null) {
            builder.and(item.price.loe(request.getMaxPrice()));
        }

        List<Item> data = queryFactory.selectFrom(item)
                //                .leftJoin(item.store).fetchJoin()
                .where(builder)
                .offset((long) request.getPage() * request.getValidatedSize())
                .limit(request.getValidatedSize())
                .orderBy(getOrderSpecifier(request.getSortOption()))
                .fetch();

        Long total = queryFactory.select(item.count())
                .from(item)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(
                data,
                PageRequest.of(request.getPage(), request.getValidatedSize()),
                total != null ? total : 0L
        );
    }

    private OrderSpecifier<?> getOrderSpecifier(ItemSortOption sortOption) {
        switch (sortOption) {
            case CREATED_AT_ASC:
                return item.createdAt.asc();
            case CREATED_AT_DESC:
                return item.createdAt.desc();
            case UPDATED_AT_ASC:
                return item.updatedAt.asc();
            case UPDATED_AT_DESC:
                return item.updatedAt.desc();
        }
        return item.createdAt.desc();
    }
}
