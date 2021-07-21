package it.gov.pagopa.bpd.payment_instrument.listener.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.CitizenStatusErrorData;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link ModelFactory}, that maps a pair containing Kafka related byte[] payload and Headers
 * into a single model for usage inside the micro-service core classes
 */

@Component
public class CitizenUpdateEventErrorPayloadModelFactory implements
        ModelFactory<CitizenStatusErrorData, byte[]> {

    private final ObjectMapper objectMapper;

    @Autowired
    public CitizenUpdateEventErrorPayloadModelFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @SneakyThrows
    @Override
    public byte[] createModel(CitizenStatusErrorData dto) {
        return objectMapper.writeValueAsBytes(dto);
    }

}