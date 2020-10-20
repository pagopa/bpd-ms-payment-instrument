package it.gov.pagopa.bpd.payment_instrument.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.event.service.ErrorPublisherService;
import eu.sia.meda.eventlistener.BaseEventListenerTest;
import it.gov.pagopa.bpd.payment_instrument.command.FilterTransactionCommand;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.SaveTransactionCommandModelFactory;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.Transaction;
import it.gov.pagopa.bpd.payment_instrument.service.TransactionErrorPublisherService;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Test class for the OnTransactionFilterRequestListener method
 */

@Import({OnTransactionFilterRequestListener.class})
@TestPropertySource(
        locations = "classpath:config/testTransactionRequestListener.properties",
        properties = {
                "listeners.eventConfigurations.items.OnTransactionFilterRequestListener.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class OnTransactionFilterRequestListenerTest extends BaseEventListenerTest {


    @SpyBean
    ObjectMapper objectMapperSpy;
    @SpyBean
    OnTransactionFilterRequestListener onTransactionFilterRequestListenerSpy;
    @SpyBean
    SaveTransactionCommandModelFactory saveTransactionCommandModelFactorySpy;
    @MockBean
    FilterTransactionCommand filterTransactionCommandMock;
    @MockBean
    TransactionErrorPublisherService transactionErrorPublisherServiceMock;
    @Value("${listeners.eventConfigurations.items.OnTransactionFilterRequestListener.topic}")
    private String topic;

    @Before
    public void setUp() throws Exception {

        Mockito.reset(
                onTransactionFilterRequestListenerSpy,
                saveTransactionCommandModelFactorySpy,
                transactionErrorPublisherServiceMock,
                filterTransactionCommandMock);
        Mockito.doReturn(true).when(filterTransactionCommandMock).execute();

    }

    @Override
    protected Object getRequestObject() {
        return Transaction.builder()
                .idTrxAcquirer("1")
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-09T16:22:45.304Z"))
                .amount(BigDecimal.valueOf(1313.13))
                .operationType("00")
                .hpan("hpan")
                .merchantId("0")
                .circuitType("00")
                .mcc("814")
                .idTrxIssuer("0")
                .amountCurrency("833")
                .correlationId("1")
                .acquirerId("0")
                .terminalId("0")
                .bin("000005")
                .build();
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected void verifyInvocation(String json) {
        try {
            BDDMockito.verify(saveTransactionCommandModelFactorySpy, Mockito.atLeastOnce())
                    .createModel(Mockito.any());
            BDDMockito.verify(objectMapperSpy, Mockito.atLeastOnce())
                    .readValue(Mockito.anyString(), Mockito.eq(Transaction.class));
            BDDMockito.verify(filterTransactionCommandMock, Mockito.atLeastOnce()).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Override
    protected ErrorPublisherService getErrorPublisherService() {
        return transactionErrorPublisherServiceMock;
    }
}