package it.gov.pagopa.bpd.payment_instrument.publisher.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Model for transaction to be sent in the outbound channel
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"idTrxAcquirer", "acquirerCode", "trxDate"}, callSuper = false)
public class Transaction {

    @NotNull
    @NotBlank
    String idTrxAcquirer;

    @NotNull
    @NotBlank
    @Size(max = 20)
    String acquirerCode;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime trxDate;

    @NotNull
    @NotBlank
    @Size(max = 64)
    String hpan;

    @NotNull
    @NotBlank
    @Pattern(regexp = "[0-9]{2}")
    String operationType;

    @NotNull
    @NotBlank
    @Pattern(regexp = "[0-9]{2}")
    String circuitType;

    @NotBlank
    @NotNull
    String idTrxIssuer;

    String correlationId;

    @NotNull
    BigDecimal amount;

    @Size(max = 3)
    String amountCurrency;

    @NotNull
    @NotBlank
    @Size(max = 5)
    String mcc;

    String acquirerId;

    @NotNull
    @NotBlank
    String merchantId;

    @NotNull
    @NotBlank
    String terminalId;

    @NotNull
    @Pattern(regexp = "([0-9]{6}|[0-9]{8})")
    String bin;

}
