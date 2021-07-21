package it.gov.pagopa.bpd.payment_instrument.listener;

import eu.sia.meda.event.service.ErrorPublisherService;
import eu.sia.meda.eventlistener.BaseEventListenerTest;
import it.gov.pagopa.bpd.payment_instrument.command.DeletePaymentInstrumentCommand;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.DeletePaymentInstrumentErrorModelFactory;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.DeletePaymentInstrumentModelFactory;
import it.gov.pagopa.bpd.payment_instrument.model.DeletePaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Test class for the OnPaymentInstrumentToDeleteListener method
 */

@Import({OnPaymentInstrumentToDeleteListener.class})
@TestPropertySource(
        locations = "classpath:config/testPaymentInstrumentToDeleteListener.properties",
        properties = {
                "listeners.eventConfigurations.items.OnPaymentInstrumentToDeleteListener.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class OnPaymentInstrumentToDeleteListenerTest extends BaseEventListenerTest {

    @SpyBean
    OnPaymentInstrumentToDeleteListener onPaymentInstrumentToDeleteListenerSpy;
    @SpyBean
    DeletePaymentInstrumentModelFactory deletePaymentInstrumentModelFactorySpy;
    @SpyBean
    DeletePaymentInstrumentErrorModelFactory deletePaymentInstrumentErrorModelFactorySpy;
    @MockBean
    DeletePaymentInstrumentCommand deletePaymentInstrumentCommandMock;
    @MockBean
    PaymentInstrumentService paymentInstrumentServiceMock;
    @Value("${listeners.eventConfigurations.items.OnPaymentInstrumentToDeleteListener.topic}")
    private String topic;

    @Before
    public void setUp() throws Exception {

        Mockito.reset(
                onPaymentInstrumentToDeleteListenerSpy,
                deletePaymentInstrumentModelFactorySpy,
                deletePaymentInstrumentErrorModelFactorySpy,
                paymentInstrumentServiceMock,
                deletePaymentInstrumentCommandMock);
        Mockito.doReturn(true).when(deletePaymentInstrumentCommandMock).execute();

    }

    @Override
    protected Object getRequestObject() {
        return DeletePaymentInstrument.builder()
                .cancellationDate(LocalDateTime.parse("2020-04-09T16:22:45"))
                .fiscalCode("AAAAA11A11A111A")
                .hpan("hpan")
                .build();
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected void verifyInvocation(String json) {
        try {
            BDDMockito.verify(deletePaymentInstrumentModelFactorySpy, Mockito.atLeastOnce())
                    .createModel(Mockito.any());
            BDDMockito.verify(deletePaymentInstrumentCommandMock, Mockito.atLeastOnce()).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Override
    protected ErrorPublisherService getErrorPublisherService() {
        return null;
    }
}