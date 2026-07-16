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
public class UpdateProductRequest {

    @NotBlank(message = "Название товара не может быть пустым")
    private String name;

    @Size(max = 1000, message = "Описание не длиннее 1000 символов")
    private String description;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.00", message = "Цена не может быть отрицательной")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal currentPrice;

    @NotNull @Min(0) @Max(100)
    private Integer discountPercent;

    @NotNull(message = "Количество обязательно")
    @Min(value = 0)
    private Integer quantityAvailable;

    @DecimalMin(value = "0.00", message = "Закупочная цена не может быть отрицательной")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal costPrice;

    private String supplier;
    private String image;
}