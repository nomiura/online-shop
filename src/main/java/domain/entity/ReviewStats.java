package domain.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class ReviewStats implements Serializable {
    private long count;
    private Double averageRating;

    public static ReviewStats empty() {
        return new ReviewStats(0L, null);
    }
}
