package hackathon.bigone.sunsak.global.redis;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RedisInitializer {
    private final RedisFoodSaver redisFoodSaver;

    @PostConstruct
    public void init(){ //서버 시작 시 한번 실행
        try {
            if(redisFoodSaver.isSaved()){
                System.out.println("Redis에 데이터 존재합니다.");
                return;
            }
            String filePath = new ClassPathResource("data/food_data.csv")
                    .getFile()
                    .getAbsolutePath();
            //redisFoodSaver.saveToRedis(filePath);
            //데이터 수정/삭제할 때 Reset 실행
            redisFoodSaver.saveWithReset(filePath);
            System.out.println("Redis 초기화 & 저장 완료");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
