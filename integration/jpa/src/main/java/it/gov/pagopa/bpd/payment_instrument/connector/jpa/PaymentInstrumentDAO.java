package it.gov.pagopa.bpd.payment_instrument.connector.jpa;

import it.gov.pagopa.bpd.common.connector.jpa.CrudJpaDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object to manage all CRUD operations to the database
 */
@Repository
public interface PaymentInstrumentDAO extends CrudJpaDAO<PaymentInstrument, String> {


}
