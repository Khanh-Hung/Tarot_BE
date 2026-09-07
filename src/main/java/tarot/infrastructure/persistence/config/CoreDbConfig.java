package tarot.infrastructure.persistence.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        basePackages = "tarot.infrastructure.persistence.repositories.core",
        entityManagerFactoryRef = "coreEntityManagerFactory",
        transactionManagerRef = "coreTransactionManager"
)
public class CoreDbConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.core")
    public DataSourceProperties coreDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "coreDataSource")
    public DataSource coreDataSource() {
        com.zaxxer.hikari.HikariDataSource ds = coreDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(com.zaxxer.hikari.HikariDataSource.class)
                .build();
        ds.setMaximumPoolSize(3);
        ds.setMinimumIdle(1);
        return ds;
    }

    @Primary
    @Bean(name = "coreEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean coreEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("coreDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.physical_naming_strategy", PascalCaseNamingStrategy.class.getName());
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("tarot.domain.entities.core")
                .persistenceUnit("corePU")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name = "coreTransactionManager")
    public PlatformTransactionManager coreTransactionManager(
            @Qualifier("coreEntityManagerFactory") LocalContainerEntityManagerFactoryBean coreEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(coreEntityManagerFactory.getObject()));
    }
}
