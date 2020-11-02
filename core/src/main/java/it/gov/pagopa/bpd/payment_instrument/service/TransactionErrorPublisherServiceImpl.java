package it.gov.pagopa.bpd.payment_instrument.service;

import eu.sia.meda.event.BaseEventConnector;
import eu.sia.meda.event.service.BaseErrorPublisherService;
import eu.sia.meda.event.transformer.ErrorEventRequestTransformer;
import eu.sia.meda.event.transformer.SimpleEventResponseTransformer;
import it.gov.pagopa.bpd.payment_instrument.publisher.TransactionErrorPublisherConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the TransactionErrorPublisherService, extends the BaseErrorPublisherService,
 * the connector has the responsibility to send error related messages on the appropriate queue
 */

@Service
class TransactionErrorPublisherServiceImpl
        extends BaseErrorPublisherService
        implements TransactionErrorPublisherService {


    private final TransactionErrorPublisherConnector transactionErrorPublisherConnector;

    @Autowired
    public TransactionErrorPublisherServiceImpl(
            TransactionErrorPublisherConnector transactionErrorPublisherConnector,
            ErrorEventRequestTransformer errorEventRequestTransformer,
            SimpleEventResponseTransformer simpleEventResponseTransformer) {
        super(errorEventRequestTransformer, simpleEventResponseTransformer);
        this.transactionErrorPublisherConnector = transactionErrorPublisherConnector;
    }

    @Override
    protected BaseEventConnector<byte[], Boolean, byte[], Void> getErrorPublisherConnector() {
        return transactionErrorPublisherConnector;
    }


}
