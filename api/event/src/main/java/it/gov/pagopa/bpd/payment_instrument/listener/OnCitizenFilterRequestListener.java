package it.gov.pagopa.bpd.payment_instrument.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.eventlistener.BaseConsumerAwareEventListener;
import it.gov.pagopa.bpd.payment_instrument.command.FilterPaymentInstrumentCommand;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.ModelFactory;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentCommandModel;
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
 * command for the check and send logic associated to the PaymentInstrumentUpdate payload
 */

@Service
@Slf4j
public class OnCitizenFilterRequestListener extends BaseConsumerAwareEventListener {

    private final ModelFactory<Pair<byte[], Headers>, PaymentInstrumentCommandModel> savePaymentInstrumentUpdateCommandModelFactory;
    private final BeanFactory beanFactory;
    private final ObjectMapper objectMapper;

    @Autowired
    public OnCitizenFilterRequestListener(
            ModelFactory<Pair<byte[], Headers>, PaymentInstrumentCommandModel> savePaymentInstrumentUpdateCommandModelFactory,
            BeanFactory beanFactory,
            ObjectMapper objectMapper) {
        this.savePaymentInstrumentUpdateCommandModelFactory = savePaymentInstrumentUpdateCommandModelFactory;
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

        PaymentInstrumentCommandModel paymentInstrumentCommandModel = null;

        try {

            if (log.isDebugEnabled()) {
                log.debug("Processing new request on inbound queue");
            }

            paymentInstrumentCommandModel = savePaymentInstrumentUpdateCommandModelFactory
                    .createModel(Pair.of(payload, headers));
            FilterPaymentInstrumentCommand command = beanFactory.getBean(
                    FilterPaymentInstrumentCommand.class, paymentInstrumentCommandModel);

            if (!command.execute()) {
                throw new Exception("Failed to execute FilterPaymentInstrumentCommand");
            }

            if (log.isDebugEnabled()) {
                log.debug("FilterPaymentInstrumentCommand successfully executed for inbound message");
            }

        } catch (Exception e) {

            String payloadString = "null";
            String error = "Unexpected error during payment instrument processing";

            try {
                payloadString = new String(payload, StandardCharsets.UTF_8);
            } catch (Exception e2) {
                if (logger.isErrorEnabled()) {
                    logger.error("Something gone wrong converting the payload into String", e2);
                }
            }

            if (paymentInstrumentCommandModel != null && paymentInstrumentCommandModel.getPayload() != null) {
                payloadString = new String(payload, StandardCharsets.UTF_8);
                error = String.format("Unexpected error during payment instrument processing: %s, %s",
                        payloadString, e.getMessage());
            } else if (payload != null) {
                error = String.format("Something gone wrong during the evaluation of the payload: %s, %s",
                        payloadString, e.getMessage());
                if (logger.isErrorEnabled()) {
                    logger.error(error, e);
                }
            }

        }
    }
}
