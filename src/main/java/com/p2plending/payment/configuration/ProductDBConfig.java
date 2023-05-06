package com.p2plending.payment.configuration;

import jakarta.persistence.EntityManagerFactory;
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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "quinaryEntityManagerFactory",
        transactionManagerRef = "quinaryTransactionManager",
        basePackages = { "com.p2plending.payment.db.productdb.repository" }
)
public class ProductDBConfig {
    @Bean(name="quinaryDataSource")
    @ConfigurationProperties(prefix="spring.dbproduct.datasource")
    public DataSource quinaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "quinaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean quinaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("quinaryDataSource") DataSource quinaryDataSource) {
        return builder
                .dataSource(quinaryDataSource)
                .packages("com.p2plending.payment.db.productdb.model")
                .build();
    }

    @Bean(name = "quinaryTransactionManager")
    public PlatformTransactionManager quinaryTransactionManager(@Qualifier("quinaryEntityManagerFactory") EntityManagerFactory quinaryEntityManagerFactory) {
        return new JpaTransactionManager(quinaryEntityManagerFactory);
    }
}
