package it.gov.pagopa.bpd.payment_instrument.service;

import eu.sia.meda.service.BaseService;
import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentHistoryDAO;
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
class PaymentInstrumentDAOServiceImpl extends BaseService implements PaymentInstrumentDAOService {

    private final PaymentInstrumentDAO paymentInstrumentDAO;
    private final PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAO;
    @Value(value = "${numMaxPaymentInstr}")
    private int numMaxPaymentInstr;

    @Autowired
    public PaymentInstrumentDAOServiceImpl(PaymentInstrumentDAO paymentInstrumentDAO, PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAO) {
        this.paymentInstrumentDAO = paymentInstrumentDAO;
        this.paymentInstrumentHistoryDAO = paymentInstrumentHistoryDAO;
    }

    @Override
    public Optional<PaymentInstrument> find(String hpan) {
        return paymentInstrumentDAO.findById(hpan);
    }

    @Override
    public PaymentInstrument update(String hpan, PaymentInstrument pi) {
        final long count = paymentInstrumentDAO.count(Example.of(pi));
        if (count >= numMaxPaymentInstr) {
            throw new RuntimeException("Numero massimo di strumenti da censire raggiunto");
        }
        pi.setHpan(hpan);

        return paymentInstrumentDAO.save(pi);
    }

    @Override
    public void delete(String hpan) {
        Optional<PaymentInstrument> paymentInstrument = paymentInstrumentDAO.findById(hpan);
        if (paymentInstrument.isPresent()) {
            paymentInstrument.get().setStatus(PaymentInstrument.Status.INACTIVE);
            paymentInstrument.get().setCancellationDate(OffsetDateTime.now());
            paymentInstrument.get().setEnabled(false);
            update(hpan, paymentInstrument.get());
        } else {
            throw new RuntimeException("Metodo di pagamento non esistente");
        }
    }

    @Override
    public boolean checkActive(String hpan, OffsetDateTime accountingDate) {
        List<PaymentInstrumentHistory> paymentInstrumentHistoryList =
                paymentInstrumentHistoryDAO.checkActive(hpan, accountingDate);
        return !paymentInstrumentHistoryList.isEmpty();
    }
}
