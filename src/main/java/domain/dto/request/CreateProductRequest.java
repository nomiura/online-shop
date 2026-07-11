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
public class CreateProductRequest {

    @NotBlank(message = "Название товара не может быть пустым")
    private String name;

    @Size(max = 1000, message = "Описание не длиннее 1000 символов")
    private String description;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.00", message = "Цена не может быть отрицательной")
    @Digits(integer = 10, fraction = 2, message = "Цена: до 10 цифр до запятой и 2 после")
    private BigDecimal currentPrice;

    @NotNull(message = "Процент скидки обязателен")
    @Min(value = 0, message = "Скидка не может быть меньше 0")
    @Max(value = 100, message = "Скидка не может быть больше 100")
    private Integer discountPercent;

    private String image;
}