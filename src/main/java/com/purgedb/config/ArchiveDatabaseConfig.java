package com.purgedb.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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

/**
 * Configuration for Archive Database
 * Contains archive tables with historical data
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.purgedb.repository.archive",
    entityManagerFactoryRef = "archiveEntityManagerFactory",
    transactionManagerRef = "archiveTransactionManager"
)
public class ArchiveDatabaseConfig {

    @Bean(name = "archiveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.archive")
    public DataSource archiveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "archiveEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean archiveEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("archiveDataSource") DataSource dataSource) {
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        
        return builder
                .dataSource(dataSource)
                .packages("com.purgedb.entity")
                .persistenceUnit("archive")
                .properties(properties)
                .build();
    }

    @Bean(name = "archiveTransactionManager")
    public PlatformTransactionManager archiveTransactionManager(
            @Qualifier("archiveEntityManagerFactory") LocalContainerEntityManagerFactoryBean archiveEntityManagerFactory) {
        return new JpaTransactionManager(archiveEntityManagerFactory.getObject());
    }
}
