package it.gov.pagopa.bpd.payment_instrument;

import eu.sia.meda.event.BaseEventConnectorTest;
import it.gov.pagopa.bpd.payment_instrument.publisher.TransactionManagerErrorPublisherConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;

/**
 * Test class for the TransactionManagerErrorPublisherConnector class
 */

@Import({TransactionManagerErrorPublisherConnector.class})
@TestPropertySource(
        locations = "classpath:config/testTransactionManagerErrorPublisher.properties",
        properties = {
                "connectors.eventConfigurations.items.TransactionManagerErrorPublisherConnector.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class TransactionManagerErrorPublisherConnectorTest extends
        BaseEventConnectorTest<byte[], Boolean, byte[], Void, TransactionManagerErrorPublisherConnector> {

    @Value("${connectors.eventConfigurations.items.TransactionManagerErrorPublisherConnector.topic}")
    private String topic;

    @Autowired
    private TransactionManagerErrorPublisherConnector transactionManagerErrorPublisherConnector;

    @Override
    protected TransactionManagerErrorPublisherConnector getEventConnector() {
        return transactionManagerErrorPublisherConnector;
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