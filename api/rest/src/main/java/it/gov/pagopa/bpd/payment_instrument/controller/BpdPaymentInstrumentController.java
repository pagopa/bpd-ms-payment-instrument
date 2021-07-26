package it.gov.pagopa.bpd.payment_instrument.controller;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import it.gov.pagopa.bpd.common.annotation.UpperCase;
import it.gov.pagopa.bpd.common.converter.UpperCaseConverter;
import it.gov.pagopa.bpd.common.util.Constants;
import it.gov.pagopa.bpd.payment_instrument.controller.model.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Controller to expose MicroService
 */
@Api(tags = "Bonus Pagamenti Digitali payment-instrument Controller")
@RequestMapping("/bpd/payment-instruments")
public interface BpdPaymentInstrumentController {

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    PaymentInstrumentResource find(
            @ApiParam(value = "${swagger.paymentInstrument.hpan}", required = true)
            @PathVariable("id")
            @NotBlank
                    String hpan,
            @ApiParam(value = "${swagger.paymentInstrument.fiscalCode}", required = false)
            @RequestParam(value = "fiscalCode", required = false)
            @Size(min = 16, max = 16)
            @JsonDeserialize(converter = UpperCaseConverter.class)
            @Pattern(regexp = Constants.FISCAL_CODE_REGEX)
                    String fiscalCode
    );

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    PaymentInstrumentResource update(
            @ApiParam(value = "${swagger.paymentInstrument.hpan}", required = true)
            @PathVariable("id")
            @NotBlank
                    String hpan,
            @RequestBody @Valid PaymentInstrumentDTO paymentInstrument);

    @DeleteMapping(value = "/fiscal-code/{id}/{channel}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteByFiscalCode(
            @ApiParam(value = "${swagger.paymentInstrument.fiscalCode}", required = true)
            @PathVariable("id")
            @NotBlank String fiscalCode,
            @ApiParam(value = "${swagger.paymentInstrument.channel}", required = true)
            @PathVariable("channel")
            @NotBlank String channel
    );

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(
            @ApiParam(value = "${swagger.paymentInstrument.hpan}", required = true)
            @PathVariable("id")
            @NotBlank
                    String hpan,
            @ApiParam(value = "${swagger.paymentInstrument.fiscalCode}", required = false)
            @RequestParam(required = false)
            @Valid @Size(min = 16, max = 16) @Pattern(regexp = Constants.FISCAL_CODE_REGEX)
                    String fiscalCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    OffsetDateTime cancellationDate
    );

    @PutMapping(value = "/rollback/{fiscalCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void rollback(
            @ApiParam(required = true)
            @PathVariable("fiscalCode")
            @NotBlank
                    String fiscalCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    OffsetDateTime requestTimestamp
    );

    @GetMapping(value = "/{id}/history/active", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    boolean checkActive(
            @ApiParam(value = "${swagger.paymentInstrument.hpan}", required = true)
            @PathVariable("id")
            @NotBlank
                    String hpan,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime accountingDate
    );

    @GetMapping(value = "/number/{fiscalCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Deprecated
    List<PaymentInstrumentConverterResource> getPaymentInstrumentNumber(
            @ApiParam(value = "${swagger.paymentInstrument.fiscalCode}", required = true)
            @PathVariable("fiscalCode") @UpperCase
            @Valid @NotBlank @Size(min = 16, max = 16) @Pattern(regexp = Constants.FISCAL_CODE_REGEX)
                    String fiscalCode,
            @ApiParam(value = "${swagger.paymentInstrument.channel}", required = false)
            @RequestParam(value = "channel", required = false)
            @Valid
                    String channel
    );

    @GetMapping(value = "/{fiscalCode}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    List<PaymentInstrumentHistoryResource> getPaymentInstrumentHistoryDetails(
            @ApiParam(value = "${swagger.paymentInstrument.fiscalCode}", required = true)
            @PathVariable("fiscalCode") @UpperCase
            @Valid @NotBlank @Size(min = 16, max = 16) @Pattern(regexp = Constants.FISCAL_CODE_REGEX)
                    String fiscalCode,
            @ApiParam(value = "${swagger.paymentInstrument.hpan}", required = false)
            @RequestParam(value = "hpan", required = false)
            @Valid
                    String hpan
    );

    @PostMapping(value = "/fiscal-code/{id}/{channel}/validate", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ChannelValidationResource validateChannelByFiscalCode(
            @ApiParam(value = "${swagger.paymentInstrument.fiscalCode}", required = true)
            @PathVariable("id")
            @NotBlank String fiscalCode,
            @ApiParam(value = "${swagger.paymentInstrument.channel}", required = true)
            @PathVariable("channel")
            @NotBlank String channel
    );

}
