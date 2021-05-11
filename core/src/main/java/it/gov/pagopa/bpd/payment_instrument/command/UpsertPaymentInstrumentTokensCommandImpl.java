package it.gov.pagopa.bpd.payment_instrument.command;

import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerCommandModel;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerData;
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
import org.springframework.transaction.annotation.Transactional;

import javax.validation.*;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * Base implementation of the SaveTransactionCommandInterface, extending Meda BaseCommand class, the command
 * represents the class interacted with at api level, hiding the multiple calls to the integration connectors
 */

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
class UpsertPaymentInstrumentTokensCommandImpl extends BaseCommand<Boolean> implements FilterTransactionCommand {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private TokenManagerCommandModel tokenManagerCommandModel;
    private PaymentInstrumentService paymentInstrumentService;
    private TransactionMapper transactionMapper;


    public UpsertPaymentInstrumentTokensCommandImpl(TokenManagerCommandModel tokenManagerCommandModel) {
        this.tokenManagerCommandModel = tokenManagerCommandModel;
    }

    public UpsertPaymentInstrumentTokensCommandImpl(
            TokenManagerCommandModel tokenManagerCommandModel,
            PaymentInstrumentService paymentInstrumentService,
            TransactionMapper transactionMapper) {
        this.tokenManagerCommandModel = tokenManagerCommandModel;
        this.paymentInstrumentService = paymentInstrumentService;
        this.transactionMapper = transactionMapper;
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

        TokenManagerData tokenManagerData = tokenManagerCommandModel.getPayload();

        try {

            validateRequest(tokenManagerData);
            return paymentInstrumentService.manageTokenData(tokenManagerData);

        } catch (Exception e) {

            if (tokenManagerData != null) {

                if (logger.isErrorEnabled()) {
                    logger.error("Error occured during processing for tokenData");
                    logger.error(e.getMessage(), e);
                }

            }

            throw e;

        }

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
    private void validateRequest(TokenManagerData request) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if (constraintViolations.size() > 0) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

}
