package habittracker.userservice.config;


import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;

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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "userServiceEntityManagerFactory",
        transactionManagerRef = "userServiceTransactionManager",
        basePackages = {"habittracker.userservice.repository"}
)
public class UserServiceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.user-service-db")
    public DataSourceProperties userServiceDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "userServiceDataSource")
    public DataSource userServiceDataSource() {
        return userServiceDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "liquibaseUserService")
    public SpringLiquibase liquibaseAuthService(@Qualifier("userServiceDataSource") DataSource userServiceDataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(userServiceDataSource);
        liquibase.setChangeLog("classpath:/db.changelog/db.changelog-master.yaml");
        return liquibase;
    }

    @Primary
    @Bean(name = "userServiceEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean userServiceEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder.dataSource(userServiceDataSource())
                .packages("habittracker.userservice")
                .persistenceUnit("user-service")
                .build();
    }

    @Primary
    @Bean(name = "userServiceTransactionManager")
    public PlatformTransactionManager authServiceTransactionManager(
            @Qualifier("userServiceEntityManagerFactory") EntityManagerFactory
                    userServiceEntityManagerFactory
    ) {
        return new JpaTransactionManager(userServiceEntityManagerFactory);
    }
}
