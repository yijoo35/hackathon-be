package hackathon.bigone.sunsak.foodbox.foodbox.dto.update;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodItemUpdateRequest { // 변경 1개씩
    @NotNull
    @JsonProperty("food_id")
    private Long foodId;

    @NotBlank
    private String name;
    private int quantity;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy.MM.dd")
    private LocalDate expiryDate;
}
