package it.gov.pagopa.bpd.payment_instrument.service;

import eu.sia.meda.event.transformer.SimpleEventRequestTransformer;
import eu.sia.meda.event.transformer.SimpleEventResponseTransformer;
import it.gov.pagopa.bpd.payment_instrument.publisher.PointTransactionPublisherConnector;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.OutgoingTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the PointTransactionPublisherService, defines the service used for the interaction
 * with the PointTransactionPublisherConnector
 */

@Service
class PointTransactionPublisherServiceImpl implements PointTransactionPublisherService {

    private final PointTransactionPublisherConnector pointTransactionPublisherConnector;
    private final SimpleEventRequestTransformer<OutgoingTransaction> simpleEventRequestTransformer;
    private final SimpleEventResponseTransformer simpleEventResponseTransformer;

    @Autowired
    public PointTransactionPublisherServiceImpl(PointTransactionPublisherConnector pointTransactionPublisherConnector,
                                                SimpleEventRequestTransformer<OutgoingTransaction> simpleEventRequestTransformer,
                                                SimpleEventResponseTransformer simpleEventResponseTransformer) {
        this.pointTransactionPublisherConnector = pointTransactionPublisherConnector;
        this.simpleEventRequestTransformer = simpleEventRequestTransformer;
        this.simpleEventResponseTransformer = simpleEventResponseTransformer;
    }

    /**
     * Calls the PointTransactionPublisherService, passing the transaction to be used as message payload
     *
     * @param outgoingTransaction OutgoingTransaction instance to be used as payload for the outbound channel used bu the related connector
     */

    @Override
    public void publishPointTransactionEvent(OutgoingTransaction outgoingTransaction) {
        pointTransactionPublisherConnector.doCall(
                outgoingTransaction, simpleEventRequestTransformer, simpleEventResponseTransformer);
    }
}
