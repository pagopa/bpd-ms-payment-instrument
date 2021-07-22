package it.gov.pagopa.bpd.payment_instrument.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;


/**
 * Resource model for the data published through {@link it.gov.pagopa.bpd.payment_instrument.command.ProcessCitizenUpdateEventCommand}
 */


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"fiscalCode"}, callSuper = false)
public class InboundCitizenStatusData {

    @NotNull
    @NotBlank
    String fiscalCode;

    @NotNull
    Boolean enabled;

    @NotNull
    @NotBlank
    String applyTo;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime updateDateTime;


}
