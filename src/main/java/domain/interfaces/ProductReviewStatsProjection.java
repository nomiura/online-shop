package domain.interfaces;

public interface ProductReviewStatsProjection {
    Long getProductId();
    Long getCount();
    Double getAverageRating();
}
