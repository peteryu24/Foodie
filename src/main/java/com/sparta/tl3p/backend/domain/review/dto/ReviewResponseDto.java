package com.sparta.tl3p.backend.domain.review.dto;

import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.order.entity.OrderItem;
import com.sparta.tl3p.backend.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {
    private String nickname;
    private String content;
    private double score;
//    private List<ItemDto> items = new ArrayList<>();

    public ReviewResponseDto(Review review) {
        this.nickname = review.getOrder().getMember().getNickname();
        this.content = review.getContent();
        this.score = review.getScore();
//        this.items = extractItemsFromOrder(review);
    }

    // todo: ItemDto import
//    private List<ItemDto> extractItemsFromOrder(Review review) {
//        List<ItemDto> itemDtoList = new ArrayList<>();
//        for (OrderItem orderItem : review.getOrder().getOrderItems()) {
//            itemDtoList.add(new ItemDto(orderItem.getItem()));
//        }
//        return itemDtoList;
//    }
}
