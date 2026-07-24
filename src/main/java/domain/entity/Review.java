package domain.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reviews")
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String reviewId;

    @NotBlank(message = "Выставление рейтинга обязательно")
    private String rating;

    @Size(min = 0, max = 1000, message = "Отзыв не может быть длиннее 1000 символов")
    private String reviewContent;

    @ManyToOne
    @JoinColumn(name = "product_product_id")
    private Product product;
}
