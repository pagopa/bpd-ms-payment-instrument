package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentConverter;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentServiceModel;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerData;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * A service to manage the Business Logic related to PaymentInstrument
 */
public interface PaymentInstrumentService {

    //    List<PaymentInstrument> find(String hpan, String fiscalCode);
    PaymentInstrument find(String hpan, String fiscalCode);

    Optional<PaymentInstrument> findByhpan(String hpan);

    PaymentInstrument findByPar(String par);

    @Deprecated
    PaymentInstrument createOrUpdate(String hpan, PaymentInstrument pi);

    PaymentInstrumentServiceModel createOrUpdate(String hpan, PaymentInstrumentServiceModel pi);

    void delete(String hpan, String fiscalCode, OffsetDateTime cancellationDate);

    void deleteByFiscalCode(String fiscalCode, String channel);

    PaymentInstrumentHistory checkActive(String hpan, OffsetDateTime accountingDate);

    PaymentInstrumentHistory checkActivePar(String par, OffsetDateTime accountingDate);

    void reactivateForRollback(String fiscalCode, OffsetDateTime requestTimestamp);

    String getFiscalCode(String hpan);

    @Deprecated
    List<PaymentInstrumentConverter> getPaymentInstrument(String fiscalCode, String channel);

    List<PaymentInstrumentHistory> findHistory(String fiscalCode, String hpan);

    Boolean manageTokenData(TokenManagerData tokenManagerData, OffsetDateTime updateDate);


}
