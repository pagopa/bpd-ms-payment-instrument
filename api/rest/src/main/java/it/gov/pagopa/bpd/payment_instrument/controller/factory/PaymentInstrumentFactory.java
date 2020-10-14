package it.gov.pagopa.bpd.payment_instrument.controller.factory;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Mapper between <PaymentInstrumentDTO> DTO class and <PaymentInstrument> Entity class
 */
@Component
public class PaymentInstrumentFactory implements ModelFactory<PaymentInstrumentDTO, PaymentInstrument> {

    @Override
    public PaymentInstrument createModel(PaymentInstrumentDTO dto) {
        final PaymentInstrument result = new PaymentInstrument();

        BeanUtils.copyProperties(dto, result);
        result.setStatus(PaymentInstrument.Status.ACTIVE);

        return result;
    }
}
