package it.gov.pagopa.bpd.payment_instrument.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.event.service.ErrorPublisherService;
import eu.sia.meda.eventlistener.BaseEventListenerTest;
import it.gov.pagopa.bpd.payment_instrument.command.ProcessCitizenUpdateEventCommand;
import it.gov.pagopa.bpd.payment_instrument.model.InboundCitizenStatusData;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.CitizenUpdateEventCommandModelFactory;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.CitizenUpdateEventErrorPayloadModelFactory;
import it.gov.pagopa.bpd.payment_instrument.service.CitizenStatusErrorPublisherService;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.OffsetDateTime;

import static it.gov.pagopa.bpd.payment_instrument.listener.constants.CitizenStatusEventConstants.ALL_ORIGIN;

@Import({OnCitizenStatusUpdateRequestListener.class})
@TestPropertySource(
        locations = "classpath:config/testCitizenEventRequestListener.properties",
        properties = {
                "listeners.eventConfigurations.items.OnCitizenStatusUpdateRequestListener.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class OnCitizenStatusUpdateRequestListenerTest extends BaseEventListenerTest {


    @Value("${listeners.eventConfigurations.items.OnCitizenStatusUpdateRequestListener.topic}")
    private String topic;

    @SpyBean
    ObjectMapper objectMapperSpy;

    @SpyBean
    OnCitizenStatusUpdateRequestListener onCitizenStatusUpdateRequestListenerSpy;

    @SpyBean
    CitizenUpdateEventCommandModelFactory citizenUpdateEventCommandModelFactorySpy;

    @SpyBean
    CitizenUpdateEventErrorPayloadModelFactory citizenUpdateEventErrorPayloadModelFactory;

    @MockBean
    CitizenStatusErrorPublisherService citizenStatusErrorPublisherServiceMock;

    @MockBean
    BeanFactory beanFactoryMock;

    @MockBean
    ProcessCitizenUpdateEventCommand processCitizenUpdateEventCommand;

    @Before
    public void setUp() throws Exception {

        Mockito.reset(
                onCitizenStatusUpdateRequestListenerSpy,
                citizenUpdateEventCommandModelFactorySpy,
                citizenStatusErrorPublisherServiceMock,
                citizenUpdateEventErrorPayloadModelFactory,
                beanFactoryMock,
                processCitizenUpdateEventCommand);
        Mockito.doReturn(true).when(processCitizenUpdateEventCommand).execute();

    }

    @Override
    protected Object getRequestObject() {
          InboundCitizenStatusData citizenStatusData = InboundCitizenStatusData.builder()
                    .fiscalCode("fiscalCode")
                    .updateDateTime(OffsetDateTime.parse("2020-04-09T16:22:45.304Z"))
                    .applyTo(ALL_ORIGIN)
                    .build();
          citizenStatusData.setEnabled(false);
          return citizenStatusData;
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected void verifyInvocation(String json) {
        try {
            BDDMockito.verify(citizenUpdateEventCommandModelFactorySpy, Mockito.atLeastOnce())
                    .createModel(Mockito.any());
            BDDMockito.verify(objectMapperSpy, Mockito.atLeastOnce())
                    .readValue(Mockito.anyString(), Mockito.eq(InboundCitizenStatusData.class));
            BDDMockito.verify(processCitizenUpdateEventCommand, Mockito.atLeastOnce()).execute();
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