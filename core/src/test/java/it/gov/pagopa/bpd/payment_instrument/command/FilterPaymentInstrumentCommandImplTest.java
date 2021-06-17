package it.gov.pagopa.bpd.payment_instrument.command;

import it.gov.pagopa.bpd.common.BaseTest;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentCommandModel;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.OutgoingPaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.PaymentInstrumentUpdate;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import it.gov.pagopa.bpd.payment_instrument.service.TkmPublisherService;
import it.gov.pagopa.bpd.payment_instrument.service.mapper.PaymentInstrumentMapper;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;

public class FilterPaymentInstrumentCommandImplTest extends BaseTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    PaymentInstrumentService paymentInstrumentServiceMock;
    @Mock
    TkmPublisherService tkmPublisherServiceMock;
    @Spy
    PaymentInstrumentMapper paymentInstrumentMapperSpy;


    @Before
    public void initTest() {
        Mockito.reset(
                paymentInstrumentServiceMock,
                tkmPublisherServiceMock,
                paymentInstrumentMapperSpy);
    }

    @Test
    public void test_BDPActive() {

        PaymentInstrumentUpdate pin = getRequestObject();
        PaymentInstrument pi = new PaymentInstrument();
        pi.setHpan("hpan");
        pi.setHpanMaster("hapanMaster");
        pi.setActivationDate(OffsetDateTime.now());
        OutgoingPaymentInstrument outgoingPaymentInstrument = paymentInstrumentMapperSpy.map(pin);
        ByteArrayOutputStream piOut = new ByteArrayOutputStream();
        FilterPaymentInstrumentCommand filterPaymentInstrumentCommand = new FilterPaymentInstrumentCommandImpl(
                PaymentInstrumentCommandModel.builder().payload(pin).build(),
                tkmPublisherServiceMock,
                paymentInstrumentServiceMock,
                paymentInstrumentMapperSpy
        );


        try {

            BDDMockito.doReturn(pi).when(paymentInstrumentServiceMock)
                    .findByPar(Mockito.eq("par"));
            BDDMockito.doReturn(pi).when(paymentInstrumentServiceMock)
                    .createOrUpdate(Mockito.eq(pi.getHpan()), Mockito.eq(pi));
            BDDMockito.doReturn(piOut).when(tkmPublisherServiceMock)
                    .cryptOutgoingPaymentInstrument(Mockito.any());
            BDDMockito.doNothing().when(tkmPublisherServiceMock)
                    .publishTkmEvent(Mockito.any());

            Boolean isOk = filterPaymentInstrumentCommand.execute();

            Assert.assertTrue(isOk);
            BDDMockito.verify(paymentInstrumentServiceMock, Mockito.atLeastOnce())
                    .findByPar(Mockito.eq("par"));
            BDDMockito.verify(tkmPublisherServiceMock, Mockito.atLeastOnce())
                    .publishTkmEvent(Mockito.any());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    protected FilterPaymentInstrumentCommand buildCommandInstance(PaymentInstrumentUpdate pi) {
        return new FilterPaymentInstrumentCommandImpl(
                PaymentInstrumentCommandModel.builder().payload(pi).headers(null).build(),
                tkmPublisherServiceMock,
                paymentInstrumentServiceMock
        );
    }

    protected PaymentInstrumentUpdate getRequestObject() {
        return PaymentInstrumentUpdate.builder()
                .par("par")
                .hpan("htoken")
                .build();
    }

    @Test
    public void test_BDPNotActive() {

        PaymentInstrumentUpdate pin = getRequestObject();
        PaymentInstrument pi = new PaymentInstrument();
        pi.setHpan("hpan");
        pi.setHpanMaster("hapanMaster");
        pi.setActivationDate(OffsetDateTime.now());
        ByteArrayOutputStream piOut = new ByteArrayOutputStream();
        OutgoingPaymentInstrument outgoingPaymentInstrument = paymentInstrumentMapperSpy.map(pin);
        FilterPaymentInstrumentCommand filterPaymentInstrumentCommand = new FilterPaymentInstrumentCommandImpl(
                PaymentInstrumentCommandModel.builder().payload(pin).build(),
                tkmPublisherServiceMock,
                paymentInstrumentServiceMock,
                paymentInstrumentMapperSpy
        );

        try {

            BDDMockito.doReturn(pi).when(paymentInstrumentServiceMock)
                    .findByPar(Mockito.eq("par"));
            BDDMockito.doReturn(null).when(paymentInstrumentServiceMock)
                    .createOrUpdate(Mockito.eq(pi.getHpan()), Mockito.eq(pi));
            BDDMockito.doReturn(piOut).when(tkmPublisherServiceMock)
                    .cryptOutgoingPaymentInstrument(Mockito.any());
            BDDMockito.doNothing().when(tkmPublisherServiceMock)
                    .publishTkmEvent(Mockito.any());

            Boolean isOk = filterPaymentInstrumentCommand.execute();

            Assert.assertTrue(isOk);
            BDDMockito.verify(paymentInstrumentServiceMock, Mockito.atLeastOnce())
                    .findByPar(Mockito.eq("par"));
            BDDMockito.verify(tkmPublisherServiceMock, Mockito.atLeastOnce())
                    .publishTkmEvent(Mockito.any());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @SneakyThrows
    @Test
    public void testExecute_KO_Validation() {

        PaymentInstrumentUpdate paymentInstrumentUpdate = getRequestObject();
        paymentInstrumentUpdate.setPar(null);
        OffsetDateTime now = OffsetDateTime.now();
        FilterPaymentInstrumentCommand paymentInstrumentCommand = buildCommandInstance(paymentInstrumentUpdate);

        BDDMockito.doThrow(new RuntimeException("Some Exception")).when(paymentInstrumentServiceMock)
                .checkActivePar(Mockito.eq(paymentInstrumentUpdate.getPar()), Mockito.eq(now));

        expectedException.expect(Exception.class);
        paymentInstrumentCommand.execute();

        BDDMockito.verifyZeroInteractions(paymentInstrumentServiceMock);
        BDDMockito.verifyZeroInteractions(tkmPublisherServiceMock);

    }

    @Test
    public void testExecute_KO_Null() {

        FilterPaymentInstrumentCommand filterPaymentInstrumentCommand = buildCommandInstance(null);

        try {

            expectedException.expect(AssertionError.class);
            filterPaymentInstrumentCommand.execute();
            BDDMockito.verifyZeroInteractions(paymentInstrumentServiceMock);
            BDDMockito.verifyZeroInteractions(tkmPublisherServiceMock);
            BDDMockito.verifyZeroInteractions();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }
}
