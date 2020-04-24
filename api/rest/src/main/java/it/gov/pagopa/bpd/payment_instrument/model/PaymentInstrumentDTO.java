package it.gov.pagopa.bpd.payment_instrument.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

@Data
public class PaymentInstrumentDTO {

    @NotBlank
    private String fiscalCode;
    //    @FutureOrPresent//FIXME
    private OffsetDateTime activationDate;

}
