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
        entityManagerFactoryRef = "quaternaryEntityManagerFactory",
        transactionManagerRef = "quaternaryTransactionManager",
        basePackages = { "com.p2plending.payment.db.fundingloandb.repository" }
)
public class FundingLoanDBConfig {
    @Bean(name="quaternaryDataSource")
    @ConfigurationProperties(prefix="spring.dbfundingloan.datasource")
    public DataSource quaternaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "quaternaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean quaternaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("quaternaryDataSource") DataSource quaternaryDataSource) {
        return builder
                .dataSource(quaternaryDataSource)
                .packages("com.p2plending.payment.db.fundingloandb.model")
                .build();
    }

    @Bean(name = "quaternaryTransactionManager")
    public PlatformTransactionManager quaternaryTransactionManager(@Qualifier("quaternaryEntityManagerFactory") EntityManagerFactory quaternaryEntityManagerFactory) {
        return new JpaTransactionManager(quaternaryEntityManagerFactory);
    }
}
