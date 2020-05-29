package it.gov.pagopa.bpd.payment_instrument.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

/**
 * Controller to expose MicroService
 */
@Api(tags = "Bonus Pagamenti Digitali payment-instrument Controller")
@RequestMapping("/bpd/payment-instruments")
public interface BpdPaymentInstrumentController {


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    PaymentInstrumentResource find(
            @ApiParam(value = "${swagger.paymentInstrument.hpan}", required = true)
            @PathVariable("id")
            @NotBlank
                    String hpan
    );

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    PaymentInstrumentResource update(
            @ApiParam(value = "${swagger.paymentInstrument.hpan}", required = true)
            @PathVariable("id")
            @NotBlank
                    String hpan,
            @RequestBody @Valid PaymentInstrumentDTO paymentInstrument);

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(
            @ApiParam(value = "${swagger.paymentInstrument.hpan}", required = true)
            @PathVariable("id")
            @NotBlank
                    String hpan
    );

    @GetMapping(value = "/{id}/history/active", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    boolean checkActive(
            @ApiParam(value = "${swagger.paymentInstrument.hpan}", required = true)
            @PathVariable("id")
            @NotBlank
                    String hpan,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime accountingDate);


}
