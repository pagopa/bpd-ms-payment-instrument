package it.gov.pagopa.bpd.payment_instrument.service.mapper;

import eu.sia.meda.event.transformer.SimpleEventRequestTransformer;
import eu.sia.meda.event.transformer.SimpleEventResponseTransformer;
import it.gov.pagopa.bpd.payment_instrument.publisher.TkmPublisherConnector;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.OutgoingPaymentInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the TkmPublisherServiceImpl, defines the service used for the interaction
 * with the TkmPublisherConnector
 */

@Service
public class TkmPublisherServiceImpl implements TkmPublisherService {

    private final TkmPublisherConnector tkmPublisherConnector;
    private final SimpleEventRequestTransformer<OutgoingPaymentInstrument> simpleEventRequestTransformer;
    private final SimpleEventResponseTransformer simpleEventResponseTransformer;

    @Autowired
    public TkmPublisherServiceImpl(TkmPublisherConnector tkmPublisherConnector,
                                   SimpleEventRequestTransformer<OutgoingPaymentInstrument> simpleEventRequestTransformer,
                                   SimpleEventResponseTransformer simpleEventResponseTransformer) {
        this.tkmPublisherConnector = tkmPublisherConnector;
        this.simpleEventRequestTransformer = simpleEventRequestTransformer;
        this.simpleEventResponseTransformer = simpleEventResponseTransformer;
    }

    /**
     * Calls the PointTransactionPublisherService, passing the transaction to be used as message payload
     *
     * @param outgoingPaymentInstrument OutgoingTransaction instance to be used as payload for the outbound channel used bu the related connector
     */

    @Override
    public void publishTkmEvent(OutgoingPaymentInstrument outgoingPaymentInstrument) {
        tkmPublisherConnector.doCall(
                outgoingPaymentInstrument, simpleEventRequestTransformer, simpleEventResponseTransformer);
    }
}
