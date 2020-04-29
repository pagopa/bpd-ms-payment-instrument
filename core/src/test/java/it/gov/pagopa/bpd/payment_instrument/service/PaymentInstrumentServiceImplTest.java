package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.PaymentInstrumentHistoryDAO;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentNotFoundException;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PaymentInstrumentServiceImpl.class)
@TestPropertySource(properties = "numMaxPaymentInstr=5")
public class PaymentInstrumentServiceImplTest {

    private static final String EXISTING_HASH_PAN = "existing-hpan";
    private static final String NOT_EXISTING_HASH_PAN = "not-existing-hpan";

    @MockBean
    private PaymentInstrumentDAO paymentInstrumentDAOMock;
    @MockBean
    private PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAOMock;
    @Autowired
    private PaymentInstrumentService paymentInstrumentService;


    @Before
    public void initTest() {
        Mockito.reset(paymentInstrumentDAOMock, paymentInstrumentHistoryDAOMock);

        when(paymentInstrumentDAOMock.getOne(anyString()))
                .thenAnswer(invocation -> {
                    String hashPan = invocation.getArgument(0, String.class);
                    if (!EXISTING_HASH_PAN.equals(hashPan)) {
                        throw new EntityNotFoundException();

                    }
                    PaymentInstrument pi = new PaymentInstrument();
                    pi.setHpan(hashPan);
                    return pi;
                });

        when(paymentInstrumentDAOMock.findById(anyString()))
                .thenAnswer(invocation -> {
                    String hashPan = invocation.getArgument(0, String.class);
                    Optional<PaymentInstrument> result = Optional.empty();
                    if (EXISTING_HASH_PAN.equals(hashPan)) {
                        PaymentInstrument pi = new PaymentInstrument();
                        pi.setHpan(hashPan);
                        result = Optional.of(pi);
                    }
                    return result;
                });


        when(paymentInstrumentDAOMock.count(any(Example.class)))
                .thenAnswer(invocation -> 4L);

        when(paymentInstrumentDAOMock.save(any(PaymentInstrument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, PaymentInstrument.class));

        when(paymentInstrumentHistoryDAOMock.checkActive(eq(EXISTING_HASH_PAN), any()))
                .thenAnswer(invocation -> new ArrayList<>());
    }


    @Test
    public void find_OK() {
        final String hashPan = EXISTING_HASH_PAN;

        PaymentInstrument result = paymentInstrumentService.find(hashPan);

        assertNotNull(result);
        verify(paymentInstrumentDAOMock, only()).findById(eq(hashPan));
        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
    }


    @Test(expected = PaymentInstrumentNotFoundException.class)
    public void find_KO() {
        final String hashPan = NOT_EXISTING_HASH_PAN;

        paymentInstrumentService.find(hashPan);

        verify(paymentInstrumentDAOMock, only()).findById(eq(hashPan));
        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
    }


    @Test
    public void createOrUpdate_createOK() {
        final String hashPan = NOT_EXISTING_HASH_PAN;
        PaymentInstrument paymentInstrument = new PaymentInstrument();

        PaymentInstrument result = paymentInstrumentService.createOrUpdate(hashPan, paymentInstrument);

        assertNotNull(paymentInstrument);
        assertEquals(hashPan, result.getHpan());
        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        verify(paymentInstrumentDAOMock, times(1)).count(eq(Example.of(paymentInstrument)));
        paymentInstrument.setHpan(hashPan);
        verify(paymentInstrumentDAOMock, times(1)).save(eq(paymentInstrument));
        verifyNoMoreInteractions(paymentInstrumentDAOMock);
    }


    @Test
    public void createOrUpdate_updateOK() {
        final String hashPan = EXISTING_HASH_PAN;
        PaymentInstrument paymentInstrument = new PaymentInstrument();

        PaymentInstrument result = paymentInstrumentService.createOrUpdate(hashPan, paymentInstrument);

        assertNotNull(paymentInstrument);
        assertEquals(hashPan, result.getHpan());
        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        paymentInstrument.setHpan(hashPan);
        verify(paymentInstrumentDAOMock, times(1)).save(eq(paymentInstrument));
        verifyNoMoreInteractions(paymentInstrumentDAOMock);
    }


    @Test
    public void deleteOK() {
        final String hashPan = EXISTING_HASH_PAN;

        paymentInstrumentService.delete(hashPan);

        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        verify(paymentInstrumentDAOMock, times(1)).save(any(PaymentInstrument.class));
        verifyNoMoreInteractions(paymentInstrumentDAOMock);
    }


    @Test(expected = PaymentInstrumentNotFoundException.class)
    public void deleteKO() {
        final String hashPan = NOT_EXISTING_HASH_PAN;

        try {
            paymentInstrumentService.delete(hashPan);

        } finally {
            verify(paymentInstrumentDAOMock, only()).findById(eq(hashPan));
            verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        }
    }


    @Test
    public void checkActive() {
        Assert.assertFalse(paymentInstrumentService.checkActive(EXISTING_HASH_PAN, OffsetDateTime.now()));
    }

}