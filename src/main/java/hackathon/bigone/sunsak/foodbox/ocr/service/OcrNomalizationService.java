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

    private final StringRedisTemplate redis;

    private static final String KEY_PREFIX = "keyword:";
    private static final String TYPE_BREED = "품종명:";
    private static final String TYPE_ITEM  = "대표식품명:";

    public Map<String, String> normalizeFreeNouns(Collection<String> nouns) {
        if (nouns == null || nouns.isEmpty()) return Collections.emptyMap();

        List<String> distinct = nouns.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        List<String> keys = distinct.stream().map(n -> KEY_PREFIX + n).toList();
        List<String> vals = redis.opsForValue().multiGet(keys);

        Map<String, String> out = new LinkedHashMap<>();
        for (int i = 0; i < distinct.size(); i++) {
            String noun = distinct.get(i);
            String v = (vals != null && i < vals.size()) ? vals.get(i) : null;
            if (v == null) continue; // 매핑 실패 → 버림

            if (v.startsWith(TYPE_BREED)) out.put(noun, v.substring(TYPE_BREED.length()));
            else if (v.startsWith(TYPE_ITEM)) out.put(noun, v.substring(TYPE_ITEM.length()));
        }
        return out;
    }
}
