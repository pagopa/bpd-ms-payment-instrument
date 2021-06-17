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
public class TokenManagerDataCard {

    @NotNull
    @NotBlank
    public String hpan;

    private String par;

    private String action;

    private List<TokenManagerDataToken> htokens;

}
