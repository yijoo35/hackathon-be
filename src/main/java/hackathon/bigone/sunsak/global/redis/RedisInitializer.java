package hackathon.bigone.sunsak.global.redis;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
@AllArgsConstructor
public class RedisInitializer {
    private final RedisFoodSaver redisFoodSaver;

    @PostConstruct
    public void init() {
        try {
            if (redisFoodSaver.isSaved()) {
                System.out.println("Redis에 데이터 존재합니다.");
                return;
            }

            // JAR 내부 리소스를 임시 파일로 복사
            Resource resource = new ClassPathResource("data/food_data.csv");
            Path tempFile = Files.createTempFile("food_data", ".csv");
            try (InputStream is = resource.getInputStream()) {
                Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            tempFile.toFile().deleteOnExit();

            // 복사된 임시 파일 경로로 Redis 저장
            redisFoodSaver.saveWithReset(tempFile.toAbsolutePath().toString());
            System.out.println("Redis 초기화 & 저장 완료");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
