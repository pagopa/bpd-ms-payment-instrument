package it.gov.pagopa.bpd.payment_instrument.command;

import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Slf4j
@PropertySource("classpath:paymentInstrument.properties")
class PaymentInstrumentDAOServiceImpl implements PaymentInstrumentDAOService {

    private final PaymentInstrumentDAO paymentInstrumentDAO;
    @Value(value = "${NUM_MAX_PAYMENT_INSTR:5}")//TODO verificare
    private int numMaxPaymentInstr;

    @Autowired
    public PaymentInstrumentDAOServiceImpl(PaymentInstrumentDAO paymentInstrumentDAO) {
        this.paymentInstrumentDAO = paymentInstrumentDAO;
    }

    @Override
    public Optional<PaymentInstrument> find(String hpan) {
        return paymentInstrumentDAO.findById(hpan);
    }

    @Override
    public PaymentInstrument update(String hpan, PaymentInstrument pi) {
        //final PaymentInstrument paymentInstrument = new PaymentInstrument();
        final long count = paymentInstrumentDAO.count(Example.of(pi));
        if (count >= numMaxPaymentInstr) {
            throw new RuntimeException("Numero massimo di strumenti da censire raggiunto");
        }
        pi.setFiscalCode(hpan);       //TODO recuperare cf dall'hpan

        return paymentInstrumentDAO.save(pi);
    }

    @Override
    public void delete(String hpan) {
        Optional<PaymentInstrument> paymentInstrument = paymentInstrumentDAO.findById(hpan);
        paymentInstrument.get().setStatus(PaymentInstrument.Status.INACTIVE);
        paymentInstrument.get().setCancellationDate(ZonedDateTime.now());
        paymentInstrument.get().setEnabled(false);
        update(hpan, paymentInstrument.get());
    }

    @Override
    public boolean checkActive(String hpan, ZonedDateTime accountingDate) {
        Object o = paymentInstrumentDAO.checkActive(hpan, accountingDate);
        return true;
    }
}
