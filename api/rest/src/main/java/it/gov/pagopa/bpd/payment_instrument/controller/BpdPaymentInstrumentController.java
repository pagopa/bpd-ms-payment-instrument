package it.gov.pagopa.bpd.payment_instrument.controller;

import io.swagger.annotations.Api;
import it.gov.pagopa.bpd.payment_instrument.model.dto.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.model.resource.PaymentInstrumentResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to expose MicroService
 */
@Api(tags = "Bonus Pagamenti Digitali payment-instrument Controller")
@RequestMapping("payment-instrument")
public interface BpdPaymentInstrumentController {


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    PaymentInstrumentResource find(@PathVariable("id") String hpan);

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    PaymentInstrumentResource update(@PathVariable("id") String hpan, @RequestBody PaymentInstrumentDTO paymentInstrument);

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") String hpan);
}
