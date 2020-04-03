package it.gov.pagopa.bpd.payment_instrument;

import eu.sia.meda.connector.jpa.JPAConnector;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PaymentInstrumentDAO extends JPAConnector<PaymentInstrument, String> {

}
