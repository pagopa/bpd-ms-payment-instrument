package it.gov.pagopa.bpd.payment_instrument.command;

import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import it.gov.pagopa.bpd.payment_instrument.model.TransactionCommandModel;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.OutgoingTransaction;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.Transaction;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import it.gov.pagopa.bpd.payment_instrument.service.PointTransactionPublisherService;
import it.gov.pagopa.bpd.payment_instrument.service.mapper.TransactionMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.*;
import java.util.Optional;
import java.util.Set;

/**
 * Base implementation of the SaveTransactionCommandInterface, extending Meda BaseCommand class, the command
 * represents the class interacted with at api level, hiding the multiple calls to the integration connectors
 */

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
class FilterTransactionCommandImpl extends BaseCommand<Boolean> implements FilterTransactionCommand {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private TransactionCommandModel transactionCommandModel;
    private PointTransactionPublisherService pointTransactionProducerService;
    private PaymentInstrumentService paymentInstrumentService;
    private TransactionMapper transactionMapper;


    public FilterTransactionCommandImpl(TransactionCommandModel transactionCommandModel) {
        this.transactionCommandModel = transactionCommandModel;
    }

    public FilterTransactionCommandImpl(
            TransactionCommandModel transactionCommandModel,
            PointTransactionPublisherService pointTransactionProducerService,
            PaymentInstrumentService paymentInstrumentService,
            TransactionMapper transactionMapper) {
        this.transactionCommandModel = transactionCommandModel;
        this.pointTransactionProducerService = pointTransactionProducerService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.transactionMapper = transactionMapper;
    }

    public FilterTransactionCommandImpl(TransactionCommandModel build, PointTransactionPublisherService pointTransactionProducerServiceMock, PaymentInstrumentService paymentInstrumentServiceMock) {
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

        Transaction transaction = transactionCommandModel.getPayload();

        try {

            validateRequest(transaction);

            Optional<PaymentInstrument> paymentInstrument = paymentInstrumentService.findByhpan(transaction.getHpan());

            if (paymentInstrument != null && paymentInstrument.isPresent() && (paymentInstrument.get().getHpan().equals(paymentInstrument.get().getHpanMaster())
                    || paymentInstrument.get().getHpanMaster() == null)) {

                PaymentInstrumentHistory checkActive = paymentInstrumentService.checkActive(transaction.getHpan(), transaction.getTrxDate());

                if (checkActive != null) {

                    OutgoingTransaction outgoingTransaction = transactionMapper.map(transaction);
                    outgoingTransaction.setFiscalCode(checkActive.getFiscalCode());
                    outgoingTransaction.setIsToUpdate(false);
                    pointTransactionProducerService.publishPointTransactionEvent(outgoingTransaction);
                } else {
                    log.info("Met a transaction for an inactive payment instrument on BPD. [{}, {}, {}]",
                            transaction.getIdTrxAcquirer(), transaction.getAcquirerCode(), transaction.getTrxDate());
                }

            } else {
                PaymentInstrumentHistory checkActivePar = paymentInstrumentService.checkActivePar(transaction.getPar(), transaction.getTrxDate());

                if (checkActivePar != null) {

                    OutgoingTransaction outgoingTransaction = transactionMapper.map(transaction);
                    outgoingTransaction.setFiscalCode(checkActivePar.getFiscalCode());

                    outgoingTransaction.setIsToUpdate(!paymentInstrument.isPresent());

                    pointTransactionProducerService.publishPointTransactionEvent(outgoingTransaction);

                } else {
                    log.info("Met a transaction for an inactive payment instrument on BPD. [{}, {}, {}]",
                            transaction.getIdTrxAcquirer(), transaction.getAcquirerCode(), transaction.getTrxDate());
                }
            }

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

    @Autowired
    public void setTransactionMapper(TransactionMapper transactionMapper) {
        this.transactionMapper = transactionMapper;
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
