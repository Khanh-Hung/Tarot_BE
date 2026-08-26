package tarot.infrastructure.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Tương đương ICurrentUserProvider & NormalizeUserId() bên .NET:
 * - Tự động điền "system" hoặc Email của User đang đăng nhập vào @CreatedBy và @LastModifiedBy.
 */
@Component("securityAuditorAware")
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Tạm thời mặc định là "system". 
        // Sau này khi tích hợp JWT / Spring Security, ta sẽ lấy User Email từ SecurityContextHolder tại đây!
        return Optional.of("system");
    }
}