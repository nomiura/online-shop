package domain.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewStats {
    private long count;
    private Double averageRating;

    public static ReviewStats empty() {
        return new ReviewStats(0L, null);
    }
}
