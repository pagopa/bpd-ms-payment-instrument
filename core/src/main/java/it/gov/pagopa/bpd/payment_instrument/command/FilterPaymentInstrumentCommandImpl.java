package it.gov.pagopa.bpd.payment_instrument.command;

import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentCommandModel;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.OutgoingPaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.PaymentInstrumentUpdate;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import it.gov.pagopa.bpd.payment_instrument.service.TkmPublisherService;
import it.gov.pagopa.bpd.payment_instrument.service.mapper.PaymentInstrumentMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.*;
import java.util.Set;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
class FilterPaymentInstrumentCommandImpl extends BaseCommand<Boolean> implements FilterPaymentInstrumentCommand {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private PaymentInstrumentCommandModel paymentInstrumentCommandModel;
    private TkmPublisherService tkmPublisherService;
    private PaymentInstrumentService paymentInstrumentService;
    private PaymentInstrumentMapper paymentInstrumentMapper;


    public FilterPaymentInstrumentCommandImpl(PaymentInstrumentCommandModel paymentInstrumentCommandModel) {
        this.paymentInstrumentCommandModel = paymentInstrumentCommandModel;
    }

    public FilterPaymentInstrumentCommandImpl(
            PaymentInstrumentCommandModel paymentInstrumentCommandModel,
            TkmPublisherService tkmPublisherService,
            PaymentInstrumentService paymentInstrumentService,
            PaymentInstrumentMapper paymentInstrumentMapper) {
        this.paymentInstrumentCommandModel = paymentInstrumentCommandModel;
        this.tkmPublisherService = tkmPublisherService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.paymentInstrumentMapper = paymentInstrumentMapper;
    }

    public FilterPaymentInstrumentCommandImpl(PaymentInstrumentCommandModel build,
                                              TkmPublisherService tkmPublisherServiceMock,
                                              PaymentInstrumentService paymentInstrumentServiceMock) {
    }

    /**
     * Implementation of the MEDA Command doExecute method, contains the logic for the inbound transaction
     * management, calls the REST endpoint to check if it the related paymentInstrument is active, and eventually
     * sends the Transaction to the proper outbound channel. In case of an error, send a
     *
     * @return boolean to indicate if the command is succesfully executed
     */

    @SneakyThrows
    @Override
    public Boolean doExecute() {

        PaymentInstrumentUpdate pi = paymentInstrumentCommandModel.getPayload();

        try {

            validateRequest(pi);

            PaymentInstrument paymentInstrument = paymentInstrumentService.findByPar(pi.getPar());

            if (paymentInstrument != null) {

                if (paymentInstrument.getHpanMaster() != null && !paymentInstrument.getHpanMaster().equals("")) {
                    paymentInstrument.setHpan(pi.getHpan());
                    paymentInstrument.setActivationDate(paymentInstrument.getParActivationDate());
                } else {
                    paymentInstrument.setHpanMaster(paymentInstrument.getHpan());
                    paymentInstrument.setHpan(pi.getHpan());
                    paymentInstrument.setActivationDate(paymentInstrument.getParActivationDate());
                }

                paymentInstrumentService.createOrUpdate(pi.getHpan(), paymentInstrument);

                OutgoingPaymentInstrument outgoingPaymentInstrument = paymentInstrumentMapper.map(pi);
                outgoingPaymentInstrument.setHpanMaster(paymentInstrument.getHpanMaster());
                tkmPublisherService.publishTkmEvent(outgoingPaymentInstrument);

            } else {
                log.info("Impossible to save payment instrument. [{}, {}]",
                        pi.getPar(), pi.getHpan());
            }

            return true;

        } catch (Exception e) {

            if (pi != null) {

                if (logger.isErrorEnabled()) {
                    logger.error("Error occured during processing payment Instrument: " +
                            pi.getHpan() + ", " +
                            pi.getPar());
                    logger.error(e.getMessage(), e);
                }

            }

            throw e;

        }

    }


    @Autowired
    public void setTkmPublisherService(TkmPublisherService tkmPublisherService) {
        this.tkmPublisherService = tkmPublisherService;
    }

    @Autowired
    public void setPaymentInstrumentService(PaymentInstrumentService paymentInstrumentService) {
        this.paymentInstrumentService = paymentInstrumentService;
    }

    @Autowired
    public void setPaymentInstrumentMapper(PaymentInstrumentMapper paymentInstrumentMapper) {
        this.paymentInstrumentMapper = paymentInstrumentMapper;
    }


    /**
     * Method to process a validation check for the parsed Transaction request
     *
     * @param request instance of Transaction, parsed from the inbound byte[] payload
     * @throws ConstraintViolationException
     */
    private void validateRequest(PaymentInstrumentUpdate request) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if (constraintViolations.size() > 0) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
