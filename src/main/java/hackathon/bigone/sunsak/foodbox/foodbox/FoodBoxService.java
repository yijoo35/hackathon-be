package hackathon.bigone.sunsak.foodbox.foodbox;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodBoxService {
    private final StringRedisTemplate redisTemplate;
    private final FoodBoxRepository foodBoxRepository;

    private static final int DEFAULT_EXPIRY_DAYS = 0;

    List<FoodBoxResponse> saved = new ArrayList<>();

    public List<FoodBoxResponse> saveSelectedFoods(List<FoodItemResponse> items) {
        for (FoodItemResponse item : items) {
            String name = item.getName();
            int quantity = item.getQuantity();

            String expiryStr = redisTemplate.opsForValue().get("expiry:" + name);
            int expiryDays = parseExpiry(expiryStr);
            LocalDate expiryDate = LocalDate.now().plusDays(expiryDays);

            FoodBox food = FoodBox.builder()
                    .name(name)
                    .quantity(quantity)
                    .expiryDate(expiryDate)
                    .build();

            saved.add(FoodBoxResponse.builder()
                    .name(food.getName())
                    .quantity(food.getQuantity())
                    .expiryDate(food.getExpiryDate())
                    .build());

            foodBoxRepository.save(food);
        }
        return saved;
    }

    private int parseExpiry(String expiryStr) {
        try{
            return expiryStr != null ? Integer.parseInt(expiryStr) : DEFAULT_EXPIRY_DAYS;
        }
        catch (NumberFormatException e){
            return DEFAULT_EXPIRY_DAYS;
        }
    }

}
