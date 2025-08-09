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
        List<String> names = clean.stream().map(FoodItemResponse::getName).toList();
        List<String> keys  = names.stream().map(n -> EXPIRY_PREFIX + n).toList();
        List<String> vals  = redisTemplate.opsForValue().multiGet(keys);
        LocalDate today = LocalDate.now();

        // 엔티티 생성
        List<FoodBox> toSave = new ArrayList<>(clean.size());
        for (int i = 0; i < clean.size(); i++) {
            FoodItemResponse it = clean.get(i);
            String raw = (vals == null || i >= vals.size()) ? null : vals.get(i);
            int days = parseExpiryDays(raw);
            LocalDate expiry = (days <= 0) ? null : today.plusDays(days);

            toSave.add(FoodBox.builder()
                    .userId(userId)
                    .name(it.getName())
                    .quantity(it.getQuantity())
                    .expiryDate(expiry)
                    .build());
        }

        // 저장
        List<FoodBox> saved = foodBoxRepository.saveAll(toSave);

        // 응답: 유통기한 오름차순 null은 마지막
        Comparator<LocalDate> dateCmp = Comparator.nullsLast(Comparator.naturalOrder());

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
