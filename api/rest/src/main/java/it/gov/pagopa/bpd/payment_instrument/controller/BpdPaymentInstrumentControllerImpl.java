package it.gov.pagopa.bpd.payment_instrument.controller;

import eu.sia.meda.core.controller.StatelessController;
import it.gov.pagopa.bpd.payment_instrument.assembler.PaymentInstrumentResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.factory.ModelFactory;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentResource;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
class BpdPaymentInstrumentControllerImpl extends StatelessController implements BpdPaymentInstrumentController {

    private final PaymentInstrumentService paymentInstrumentService;
    private final PaymentInstrumentResourceAssembler paymentInstrumentResourceAssembler;
    private final ModelFactory<PaymentInstrumentDTO, PaymentInstrument> paymentInstrumentFactory;


    @Autowired
    public BpdPaymentInstrumentControllerImpl(PaymentInstrumentService paymentInstrumentService,
                                              PaymentInstrumentResourceAssembler paymentInstrumentResourceAssembler,
                                              ModelFactory<PaymentInstrumentDTO, PaymentInstrument> paymentInstrumentFactory) {
        this.paymentInstrumentService = paymentInstrumentService;
        this.paymentInstrumentResourceAssembler = paymentInstrumentResourceAssembler;
        this.paymentInstrumentFactory = paymentInstrumentFactory;
    }


    @Override
    public PaymentInstrumentResource find(String hpan) {
        if (logger.isDebugEnabled()) {
            logger.debug("BpdPaymentInstrumentControllerImpl.find");
            logger.debug("hpan = [" + hpan + "]");
        }

        final PaymentInstrument entity = paymentInstrumentService.find(hpan);

        return paymentInstrumentResourceAssembler.toResource(entity);
    }


    @Override
    public PaymentInstrumentResource update(String hpan, PaymentInstrumentDTO paymentInstrument) {
        if (logger.isDebugEnabled()) {
            logger.debug("BpdPaymentInstrumentControllerImpl.createOrUpdate");
            logger.debug("hpan = [" + hpan + "], paymentInstrument = [" + paymentInstrument + "]");
        }

        final PaymentInstrument entity = paymentInstrumentFactory.createModel(paymentInstrument);

        PaymentInstrument paymentInstrumentEntity = paymentInstrumentService.createOrUpdate(hpan, entity);

        return paymentInstrumentResourceAssembler.toResource(paymentInstrumentEntity);
    }


    @Override
    public void delete(String hpan) {
        if (logger.isDebugEnabled()) {
            logger.debug("BpdPaymentInstrumentControllerImpl.delete");
            logger.debug("hpan = [" + hpan + "]");
        }

        paymentInstrumentService.delete(hpan);
    }


    @Override
    public boolean checkActive(String hpan, OffsetDateTime accountingDate) {
        if (logger.isDebugEnabled()) {
            logger.debug("BpdPaymentInstrumentControllerImpl.checkActive");
            logger.debug("hpan = [" + hpan + "], accountingDate = [" + accountingDate + "]");
        }

        return paymentInstrumentService.checkActive(hpan, accountingDate);
    }

}
