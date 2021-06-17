package it.gov.pagopa.bpd.payment_instrument.listener.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for the OnTransactionSaveRequestListener class
 */

@Configuration
@PropertySource({"classpath:config/transactionRequestListener.properties",
        "classpath:config/citizenRequestListener.properties"})
public class EventRequestConfig {
}
