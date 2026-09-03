package tarot.infrastructure.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tarot.domain.enums.UserRole;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        if (attribute == null) {
            return "User";
        }
        return attribute == UserRole.ADMIN ? "Admin" : "User";
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return UserRole.USER;
        }
        return "ADMIN".equalsIgnoreCase(dbData.trim()) ? UserRole.ADMIN : UserRole.USER;
    }
}
