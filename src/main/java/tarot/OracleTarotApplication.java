package tarot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "securityAuditorAware") // 🔥 Tự động nạp "system" / User ID vào @CreatedBy
public class OracleTarotApplication {
    public static void main(String[] args) {
        SpringApplication.run(OracleTarotApplication.class, args);
    }
}