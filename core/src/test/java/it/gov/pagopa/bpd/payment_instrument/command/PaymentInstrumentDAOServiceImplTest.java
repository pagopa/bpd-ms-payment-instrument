package it.gov.pagopa.bpd.payment_instrument.command;

import eu.sia.meda.BaseTest;
import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentHistoryDAO;
import it.gov.pagopa.bpd.payment_instrument.command.config.PaymentInstrumentConfig;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
//@EnableConfigurationProperties(value = PaymentInstrumentConfig.class)
//@ContextConfiguration(classes = PaymentInstrumentDAOServiceImpl.class)
//@Import(value = PaymentInstrumentConfig.class)
@TestPropertySource(properties = "numMaxPaymentInstr:5")
public class PaymentInstrumentDAOServiceImplTest extends BaseTest {

    private PaymentInstrumentDAO paymentInstrumentDAOMock;
    private PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAOMock;
    private PaymentInstrumentDAOService paymentInstrumentDAOService;

    @Value(value = "${numMaxPaymentInstr}")
    private long numMaxPaymentInstr;


    public PaymentInstrumentDAOServiceImplTest() {
        this.paymentInstrumentHistoryDAOMock = Mockito.mock(PaymentInstrumentHistoryDAO.class);
        this.paymentInstrumentDAOMock = Mockito.mock(PaymentInstrumentDAO.class);
        this.paymentInstrumentDAOService = new PaymentInstrumentDAOServiceImpl
                (paymentInstrumentDAOMock, paymentInstrumentHistoryDAOMock);
        ReflectionTestUtils.setField(paymentInstrumentDAOService,"numMaxPaymentInstr",5);
    }

    @Before
    public void initTest(){
        Mockito.reset(paymentInstrumentDAOMock, paymentInstrumentHistoryDAOMock);

        BDDMockito.when(paymentInstrumentDAOMock.findById(Mockito.any())).thenAnswer((Answer<Optional<PaymentInstrument>>)
                invocation -> { PaymentInstrument paymentInstrument = new PaymentInstrument();
                return Optional.of(paymentInstrument);});

        PaymentInstrument paymentInstrument = new PaymentInstrument();

        BDDMockito.when(paymentInstrumentDAOMock.count(Mockito.eq((Example.of(paymentInstrument)))))
                .thenAnswer((Answer<Long>) invocation -> { return 4l; });
        BDDMockito.when(paymentInstrumentDAOMock.save(Mockito.eq(paymentInstrument))).thenAnswer((Answer<PaymentInstrument>)
                invocation -> { return paymentInstrument; });

        BDDMockito.when(paymentInstrumentHistoryDAOMock.checkActive(Mockito.eq("test"), Mockito.any()))
                .thenAnswer((Answer<List<PaymentInstrument>>) invocation -> { return new ArrayList<PaymentInstrument>(); });

    }


    @Test
    public void find() {
        Optional<PaymentInstrument> paymentInstrument = paymentInstrumentDAOService.find("test");
        Assert.assertNotNull(paymentInstrument.orElse(null));
        BDDMockito.verify(paymentInstrumentDAOMock).findById(Mockito.eq("test"));
    }

    @Test
    public void update() {
        PaymentInstrument paymentInstrument =  new PaymentInstrument();
        paymentInstrumentDAOService.update("test", paymentInstrument);

        Assert.assertNotNull(paymentInstrument);

    }

    @Test
    public void delete() { //TODO SISTEMARE LOGICA
        Optional<PaymentInstrument> paymentInstrument = paymentInstrumentDAOService.find("test");
        Assert.assertNotNull(paymentInstrument.orElse(null));
    }

    @Test
    public void checkActive() {
        Assert.assertFalse(paymentInstrumentDAOService.checkActive("test", ZonedDateTime.now()));
    }
}