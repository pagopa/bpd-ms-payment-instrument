package it.gov.pagopa.bpd.payment_instrument.publisher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutgoingPaymentInstrument implements Serializable {

    @NotNull
    @NotBlank
    String hashToken;

    @NotNull
    @NotBlank
    String par;

    @NotNull
    @NotBlank
    String hpanMaster;

    @NotNull
    @NotBlank
    String fiscalCode;
}
