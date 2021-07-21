package it.gov.pagopa.bpd.payment_instrument.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
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
import java.time.LocalDateTime;
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

    @JsonProperty("taxCode")
    @Valid @Size(min = 16, max = 16)
    @Pattern(regexp = Constants.FISCAL_CODE_REGEX)
    String fiscalCode;

    @JsonProperty("timestamp")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss:SSSS")
    LocalDateTime cancellationDate;
}
