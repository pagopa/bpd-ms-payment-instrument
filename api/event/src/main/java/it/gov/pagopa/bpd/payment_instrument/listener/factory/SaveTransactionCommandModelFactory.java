package it.gov.pagopa.bpd.payment_instrument.listener.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.rtd.transaction_manager.model.SaveTransactionCommandModel;
import it.gov.pagopa.rtd.transaction_manager.model.Transaction;
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
public class SaveTransactionCommandModelFactory implements
        ModelFactory<Pair<byte[], Headers>, SaveTransactionCommandModel>  {

    private final ObjectMapper objectMapper;

    @Autowired
    public SaveTransactionCommandModelFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     *
     * @param requestData
     * @return instance of SaveTransactionModel, containing a Transaction instance,
     * mapped from the byte[] payload in the requestData, and the inbound Kafka headers
     */

    @SneakyThrows
    @Override
    public SaveTransactionCommandModel createModel(Pair<byte[], Headers> requestData) {
        Transaction transaction = parsePayload(requestData.getLeft());
        SaveTransactionCommandModel saveTransactionCommandModel =
                SaveTransactionCommandModel.builder()
                    .payload(transaction)
                    .headers(requestData.getRight())
                    .build();
        return saveTransactionCommandModel;
    }

    /**
     * Method containing the logic for the parsing of the byte[] payload into an instance of Transaction,
     * using the ObjectMapper
     * @param payload
     *          inbound JSON payload in byte[] format, defining a Transaction
     * @return instance of Transaction, mapped from the input json byte[] payload
     */
    private Transaction parsePayload(byte[] payload) {
        String json = new String(payload, StandardCharsets.UTF_8);
        try {
            return objectMapper.readValue(json, Transaction.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("Cannot parse the payload as a valid %s", Transaction.class), e);
        }
    }

}
