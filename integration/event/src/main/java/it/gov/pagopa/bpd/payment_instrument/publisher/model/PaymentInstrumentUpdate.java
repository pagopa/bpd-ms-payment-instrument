package it.gov.pagopa.bpd.payment_instrument.publisher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInstrumentUpdate {

    @NotNull
    @NotBlank
    String hpan;

    @NotNull
    @NotBlank
    String par;
}
