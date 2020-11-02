package it.gov.pagopa.bpd.payment_instrument.listener.factory;

/**
 * interface to be used for inheritance for model factories from a DTO
 */

public interface ModelFactory<T, U> {

    U createModel(T dto);

}
