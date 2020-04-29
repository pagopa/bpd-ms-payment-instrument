package it.gov.pagopa.bpd.payment_instrument.service;

import eu.sia.meda.service.BaseService;
import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentHistoryDAO;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentNotFoundException;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrumentHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
class PaymentInstrumentServiceImpl extends BaseService implements PaymentInstrumentService {

    private final PaymentInstrumentDAO paymentInstrumentDAO;
    private final PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAO;

    @Value(value = "${numMaxPaymentInstr}")
    private int numMaxPaymentInstr;


    @Autowired
    public PaymentInstrumentServiceImpl(PaymentInstrumentDAO paymentInstrumentDAO, PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAO) {
        this.paymentInstrumentDAO = paymentInstrumentDAO;
        this.paymentInstrumentHistoryDAO = paymentInstrumentHistoryDAO;
    }


    @Override
    public PaymentInstrument find(String hpan) {
        return paymentInstrumentDAO.findById(hpan).orElseThrow(() -> new PaymentInstrumentNotFoundException(hpan));
    }


    @Override
    public PaymentInstrument createOrUpdate(String hpan, PaymentInstrument pi) {
        final Optional<PaymentInstrument> found = paymentInstrumentDAO.findById(hpan);
        if (!found.isPresent()) {
            final long count = paymentInstrumentDAO.count(Example.of(pi));
            if (count >= numMaxPaymentInstr) {
                throw new IllegalStateException("Numero massimo di strumenti da censire raggiunto");
            }
        }
        pi.setHpan(hpan);

        return paymentInstrumentDAO.save(pi);
    }


    @Override
    public void delete(String hpan) {
        PaymentInstrument paymentInstrument = paymentInstrumentDAO.findById(hpan).orElseThrow(() -> new PaymentInstrumentNotFoundException(hpan));
        paymentInstrument.setStatus(PaymentInstrument.Status.INACTIVE);
        paymentInstrument.setCancellationDate(OffsetDateTime.now());
        paymentInstrument.setEnabled(false);
        //TODO: set update user with logged fiscal code
        paymentInstrumentDAO.save(paymentInstrument);
    }


    @Override
    public boolean checkActive(String hpan, OffsetDateTime accountingDate) {
        List<PaymentInstrumentHistory> paymentInstrumentHistoryList =
                paymentInstrumentHistoryDAO.checkActive(hpan, accountingDate);

        return !paymentInstrumentHistoryList.isEmpty();
    }

}
