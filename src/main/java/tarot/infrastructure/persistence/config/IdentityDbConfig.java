package tarot.infrastructure.persistence.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "tarot.infrastructure.persistence.repositories.identity",
        entityManagerFactoryRef = "identityEntityManagerFactory",
        transactionManagerRef = "identityTransactionManager"
)
public class IdentityDbConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.identity")
    public DataSourceProperties identityDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "identityDataSource")
    public DataSource identityDataSource() {
        return identityDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "identityEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean identityEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("identityDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        // Java chỉ đọc/ghi dữ liệu User, để .NET toàn quyền quản lý Migration
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("tarot.domain.entities.identity")
                .persistenceUnit("identityPU")
                .properties(properties)
                .build();
    }

    @Bean(name = "identityTransactionManager")
    public PlatformTransactionManager identityTransactionManager(
            @Qualifier("identityEntityManagerFactory") LocalContainerEntityManagerFactoryBean identityEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(identityEntityManagerFactory.getObject()));
    }
}
