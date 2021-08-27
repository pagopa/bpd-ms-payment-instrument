package it.gov.pagopa.bpd.payment_instrument.listener;

import eu.sia.meda.eventlistener.BaseConsumerAwareEventListener;
import it.gov.pagopa.bpd.payment_instrument.command.DeletePaymentInstrumentCommand;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.DeletePaymentInstrumentErrorModelFactory;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.DeletePaymentInstrumentModelFactory;
import it.gov.pagopa.bpd.payment_instrument.model.DeletePaymentInstrumentCommandModel;
import it.gov.pagopa.bpd.payment_instrument.model.DeletePaymentInstrumentErrorServiceModel;
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
 * command for the check and send logic associated to the Transaction payload
 */

@Service
@Slf4j
public class OnPaymentInstrumentToDeleteListener extends BaseConsumerAwareEventListener {

    private final PaymentInstrumentService paymentInstrumentService;
    private final DeletePaymentInstrumentModelFactory deletePaymentInstrumentModelFactory;
    private final DeletePaymentInstrumentErrorModelFactory deletePaymentInstrumentErrorModelFactory;
    private final BeanFactory beanFactory;


    @Autowired
    public OnPaymentInstrumentToDeleteListener(PaymentInstrumentService paymentInstrumentService,
                                               DeletePaymentInstrumentModelFactory deletePaymentInstrumentModelFactory,
                                               DeletePaymentInstrumentErrorModelFactory deletePaymentInstrumentErrorModelFactory,
                                               BeanFactory beanFactory) {
        this.paymentInstrumentService = paymentInstrumentService;
        this.deletePaymentInstrumentModelFactory = deletePaymentInstrumentModelFactory;
        this.deletePaymentInstrumentErrorModelFactory = deletePaymentInstrumentErrorModelFactory;
        this.beanFactory = beanFactory;
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

        DeletePaymentInstrumentCommandModel deletePaymentInstrumentCommandModel = null;
        DeletePaymentInstrumentErrorServiceModel deletePaymentInstrumentErrorServiceModel = null;


        try {
            if (log.isDebugEnabled()) {
                log.debug("Processing new request on inbound queue");
            }


            deletePaymentInstrumentCommandModel = deletePaymentInstrumentModelFactory
                    .createModel(Pair.of(payload, headers));
            DeletePaymentInstrumentCommand command = beanFactory.getBean(
                    DeletePaymentInstrumentCommand.class, deletePaymentInstrumentCommandModel);

            if (!command.execute()) {
                throw new Exception("Failed to execute DeletePaymentInstrumentCommand");
            }


            if (log.isDebugEnabled()) {
                log.debug("DeletePaymentInstrumentCommand successfully executed for inbound message");
            }

        } catch (Exception e) {

            if (logger.isErrorEnabled()) {
                logger.error("Something gone wrong deleting payment instrument", e);
            }

            String payloadString = "null";
            String error = "Unexpected error during message processing";

            try {
                payloadString = new String(payload, StandardCharsets.UTF_8);
            } catch (Exception e2) {
                if (logger.isErrorEnabled()) {
                    logger.error("Something gone wrong converting the payload into String", e2);
                }
            }

            error = String.format("Unexpected error during message processing: %s, %s",
                    payloadString, e.getMessage());
            deletePaymentInstrumentErrorServiceModel = deletePaymentInstrumentErrorModelFactory
                    .createModel(Pair.of(payload, headers), error);

            if (deletePaymentInstrumentErrorServiceModel != null &&
                    deletePaymentInstrumentErrorServiceModel.getPayload() != null) {
                paymentInstrumentService.createDeleteErrorRecord(deletePaymentInstrumentErrorServiceModel.getPayload());

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
