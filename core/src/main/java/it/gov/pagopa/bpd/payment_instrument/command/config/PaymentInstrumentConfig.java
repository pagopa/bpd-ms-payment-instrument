package it.gov.pagopa.bpd.payment_instrument.command.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/paymentInstrument.properties")
public class PaymentInstrumentConfig {
}
