package it.gov.pagopa.bpd.payment_instrument.listener.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerCommandModel;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerData;
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
public class SaveTokenManagerCommandModelFactory implements
        ModelFactory<Pair<byte[], Headers>, TokenManagerCommandModel> {

    private final ObjectMapper objectMapper;

    @Autowired
    public SaveTokenManagerCommandModelFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * @param requestData
     * @return instance of TokenManagerCommandModel, containing a TokenManagerData instance,
     * mapped from the byte[] payload in the requestData, and the inbound Kafka headers
     */

    @SneakyThrows
    @Override
    public TokenManagerCommandModel createModel(Pair<byte[], Headers> requestData) {
        TokenManagerData tokenManagerData = parsePayload(requestData.getLeft());
        return TokenManagerCommandModel.builder()
                .payload(tokenManagerData)
                .headers(requestData.getRight())
                .build();
    }

    /**
     * Method containing the logic for the parsing of the byte[] payload into an instance of TokenManagerData,
     * using the ObjectMapper
     *
     * @param payload inbound JSON payload in byte[] format, defining a TokenManagerData
     * @return instance of TokenManagerData, mapped from the input json byte[] payload
     */
    private TokenManagerData parsePayload(byte[] payload) {
        String json = new String(payload, StandardCharsets.UTF_8);
        try {
            return objectMapper.readValue(json, TokenManagerData.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("Cannot parse the payload as a valid %s",
                            TokenManagerData.class), e);
        }
    }

}
