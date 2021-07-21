package it.gov.pagopa.bpd.payment_instrument;

import eu.sia.meda.event.BaseEventConnectorTest;
import it.gov.pagopa.bpd.payment_instrument.publisher.CitizenStatusErrorPublisherConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;

/**
 * Test class for the CitizenStatusErrorPublisherConnector class
 */

@Import({CitizenStatusErrorPublisherConnector.class})
@TestPropertySource(
        locations = "classpath:config/testCitizenStatusErrorPublisher.properties",
        properties = {
                "connectors.eventConfigurations.items.CitizenStatusErrorPublisherConnector.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class CitizenStatusErrorPublisherConnectorTest extends
        BaseEventConnectorTest<byte[], Boolean, byte[], Void, CitizenStatusErrorPublisherConnector> {

    @Value("${connectors.eventConfigurations.items.CitizenStatusErrorPublisherConnector.topic}")
    private String topic;

    @Autowired
    private CitizenStatusErrorPublisherConnector citizenStatusErrorPublisherConnector;

    @Override
    protected CitizenStatusErrorPublisherConnector getEventConnector() {
        return citizenStatusErrorPublisherConnector;
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