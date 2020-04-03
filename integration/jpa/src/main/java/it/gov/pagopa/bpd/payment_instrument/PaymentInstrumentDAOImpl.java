package it.gov.pagopa.bpd.payment_instrument;

import eu.sia.meda.connector.jpa.JPAConnectorImpl;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service
class PaymentInstrumentDAOImpl extends JPAConnectorImpl<PaymentInstrument, String> implements PaymentInstrumentDAO {
    @Autowired
    public PaymentInstrumentDAOImpl(EntityManager em) {
        super(PaymentInstrument.class, em);
    }

}