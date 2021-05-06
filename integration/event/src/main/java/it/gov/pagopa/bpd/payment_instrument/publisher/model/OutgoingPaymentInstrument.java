package it.gov.pagopa.bpd.payment_instrument.publisher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutgoingPaymentInstrument implements Serializable {


    @NotNull
    @NotBlank
    String fiscalCode;

    @NotNull
    @NotBlank
    String hpanMaster;

    @NotNull
    @NotBlank
    String par;

    @NotNull
    @NotBlank
    List<String> hashToken;


}
