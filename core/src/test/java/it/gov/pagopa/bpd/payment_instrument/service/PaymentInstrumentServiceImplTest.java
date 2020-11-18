package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentHistoryDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentDifferentChannelException;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentNotFoundException;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentNumbersExceededException;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentOnDifferentUserException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
@TestPropertySource(locations = "classpath:config/paymentInstrument.properties")
public class PaymentInstrumentServiceImplTest {

    private static final String EXISTING_HASH_PAN = "existing-hpan";
    private static final String EXISTING_FISCAL_CODE = "existing-fiscal-code";
    private static final String EXISTING_FISCAL_CODE_ERROR = "existing-fiscal-code-error";
    private static final String EXISTING_HASH_PAN_INACTIVE = "existing-hpan-inactive";
    private static final String NOT_EXISTING_HASH_PAN = "not-existing-hpan";
    private static final String APPIO_CHANNEL = "app-io-channel";
    private static final String ANOTHER_CHANNEL_1 = "another-channel_1";
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
                        pi.setFiscalCode(EXISTING_FISCAL_CODE);
                        result = Optional.of(pi);
                    }
                    if (EXISTING_HASH_PAN_INACTIVE.equals(hashPan)) {
                        PaymentInstrument pi = new PaymentInstrument();
                        pi.setHpan(hashPan);
                        pi.setFiscalCode(EXISTING_FISCAL_CODE);
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
                    activePI.setFiscalCode(EXISTING_FISCAL_CODE);
                    activePI.setHpan(EXISTING_HASH_PAN);
                    activePI.setChannel(APPIO_CHANNEL);
                    result.add(activePI);

                    PaymentInstrument inactivePI = new PaymentInstrument();
                    inactivePI.setHpan(EXISTING_HASH_PAN_INACTIVE);
                    inactivePI.setFiscalCode(EXISTING_FISCAL_CODE);
                    inactivePI.setChannel(APPIO_CHANNEL);
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
        final String fiscalCode = EXISTING_FISCAL_CODE;

        PaymentInstrument result = paymentInstrumentService.find(hashPan,fiscalCode);

        assertNotNull(result);
        verify(paymentInstrumentDAOMock, only()).findById(eq(hashPan));
        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
    }


    @Test(expected = PaymentInstrumentNotFoundException.class)
    public void find_KO() {
        final String hashPan = NOT_EXISTING_HASH_PAN;
        final String fiscalCode = EXISTING_FISCAL_CODE;

        paymentInstrumentService.find(hashPan,fiscalCode);

        verify(paymentInstrumentDAOMock, only()).findById(eq(hashPan));
        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
    }

    @Test(expected = PaymentInstrumentOnDifferentUserException.class)
    public void find_KO_FiscalCode() {
        final String hashPan = EXISTING_HASH_PAN;
        final String fiscalCode = EXISTING_FISCAL_CODE_ERROR;

        paymentInstrumentService.find(hashPan,fiscalCode);

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
//        verify(paymentInstrumentDAOMock, times(1)).count(any(Specification.class));
        verify(paymentInstrumentDAOMock, times(1)).save(eq(paymentInstrument));
    }


    @Test
    public void createOrUpdate_updateOK() {
        final String hashPan = EXISTING_HASH_PAN_INACTIVE;
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setFiscalCode(EXISTING_FISCAL_CODE);

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
        paymentInstrument.setFiscalCode(EXISTING_FISCAL_CODE);

        PaymentInstrument result = paymentInstrumentService.createOrUpdate(hashPan, paymentInstrument);

        assertNotNull(paymentInstrument);
        assertEquals(hashPan, result.getHpan());
        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        verifyNoMoreInteractions(paymentInstrumentDAOMock);
    }

    @Test(expected = PaymentInstrumentOnDifferentUserException.class)
    public void createOrUpdate_updateOK_AlreadyActive_DifferentUser() {
        final String hashPan = EXISTING_HASH_PAN;
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setFiscalCode(EXISTING_FISCAL_CODE_ERROR);

        PaymentInstrument result = paymentInstrumentService.createOrUpdate(hashPan, paymentInstrument);

        assertNotNull(paymentInstrument);
        assertEquals(hashPan, result.getHpan());
        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        verifyNoMoreInteractions(paymentInstrumentDAOMock);
    }


