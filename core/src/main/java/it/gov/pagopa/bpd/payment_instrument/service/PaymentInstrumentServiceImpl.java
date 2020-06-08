package it.gov.pagopa.bpd.payment_instrument.service;

import eu.sia.meda.service.BaseService;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentHistoryDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentNotFoundException;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentNumbersExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @See PaymentInstrumentService
 */
@Service
class PaymentInstrumentServiceImpl extends BaseService implements PaymentInstrumentService {

    private final PaymentInstrumentDAO paymentInstrumentDAO;
    private final PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAO;

    @Value(value = "${numMaxPaymentInstr}")
    private int numMaxPaymentInstr;


    @Autowired
    public PaymentInstrumentServiceImpl(PaymentInstrumentDAO paymentInstrumentDAO,
                                        PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAO) {
        this.paymentInstrumentDAO = paymentInstrumentDAO;
        this.paymentInstrumentHistoryDAO = paymentInstrumentHistoryDAO;
    }


    @Override
    public PaymentInstrument find(String hpan) {
        return paymentInstrumentDAO.findById(hpan).orElseThrow(() -> new PaymentInstrumentNotFoundException(hpan));
    }


    @Override
    public PaymentInstrument createOrUpdate(String hpan, PaymentInstrument pi) {
        final Optional<PaymentInstrument> foundPIOpt = paymentInstrumentDAO.findById(hpan);
        pi.setHpan(hpan);
        if (!foundPIOpt.isPresent()) {
            final long count = paymentInstrumentDAO.count(Example.of(pi));
            if (count >= numMaxPaymentInstr) {
                throw new PaymentInstrumentNumbersExceededException(
                        PaymentInstrument.class, numMaxPaymentInstr);
            }
            return paymentInstrumentDAO.save(pi);
        } else {
            PaymentInstrument foundPI = foundPIOpt.get();
            if (!foundPI.isEnabled()) {
                foundPI.setEnabled(true);
                foundPI.setHpan(hpan);
                foundPI.setActivationDate(pi.getActivationDate());
                foundPI.setFiscalCode(pi.getFiscalCode());
                foundPI.setStatus(PaymentInstrument.Status.ACTIVE);
                return paymentInstrumentDAO.save(pi);
            }
        }

        return pi;
    }


    @Override
    public void delete(String hpan) {
        PaymentInstrument paymentInstrument = paymentInstrumentDAO.findById(hpan).orElseThrow(
                () -> new PaymentInstrumentNotFoundException(hpan));
        if (paymentInstrument.isEnabled()) {
            paymentInstrument.setStatus(PaymentInstrument.Status.INACTIVE);
            paymentInstrument.setDeactivationDate(OffsetDateTime.now());
            paymentInstrument.setEnabled(false);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                paymentInstrument.setFiscalCode(authentication.getName());
            }
        }
        paymentInstrumentDAO.save(paymentInstrument);
    }


    @Override
    public boolean checkActive(String hpan, OffsetDateTime accountingDate) {
        List<PaymentInstrumentHistory> paymentInstrumentHistoryList =
                paymentInstrumentHistoryDAO.checkActive(hpan, accountingDate);
        return !paymentInstrumentHistoryList.isEmpty();
    }

}
