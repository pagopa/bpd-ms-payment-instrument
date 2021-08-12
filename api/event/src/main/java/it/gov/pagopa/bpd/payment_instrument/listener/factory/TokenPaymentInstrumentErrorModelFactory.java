package it.gov.pagopa.bpd.payment_instrument.listener.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentErrorDelete;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentErrorToken;
import it.gov.pagopa.bpd.payment_instrument.model.DeletePaymentInstrumentErrorServiceModel;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Implementation of the ModelFactory interface, that maps a pair containing Kafka related byte[] payload and Headers
 * into a single model for usage inside the microservice core classes
 */

@Component
public class TokenPaymentInstrumentErrorModelFactory {

    public TokenPaymentInstrumentErrorModelFactory() {}

    /**
     * @param payload
     * @return instance of PaymentInstrumentErrorToken, containing a instance,
     * mapped from the byte[] payload in the requestData, and the inbound Kafka headers
     */

    @SneakyThrows
    public PaymentInstrumentErrorToken createModel(String payload,
                                                   String exceptionDescription) {
        PaymentInstrumentErrorToken paymentInstrumentErrorToken = new PaymentInstrumentErrorToken();
        paymentInstrumentErrorToken.setId(UUID.randomUUID().toString());
        paymentInstrumentErrorToken.setExceptionMessage(exceptionDescription);
        paymentInstrumentErrorToken.setTokenData(payload);
        return paymentInstrumentErrorToken;
    }


}
