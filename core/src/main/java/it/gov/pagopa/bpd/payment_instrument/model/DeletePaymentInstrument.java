package it.gov.pagopa.bpd.payment_instrument.model;

import it.gov.pagopa.bpd.common.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletePaymentInstrument {

    @NotNull
    @NotBlank
    @Size(max = 64)
    String hpan;

    @Valid @Size(min = 16, max = 16)
    @Pattern(regexp = Constants.FISCAL_CODE_REGEX)
    String fiscalCode;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime cancellationDate;
}
