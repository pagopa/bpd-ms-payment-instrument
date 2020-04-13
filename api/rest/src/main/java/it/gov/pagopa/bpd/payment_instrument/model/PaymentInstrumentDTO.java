package it.gov.pagopa.bpd.payment_instrument.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class PaymentInstrumentDTO {

    private String fiscalCode;
    private OffsetDateTime activationDate;

}
