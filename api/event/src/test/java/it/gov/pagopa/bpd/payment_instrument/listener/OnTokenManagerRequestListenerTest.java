package it.gov.pagopa.bpd.payment_instrument.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.event.service.ErrorPublisherService;
import eu.sia.meda.eventlistener.BaseEventListenerTest;
import it.gov.pagopa.bpd.payment_instrument.command.FilterPaymentInstrumentCommand;
import it.gov.pagopa.bpd.payment_instrument.command.UpsertPaymentInstrumentTokensCommand;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.SaveCitizenCommandModelFactory;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.SaveTokenManagerCommandModelFactory;
import it.gov.pagopa.bpd.payment_instrument.listener.factory.TokenPaymentInstrumentErrorModelFactory;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerCommandModel;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerData;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerDataCard;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerDataToken;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.PaymentInstrumentUpdate;
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
import java.util.Arrays;
import java.util.Collections;


@Import({OnTokenManagerRequestListener.class})
@TestPropertySource(
        locations = "classpath:config/testTokenManagementRequestListener.properties",
        properties = {
                "listeners.eventConfigurations.items.OnTokenManagerRequestListener.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class OnTokenManagerRequestListenerTest extends BaseEventListenerTest {

    @SpyBean
    ObjectMapper objectMapperSpy;
    @SpyBean
    OnTokenManagerRequestListener onTokenManagerRequestListenerSpy;
    @SpyBean
    TokenPaymentInstrumentErrorModelFactory tokenPaymentInstrumentErrorModelFactory;
    @SpyBean
    SaveTokenManagerCommandModelFactory saveTokenManagerCommandModelFactorySpy;
    @MockBean
    UpsertPaymentInstrumentTokensCommand upsertPaymentInstrumentTokensCommandMock;
    @MockBean
    PaymentInstrumentService paymentInstrumentService;

    @Value("${listeners.eventConfigurations.items.OnTokenManagerRequestListener.topic}")
    private String topic;

    @Before
    public void setUp() throws Exception {

        Mockito.reset(
                onTokenManagerRequestListenerSpy,
                saveTokenManagerCommandModelFactorySpy,
                upsertPaymentInstrumentTokensCommandMock,
                paymentInstrumentService,
                tokenPaymentInstrumentErrorModelFactory);
        Mockito.doReturn(false).when(upsertPaymentInstrumentTokensCommandMock).execute();

    }

    @Override
    protected Object getRequestObject() {

        TokenManagerDataToken tokenToInsert =
                TokenManagerDataToken.builder()
                        .htoken("token1")
                        .haction("INSERT_UPDATE")
                        .build();

        TokenManagerDataToken tokenToUpdate =
                TokenManagerDataToken.builder()
                        .htoken("token2")
                        .haction("INSERT_UPDATE")
                        .build();

        TokenManagerDataToken tokenToRemove =
                TokenManagerDataToken.builder()
                        .htoken("token3")
                        .haction("DELETE")
                        .build();

        TokenManagerDataCard tokenManagerDataCard =
                TokenManagerDataCard.builder()
                        .hpan("hpan")
                        .par("par")
                        .action("INSERT_UPDATE")
                        .htokens(Arrays.asList(tokenToInsert, tokenToUpdate, tokenToRemove))
                        .build();

        return TokenManagerData.builder()
                .taxCode("fiscalCode")
                .timestamp(LocalDateTime.now())
                .cards(Collections.singletonList(tokenManagerDataCard))
                .build();
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected void verifyInvocation(String json) {
        try {
            BDDMockito.verify(saveTokenManagerCommandModelFactorySpy, Mockito.atLeastOnce())
                    .createModel(Mockito.any());
            BDDMockito.verify(objectMapperSpy, Mockito.atLeastOnce())
                    .readValue(Mockito.anyString(), Mockito.eq(TokenManagerData.class));
            BDDMockito.verify(upsertPaymentInstrumentTokensCommandMock, Mockito.atLeastOnce()).execute();
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
