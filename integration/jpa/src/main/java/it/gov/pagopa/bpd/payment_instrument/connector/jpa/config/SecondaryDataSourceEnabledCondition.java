package it.gov.pagopa.bpd.payment_instrument.connector.jpa.config;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

 class SecondaryDataSourceEnabledCondition extends AllNestedConditions {

    public SecondaryDataSourceEnabledCondition() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnProperty(prefix = "spring.secondary.datasource", name = "enable", havingValue = "true")
    public static class SecondaryDataSourceEnabled {
    }
}
