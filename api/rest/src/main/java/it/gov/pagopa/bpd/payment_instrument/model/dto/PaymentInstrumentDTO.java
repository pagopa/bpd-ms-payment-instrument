package it.gov.pagopa.bpd.payment_instrument.model.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class PaymentInstrumentDTO {

    private ZonedDateTime activationDate;

}
