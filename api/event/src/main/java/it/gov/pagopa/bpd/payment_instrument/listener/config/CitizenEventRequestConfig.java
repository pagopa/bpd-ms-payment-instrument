package it.gov.pagopa.bpd.payment_instrument.listener.config;

import it.gov.pagopa.bpd.payment_instrument.listener.OnCitizenStatusUpdateRequestListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for {@link OnCitizenStatusUpdateRequestListener}
 */

@Configuration
@PropertySource("classpath:config/citizenEventRequestListener.properties")
public class CitizenEventRequestConfig {
}
