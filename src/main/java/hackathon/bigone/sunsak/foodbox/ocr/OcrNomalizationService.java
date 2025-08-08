package hackathon.bigone.sunsak.foodbox.ocr;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

//품목명, 품종명으로 바꾸는 부분
@Service
@RequiredArgsConstructor
public class OcrNomalizationService {
    private final StringRedisTemplate stringRedisTemplate;

    public String normalizeFromOcr(String ocrText, List<String> nouns){

        //품종명
        for(String noun: nouns){
            String value = stringRedisTemplate.opsForValue().get("keyword:"+noun);
            if(value!=null&&value.startsWith("품종명:")){
                System.out.println("품종명 매핑:"+noun);
                return noun;
            }
        }

        for(String noun: nouns){
            String value = stringRedisTemplate.opsForValue().get("keyword:"+noun);
            if(value!=null&&value.startsWith("대표식품명:")){
                System.out.println("대표식품명 매핑:"+noun);
                return noun;
            }
        }

        System.out.println("매핑 실패");
        return null;
    }
}
