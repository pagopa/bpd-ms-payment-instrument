package it.gov.pagopa.bpd.payment_instrument.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenManagerData {

    @NotNull
    @NotBlank
    private String taxCode;

    @NotNull
    private List<TokenManagerDataCard> cards;

}
