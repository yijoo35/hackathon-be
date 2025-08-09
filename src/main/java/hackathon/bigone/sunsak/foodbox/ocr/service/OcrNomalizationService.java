package hackathon.bigone.sunsak.foodbox.ocr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrNomalizationService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String KEY_PREFIX = "keyword:";
    private static final String TYPE_BREED = "품종명:";
    private static final String TYPE_ITEM  = "대표식품명:";

    /**
     * 여러 noun을 한 번에 정규화.
     * 반환: 원문(noun) -> 최종 정규화명(없으면 원문)
     * 규칙: 품종명 > 대표식품명 > 원문
     */
    public Map<String, String> bulkNormalizeFromOcr(List<String> nouns) {
        if (nouns == null || nouns.isEmpty()) return Collections.emptyMap();

        // 중복 제거 + 정리
        List<String> distinct = nouns.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        if (distinct.isEmpty()) return Collections.emptyMap();

        // Redis multiGet
        List<String> keys = distinct.stream().map(n -> KEY_PREFIX + n).toList();
        List<String> values = stringRedisTemplate.opsForValue().multiGet(keys);

        // noun -> redisValue 매핑 캐시
        Map<String, String> valueByNoun = new HashMap<>();
        for (int i = 0; i < distinct.size(); i++) {
            valueByNoun.put(distinct.get(i), (values == null ? null : values.get(i)));
        }

        // 품종/대표식품 플래그 세트
        Set<String> breedSet = new HashSet<>(); //품종
        Set<String> itemSet  = new HashSet<>(); //대표 식품명
        for (String noun : distinct) {
            String v = valueByNoun.get(noun);
            if (v == null) continue;
            if (v.startsWith(TYPE_BREED)) breedSet.add(noun);
            else if (v.startsWith(TYPE_ITEM)) itemSet.add(noun);
        }

        // 최종 결과: 우선순위 적용, 없으면 자기 자신
        Map<String, String> result = new LinkedHashMap<>();
        for (String noun : distinct) {
            String v = valueByNoun.get(noun);
            if (v == null) continue; // 매핑 안 된 경우 → 저장 안 함

            if (v.startsWith(TYPE_BREED)) {
                result.put(noun, v.substring(TYPE_BREED.length())); // 품종명 값만
            } else if (v.startsWith(TYPE_ITEM)) {
                result.put(noun, v.substring(TYPE_ITEM.length())); // 대표식품명 값만
            }
        }
        log.debug("bulkNormalizeFromOcr result: {}", result);
        return result;
    }
}
