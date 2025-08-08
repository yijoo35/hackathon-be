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

    public List<FoodBoxResponse> saveSelectedFoods(Long userId, List<FoodItemResponse> items) {
        List<FoodBoxResponse> saved = new ArrayList<>();

        for (FoodItemResponse item : items) {
            String name = item.getName();
            int quantity = item.getQuantity();

            // Redis에서 유통기한 정보 가져오기
            String expiryStr = redisTemplate.opsForValue().get("expiry:" + name);
            int expiryDays = parseExpiry(expiryStr);
            LocalDate expiryDate = LocalDate.now().plusDays(expiryDays);

            // 엔티티 저장
            FoodBox food = foodBoxRepository.save(
                    FoodBox.builder()
                            .userId(userId) // 👈 사용자 식별
                            .name(name)
                            .quantity(quantity)
                            .expiryDate(expiryDate)
                            .build()
            );

            // 저장된 엔티티의 PK 포함한 응답 생성
            saved.add(FoodBoxResponse.builder()
                    .foodId(food.getId()) // 👈 PK
                    .name(food.getName())
                    .quantity(food.getQuantity())
                    .expiryDate(food.getExpiryDate())
                    .build());
        }

        return saved;
    }

    private int parseExpiry(String expiryStr) {
        try {
            return expiryStr != null ? Integer.parseInt(expiryStr) : DEFAULT_EXPIRY_DAYS;
        } catch (NumberFormatException e) {
            return DEFAULT_EXPIRY_DAYS;
        }
    }
}
