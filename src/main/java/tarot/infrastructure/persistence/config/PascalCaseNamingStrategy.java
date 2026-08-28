package tarot.infrastructure.persistence.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.io.Serializable;

/**
 * Strategy định dạng tên bảng và cột theo chuẩn PascalCase (viết hoa chữ cái đầu):
 * - Tự động quote để PostgreSQL bảo toàn chính xác chữ hoa ("CreatedAt", "UserId", "Cards")
 */
public class PascalCaseNamingStrategy implements PhysicalNamingStrategy, Serializable {

    @Override
    public Identifier toPhysicalCatalogName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return logicalName;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return logicalName;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return formatToPascal(logicalName);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return formatToPascal(logicalName);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return formatToPascal(logicalName);
    }

    private Identifier formatToPascal(Identifier name) {
        if (name == null || name.getText() == null || name.getText().isBlank()) {
            return name;
        }
        String text = name.getText();

        // Xử lý các chuỗi có snake_case hoặc camelCase sang PascalCase
        StringBuilder result = new StringBuilder();
        boolean nextUpper = true;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    result.append(c);
                }
            }
        }

        String pascal = result.toString();
        if (!pascal.isEmpty() && Character.isLowerCase(pascal.charAt(0))) {
            pascal = Character.toUpperCase(pascal.charAt(0)) + pascal.substring(1);
        }

        // Trong PostgreSQL, để giữ nguyên chữ hoa dạng PascalCase bắt buộc phải Quoted (isQuoted = true)
        return Identifier.toIdentifier(pascal, true);
    }
}
