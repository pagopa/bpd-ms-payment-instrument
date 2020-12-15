package it.gov.pagopa.bpd.payment_instrument.controller;

import eu.sia.meda.core.controller.StatelessController;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentConverter;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.controller.assembler.PaymentInstrumentConverterResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.controller.assembler.PaymentInstrumentResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.controller.factory.ModelFactory;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentConverterResource;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentResource;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentServiceModel;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * See {@link BpdPaymentInstrumentController}
 */
@RestController
class BpdPaymentInstrumentControllerImpl extends StatelessController implements BpdPaymentInstrumentController {

    private final PaymentInstrumentService paymentInstrumentService;
    private final PaymentInstrumentResourceAssembler paymentInstrumentResourceAssembler;
    private final PaymentInstrumentConverterResourceAssembler paymentInstrumentConverterResourceAssembler;
    private final ModelFactory<PaymentInstrumentDTO, PaymentInstrumentServiceModel> paymentInstrumentFactory;


    @Autowired
    public BpdPaymentInstrumentControllerImpl(PaymentInstrumentService paymentInstrumentService,
                                              PaymentInstrumentResourceAssembler paymentInstrumentResourceAssembler,
                                              PaymentInstrumentConverterResourceAssembler paymentInstrumentConverterResourceAssembler,
                                              ModelFactory<PaymentInstrumentDTO, PaymentInstrumentServiceModel> paymentInstrumentFactory) {
        this.paymentInstrumentService = paymentInstrumentService;
        this.paymentInstrumentResourceAssembler = paymentInstrumentResourceAssembler;
        this.paymentInstrumentConverterResourceAssembler = paymentInstrumentConverterResourceAssembler;
        this.paymentInstrumentFactory = paymentInstrumentFactory;
    }


    @Override
    public PaymentInstrumentResource find(String hpan, String fiscalCode) {
        if (logger.isDebugEnabled()) {
            logger.debug("BpdPaymentInstrumentControllerImpl.find");
            logger.debug("hpan = [" + hpan + "]");
            logger.debug("fiscalCode = [" + fiscalCode + "]");
        }

        final PaymentInstrument entity = paymentInstrumentService.find(hpan, fiscalCode);

        return paymentInstrumentResourceAssembler.toResource(entity);
    }


    @Override
    public PaymentInstrumentResource update(String hpan, PaymentInstrumentDTO paymentInstrument) {
        if (logger.isDebugEnabled()) {
            logger.debug("BpdPaymentInstrumentControllerImpl.createOrUpdate");
            logger.debug("hpan = [" + hpan + "], paymentInstrument = [" + paymentInstrument + "]");
        }

        final PaymentInstrumentServiceModel serviceModel = paymentInstrumentFactory.createModel(paymentInstrument);

        return paymentInstrumentResourceAssembler.fromServiceToResource(paymentInstrumentService.createOrUpdate(hpan, serviceModel));
    }


    @Override
    public void delete(String hpan, String fiscalCode, OffsetDateTime cancellationDate) {
        if (logger.isDebugEnabled()) {
            logger.debug("BpdPaymentInstrumentControllerImpl.delete");
            logger.debug("hpan = [" + hpan + "]");
        }

        paymentInstrumentService.delete(hpan, fiscalCode, cancellationDate);
    }

    @Override
    public void rollback(String fiscalCode, OffsetDateTime requestTimestamp) {
        if (logger.isDebugEnabled()) {
            logger.debug("BpdPaymentInstrumentControllerImpl.rollback");
            logger.debug("fiscalCode = [" + fiscalCode + "], requestTimestamp = [" + requestTimestamp + "]");
        }
        paymentInstrumentService.reactivateForRollback(fiscalCode, requestTimestamp);
    }

    @Override
    public void deleteByFiscalCode(String fiscalCode, String channel) {
        if (logger.isDebugEnabled()) {
            logger.debug("BpdPaymentInstrumentControllerImpl.deleteByFiscalCode");
            logger.debug("fiscalCode = [" + fiscalCode + "], channel = [" + channel + "]");
        }

        paymentInstrumentService.deleteByFiscalCode(fiscalCode, channel);
    }


    @Override
    public boolean checkActive(String hpan, OffsetDateTime accountingDate) {
        if (logger.isDebugEnabled()) {
            logger.debug("BpdPaymentInstrumentControllerImpl.checkActive");
            logger.debug("hpan = [" + hpan + "], accountingDate = [" + accountingDate + "]");
        }

        return paymentInstrumentService.checkActive(hpan, accountingDate) != null;
    }


    @Override
    public List<PaymentInstrumentConverterResource> getPaymentInstrumentNumber(String fiscalCode, String channel) {
        List<PaymentInstrumentConverter> pi = paymentInstrumentService.getPaymentInstrument(fiscalCode, channel);
        return paymentInstrumentConverterResourceAssembler.toResource(pi);
    }

}
