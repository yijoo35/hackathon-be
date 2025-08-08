package hackathon.bigone.sunsak.foodbox.foodbox;

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
    private LocalDate expiryDate;
}
