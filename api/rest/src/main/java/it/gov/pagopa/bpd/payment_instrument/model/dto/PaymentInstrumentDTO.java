package it.gov.pagopa.bpd.payment_instrument.model.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class PaymentInstrumentDTO {

    private OffsetDateTime activationDate;

}
