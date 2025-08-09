package hackathon.bigone.sunsak.foodbox.foodbox.dto.update;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItemBatchUpdateRequest { //여러개 update
    private List<FoodItemUpdateRequest> items;
}
