package it.gov.pagopa.bpd.payment_instrument.controller;

import eu.sia.meda.core.controller.StatelessController;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentConverter;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import it.gov.pagopa.bpd.payment_instrument.controller.assembler.ChannelValidationResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.controller.assembler.PaymentInstrumentConverterResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.controller.assembler.PaymentInstrumentHistoryResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.controller.assembler.PaymentInstrumentResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.controller.factory.ModelFactory;
import it.gov.pagopa.bpd.payment_instrument.controller.model.*;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * See {@link BpdPaymentInstrumentController}
 */
@RestController
class BpdPaymentInstrumentControllerImpl extends StatelessController implements BpdPaymentInstrumentController {

    private final PaymentInstrumentService paymentInstrumentService;
    private final ChannelValidationResourceAssembler channelValidationResourceAssembler;
    private final PaymentInstrumentResourceAssembler paymentInstrumentResourceAssembler;
    private final PaymentInstrumentConverterResourceAssembler paymentInstrumentConverterResourceAssembler;
    private final PaymentInstrumentHistoryResourceAssembler paymentInstrumentHistoryResourceAssembler;
    private final ModelFactory<PaymentInstrumentDTO, PaymentInstrument> paymentInstrumentFactory;


    @Autowired
    public BpdPaymentInstrumentControllerImpl(PaymentInstrumentService paymentInstrumentService,
                                              ChannelValidationResourceAssembler channelValidationResourceAssembler,
                                              PaymentInstrumentResourceAssembler paymentInstrumentResourceAssembler,
                                              PaymentInstrumentConverterResourceAssembler paymentInstrumentConverterResourceAssembler,
                                              PaymentInstrumentHistoryResourceAssembler paymentInstrumentHistoryResourceAssembler,
                                              ModelFactory<PaymentInstrumentDTO, PaymentInstrument> paymentInstrumentFactory) {
        this.paymentInstrumentService = paymentInstrumentService;
        this.paymentInstrumentResourceAssembler = paymentInstrumentResourceAssembler;
        this.paymentInstrumentConverterResourceAssembler = paymentInstrumentConverterResourceAssembler;
        this.paymentInstrumentHistoryResourceAssembler = paymentInstrumentHistoryResourceAssembler;
        this.paymentInstrumentFactory = paymentInstrumentFactory;
        this.channelValidationResourceAssembler = channelValidationResourceAssembler;
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


//    @Override
//    public PaymentInstrumentResource update(String hpan, PaymentInstrumentDTO paymentInstrument) {
//        if (logger.isDebugEnabled()) {
//            logger.debug("BpdPaymentInstrumentControllerImpl.createOrUpdate");
//            logger.debug("hpan = [" + hpan + "], paymentInstrument = [" + paymentInstrument + "]");
//        }
//
//        final PaymentInstrumentServiceModel serviceModel = paymentInstrumentFactory.createModel(paymentInstrument);
//
//        return paymentInstrumentResourceAssembler.fromServiceToResource(paymentInstrumentService.createOrUpdate(hpan, serviceModel));
//    }

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
    @Deprecated
    public List<PaymentInstrumentConverterResource> getPaymentInstrumentNumber(String fiscalCode, String channel) {
        List<PaymentInstrumentConverter> pi = paymentInstrumentService.getPaymentInstrument(fiscalCode, channel);
        return paymentInstrumentConverterResourceAssembler.toResource(pi);
    }

    @Override
    public List<PaymentInstrumentHistoryResource> getPaymentInstrumentHistoryDetails(String fiscalCode, String hpan) {
        List<PaymentInstrumentHistory> pih = paymentInstrumentService.findHistory(fiscalCode, hpan);
        return paymentInstrumentHistoryResourceAssembler.toResource(pih);
    }

    @Override
    public ChannelValidationResource validateChannelByFiscalCode(@NotBlank String fiscalCode, @NotBlank String channel) {
        Boolean result = paymentInstrumentService.validateChannel(fiscalCode, channel);
        return channelValidationResourceAssembler.toResource(result);
    }

}
