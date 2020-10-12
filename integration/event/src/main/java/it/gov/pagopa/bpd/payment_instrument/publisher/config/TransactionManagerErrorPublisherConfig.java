package it.gov.pagopa.bpd.payment_instrument.publisher.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for the TransactionManagerErrorPublisherConnector
 */

@Configuration
@PropertySource("classpath:config/transactionManagerErrorPublisher.properties")
public class TransactionManagerErrorPublisherConfig { }
