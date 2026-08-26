package tarot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // 🔥 Kích hoạt tự động điền @CreatedDate, @LastModifiedDate
public class OracleTarotApplication {
    public static void main(String[] args) {
        SpringApplication.run(OracleTarotApplication.class, args);
    }
}