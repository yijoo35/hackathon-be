package hackathon.bigone.sunsak.global.redis.controller;

import hackathon.bigone.sunsak.global.redis.RedisFoodSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/redis")
@RequiredArgsConstructor
public class RedisResetController {
    private final RedisFoodSaver redisFoodSaver;

    @PostMapping("/reset")
    public ResponseEntity<String> redisReset(){
        try{
            String filePath = new ClassPathResource("data/food_data.csv")
                    .getFile()
                    .getAbsolutePath();
            redisFoodSaver.saveWithReset(filePath);
            return ResponseEntity.ok("Redis 초기화 완료");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Redis 초기화 실패: "+e.getMessage());
        }
    }
}