package hackathon.bigone.sunsak.foodbox.foodbox.service;

import hackathon.bigone.sunsak.foodbox.foodbox.dto.FoodBoxResponse;
import hackathon.bigone.sunsak.foodbox.foodbox.dto.FoodItemResponse;
import hackathon.bigone.sunsak.foodbox.foodbox.entity.FoodBox;
import hackathon.bigone.sunsak.foodbox.foodbox.repository.FoodBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FoodBoxService {
    private final StringRedisTemplate redisTemplate;
    private final FoodBoxRepository foodBoxRepository;

    private static final String EXPIRY_PREFIX = "expiry:";
    private static final int DEFAULT_EXPIRY_DAYS = 0; // 0/없음 → null(빈칸)

    @Transactional
    public List<FoodBoxResponse> saveSelectedFoods(Long userId, List<FoodItemResponse> items) {
        if (userId == null) throw new IllegalArgumentException("userId가 없습니다.");
        if (items == null || items.isEmpty()) return Collections.emptyList();

        // 입력 정리
        List<FoodItemResponse> clean = items.stream()
                .filter(Objects::nonNull)
                .filter(i -> i.getName() != null && !i.getName().isBlank())
                .map(i -> new FoodItemResponse(i.getName().trim(), Math.max(1, i.getQuantity())))
                .toList();

        if (clean.isEmpty()) return Collections.emptyList();

        // Redis에서 유통기한 일수 일괄 조회
        List<String> distinctNames = clean.stream().map(FoodItemResponse::getName).distinct().toList();
        List<String> keys = distinctNames.stream().map(n -> EXPIRY_PREFIX + n).toList();
        List<String> vals = redisTemplate.opsForValue().multiGet(keys);

        Map<String, Integer> daysByName = new HashMap<>();
        for (int i = 0; i < distinctNames.size(); i++) {
            String raw = (vals != null && i < vals.size()) ? vals.get(i) : null;
            daysByName.put(distinctNames.get(i), parseExpiryDays(raw));
        }

        LocalDate today = LocalDate.now();

        //name - exprity 기준으로 수량 합치기 (같은이름 수량 합치기)
        record Key(String name, LocalDate expiry) {}
        Map<Key, Integer> merged = new HashMap<>();
        for (FoodItemResponse it : clean) {
            String name = it.getName();
            int qty = it.getQuantity();

            int days = daysByName.getOrDefault(name, DEFAULT_EXPIRY_DAYS);
            LocalDate expiry = (days <= 0) ? null : today.plusDays(days);

            merged.merge(new Key(name, expiry), qty, Integer::sum);
        }


        for (var e : merged.entrySet()) {
            Key k = e.getKey();
            int qty = e.getValue();

            var existing = foodBoxRepository.findByUserIdAndNameAndExpiryDate(userId, k.name(), k.expiry());
            if (existing.isPresent()) {
                existing.get().setQuantity(existing.get().getQuantity() + qty); // 같은이름 ,날짜
            } else {
                foodBoxRepository.save(FoodBox.builder()
                        .userId(userId)
                        .name(k.name())
                        .quantity(qty)
                        .expiryDate(k.expiry())
                        .build());
            }
        }

        return foodBoxRepository.findAllSortedByUserId(userId).stream()
                .map(f -> FoodBoxResponse.builder()
                        .foodId(f.getId())
                        .name(f.getName())
                        .quantity(f.getQuantity())
                        .expiryDate(f.getExpiryDate())
                        .build())
                .toList();
    }

    private int parseExpiryDays(String s) {
        if (s == null || s.isBlank()) return DEFAULT_EXPIRY_DAYS;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return DEFAULT_EXPIRY_DAYS; }
    }
}
