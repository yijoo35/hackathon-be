package hackathon.bigone.sunsak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //date 자동생성을 위한 어노테이션
public class SunsakApplication {

	public static void main(String[] args) {
		SpringApplication.run(SunsakApplication.class, args);
	}

}
