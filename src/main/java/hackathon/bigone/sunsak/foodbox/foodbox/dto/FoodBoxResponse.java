package hackathon.bigone.sunsak.foodbox.foodbox.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class FoodBoxResponse {
    @JsonProperty("food_id")
    private Long foodId;
    private String name;
    private int quantity;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy.M.d")
    private LocalDate expiryDate;
}
