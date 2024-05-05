package project.restaurantmanagement.dto;

import lombok.*;
import project.restaurantmanagement.model.Type.Rating;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private String title;
    private String comments;
    private Rating rating;

}
