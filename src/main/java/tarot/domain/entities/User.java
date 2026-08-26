package tarot.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.UserRole;
import tarot.domain.enums.ZodiacSign;

@Entity
@Table(name = "users")
@SoftDelete // 🔥 Hibernate 6: Tự động đổi DELETE thành UPDATE is_deleted=true và lọc tự động
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "zodiac_sign", nullable = false, length = 30)
    @Builder.Default
    private ZodiacSign zodiacSign = ZodiacSign.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}