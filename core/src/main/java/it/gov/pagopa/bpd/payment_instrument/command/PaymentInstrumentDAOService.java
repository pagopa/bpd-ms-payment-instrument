package it.gov.pagopa.bpd.payment_instrument.command;

import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface PaymentInstrumentDAOService {

    Optional<PaymentInstrument> find(String fiscalCode);

    PaymentInstrument update(String hpan, PaymentInstrument pi);

    void delete(String hpan);

    boolean checkActive(String hpan, ZonedDateTime accountingDate);
}
