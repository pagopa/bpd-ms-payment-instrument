package it.gov.pagopa.bpd.payment_instrument.listener;

import eu.sia.meda.event.service.ErrorPublisherService;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

/**
 * Integration Testing class for the whole micro-service, it executes the error flow starting from the
 * inbound event listener, to the production of a message in the outbound error channel
 */

@TestPropertySource(properties = {
        "connectors.eventConfigurations.items.TransactionErrorPublisherConnector.topic=rtd-trx-valid-error-test"
})
public class OnTransactionFilterRequestListenerInvalidPayloadErrorIntegrationTest extends OnTransactionFilterRequestListenerIntegrationTest {

    @Value("${connectors.eventConfigurations.items.TransactionErrorPublisherConnector.topic}")
    private String topicPublished;

    @Override
    protected ErrorPublisherService getErrorPublisherService() {
        return null;
    }

    @Override
    protected Object getRequestObject() {
        return "unparsable";
    }

    @Override
    protected String getTopicPublished() {
        return topicPublished;
    }

    @SneakyThrows
    @Override
    protected void verifyPublishedMessages(List<ConsumerRecord<String, String>> records) {
        Assert.assertEquals(1, records.size());
        String sentPayload = (String) getRequestObject();
        Assert.assertEquals(sentPayload, records.get(0).value());
    }

}