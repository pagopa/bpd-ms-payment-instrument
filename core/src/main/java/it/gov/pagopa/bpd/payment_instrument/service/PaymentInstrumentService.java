package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentConverter;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * A service to manage the Business Logic related to PaymentInstrument
 */
public interface PaymentInstrumentService {

    PaymentInstrument find(String hpan, String fiscalCode);

    PaymentInstrument createOrUpdate(String hpan, PaymentInstrument pi);

    void delete(String hpan, String fiscalCode, OffsetDateTime cancellationDate);

    void deleteByFiscalCode(String fiscalCode, String channel);

    PaymentInstrumentHistory checkActive(String hpan, OffsetDateTime accountingDate);

    void reactivateForRollback(String fiscalCode, OffsetDateTime requestTimestamp);

    String getFiscalCode(String hpan);

    List<PaymentInstrumentConverter> getPaymentInstrument(String fiscalCode, String channel);
}
