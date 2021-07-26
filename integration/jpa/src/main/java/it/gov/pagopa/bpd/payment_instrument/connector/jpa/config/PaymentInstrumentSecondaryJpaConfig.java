package it.gov.pagopa.bpd.payment_instrument.connector.jpa.config;

import it.gov.pagopa.bpd.common.connector.jpa.CustomJpaRepository;
import it.gov.pagopa.bpd.common.connector.jpa.ReadOnlyRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:config/PaymentInstrumentSecondaryJpaConnectionConfig.properties")
@Conditional(SecondaryDataSourceEnabledCondition.class)
@EnableJpaRepositories(
        repositoryBaseClass = CustomJpaRepository.class,
        basePackages = {"it.gov.pagopa.bpd.payment_instrument.connector.jpa"},
        excludeFilters = @ComponentScan.Filter(Repository.class),
        includeFilters = @ComponentScan.Filter(ReadOnlyRepository.class),
        entityManagerFactoryRef = "entityManagerFactorySecondary",
        transactionManagerRef = "transactionManagerSecondary"
)
@EntityScan(
        basePackages = {"it.gov.pagopa.bpd.payment_instrument.connector.jpa"}
)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class PaymentInstrumentSecondaryJpaConfig {

    @Bean("dataSourceSecondaryProperties")
    @ConfigurationProperties("spring.secondary.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean("dataSourceSecondary")
    @ConfigurationProperties(prefix = "spring.secondary.datasource.hikari")
    public DataSource dataSource(@Qualifier("dataSourceSecondaryProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }


    @Bean("jpaSecondaryProperties")
    @ConfigurationProperties("spring.secondary.jpa")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }


    @Bean("entityManagerFactorySecondary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("jpaSecondaryProperties") JpaProperties properties,
            @Qualifier("dataSourceSecondary") DataSource dataSource,
            ObjectProvider<PersistenceUnitManager> persistenceUnitManager,
            ObjectProvider<EntityManagerFactoryBuilderCustomizer> customizers) {
        AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(properties.isShowSql());
        if (properties.getDatabase() != null) {
            adapter.setDatabase(properties.getDatabase());
        }
        if (properties.getDatabasePlatform() != null) {
            adapter.setDatabasePlatform(properties.getDatabasePlatform());
        }
        adapter.setGenerateDdl(properties.isGenerateDdl());

        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(
                adapter,
                properties.getProperties(),
                persistenceUnitManager.getIfAvailable());
        customizers.orderedStream()
                .forEach((customizer) -> customizer.customize(builder));
        return builder
                .dataSource(dataSource)
                .persistenceUnit("secondary")
                .packages("it.gov.pagopa.bpd.payment_instrument.connector.jpa.model")
                .build();
    }

    @Bean("transactionManagerSecondary")
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactorySecondary")
                                                                 EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
