package it.gov.pagopa.bpd.payment_instrument.controller.factory;

public interface ModelFactory<T, U> {

    U createModel(T dto);

}
