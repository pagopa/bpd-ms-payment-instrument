package it.gov.pagopa.bpd.payment_instrument.controller;

import eu.sia.meda.core.controller.StatelessController;
import it.gov.pagopa.bpd.payment_instrument.assembler.PaymentInstrumentResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.factory.ModelFactory;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentResource;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentDAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Optional;

@RestController
class BpdPaymentInstrumentControllerImpl extends StatelessController implements BpdPaymentInstrumentController {

    private final PaymentInstrumentDAOService paymentInstrumentDAOService;
    private final PaymentInstrumentResourceAssembler paymentInstrumentResourceAssembler;
    private final ModelFactory<PaymentInstrumentDTO, PaymentInstrument> paymentInstrumentFactory;


    @Autowired
    public BpdPaymentInstrumentControllerImpl(PaymentInstrumentDAOService paymentInstrumentDAOService,
                                              PaymentInstrumentResourceAssembler paymentInstrumentResourceAssembler,
                                              ModelFactory<PaymentInstrumentDTO, PaymentInstrument> paymentInstrumentFactory) {
        this.paymentInstrumentDAOService = paymentInstrumentDAOService;
        this.paymentInstrumentResourceAssembler = paymentInstrumentResourceAssembler;
        this.paymentInstrumentFactory = paymentInstrumentFactory;
    }

    @Override
    public PaymentInstrumentResource find(String hpan) {
        logger.debug("Start find by hpan");
        logger.debug("hpan = [" + hpan + "]");

        final Optional<PaymentInstrument> entity = paymentInstrumentDAOService.find(hpan);

        return paymentInstrumentResourceAssembler.toResource(entity.get());
    }

    @Override
    public PaymentInstrumentResource update(String hpan, PaymentInstrumentDTO paymentInstrument) {
        logger.debug("Start update");

        final PaymentInstrument entity = paymentInstrumentFactory.createModel(paymentInstrument);
        entity.setHpan(hpan);
        entity.setStatus(PaymentInstrument.Status.ACTIVE);

        PaymentInstrument paymentInstrumentEntity = paymentInstrumentDAOService.update(hpan, entity);
        return paymentInstrumentResourceAssembler.toResource(paymentInstrumentEntity);
    }

    @Override
    public void delete(String hpan) {
        logger.debug("Start delete");
        logger.debug("fiscalCode = [" + hpan + "]");

        paymentInstrumentDAOService.delete(hpan);

    }

    @Override
    public boolean checkActive(String hpan, OffsetDateTime accountingDate) {
        logger.debug("Start checkout");
        return paymentInstrumentDAOService.checkActive(hpan, accountingDate);
    }
}
