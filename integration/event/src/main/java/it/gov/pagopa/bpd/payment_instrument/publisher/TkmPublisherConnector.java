package it.gov.pagopa.bpd.payment_instrument.publisher;

import eu.sia.meda.event.BaseEventConnector;
import eu.sia.meda.event.transformer.IEventRequestTransformer;
import eu.sia.meda.event.transformer.IEventResponseTransformer;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.OutgoingPaymentInstrument;
import org.springframework.stereotype.Service;

@Service
public class TkmPublisherConnector
        extends BaseEventConnector<OutgoingPaymentInstrument, Boolean, OutgoingPaymentInstrument, Void> {

    /**
     * @param outgoingPaymentInstrument OutgoingPaymentInstrument instance to be used as message content
     * @param requestTransformer        Trannsformer for the request data
     * @param responseTransformer       Transformer for the call response
     * @param args                      Additional args to be used in the call
     * @return Exit status for the call
     */
    public Boolean doCall(
            OutgoingPaymentInstrument outgoingPaymentInstrument, IEventRequestTransformer<OutgoingPaymentInstrument,
            OutgoingPaymentInstrument> requestTransformer,
            IEventResponseTransformer<Void, Boolean> responseTransformer,
            Object... args) {
        return this.call(outgoingPaymentInstrument, requestTransformer, responseTransformer, args);
    }
}
