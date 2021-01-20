package it.gov.pagopa.bpd.payment_instrument.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class PaymentInstrumentHistoryResource {

    @ApiModelProperty(value = "${swagger.paymentInstrument.hpan}", required = true)
    @JsonProperty(required = true)
    private String hpan;
    @ApiModelProperty(value = "${swagger.paymentInstrument.activationDate}", required = true)
    @JsonProperty(required = true)
    private OffsetDateTime activationDate;
    @ApiModelProperty(value = "${swagger.paymentInstrument.deactivationDate}", required = true)
    @JsonProperty(required = true)
    private OffsetDateTime deactivationDate;
    @ApiModelProperty(value = "${swagger.paymentInstrument.fiscalCode}", required = true)
    @JsonProperty(required = true)
    private String fiscalCode;

}
