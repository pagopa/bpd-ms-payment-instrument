package it.gov.pagopa.bpd.payment_instrument.exception;

import it.gov.pagopa.bpd.common.exception.ResourceNotFoundException;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;

public class PaymentInstrumentNotFoundException extends ResourceNotFoundException {

    public PaymentInstrumentNotFoundException(String id) {
        super(PaymentInstrument.class, id);
    }

}