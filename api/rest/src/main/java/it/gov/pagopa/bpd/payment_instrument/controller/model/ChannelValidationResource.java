package it.gov.pagopa.bpd.payment_instrument.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(of = "isValid", callSuper = false)
public class ChannelValidationResource {

    @ApiModelProperty(value = "${swagger.paymentInstrument.isValid}", required = true)
    @JsonProperty(required = true)
    private Boolean isValid;


}
