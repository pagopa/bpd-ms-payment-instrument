package it.gov.pagopa.bpd.payment_instrument.command;

import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentHistoryDAO;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PaymentInstrumentDAOServiceImpl.class)
@TestPropertySource(properties = "numMaxPaymentInstr=5")
public class PaymentInstrumentDAOServiceImplTest {

    @MockBean
    private PaymentInstrumentDAO paymentInstrumentDAOMock;
    @MockBean
    private PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAOMock;
    @Autowired
    private PaymentInstrumentDAOService paymentInstrumentDAOService;


    @Before
    public void initTest() {
        Mockito.reset(paymentInstrumentDAOMock, paymentInstrumentHistoryDAOMock);

        BDDMockito.when(paymentInstrumentDAOMock.findById(Mockito.eq("prova"))).thenAnswer((Answer<PaymentInstrument>)
                invocation -> {
                    PaymentInstrument pi = new PaymentInstrument();
                    return pi;
                });

        BDDMockito.when(paymentInstrumentDAOMock.findById(Mockito.any())).thenAnswer((Answer<Optional<PaymentInstrument>>)
                invocation -> {
                    PaymentInstrument paymentInstrument = new PaymentInstrument();
                    return Optional.of(paymentInstrument);
                });

        PaymentInstrument paymentInstrument = new PaymentInstrument();

        BDDMockito.when(paymentInstrumentDAOMock.count(Mockito.eq((Example.of(paymentInstrument)))))
                .thenAnswer((Answer<Long>) invocation -> 4L);
        BDDMockito.when(paymentInstrumentDAOMock.save(Mockito.eq(paymentInstrument))).thenAnswer((Answer<PaymentInstrument>)
                invocation -> paymentInstrument);

        BDDMockito.when(paymentInstrumentHistoryDAOMock.checkActive(Mockito.eq("test"), Mockito.any()))
                .thenAnswer((Answer<List<PaymentInstrument>>) invocation -> new ArrayList<>());
    }


    @Test
    public void find() {
        Optional<PaymentInstrument> paymentInstrument = paymentInstrumentDAOService.find("test");
        Assert.assertNotNull(paymentInstrument.orElse(null));
        BDDMockito.verify(paymentInstrumentDAOMock).findById(Mockito.eq("test"));
    }

    @Test
    public void update() {
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrumentDAOService.update("test", paymentInstrument);
        Assert.assertNotNull(paymentInstrument);
        BDDMockito.verify(paymentInstrumentDAOMock).count(Mockito.eq(Example.of(paymentInstrument)));
        paymentInstrument.setHpan("test");
        BDDMockito.verify(paymentInstrumentDAOMock).save(Mockito.eq(paymentInstrument));
    }

    @Test
    public void delete() {
        paymentInstrumentDAOService.delete("prova");
        BDDMockito.verify(paymentInstrumentDAOMock).findById(Mockito.eq("prova"));
        BDDMockito.verify(paymentInstrumentDAOMock).count(Mockito.any(Example.class));
        BDDMockito.verify(paymentInstrumentDAOMock).save(Mockito.any(PaymentInstrument.class));
    }

    @Test
    public void checkActive() {
        Assert.assertFalse(paymentInstrumentDAOService.checkActive("test", ZonedDateTime.now()));
    }
}