package hackathon.bigone.sunsak.foodbox.foodbox.dto.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import hackathon.bigone.sunsak.foodbox.foodbox.dto.FoodBoxResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodItemUpdateResult {
    @JsonProperty("food_id")
    private Long foodId;
    private FoodBoxResponse before; // 수정 전
    private FoodBoxResponse after; // 수정 후
}
