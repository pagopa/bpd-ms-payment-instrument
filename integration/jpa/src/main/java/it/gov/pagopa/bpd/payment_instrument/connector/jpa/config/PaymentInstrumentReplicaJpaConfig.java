package it.gov.pagopa.bpd.payment_instrument.connector.jpa.config;

import it.gov.pagopa.bpd.common.connector.jpa.CustomJpaRepository;
import it.gov.pagopa.bpd.common.connector.jpa.ReadOnlyRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Configuration
@PropertySource("classpath:config/replicaJpaConnectionConfig.properties")
@EnableJpaRepositories(
        repositoryBaseClass = CustomJpaRepository.class,
        basePackages = {"it.gov.pagopa.bpd.payment_instrument.connector.jpa"},
        excludeFilters = @ComponentScan.Filter(Repository.class),
        includeFilters = @ComponentScan.Filter(ReadOnlyRepository.class),
        entityManagerFactoryRef = "readEntityManagerFactory",
        transactionManagerRef = "readTransactionManager"
)
public class PaymentInstrumentReplicaJpaConfig /* extends BaseJpaConfig */ {
    @Value("${spring.replica.jpa.database-platform}")
    private String hibernateDialect;

    @Value("${spring.replica.jpa.show-sql}")
    private boolean showSql;

    @Value("${spring.replica.jpa.hibernate.ddl-auto}")
    private String hibernateDdlAuto;

    @Bean(name = {"readDataSource"})
    @ConfigurationProperties(prefix = "spring.replica.datasource.hikari")
    public DataSource readDataSource() {
        return readDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = {"readDataSourceProperties"})
    @ConfigurationProperties("spring.replica.datasource")
    public DataSourceProperties readDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(
            name = {"readEntityManagerFactory"}
    )
    public LocalContainerEntityManagerFactoryBean readEntityManagerFactory(
            @Qualifier("readDataSource") DataSource datasource
    ) {
        Properties jpaProperties = new Properties();


        jpaProperties.put("hibernate.dialect", this.hibernateDialect);
        jpaProperties.put("hibernate.show_sql", this.showSql);
        jpaProperties.put("hibernate.jdbc.batch_size", 5);
        jpaProperties.put("hibernate.order_inserts", Boolean.FALSE);
        jpaProperties.put("hibernate.order_updates", Boolean.FALSE);
        jpaProperties.put("hibernate.jdbc.batch_versioned_data", Boolean.FALSE);
        jpaProperties.put("hibernate.id.new_generator_mappings", Boolean.FALSE);
        jpaProperties.put("hibernate.jdbc.lob.non_contextual_creation",
                Objects.isNull(null) ? Boolean.TRUE
                        : null);
        if (Boolean.FALSE) {
            jpaProperties.put("hibernate.hbm2ddl.auto", "none");
        } else {
            jpaProperties.put("hibernate.hbm2ddl.auto", this.hibernateDdlAuto);
        }

        List<String> propertyPackages = new ArrayList<String>();
        if (propertyPackages.isEmpty()) {
            propertyPackages.add("eu.sia.meda");
            propertyPackages.add("it.gov.pagopa.bpd.payment_instrument.connector.jpa.model");
        }
        String[] packagesToScan = (String[]) propertyPackages.toArray(new String[propertyPackages.size()]);

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(datasource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan(packagesToScan);
        entityManagerFactoryBean.setJpaProperties(jpaProperties);
        return entityManagerFactoryBean;
    }

    @Bean(
            name = {"readTransactionManager"}
    )
    public PlatformTransactionManager readTransactionManager() throws Exception {
        return new JpaTransactionManager(this.readEntityManagerFactory(this.readDataSource()).getObject());
    }
}
