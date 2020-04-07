package it.gov.pagopa.bpd.payment_instrument;

import eu.sia.meda.connector.jpa.CrudJpaDAO;
import eu.sia.meda.connector.jpa.JPAConnector;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface PaymentInstrumentDAO extends CrudJpaDAO<PaymentInstrument, String> {


}
