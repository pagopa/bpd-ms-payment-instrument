package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentHistoryDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentNotFoundException;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentNumbersExceededException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
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
    private static final String EXISTING_FISCAL_CODE = "existing-fiscal-code";
    private static final String EXISTING_HASH_PAN_INACTIVE = "existing-hpan-inactive";
    private static final String NOT_EXISTING_HASH_PAN = "not-existing-hpan";
    private long countResult;


    @MockBean
    private PaymentInstrumentDAO paymentInstrumentDAOMock;
    @MockBean
    private PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAOMock;
    @Autowired
    private PaymentInstrumentService paymentInstrumentService;


    @PostConstruct
    public void configureTest() {
        when(paymentInstrumentDAOMock.findById(anyString()))
                .thenAnswer(invocation -> {
                    String hashPan = invocation.getArgument(0, String.class);
                    Optional<PaymentInstrument> result = Optional.empty();
                    if (EXISTING_HASH_PAN.equals(hashPan)) {
                        PaymentInstrument pi = new PaymentInstrument();
                        pi.setHpan(hashPan);
                        result = Optional.of(pi);
                    }
                    if (EXISTING_HASH_PAN_INACTIVE.equals(hashPan)) {
                        PaymentInstrument pi = new PaymentInstrument();
                        pi.setHpan(hashPan);
                        pi.setEnabled(false);
                        result = Optional.of(pi);
                    }
                    return result;
                });


        when(paymentInstrumentDAOMock.count(any(Specification.class)))
                .thenAnswer(invocation -> countResult);

        when(paymentInstrumentDAOMock.save(any(PaymentInstrument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, PaymentInstrument.class));

        when(paymentInstrumentHistoryDAOMock.countActive(eq(EXISTING_HASH_PAN), any()))
                .thenAnswer(invocation -> (long) 0);

        when(paymentInstrumentDAOMock.findByFiscalCode(eq(EXISTING_FISCAL_CODE)))
                .thenAnswer(invocation -> {
                    ArrayList<PaymentInstrument> result = new ArrayList<>();

                    PaymentInstrument activePI = new PaymentInstrument();
                    activePI.setHpan(EXISTING_HASH_PAN);
                    result.add(activePI);

                    PaymentInstrument inactivePI = new PaymentInstrument();
                    inactivePI.setHpan(EXISTING_HASH_PAN_INACTIVE);
                    inactivePI.setEnabled(false);
                    result.add(inactivePI);

                    return result;
                });

    }


    @Before
    public void initMockVariables() {
        countResult = 4;
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
        paymentInstrument.setFiscalCode("ALSTRD85M84K048F");

        PaymentInstrument result = paymentInstrumentService.createOrUpdate(hashPan, paymentInstrument);

        assertNotNull(paymentInstrument);
        assertEquals(hashPan, result.getHpan());
        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        verify(paymentInstrumentDAOMock, times(1)).count(any(Specification.class));
        verify(paymentInstrumentDAOMock, times(1)).save(eq(paymentInstrument));
    }


    @Test
    public void createOrUpdate_updateOK() {
        final String hashPan = EXISTING_HASH_PAN_INACTIVE;
        PaymentInstrument paymentInstrument = new PaymentInstrument();

        PaymentInstrument result = paymentInstrumentService.createOrUpdate(hashPan, paymentInstrument);

        assertNotNull(paymentInstrument);
        assertEquals(hashPan, result.getHpan());
        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        verify(paymentInstrumentDAOMock, times(1)).save(eq(paymentInstrument));
        verifyNoMoreInteractions(paymentInstrumentDAOMock);
    }

    @Test
    public void createOrUpdate_updateOK_AlreadyActive() {
        final String hashPan = EXISTING_HASH_PAN;
        PaymentInstrument paymentInstrument = new PaymentInstrument();

        PaymentInstrument result = paymentInstrumentService.createOrUpdate(hashPan, paymentInstrument);

        assertNotNull(paymentInstrument);
        assertEquals(hashPan, result.getHpan());
        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        verifyNoMoreInteractions(paymentInstrumentDAOMock);
    }


    @Test(expected = PaymentInstrumentNumbersExceededException.class)
    public void createOrUpdate_paymentInstrumentNumbersExceededError() {
        countResult = 5;
        final String hashPan = NOT_EXISTING_HASH_PAN;
        PaymentInstrument paymentInstrument = new PaymentInstrument();

        PaymentInstrument result = paymentInstrumentService.createOrUpdate(hashPan, paymentInstrument);

        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        verify(paymentInstrumentDAOMock, times(1)).count(any(Specification.class));
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
    public void deleteByFiscalCodeOK() {
        final String fiscalCode = EXISTING_FISCAL_CODE;
        paymentInstrumentService.deleteByFiscalCode(fiscalCode);

        verify(paymentInstrumentDAOMock, times(1)).findByFiscalCode(eq(fiscalCode));
        verify(paymentInstrumentDAOMock, times(2)).save(any(PaymentInstrument.class));
    }

    @Test
    public void checkActive() {
        Assert.assertFalse(paymentInstrumentService.checkActive(EXISTING_HASH_PAN, OffsetDateTime.now()));
    }

}