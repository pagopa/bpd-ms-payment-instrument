package it.gov.pagopa.bpd.payment_instrument.listener.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.bpd.payment_instrument.model.DeletePaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.model.DeletePaymentInstrumentCommandModel;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of the ModelFactory interface, that maps a pair containing Kafka related byte[] payload and Headers
 * into a single model for usage inside the microservice core classes
 */

@Component
public class DeletePaymentInstrumentModelFactory implements
        ModelFactory<Pair<byte[], Headers>, DeletePaymentInstrumentCommandModel> {

    private final ObjectMapper objectMapper;

    @Autowired
    public DeletePaymentInstrumentModelFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * @param requestData
     * @return instance of DeletePaymentInstrumentCommandModel, containing a DeletePaymentInstrument instance,
     * mapped from the byte[] payload in the requestData, and the inbound Kafka headers
     */

    @SneakyThrows
    @Override
    public DeletePaymentInstrumentCommandModel createModel(Pair<byte[], Headers> requestData) {
        DeletePaymentInstrument deletePaymentInstrument = parsePayload(requestData.getLeft());
        DeletePaymentInstrumentCommandModel deletePaymentInstrumentCommandModel =
                DeletePaymentInstrumentCommandModel.builder()
                        .payload(deletePaymentInstrument)
                        .headers(requestData.getRight())
                        .build();
        return deletePaymentInstrumentCommandModel;
    }

    /**
     * Method containing the logic for the parsing of the byte[] payload into an instance of DeletePaymentInstrument,
     * using the ObjectMapper
     *
     * @param payload inbound JSON payload in byte[] format, defining a DeletePaymentInstrument
     * @return instance of DeletePaymentInstrument, mapped from the input json byte[] payload
     */
    private DeletePaymentInstrument parsePayload(byte[] payload) {
        String json = new String(payload, StandardCharsets.UTF_8);
        try {
            return objectMapper.readValue(json, DeletePaymentInstrument.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("Cannot parse the payload as a valid %s", DeletePaymentInstrument.class), e);
        }
    }

}
