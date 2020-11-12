package it.gov.pagopa.bpd.payment_instrument.exception;

import eu.sia.meda.exceptions.MedaDomainRuntimeException;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class PaymentInstrumentOnDifferentUserException extends MedaDomainRuntimeException {

    private static final String CODE = "hpan already exists";
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;


    public <K extends Serializable> PaymentInstrumentOnDifferentUserException(String id) {
        super(getMessage(id), CODE, STATUS);
    }

    private static String getMessage(String id) {
        return String.format("impossible to get or activate Payment method", id);
    }

}
