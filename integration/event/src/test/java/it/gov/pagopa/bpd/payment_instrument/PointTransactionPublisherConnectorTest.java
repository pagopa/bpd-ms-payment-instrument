package it.gov.pagopa.bpd.payment_instrument;

import eu.sia.meda.event.BaseEventConnectorTest;
import eu.sia.meda.util.TestUtils;
import it.gov.pagopa.bpd.payment_instrument.publisher.PointTransactionPublisherConnector;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.OutgoingTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

/**
 * Test class for the PointTransactionPublisherConnector class
 */

@Import({PointTransactionPublisherConnector.class})
@TestPropertySource(
        locations = "classpath:config/testPointTransactionPublisher.properties",
        properties = {
                "connectors.eventConfigurations.items.PointTransactionPublisherConnector.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class PointTransactionPublisherConnectorTest extends
        BaseEventConnectorTest<OutgoingTransaction, Boolean, OutgoingTransaction, Void, PointTransactionPublisherConnector> {

    @Value("${connectors.eventConfigurations.items.PointTransactionPublisherConnector.topic}")
    private String topic;

    @Autowired
    private PointTransactionPublisherConnector pointTransactionPublisherConnector;

    @Override
    protected PointTransactionPublisherConnector getEventConnector() {
        return pointTransactionPublisherConnector;
    }

    @Override
    protected OutgoingTransaction getRequestObject() {
        return TestUtils.mockInstance(new OutgoingTransaction());
    }

    @Override
    protected String getTopic() {
        return topic;
    }

}