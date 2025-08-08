package hackathon.bigone.sunsak.foodbox.foodbox;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class FoodBoxResponse {
    private String name;
    private int quantity;
    private LocalDate expiryDate;
}
