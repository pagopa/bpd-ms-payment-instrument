package it.gov.pagopa.bpd.payment_instrument.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
public class PaymentInstrumentDTO {

    @NotNull
    private String fiscalCode;
    private OffsetDateTime activationDate;

}
