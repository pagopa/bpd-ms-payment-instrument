package it.gov.pagopa.bpd.payment_instrument.exception;

import eu.sia.meda.exceptions.MedaDomainRuntimeException;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class PaymentInstrumentDifferentChannelException extends MedaDomainRuntimeException {

    private static final String CODE = "too many channel";
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;


    public <K extends Serializable> PaymentInstrumentDifferentChannelException(String id) {
        super(getMessage(id), CODE, STATUS);
    }

    private static String getMessage(String id) {
        return String.format("impossible to delete Payment method", id);
    }

}
