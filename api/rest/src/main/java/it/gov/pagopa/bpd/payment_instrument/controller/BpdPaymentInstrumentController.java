package it.gov.pagopa.bpd.payment_instrument.controller;

import io.swagger.annotations.Api;
import it.gov.pagopa.bpd.payment_instrument.model.dto.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.model.resource.PaymentInstrumentResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

/**
 * Controller to expose MicroService
 */
@Api(tags = "Bonus Pagamenti Digitali payment-instrument Controller")
@RequestMapping("/bpd/payment-instruments")
public interface BpdPaymentInstrumentController {


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    PaymentInstrumentResource find(@PathVariable("id") String hpan);

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    PaymentInstrumentResource update(@PathVariable("id") String hpan, @RequestBody PaymentInstrumentDTO paymentInstrument);

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") String hpan);

    @GetMapping(value = "/{id}/history", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    boolean checkActive(@PathVariable("id") String hpan, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime accountingDate);


}
