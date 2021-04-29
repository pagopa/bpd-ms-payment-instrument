package it.gov.pagopa.bpd.payment_instrument.listener.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentErrorDelete;
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
public class DeletePaymentInstrumentErrorModelFactory {

    private final ObjectMapper objectMapper;

    @Autowired
    public DeletePaymentInstrumentErrorModelFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * @param requestData
     * @return instance of DeletePaymentInstrumentCommandModel, containing a DeletePaymentInstrument instance,
     * mapped from the byte[] payload in the requestData, and the inbound Kafka headers
     */

    @SneakyThrows
    public DeletePaymentInstrumentErrorServiceModel createModel(Pair<byte[], Headers> requestData,
                                                                String exceptionDescription) {
        PaymentInstrumentErrorDelete paymentInstrumentErrorDelete = parsePayload(requestData.getLeft());
        paymentInstrumentErrorDelete.setId(UUID.randomUUID().toString());
        paymentInstrumentErrorDelete.setExceptionMessage(exceptionDescription);
        return DeletePaymentInstrumentErrorServiceModel.builder()
                .payload(paymentInstrumentErrorDelete)
                .headers(requestData.getRight())
                .build();
    }

    /**
     * Method containing the logic for the parsing of the byte[] payload into an instance of DeletePaymentInstrument,
     * using the ObjectMapper
     *
     * @param payload inbound JSON payload in byte[] format, defining a DeletePaymentInstrument
     * @return instance of DeletePaymentInstrument, mapped from the input json byte[] payload
     */
    private PaymentInstrumentErrorDelete parsePayload(byte[] payload) {
        String json = new String(payload, StandardCharsets.UTF_8);
        try {
            return objectMapper.readValue(json, PaymentInstrumentErrorDelete.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("Cannot parse the payload as a valid %s", PaymentInstrumentErrorDelete.class), e);
        }
    }

}
