package it.gov.pagopa.bpd.payment_instrument.command;

import it.gov.pagopa.bpd.common.BaseTest;
import it.gov.pagopa.bpd.payment_instrument.model.DeletePaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.model.DeletePaymentInstrumentCommandModel;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import it.gov.pagopa.bpd.payment_instrument.service.mapper.TransactionMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Test class for the DeletePaymentInstrumentCommand method
 */

public class DeletePaymentInstrumentCommandTest extends BaseTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    PaymentInstrumentService paymentInstrumentServiceMock;
    @Spy
    TransactionMapper transactionMapperSpy;

    @Before
    public void initTest() {
        Mockito.reset(
                paymentInstrumentServiceMock,
                transactionMapperSpy);
    }

    @Test
    public void TestExecute_OK() {

        DeletePaymentInstrument deletePaymentInstrument = getRequestObject();
        DeletePaymentInstrumentCommandImpl deletePaymentInstrumentCommand = buildCommandInstance(deletePaymentInstrument);

        Boolean executed = deletePaymentInstrumentCommand.doExecute();
        Mockito.verify(paymentInstrumentServiceMock).delete(Mockito.any(), Mockito.any(), Mockito.any());
        Assert.assertTrue(executed);
    }

    protected DeletePaymentInstrumentCommandImpl buildCommandInstance(DeletePaymentInstrument deletePaymentInstrument) {
        return new DeletePaymentInstrumentCommandImpl(
                DeletePaymentInstrumentCommandModel.builder().payload(deletePaymentInstrument).headers(null).build(),
                paymentInstrumentServiceMock
        );
    }

    protected DeletePaymentInstrument getRequestObject() {
        return DeletePaymentInstrument.builder()
                .cancellationDate(LocalDateTime.parse("2020-04-09T16:22:45"))
                .hpan("hpan")
                .fiscalCode("AAAAAA00A00A000A")
                .build();
    }


    @Test
    public void testExecute_KO_Null() {

        DeletePaymentInstrumentCommand deletePaymentInstrumentCommand = buildCommandInstance(null);

        try {

            expectedException.expect(AssertionError.class);
            deletePaymentInstrumentCommand.execute();
            BDDMockito.verifyZeroInteractions(paymentInstrumentServiceMock);
            BDDMockito.verifyZeroInteractions();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}