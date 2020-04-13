package it.gov.pagopa.bpd.payment_instrument.assembler;

import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentResource;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class PaymentInstrumentResourceAssembler {

    public PaymentInstrumentResource toResource(PaymentInstrument paymentInstrument) {
        PaymentInstrumentResource resource = null;

        if (paymentInstrument != null) {
            resource = new PaymentInstrumentResource();
            BeanUtils.copyProperties(paymentInstrument, resource);
        }

        return resource;
    }

}
