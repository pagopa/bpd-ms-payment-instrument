package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface PaymentInstrumentService {

    Optional<PaymentInstrument> find(String fiscalCode);

    PaymentInstrument update(String hpan, PaymentInstrument pi);

    void delete(String hpan);

    boolean checkActive(String hpan, OffsetDateTime accountingDate);
}
