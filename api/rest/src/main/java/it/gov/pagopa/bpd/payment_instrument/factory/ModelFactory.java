package it.gov.pagopa.bpd.payment_instrument.factory;

public interface ModelFactory<T, U> {

    U createModel(T dto);

}
