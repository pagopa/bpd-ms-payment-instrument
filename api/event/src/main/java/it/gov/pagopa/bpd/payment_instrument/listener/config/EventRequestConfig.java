package it.gov.pagopa.bpd.payment_instrument.listener.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for the OnTransactionSaveRequestListener class
 */

@Configuration
@PropertySource({"classpath:config/transactionRequestListener.properties",
        "classpath:config/citizenRequestListener.properties",
        "classpath:config/paymentInstrumentToDeleteListener.properties",
        "classpath:config/tokenManagementRequestListener.properties"})
public class EventRequestConfig {
}
