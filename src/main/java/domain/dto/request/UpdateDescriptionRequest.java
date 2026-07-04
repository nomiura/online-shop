package domain.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateDescriptionRequest {
    @NotBlank(message = "Комментарий не должен быть пустым.")
    @Size(max = 500)
    private String description;

}