//    @Test(expected = PaymentInstrumentNumbersExceededException.class)
//    public void createOrUpdate_paymentInstrumentNumbersExceededError() {
//        countResult = 5;
//        final String hashPan = NOT_EXISTING_HASH_PAN;
//        PaymentInstrument paymentInstrument = new PaymentInstrument();
//
//        PaymentInstrument result = paymentInstrumentService.createOrUpdate(hashPan, paymentInstrument);
//
//        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
//        verify(paymentInstrumentDAOMock, times(1)).count(any(Specification.class));
//        verifyNoMoreInteractions(paymentInstrumentDAOMock);
//    }

    @Test
    public void deleteOK() {
        final String hashPan = EXISTING_HASH_PAN;

        paymentInstrumentService.delete(hashPan, null, null);

        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        verify(paymentInstrumentDAOMock, times(1)).save(any(PaymentInstrument.class));
        verifyNoMoreInteractions(paymentInstrumentDAOMock);
    }

    @Test
    public void deleteOK_WithFiscalCodeAndCancellationDate() {
        final String hashPan = EXISTING_HASH_PAN;

        paymentInstrumentService.delete(hashPan, EXISTING_FISCAL_CODE, OffsetDateTime.now());

        verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        verify(paymentInstrumentDAOMock, times(1)).save(any(PaymentInstrument.class));
        verifyNoMoreInteractions(paymentInstrumentDAOMock);
    }

    @Test(expected = PaymentInstrumentOnDifferentUserException.class)
    public void deleteKO_WithWrongFiscalCode() {
        final String hashPan = EXISTING_HASH_PAN;

        try {
            paymentInstrumentService.delete(hashPan, EXISTING_FISCAL_CODE_ERROR, OffsetDateTime.now());
        } finally {
            verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
            verifyNoMoreInteractions(paymentInstrumentDAOMock);
        }

    }


    @Test(expected = PaymentInstrumentNotFoundException.class)
    public void deleteKO() {
        final String hashPan = NOT_EXISTING_HASH_PAN;

        try {
            paymentInstrumentService.delete(hashPan, null, null);

        } finally {
            verify(paymentInstrumentDAOMock, only()).findById(eq(hashPan));
            verify(paymentInstrumentDAOMock, times(1)).findById(eq(hashPan));
        }
    }

    @Test
    public void deleteByFiscalCode_AppIOChannel_OK() {
        final String fiscalCode = EXISTING_FISCAL_CODE;
        paymentInstrumentService.deleteByFiscalCode(fiscalCode, APPIO_CHANNEL);

        verify(paymentInstrumentDAOMock, times(1)).findByFiscalCode(eq(fiscalCode));
        verify(paymentInstrumentDAOMock, times(2)).save(any(PaymentInstrument.class));
    }

    @Test(expected = PaymentInstrumentDifferentChannelException.class)
    public void deleteByFiscalCode_AnotherChannel_KO() {
        paymentInstrumentService.deleteByFiscalCode(EXISTING_FISCAL_CODE, ANOTHER_CHANNEL_1);
    }

    @Test
    public void checkActive() {
        Assert.assertFalse(paymentInstrumentService.checkActive(EXISTING_HASH_PAN, OffsetDateTime.now()));
    }

    @Test
    public void reactivateForRollback() {
        paymentInstrumentService.reactivateForRollback("fiscalCode", OffsetDateTime.now());
        verify(paymentInstrumentDAOMock, times(1)).reactivateForRollback(Mockito.any(), Mockito.any());
    }

}