package domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchProductRequest {

    private String name;

    @Size(max = 1000)
    private String description;

    @DecimalMin(value = "0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal currentPrice;

    @Min(0) @Max(100)
    private Integer discountPercent;

    @Min(0)
    private Integer quantityAvailable;

    @DecimalMin(value = "0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal costPrice;

    private String supplier;
    private String image;
}