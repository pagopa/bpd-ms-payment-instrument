package it.gov.pagopa.bpd.payment_instrument.service;

import eu.sia.meda.event.transformer.SimpleEventRequestTransformer;
import eu.sia.meda.event.transformer.SimpleEventResponseTransformer;
import it.gov.pagopa.bpd.payment_instrument.encryption.EncryptUtil;
import it.gov.pagopa.bpd.payment_instrument.publisher.TkmPublisherConnector;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.OutgoingPaymentInstrument;
import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.NoSuchProviderException;

/**
 * Implementation of the TkmPublisherService, defines the service used for the interaction
 * with the TkmPublisherConnector
 */

@Service
class TkmPublisherServiceImpl implements TkmPublisherService {

    private final TkmPublisherConnector tkmPublisherConnector;
    private final SimpleEventRequestTransformer<ByteArrayOutputStream> simpleEventRequestTransformer;
    private final SimpleEventResponseTransformer simpleEventResponseTransformer;
    private final String publicKey;

    @Autowired
    public TkmPublisherServiceImpl(TkmPublisherConnector tkmPublisherConnector,
                                   SimpleEventRequestTransformer<ByteArrayOutputStream> simpleEventRequestTransformer,
                                   SimpleEventResponseTransformer simpleEventResponseTransformer,
                                   @Value("${core.FilterPaymentInstrumentCommand.publicKey}") String publicKey) {
        this.tkmPublisherConnector = tkmPublisherConnector;
        this.simpleEventRequestTransformer = simpleEventRequestTransformer;
        this.simpleEventResponseTransformer = simpleEventResponseTransformer;
        this.publicKey = publicKey;

    }

    /**
     * Calls the TkmPublisherService, passing the transaction to be used as message payload
     *
     * @param outgoingPaymentInstrument OutgoingTransaction instance to be used as payload for the outbound channel used bu the related connector
     */

    @Override
    public void publishTkmEvent(ByteArrayOutputStream outgoingPaymentInstrument) {
        tkmPublisherConnector.doCall(
                outgoingPaymentInstrument, simpleEventRequestTransformer, simpleEventResponseTransformer);
    }

    @Override
    public ByteArrayOutputStream cryptOutgoingPaymentInstrument(OutgoingPaymentInstrument outgoingPaymentInstrument) throws IOException, PGPException, NoSuchProviderException {
        String publicKeyWithLineBreaks = publicKey.replace("\\n", System.lineSeparator());
        InputStream publicKeyIS = new ByteArrayInputStream(publicKeyWithLineBreaks.getBytes());
        ByteArrayOutputStream outPi = new ByteArrayOutputStream();
        ByteArrayInputStream inPi = serializeObject(outgoingPaymentInstrument);

        try (ByteArrayOutputStream outputPi = new ByteArrayOutputStream()) {
            return EncryptUtil.encryptOutgoingPaymentInstrument(outputPi,
                    EncryptUtil.readPublicKey(publicKeyIS),
                    inPi,
                    false, true);
        }
    }

    private ByteArrayInputStream serializeObject(OutgoingPaymentInstrument outgoingPaymentInstrument) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
        oos.writeObject(outgoingPaymentInstrument);
        oos.flush();
        byte[] bytes = bytesOut.toByteArray();
        bytesOut.close();
        oos.close();
        return new ByteArrayInputStream(bytes);
    }

}
