package it.gov.pagopa.bpd.payment_instrument;

import eu.sia.meda.event.BaseEventConnectorTest;
import it.gov.pagopa.bpd.payment_instrument.publisher.TransactionErrorPublisherConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;

/**
 * Test class for the TransactionErrorPublisherConnector class
 */

@Import({TransactionErrorPublisherConnector.class})
@TestPropertySource(
        locations = "classpath:config/testTransactionErrorPublisher.properties",
        properties = {
                "connectors.eventConfigurations.items.TransactionErrorPublisherConnector.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class TransactionErrorPublisherConnectorTest extends
        BaseEventConnectorTest<byte[], Boolean, byte[], Void, TransactionErrorPublisherConnector> {

    @Value("${connectors.eventConfigurations.items.TransactionErrorPublisherConnector.topic}")
    private String topic;

    @Autowired
    private TransactionErrorPublisherConnector transactionErrorPublisherConnector;

    @Override
    protected TransactionErrorPublisherConnector getEventConnector() {
        return transactionErrorPublisherConnector;
    }

    @Override
    protected byte[] getRequestObject() {
        return "error".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected String getTopic() {
        return topic;
    }

}