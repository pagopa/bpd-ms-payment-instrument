package it.gov.pagopa.bpd.payment_instrument.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/paymentInstrument.properties")
public class PaymentInstrumentConfig {
}
