package it.gov.pagopa.bpd.payment_instrument.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.eventlistener.BaseConsumerAwareEventListener;
import it.gov.pagopa.bpd.payment_instrument.command.UpsertPaymentInstrumentTokensCommand;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentErrorToken;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.ModelFactory;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.TokenPaymentInstrumentErrorModelFactory;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerCommandModel;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
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
 * command to save tokenPan associations
 */

@Service
@Slf4j
public class OnTokenManagerRequestListener extends BaseConsumerAwareEventListener {

    private final TokenPaymentInstrumentErrorModelFactory tokenPaymentInstrumentErrorModelFactory;
    private final PaymentInstrumentService paymentInstrumentService;
    private final ModelFactory<Pair<byte[], Headers>, TokenManagerCommandModel> tokenManagerCommandModelModelFactory;
    private final BeanFactory beanFactory;
    private final ObjectMapper objectMapper;

    @Autowired
    public OnTokenManagerRequestListener(
            TokenPaymentInstrumentErrorModelFactory tokenPaymentInstrumentErrorModelFactory,
            ModelFactory<Pair<byte[], Headers>, TokenManagerCommandModel> tokenManagerCommandModelModelFactory,
            BeanFactory beanFactory,
            ObjectMapper objectMapper,
            PaymentInstrumentService paymentInstrumentService) {
        this.tokenManagerCommandModelModelFactory = tokenManagerCommandModelModelFactory;
        this.beanFactory = beanFactory;
        this.objectMapper = objectMapper;
        this.paymentInstrumentService = paymentInstrumentService;
        this.tokenPaymentInstrumentErrorModelFactory = tokenPaymentInstrumentErrorModelFactory;
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

        TokenManagerCommandModel tokenManagerCommandModel = null;

        try {

            if (log.isDebugEnabled()) {
                log.debug("Processing new request on inbound queue");
            }

            tokenManagerCommandModel = tokenManagerCommandModelModelFactory
                    .createModel(Pair.of(payload, headers));
            UpsertPaymentInstrumentTokensCommand command = beanFactory.getBean(
                    UpsertPaymentInstrumentTokensCommand.class, tokenManagerCommandModel);

            if (!command.execute()) {
                throw new Exception("Failed to execute UpsertPaymentInstrumentTokensCommand");
            }

            if (log.isDebugEnabled()) {
                log.debug("UpsertPaymentInstrumentTokensCommand " +
                        "successfully executed for inbound message");
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

            error = String.format("Unexpected error during message processing: %s, %s",
                    payloadString, e.getMessage());

            if (tokenManagerCommandModel != null &&
                    tokenManagerCommandModel.getPayload() != null) {
                PaymentInstrumentErrorToken paymentInstrumentErrorToken =
                        tokenPaymentInstrumentErrorModelFactory.createModel(payloadString,error);
                paymentInstrumentService.createTokenErrorRecord(paymentInstrumentErrorToken);

            } else if (payload != null) {
                error = String.format("Something gone wrong during the evaluation of the payload: %s, %s",
                        payloadString, e.getMessage());
                logger.error(error, e);
            }

        }
    }

}
