package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;

import java.time.OffsetDateTime;

public interface PaymentInstrumentService {

    PaymentInstrument find(String fiscalCode);

    PaymentInstrument createOrUpdate(String hpan, PaymentInstrument pi);

    void delete(String hpan);

    boolean checkActive(String hpan, OffsetDateTime accountingDate);
}
