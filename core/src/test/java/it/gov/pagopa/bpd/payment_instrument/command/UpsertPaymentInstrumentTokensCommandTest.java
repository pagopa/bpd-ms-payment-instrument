package it.gov.pagopa.bpd.payment_instrument.command;

import it.gov.pagopa.bpd.common.BaseTest;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerCommandModel;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerData;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerDataCard;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerDataToken;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;

public class UpsertPaymentInstrumentTokensCommandTest extends BaseTest {


    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    PaymentInstrumentService paymentInstrumentServiceMock;

    @Before
    public void initTest() {
        Mockito.reset(paymentInstrumentServiceMock);
    }

    @Test
    public void test_OK() {

        TokenManagerData tokenManagerDataToken = getRequestObject();
        BDDMockito.doReturn(true).when(paymentInstrumentServiceMock)
                .manageTokenData(Mockito.eq(tokenManagerDataToken));
        Boolean upsertPaymentInstrumentTokensCommandResult =
                new UpsertPaymentInstrumentTokensCommandImpl(TokenManagerCommandModel.builder()
                        .payload(tokenManagerDataToken).build(), paymentInstrumentServiceMock).doExecute();
        Assert.assertTrue(upsertPaymentInstrumentTokensCommandResult);
        BDDMockito.verify(paymentInstrumentServiceMock).manageTokenData(Mockito.eq(tokenManagerDataToken));

    }

    @Test
    public void test_KO() {
        TokenManagerData tokenManagerDataToken = getRequestObject();
        tokenManagerDataToken.setTaxCode(null);
        expectedException.expect(Exception.class);
        Boolean upsertPaymentInstrumentTokensCommandResult =
                new UpsertPaymentInstrumentTokensCommandImpl(TokenManagerCommandModel.builder()
                        .payload(tokenManagerDataToken).build(), paymentInstrumentServiceMock).doExecute();
        Assert.assertTrue(upsertPaymentInstrumentTokensCommandResult);
        BDDMockito.verifyNoMoreInteractions(paymentInstrumentServiceMock);
    }

    protected TokenManagerData getRequestObject() {

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
                .timestamp(OffsetDateTime.now().toLocalDateTime())
                .cards(Collections.singletonList(tokenManagerDataCard))
                .build();
    }

}
