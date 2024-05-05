package project.restaurantmanagement.dto;

import lombok.*;
import project.restaurantmanagement.entity.ReviewEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {

    private Long id;
    private Long customerId;
    private String title;
    private String comments;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ReviewDto from(ReviewEntity review) {
        return ReviewDto.builder()
                .id(review.getId())
                .customerId(review.getCustomerEntity().getId())
                .title(review.getTitle())
                .comments(review.getComment())
                .createdAt(review.getCreatedAt())
                .modifiedAt(review.getModifiedAt())
                .build();
    }
}
