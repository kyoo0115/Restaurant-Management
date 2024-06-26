package project.restaurantmanagement.dto;

import lombok.*;
import project.restaurantmanagement.entity.ReviewEntity;
import project.restaurantmanagement.model.type.Rating;

import java.util.List;

/**
 * 리뷰 정보 DTO
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private String title;
    private String comments;
    private Rating rating;

    public static ReviewDto from(ReviewEntity reviewEntity) {
        return ReviewDto.builder()
                .title(reviewEntity.getTitle())
                .comments(reviewEntity.getComment())
                .rating(Rating.fromValue(reviewEntity.getRating()))
                .build();
    }

    public static List<ReviewDto> from(List<ReviewEntity> reviewEntities) {
        return reviewEntities.stream().map(ReviewDto::from).toList();
    }
}
