package it.gov.pagopa.bpd.payment_instrument.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TokenizedInstrument {
    @ApiModelProperty(value = "${swagger.paymentInstrument.hashToken}", required = true)
    @JsonProperty(required = true)
    private String hashToken;
    @ApiModelProperty(value = "${swagger.paymentInstrument.fiscalCode}", required = true)
    @JsonProperty(required = true)
    private String fiscalCode;
    @ApiModelProperty(value = "${swagger.paymentInstrument.activationDate}", required = true)
    @JsonProperty(required = true)
    private OffsetDateTime activationDate;
    @ApiModelProperty(value = "${swagger.paymentInstrument.deactivationDate}", required = true)
    @JsonProperty(required = true)
    private OffsetDateTime deactivationDate;
    @ApiModelProperty(value = "${swagger.paymentInstrument.Status}", required = true)
    @JsonProperty(required = true)
    private PaymentInstrument.Status Status;
}