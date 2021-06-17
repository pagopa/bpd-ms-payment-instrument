package it.gov.pagopa.bpd.payment_instrument.publisher.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for the TkmPublisherConfig
 */

@Configuration
@PropertySource("classpath:config/tkmPublisher.properties")
public class TkmPublisherConfig {
}
