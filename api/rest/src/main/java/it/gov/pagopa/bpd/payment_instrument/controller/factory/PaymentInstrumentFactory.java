package it.gov.pagopa.bpd.payment_instrument.controller.factory;

import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentServiceModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Mapper between <PaymentInstrumentDTO> DTO class and <PaymentInstrument> Entity class
 */
@Component
public class PaymentInstrumentFactory implements ModelFactory<PaymentInstrumentDTO, PaymentInstrumentServiceModel> {

    @Override
    public PaymentInstrumentServiceModel createModel(PaymentInstrumentDTO dto) {
        final PaymentInstrumentServiceModel result = new PaymentInstrumentServiceModel();

        BeanUtils.copyProperties(dto, result);

        return result;
    }
}
