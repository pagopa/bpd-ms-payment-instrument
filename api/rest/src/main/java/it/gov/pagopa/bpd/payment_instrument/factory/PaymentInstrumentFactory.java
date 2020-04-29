package it.gov.pagopa.bpd.payment_instrument.factory;

import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
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
