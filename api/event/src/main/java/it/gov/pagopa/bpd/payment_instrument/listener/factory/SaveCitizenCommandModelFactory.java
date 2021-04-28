package it.gov.pagopa.bpd.payment_instrument.listener.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentCommandModel;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.PaymentInstrumentUpdate;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SaveCitizenCommandModelFactory implements
        ModelFactory<Pair<byte[], Headers>, PaymentInstrumentCommandModel> {

    private final ObjectMapper objectMapper;

    @Autowired
    public SaveCitizenCommandModelFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * @param requestData
     * @return instance of PaymentInstrumentUpdateModel, containing a PaymentInstrumentUpdate instance,
     * mapped from the byte[] payload in the requestData, and the inbound Kafka headers
     */

    @SneakyThrows
    @Override
    public PaymentInstrumentCommandModel createModel(Pair<byte[], Headers> requestData) {
        PaymentInstrumentUpdate paymentInstrumentUpdate = parsePayload(requestData.getLeft());
        //TODO: Proper fix
        PaymentInstrumentCommandModel paymentInstrumentCommandModel =
                PaymentInstrumentCommandModel.builder()
                        .payload(paymentInstrumentUpdate)
                        .headers(requestData.getRight())
                        .build();
        return paymentInstrumentCommandModel;
    }

    /**
     * Method containing the logic for the parsing of the byte[] payload into an instance of PaymentInstrumentUpdate,
     * using the ObjectMapper
     *
     * @param payload inbound JSON payload in byte[] format, defining a PaymentInstrumentUpdate
     * @return instance of PaymentInstrumentUpdate, mapped from the input json byte[] payload
     */
    private PaymentInstrumentUpdate parsePayload(byte[] payload) {
        String json = new String(payload, StandardCharsets.UTF_8);
        try {
            return objectMapper.readValue(json, PaymentInstrumentUpdate.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("Cannot parse the payload as a valid %s", PaymentInstrumentUpdate.class), e);
        }
    }
}
