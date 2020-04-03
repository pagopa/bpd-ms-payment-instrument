package it.gov.pagopa.bpd.payment_instrument.factory;

import it.gov.pagopa.bpd.payment_instrument.model.dto.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import org.springframework.stereotype.Component;

@Component
public class PaymentInstrumentFactory implements ModelFactory<PaymentInstrumentDTO, PaymentInstrument> {

    @Override
    public PaymentInstrument createModel(PaymentInstrumentDTO dto) {
        final PaymentInstrument result = new PaymentInstrument();

        result.setActivationDate(dto.getActivationDate());

        return result;
    }
}
