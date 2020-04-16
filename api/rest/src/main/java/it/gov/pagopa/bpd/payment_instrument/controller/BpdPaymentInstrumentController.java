package it.gov.pagopa.bpd.payment_instrument.controller;

import io.swagger.annotations.Api;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentResource;
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
    PaymentInstrumentResource find(@PathVariable("id") @Valid @NotBlank String hpan);

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    PaymentInstrumentResource update(@PathVariable("id") @Valid @NotBlank String hpan, @RequestBody @Valid @NotBlank PaymentInstrumentDTO paymentInstrument);

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") @Valid @NotBlank String hpan);

    @GetMapping(value = "/{id}/history", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    boolean checkActive(@PathVariable("id") @Valid @NotBlank String hpan, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Valid @NotBlank OffsetDateTime accountingDate);


}
