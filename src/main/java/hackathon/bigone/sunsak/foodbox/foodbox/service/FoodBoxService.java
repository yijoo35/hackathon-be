package hackathon.bigone.sunsak.foodbox.foodbox.service;

import hackathon.bigone.sunsak.foodbox.foodbox.dto.FoodBoxResponse;
import hackathon.bigone.sunsak.foodbox.foodbox.dto.FoodItemRequest;
import hackathon.bigone.sunsak.foodbox.foodbox.entity.FoodBox;
import hackathon.bigone.sunsak.foodbox.foodbox.repository.FoodBoxRepository;
import hackathon.bigone.sunsak.foodbox.nlp.service.NlpService;
import hackathon.bigone.sunsak.foodbox.ocr.dto.OcrExtractedItem;
import hackathon.bigone.sunsak.foodbox.ocr.service.OcrNomalizationService;
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
    private final NlpService nlpService;                       // 🔹 Komoran 분석
    private final OcrNomalizationService normalizationService; // 🔹 자유명사 Redis keyword 매핑

    private static final String EXPIRY_PREFIX = "expiry:";
    private static final int DEFAULT_EXPIRY_DAYS = 0; // 0/없음 → null(빈칸)

    /**
     * OCR 기반 저장
     * - user_dict 그룹: 그대로 저장
     * - 자유명사 그룹: Redis keyword 매핑 성공 시 저장
     * - 두 그룹 모두 Redis expiry 매핑
     */
    @Transactional
    public List<FoodBoxResponse> saveFromOcr(Long userId, List<OcrExtractedItem> ocrItems) {
        if (userId == null) throw new IllegalArgumentException("userId가 없습니다.");
        if (ocrItems == null || ocrItems.isEmpty()) return getFoodsByUser(userId);

        // 1) Komoran 분석 + user_dict / freeNoun 그룹 분류
        NlpService.ClassifiedTokens classified = nlpService.classifyByUserDict(ocrItems);
        Map<String, Integer> userDictGroup = classified.getUserDict();
        Map<String, Integer> freeNounGroup = classified.getFreeNouns();

        // 2) 자유명사 → Redis keyword 매핑
        Map<String, String> mappedFree = normalizationService.normalizeFreeNouns(
                new ArrayList<>(freeNounGroup.keySet())
        );

        // 3) 최종 표준명 → 수량 합산
        Map<String, Integer> finalCount = new LinkedHashMap<>();
        // user_dict 그룹
        for (var e : userDictGroup.entrySet()) {
            finalCount.merge(e.getKey(), e.getValue(), Integer::sum);
        }
        // 매핑된 자유명사
        for (var e : freeNounGroup.entrySet()) {
            String std = mappedFree.get(e.getKey());
            if (std == null) continue; // 매핑 실패 시 버림
            finalCount.merge(std, e.getValue(), Integer::sum);
        }

        if (finalCount.isEmpty()) return getFoodsByUser(userId);

        // 4) Redis expiry 조회
        List<String> names = new ArrayList<>(finalCount.keySet());
        List<String> expiryKeys = names.stream().map(n -> EXPIRY_PREFIX + n).toList();
        List<String> expiryVals = redisTemplate.opsForValue().multiGet(expiryKeys);

        Map<String, Integer> daysByName = new HashMap<>();
        for (int i = 0; i < names.size(); i++) {
            String raw = (expiryVals != null && i < expiryVals.size()) ? expiryVals.get(i) : null;
            daysByName.put(names.get(i), parseExpiryDays(raw));
        }

        // 5) 저장 (OCR은 name+expiry 기준 병합)
        LocalDate today = LocalDate.now();
        record Key(String name, LocalDate expiry) {}
        Map<Key, Integer> merged = new LinkedHashMap<>();

        for (var e : finalCount.entrySet()) {
            String name = e.getKey();
            int qty = e.getValue();
            int days = daysByName.getOrDefault(name, DEFAULT_EXPIRY_DAYS);
            LocalDate expiry = (days <= 0) ? null : today.plusDays(days);

            merged.merge(new Key(name, expiry), qty, Integer::sum);
        }

        for (var e : merged.entrySet()) {
            var k = e.getKey();
            int qty = e.getValue();

            var existing = foodBoxRepository.findByUserIdAndNameAndExpiryDate(userId, k.name(), k.expiry());
            if (existing.isPresent()) {
                existing.get().setQuantity(existing.get().getQuantity() + qty);
            } else {
                foodBoxRepository.save(FoodBox.builder()
                        .userId(userId)
                        .name(k.name())
                        .quantity(qty)
                        .expiryDate(k.expiry())
                        .build());
            }
        }

        return getFoodsByUser(userId);
    }

    private int parseExpiryDays(String s) {
        if (s == null || s.isBlank()) return DEFAULT_EXPIRY_DAYS;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return DEFAULT_EXPIRY_DAYS; }
    }

    // 기존 수동 저장 로직
    @Transactional
    public List<FoodBoxResponse> saveFoods(Long userId, List<FoodItemRequest> items) {
        if (userId == null) throw new IllegalArgumentException("userId가 없습니다.");
        if (items == null || items.isEmpty()) return getFoodsByUser(userId);

        for (FoodItemRequest it : items) {
            if (it == null || it.getName() == null || it.getName().isBlank()) continue;
            if (it.getExpiryDate() == null)
                throw new IllegalArgumentException("유통기한 기간을 입력해주세요.");

            foodBoxRepository.save(FoodBox.builder()
                    .userId(userId)
                    .name(it.getName().trim())
                    .quantity(Math.max(1, it.getQuantity()))
                    .expiryDate(it.getExpiryDate())
                    .build());
        }

        return getFoodsByUser(userId);
    }

    // 로그인한 유저의 foodbox
    public List<FoodBoxResponse> getFoodsByUser(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId가 없습니다.");
        return foodBoxRepository.findAllSortedByUserId(userId).stream()
                .map(f -> FoodBoxResponse.builder()
                        .foodId(f.getId())
                        .name(f.getName())
                        .quantity(f.getQuantity())
                        .expiryDate(f.getExpiryDate())
                        .build())
                .toList();
    }
}
