package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentHistoryDAO;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PaymentInstrumentServiceImpl.class)
@TestPropertySource(properties = "numMaxPaymentInstr=5")
public class PaymentInstrumentServiceImplTest {

    private static final String HASH_PAN = "hashPan";

    @MockBean
    private PaymentInstrumentDAO paymentInstrumentDAOMock;
    @MockBean
    private PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAOMock;
    @Autowired
    private PaymentInstrumentService paymentInstrumentService;


    @Before
    public void initTest() {
        Mockito.reset(paymentInstrumentDAOMock, paymentInstrumentHistoryDAOMock);

        BDDMockito.when(paymentInstrumentDAOMock.findById(Mockito.eq(HASH_PAN))).thenAnswer((Answer<PaymentInstrument>)
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

        BDDMockito.when(paymentInstrumentHistoryDAOMock.checkActive(Mockito.eq(HASH_PAN), Mockito.any()))
                .thenAnswer((Answer<List<PaymentInstrument>>) invocation -> new ArrayList<>());
    }


    @Test
    public void find() {
        Optional<PaymentInstrument> paymentInstrument = paymentInstrumentService.find(HASH_PAN);

        Assert.assertNotNull(paymentInstrument.orElse(null));
        BDDMockito.verify(paymentInstrumentDAOMock).findById(Mockito.eq(HASH_PAN));
    }

    @Test
    public void update() {
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrumentService.update(HASH_PAN, paymentInstrument);

        Assert.assertNotNull(paymentInstrument);
        BDDMockito.verify(paymentInstrumentDAOMock).count(Mockito.eq(Example.of(paymentInstrument)));
        paymentInstrument.setHpan(HASH_PAN);
        BDDMockito.verify(paymentInstrumentDAOMock).save(Mockito.eq(paymentInstrument));
    }

    @Test
    public void deleteOK() {
        paymentInstrumentService.delete(HASH_PAN);

        BDDMockito.verify(paymentInstrumentDAOMock).findById(Mockito.eq(HASH_PAN));
        BDDMockito.verify(paymentInstrumentDAOMock).count(Mockito.any(Example.class));
        BDDMockito.verify(paymentInstrumentDAOMock).save(Mockito.any(PaymentInstrument.class));
    }

    @Test(expected = NeverWantedButInvoked.class)
    public void deleteKO() {
        try {
            paymentInstrumentService.delete("invalid-test");
        } finally {
            BDDMockito.verify(paymentInstrumentDAOMock, Mockito.never()).save(Mockito.any(PaymentInstrument.class));
        }
    }

    @Test
    public void checkActive() {
        Assert.assertFalse(paymentInstrumentService.checkActive(HASH_PAN, OffsetDateTime.now()));
    }
}