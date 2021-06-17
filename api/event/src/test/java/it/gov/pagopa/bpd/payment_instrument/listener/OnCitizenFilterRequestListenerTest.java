package it.gov.pagopa.bpd.payment_instrument.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.event.service.ErrorPublisherService;
import eu.sia.meda.eventlistener.BaseEventListenerTest;
import it.gov.pagopa.bpd.payment_instrument.command.FilterPaymentInstrumentCommand;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.SaveCitizenCommandModelFactory;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.PaymentInstrumentUpdate;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;


@Import({OnCitizenFilterRequestListener.class})
@TestPropertySource(
        locations = "classpath:config/testCitizenRequestListener.properties",
        properties = {
                "listeners.eventConfigurations.items.OnCitizenFilterRequestListener.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class OnCitizenFilterRequestListenerTest extends BaseEventListenerTest {

    @SpyBean
    ObjectMapper objectMapperSpy;
    @SpyBean
    OnCitizenFilterRequestListener onTransactionFilterRequestListenerSpy;
    @SpyBean
    SaveCitizenCommandModelFactory saveCitizenCommandModelFactorySpy;
    @MockBean
    FilterPaymentInstrumentCommand filterPaymentInstrumentCommandMock;

    @Value("${listeners.eventConfigurations.items.OnCitizenFilterRequestListener.topic}")
    private String topic;

    @Before
    public void setUp() throws Exception {

        Mockito.reset(
                onTransactionFilterRequestListenerSpy,
                saveCitizenCommandModelFactorySpy,
                filterPaymentInstrumentCommandMock);
        Mockito.doReturn(true).when(filterPaymentInstrumentCommandMock).execute();

    }

    @Override
    protected Object getRequestObject() {
        return PaymentInstrumentUpdate.builder()
                .hpan("hpan")
                .par("par")
                .build();
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected void verifyInvocation(String json) {
        try {
            BDDMockito.verify(saveCitizenCommandModelFactorySpy, Mockito.atLeastOnce())
                    .createModel(Mockito.any());
            BDDMockito.verify(objectMapperSpy, Mockito.atLeastOnce())
                    .readValue(Mockito.anyString(), Mockito.eq(PaymentInstrumentUpdate.class));
            BDDMockito.verify(filterPaymentInstrumentCommandMock, Mockito.atLeastOnce()).execute();
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
