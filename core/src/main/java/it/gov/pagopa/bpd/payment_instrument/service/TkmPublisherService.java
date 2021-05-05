package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.publisher.model.OutgoingPaymentInstrument;
import org.bouncycastle.openpgp.PGPException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;

/**
 * public interface for the TkmPublisherService
 */

public interface TkmPublisherService {

    /**
     * Method that has the logic for publishing a Transaction to the point-processor outbound channel,
     * calling on the appropriate connector
     *
     * @param outgoingPaymentInstrument OutgoingPaymentInstrument instance to be published
     */
    void publishTkmEvent(ByteArrayOutputStream outgoingPaymentInstrument);

    ByteArrayOutputStream cryptOutgoingPaymentInstrument(OutgoingPaymentInstrument outgoingPaymentInstrument) throws IOException, PGPException, NoSuchProviderException;

}
