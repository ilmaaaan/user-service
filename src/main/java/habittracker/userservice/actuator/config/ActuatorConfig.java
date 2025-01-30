package habittracker.userservice.actuator.config;

import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "actuatorInfoEntityManagerFactory",
        transactionManagerRef = "actuatorInfoTransactionManager",
        basePackages = {"habittracker.userservice.actuator.repository"}
)
public class ActuatorConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.app-metrics-db")
    public DataSourceProperties actuatorInfoDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "actuatorInfoDataSource")
    public DataSource actuatorInfoDataSource() {
        return actuatorInfoDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "liquibaseActuatorInfo")
    public SpringLiquibase liquibaseActuatorInfo(@Qualifier("actuatorInfoDataSource")
                                                     DataSource actuatorInfoDataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(actuatorInfoDataSource);
        liquibase.setChangeLog("classpath:/actuator/changelog.yaml");
        return liquibase;
    }

    @Bean(name = "actuatorInfoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean actuatorInfoEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder.dataSource(actuatorInfoDataSource())
                .packages("habittracker.userservice.actuator.entity")
                .persistenceUnit("app-metrics-db")
                .build();
    }

    @Bean(name = "actuatorInfoTransactionManager")
    public PlatformTransactionManager actuatorInfoTransactionManager(
            @Qualifier("actuatorInfoEntityManagerFactory") EntityManagerFactory
                    actuatorInfoEntityManagerFactory
    ) {
        return new JpaTransactionManager(actuatorInfoEntityManagerFactory);
    }

}
