package hackathon.bigone.sunsak.foodbox.foodbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FoodItemResponse {
    private String name;
    private int quantity;
}
