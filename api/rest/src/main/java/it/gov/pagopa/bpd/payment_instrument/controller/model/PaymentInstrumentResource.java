package it.gov.pagopa.bpd.payment_instrument.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(of = "hpan", callSuper = false)
public class PaymentInstrumentResource {

    @ApiModelProperty(value = "${swagger.paymentInstrument.hpan}", required = true)
    @JsonProperty(required = true)
    private String hpan;
    @ApiModelProperty(value = "${swagger.paymentInstrument.fiscalCode}", required = true)
    @JsonProperty(required = true)
    private String fiscalCode;
    @ApiModelProperty(value = "${swagger.paymentInstrument.activationDate}", required = true)
    @JsonProperty(required = true)
    private OffsetDateTime activationDate;
    @ApiModelProperty(value = "${swagger.paymentInstrument.cancellationDate}", required = true)
    @JsonProperty(required = true)
    private OffsetDateTime deactivationDate;
    @ApiModelProperty(value = "${swagger.paymentInstrument.Status}", required = true)
    @JsonProperty(required = true)
    private PaymentInstrument.Status Status;

}
