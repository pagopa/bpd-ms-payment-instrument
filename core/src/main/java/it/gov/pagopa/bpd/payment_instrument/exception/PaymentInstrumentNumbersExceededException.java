package it.gov.pagopa.bpd.payment_instrument.exception;

import eu.sia.meda.exceptions.MedaDomainRuntimeException;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class PaymentInstrumentNumbersExceededException extends MedaDomainRuntimeException {

    private static final String CODE = "too many hashpan";
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;


    public <K extends Serializable> PaymentInstrumentNumbersExceededException(Class<?> resourceClass, K id) {
        super(getMessage(resourceClass, id), CODE, STATUS);
    }

    private static String getMessage(Class<?> resourceClass, Object id) {
        return String.format("impossible to add others Payment method", resourceClass.getSimpleName(), id);
    }

}
