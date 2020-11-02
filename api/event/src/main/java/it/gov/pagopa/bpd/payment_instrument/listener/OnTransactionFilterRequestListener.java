package it.gov.pagopa.bpd.payment_instrument.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.eventlistener.BaseConsumerAwareEventListener;
import it.gov.pagopa.bpd.payment_instrument.command.FilterTransactionCommand;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.ModelFactory;
import it.gov.pagopa.bpd.payment_instrument.model.TransactionCommandModel;
import it.gov.pagopa.bpd.payment_instrument.service.TransactionErrorPublisherService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Class Extending the MEDA BaseEventListener, manages the inbound requests, and calls on the appropriate
 * command for the check and send logic associated to the Transaction payload
 */

@Service
@Slf4j
public class OnTransactionFilterRequestListener extends BaseConsumerAwareEventListener {

    private final TransactionErrorPublisherService transactionErrorPublisherService;
    private final ModelFactory<Pair<byte[], Headers>, TransactionCommandModel> saveTransactionCommandModelFactory;
    private final BeanFactory beanFactory;
    private final ObjectMapper objectMapper;

    @Autowired
    public OnTransactionFilterRequestListener(
            TransactionErrorPublisherService transactionErrorPublisherService,
            ModelFactory<Pair<byte[], Headers>, TransactionCommandModel> saveTransactionCommandModelFactory,
            BeanFactory beanFactory,
            ObjectMapper objectMapper) {
        this.transactionErrorPublisherService = transactionErrorPublisherService;
        this.saveTransactionCommandModelFactory = saveTransactionCommandModelFactory;
        this.beanFactory = beanFactory;
        this.objectMapper = objectMapper;
    }

    /**
     * Method called on receiving a message in the inbound queue,
     * that should contain a JSON payload containing transaction data,
     * calls on a command to execute the check and send logic for the input Transaction data
     * In case of error, sends data to an error channel
     *
     * @param payload Message JSON payload in byte[] format
     * @param headers Kafka headers from the inbound message
     */

    @SneakyThrows
    @Override
    public void onReceived(byte[] payload, Headers headers) {

        TransactionCommandModel transactionCommandModel = null;

        try {

            if (log.isDebugEnabled()) {
                log.debug("Processing new request on inbound queue");
            }

            transactionCommandModel = saveTransactionCommandModelFactory
                    .createModel(Pair.of(payload, headers));
            FilterTransactionCommand command = beanFactory.getBean(
                    FilterTransactionCommand.class, transactionCommandModel);

            if (!command.execute()) {
                throw new Exception("Failed to execute FilterTransactionCommand");
            }

            if (log.isDebugEnabled()) {
                log.debug("FilterTransactionCommand successfully executed for inbound message");
            }

        } catch (Exception e) {

            String payloadString = "null";
            String error = "Unexpected error during transaction processing";

            try {
                payloadString = new String(payload, StandardCharsets.UTF_8);
            } catch (Exception e2) {
                if (logger.isErrorEnabled()) {
                    logger.error("Something gone wrong converting the payload into String", e2);
                }
            }

            if (transactionCommandModel != null && transactionCommandModel.getPayload() != null) {
                payloadString = new String(payload, StandardCharsets.UTF_8);
                error = String.format("Unexpected error during transaction processing: %s, %s",
                        payloadString, e.getMessage());
            } else if (payload != null) {
                error = String.format("Something gone wrong during the evaluation of the payload: %s, %s",
                        payloadString, e.getMessage());
                if (logger.isErrorEnabled()) {
                    logger.error(error, e);
                }
            }

            if (!transactionErrorPublisherService.publishErrorEvent(payload, headers, error)) {
                if (log.isErrorEnabled()) {
                    log.error("Could not publish transaction processing error");
                }
                throw e;
            }

        }
    }

}
