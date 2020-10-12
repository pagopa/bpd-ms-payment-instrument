package it.gov.pagopa.bpd.payment_instrument.command;

import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.payment_instrument.model.SaveTransactionCommandModel;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.Transaction;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import it.gov.pagopa.bpd.payment_instrument.service.PointTransactionPublisherService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import javax.validation.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Base implementation of the SaveTransactionCommandInterface, extending Meda BaseCommand class, the command
 * represents the class interacted with at api level, hiding the multiple calls to the integration connectors
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
abstract class BaseSaveTransactionCommandImpl extends BaseCommand<Boolean> implements SaveTransactionCommand {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private SaveTransactionCommandModel saveTransactionCommandModel;
    private PointTransactionPublisherService pointTransactionProducerService;
    private PaymentInstrumentService paymentInstrumentService;


    public BaseSaveTransactionCommandImpl(SaveTransactionCommandModel saveTransactionCommandModel) {
        this.saveTransactionCommandModel = saveTransactionCommandModel;
    }

    public BaseSaveTransactionCommandImpl(
            SaveTransactionCommandModel saveTransactionCommandModel,
            PointTransactionPublisherService pointTransactionProducerService,
            PaymentInstrumentService paymentInstrumentService) {
        this.saveTransactionCommandModel = saveTransactionCommandModel;
        this.pointTransactionProducerService = pointTransactionProducerService;
        this.paymentInstrumentService = paymentInstrumentService
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

        Transaction transaction = saveTransactionCommandModel.getPayload();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss.SSSXXXXX");

        try {

            OffsetDateTime exec_start = OffsetDateTime.now();

            validateRequest(transaction);

            try {

                Boolean checkActive = true;

//                    OffsetDateTime check_start = OffsetDateTime.now();
//
//                    Boolean checkActive = paymentInstrumentConnectorService
//                            .checkActive(transaction.getHpan(), transaction.getTrxDate());
//
//                    OffsetDateTime check_end = OffsetDateTime.now();

//                    log.info("Executed checkActive for transaction: {}, {}, {} " +
//                                    "- Started at {}, Ended at {} - Total exec time: {}" ,
//                            transaction.getIdTrxAcquirer(),
//                            transaction.getAcquirerCode(),
//                            transaction.getTrxDate(),
//                            dateTimeFormatter.format(check_start),
//                            dateTimeFormatter.format(check_end),
//                            ChronoUnit.MILLIS.between(check_start, check_end));


                if (checkActive) {

                    OffsetDateTime pub_start = OffsetDateTime.now();

                    pointTransactionProducerService.publishPointTransactionEvent(transaction);

                    OffsetDateTime pub_end = OffsetDateTime.now();

                    log.info("Executed publishing on BPD for transaction: {}, {}, {} " +
                                    "- Started at {}, Ended at {} - Total exec time: {}",
                            transaction.getIdTrxAcquirer(),
                            transaction.getAcquirerCode(),
                            transaction.getTrxDate(),
                            dateTimeFormatter.format(pub_start),
                            dateTimeFormatter.format(pub_end),
                            ChronoUnit.MILLIS.between(pub_start, pub_end));

                } else {
                    log.info("Met a transaction for an inactive payment instrument on BPD.");
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            OffsetDateTime end_exec = OffsetDateTime.now();

            log.info("Executed SaveTransactionCommand for transaction: {}, {}, {} " +
                            "- Started at {}, Ended at {} - Total exec time: {}",
                    transaction.getIdTrxAcquirer(),
                    transaction.getAcquirerCode(),
                    transaction.getTrxDate(),
                    dateTimeFormatter.format(exec_start),
                    dateTimeFormatter.format(end_exec),
                    ChronoUnit.MILLIS.between(exec_start, end_exec));

            return true;

        } catch (Exception e) {

            if (transaction != null) {

                if (logger.isErrorEnabled()) {
                    logger.error("Error occured during processing for transaction: " +
                            transaction.getIdTrxAcquirer() + ", " +
                            transaction.getAcquirerCode() + ", " +
                            transaction.getTrxDate());
                    logger.error(e.getMessage(), e);
                }

            }

            throw e;

        }

    }


    @Autowired
    public void setPointTransactionProducerService(
            PointTransactionPublisherService pointTransactionProducerService) {
        this.pointTransactionProducerService = pointTransactionProducerService;
    }

    @Autowired
    public void setPaymentInstrumentService(PaymentInstrumentService paymentInstrumentService) {
        this.paymentInstrumentService = paymentInstrumentService;
    }


    /**
     * Method to process a validation check for the parsed Transaction request
     *
     * @param request instance of Transaction, parsed from the inbound byte[] payload
     * @throws ConstraintViolationException
     */
    private void validateRequest(Transaction request) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if (constraintViolations.size() > 0) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

}
