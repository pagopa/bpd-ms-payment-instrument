package it.gov.pagopa.bpd.payment_instrument.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
public class PaymentInstrumentDTO {

    @NotNull
    @NotBlank
    private String fiscalCode;
    @NotNull
    private OffsetDateTime activationDate;

}
