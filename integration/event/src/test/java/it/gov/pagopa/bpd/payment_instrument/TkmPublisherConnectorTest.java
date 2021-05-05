package it.gov.pagopa.bpd.payment_instrument;

import eu.sia.meda.event.BaseEventConnectorTest;
import eu.sia.meda.util.TestUtils;
import it.gov.pagopa.bpd.payment_instrument.publisher.TkmPublisherConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayOutputStream;

@Import({TkmPublisherConnector.class})
@TestPropertySource(
        locations = "classpath:config/testTkmPublisher.properties",
        properties = {
                "connectors.eventConfigurations.items.TkmPublisherConnector.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class TkmPublisherConnectorTest extends
        BaseEventConnectorTest<ByteArrayOutputStream, Boolean, ByteArrayOutputStream, Void, TkmPublisherConnector> {

    @Value("${connectors.eventConfigurations.items.TkmPublisherConnector.topic}")
    private String topic;

    @Autowired
    private TkmPublisherConnector tkmPublisherConnector;

    @Override
    protected TkmPublisherConnector getEventConnector() {
        return tkmPublisherConnector;
    }

    @Override
    protected ByteArrayOutputStream getRequestObject() {
        return TestUtils.mockInstance(new ByteArrayOutputStream());
    }

    @Override
    protected String getTopic() {
        return topic;
    }
}
