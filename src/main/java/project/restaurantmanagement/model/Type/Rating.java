package project.restaurantmanagement.model.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰 평점(1~5)
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Rating {
    STAR_1(1.0),
    STAR_2(2.0),
    STAR_3(3.0),
    STAR_4(4.0),
    STAR_5(5.0);

    private Double rateValue;

    public static Rating fromValue(double value) {
        for (Rating rating : Rating.values()) {
            if (rating.getRateValue() == value) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Unknown rating value: " + value);
    }
}