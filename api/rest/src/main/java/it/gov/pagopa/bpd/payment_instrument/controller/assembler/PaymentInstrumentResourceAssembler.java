package it.gov.pagopa.bpd.payment_instrument.controller.assembler;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentResource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Mapper between <PaymentInstrument> Entity class and <PaymentInstrumentResource> Resource class
 */
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
