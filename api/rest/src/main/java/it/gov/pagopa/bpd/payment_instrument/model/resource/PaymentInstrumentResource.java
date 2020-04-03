package it.gov.pagopa.bpd.payment_instrument.model.resource;

import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(of = "hpan", callSuper = false)
public class PaymentInstrumentResource {

    private String hpan;
    private String fiscalCode;
    private ZonedDateTime activationDate;
    private ZonedDateTime cancellationDate;
    private PaymentInstrument.Status Status;

}
