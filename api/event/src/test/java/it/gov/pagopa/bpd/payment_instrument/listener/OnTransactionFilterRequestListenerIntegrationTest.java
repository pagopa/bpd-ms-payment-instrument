package it.gov.pagopa.bpd.payment_instrument.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.event.service.ErrorPublisherService;
import eu.sia.meda.eventlistener.BaseEventListenerIntegrationTest;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentHistoryDAO;
import it.gov.pagopa.bpd.payment_instrument.listener.config.TestConfig;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.SaveTransactionCommandModelFactory;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.Transaction;
import it.gov.pagopa.bpd.payment_instrument.service.PointTransactionPublisherService;
import it.gov.pagopa.bpd.payment_instrument.service.TransactionErrorPublisherService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Assert;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Integration Testing class for the whole microservice, it executes the entire flow starting from the
 * inbound event listener, to the production of a message in the outbound channel
 */

@EnableConfigurationProperties
@ContextConfiguration(
        classes = {
                TestConfig.class,
                JacksonAutoConfiguration.class,
                ObjectPostProcessorConfiguration.class,
                AuthenticationConfiguration.class,
                KafkaAutoConfiguration.class
        })
@TestPropertySource(
        locations = {
                "classpath:config/testTransactionRequestListener.properties",
                "classpath:config/testPointTransactionPublisher.properties",
                "classpath:config/testTransactionErrorPublisher.properties"
        },
        properties = {
                "logging.level.it.gov.pagopa.bpd.payment_instrument=DEBUG",
                "listeners.eventConfigurations.items.OnTransactionFilterRequestListener.bootstrapServers=${spring.embedded.kafka.brokers}",
                "connectors.eventConfigurations.items.PointTransactionPublisherConnector.bootstrapServers=${spring.embedded.kafka.brokers}",
                "connectors.eventConfigurations.items.TransactionErrorPublisherConnector.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class OnTransactionFilterRequestListenerIntegrationTest extends BaseEventListenerIntegrationTest {

    @SpyBean
    SaveTransactionCommandModelFactory saveTransactionCommandModelFactorySpy;
    @SpyBean
    TransactionErrorPublisherService transactionErrorPublisherService;
    @Autowired
    ObjectMapper objectMapper;
    @Value("${listeners.eventConfigurations.items.OnTransactionFilterRequestListener.topic}")
    private String topicSubscription;
    //    @SpyBean
//    private PaymentInstrumentService paymentInstrumentServiceSpy;
    @Value("${connectors.eventConfigurations.items.PointTransactionPublisherConnector.topic}")
    private String topicPublished;
    @SpyBean
    private PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAOSpy;
    @SpyBean
    private PointTransactionPublisherService pointTransactionPublisherServiceSpy;

    @Override
    protected ErrorPublisherService getErrorPublisherService() {
        return null;
    }

    @Override
    protected Object getRequestObject() {
        return Transaction.builder()
                .idTrxAcquirer("1")
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-10T14:59:59.245Z"))
                .amount(BigDecimal.valueOf(1313.13))
                .operationType("00")
                .hpan("test")
                .merchantId("0")
                .circuitType("00")
                .mcc("813")
                .idTrxIssuer("0")
                .amountCurrency("833")
                .correlationId("1")
                .acquirerId("0")
                .terminalId("2")
                .bin("000001")
                .build();
    }

    @Override
    protected String getTopicSubscription() {
        return topicSubscription;
    }

    @Override
    protected String getTopicPublished() {
        return topicPublished;
    }

    @Override
    protected void verifyPublishedMessages(List<ConsumerRecord<String, String>> records) {

        try {

            Transaction sentTransaction = (Transaction) getRequestObject();
            sentTransaction.setTrxDate(OffsetDateTime.parse("2020-04-10T16:59:59.245+02:00"));
            Assert.assertEquals(1, records.size());
            BDDMockito.verify(paymentInstrumentHistoryDAOSpy, Mockito.atLeastOnce())
                    .countActive(Mockito.eq(sentTransaction.getHpan()), Mockito.any());
            BDDMockito.verify(pointTransactionPublisherServiceSpy).publishPointTransactionEvent(Mockito.any());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}