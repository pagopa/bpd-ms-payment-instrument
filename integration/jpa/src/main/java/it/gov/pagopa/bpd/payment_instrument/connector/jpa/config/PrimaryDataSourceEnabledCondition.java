package it.gov.pagopa.bpd.payment_instrument.connector.jpa.config;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

 class PrimaryDataSourceEnabledCondition extends AllNestedConditions  {

    public PrimaryDataSourceEnabledCondition() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnProperty(prefix = "spring.primary.datasource", name = "enable", havingValue = "true")
    public static class PrimaryDataSourceEnabled {
    }
}
