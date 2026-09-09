package tarot.infrastructure.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tarot.domain.enums.Gender;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        if (attribute == null) {
            return null;
        }
        return switch (attribute) {
            case MALE -> "Male";
            case FEMALE -> "Female";
            case OTHER -> "Other";
            default -> null;
        };
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Gender.UNKNOWN;
        }
        return Gender.fromString(dbData);
    }
}
