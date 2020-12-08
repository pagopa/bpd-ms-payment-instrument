package it.gov.pagopa.bpd.payment_instrument.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PaymentInstrumentConverterResource {

    @ApiModelProperty(value = "${swagger.paymentInstrument.channel}", required = true)
    @JsonProperty(required = true)
    private String channel;
    @JsonProperty(required = true)
    private Long count;
}
