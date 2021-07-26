package it.gov.pagopa.bpd.payment_instrument.command;

import eu.sia.meda.BaseTest;
import it.gov.pagopa.bpd.payment_instrument.model.InboundCitizenStatusData;
import it.gov.pagopa.bpd.payment_instrument.model.ProcessCitizenUpdateEventCommandModel;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.CitizenStatusData;
import it.gov.pagopa.bpd.payment_instrument.service.mapper.CitizenStatusDataMapper;
import it.gov.pagopa.bpd.payment_instrument.service.CitizenStatusDataService;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;

public class ProcessCitizenUpdateEventCommandImplTest extends BaseTest {

    @Mock
    PaymentInstrumentService paymentInstrumentService;

    @Mock
    CitizenStatusDataService citizenStatusUpdateService;

    @Spy
    CitizenStatusDataMapper citizenStatusDataMapperSpy;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void initTest() {
        Mockito.reset(paymentInstrumentService, citizenStatusUpdateService, citizenStatusDataMapperSpy);
    }

    @Test
    public void TestExecute_OK() {

        InboundCitizenStatusData inboundCitizenStatusData = getRequestModel();
        CitizenStatusData citizenStatusData = getSavedModel();

        BDDMockito.doReturn(true).when(citizenStatusUpdateService).checkAndCreate(
                Mockito.eq(citizenStatusData));
        ProcessCitizenUpdateEventCommandImpl saveTransactionCommand = new ProcessCitizenUpdateEventCommandImpl(
                ProcessCitizenUpdateEventCommandModel.builder().payload(inboundCitizenStatusData).build(),
                citizenStatusUpdateService,
                paymentInstrumentService,
                citizenStatusDataMapperSpy);
        Boolean executed = saveTransactionCommand.doExecute();
        Mockito.verify(citizenStatusDataMapperSpy).map(Mockito.eq(inboundCitizenStatusData));
        Mockito.verify(citizenStatusUpdateService).checkAndCreate(Mockito.eq(citizenStatusData));
        Mockito.verify(paymentInstrumentService).deleteByFiscalCodeIfNotUpdated(
                Mockito.eq(getRequestModel().getFiscalCode()), Mockito.eq(getRequestModel().getUpdateDateTime()));
        Assert.assertTrue(executed);

    }

    @Test
    public void TestExecute_OK_Outdated() {

        InboundCitizenStatusData inboundCitizenStatusData = getRequestModel();
        CitizenStatusData citizenStatusData = getSavedModel();

        BDDMockito.doReturn(false).when(citizenStatusUpdateService).checkAndCreate(
                Mockito.eq(citizenStatusData));
        ProcessCitizenUpdateEventCommandImpl saveTransactionCommand = new ProcessCitizenUpdateEventCommandImpl(
                ProcessCitizenUpdateEventCommandModel.builder().payload(inboundCitizenStatusData).build(),
                citizenStatusUpdateService,
                paymentInstrumentService,
                citizenStatusDataMapperSpy);
        Boolean executed = saveTransactionCommand.doExecute();
        Mockito.verify(citizenStatusDataMapperSpy).map(Mockito.eq(inboundCitizenStatusData));
        Mockito.verify(citizenStatusUpdateService).checkAndCreate(Mockito.eq(citizenStatusData));
        Mockito.verifyNoMoreInteractions(paymentInstrumentService);
        Assert.assertTrue(executed);

    }


    @Test
    public void TestExecute_KO() {

        InboundCitizenStatusData inboundCitizenStatusData = getRequestModel();
        inboundCitizenStatusData.setEnabled(null);

        ProcessCitizenUpdateEventCommandImpl saveTransactionCommand = new ProcessCitizenUpdateEventCommandImpl(
                ProcessCitizenUpdateEventCommandModel.builder().payload(inboundCitizenStatusData).build(),
                citizenStatusUpdateService,
                paymentInstrumentService,
                citizenStatusDataMapperSpy);
        exceptionRule.expect(ConstraintViolationException.class);
        saveTransactionCommand.doExecute();
        Mockito.verifyZeroInteractions(paymentInstrumentService);

    }

    protected InboundCitizenStatusData getRequestModel() {
        return InboundCitizenStatusData.builder()
                .updateDateTime(OffsetDateTime.parse("2020-04-10T16:22:45.304Z"))
                .enabled(false)
                .fiscalCode("fiscalCode")
                .applyTo("all")
                .build();
    }

    protected CitizenStatusData getSavedModel() {
        CitizenStatusData citizenStatusData = CitizenStatusData.builder()
                .updateDateTime(OffsetDateTime.parse("2020-04-09T16:22:45.304Z"))
                .fiscalCode("fiscalCode")
                .build();
        citizenStatusData.setEnabled(false);
        return citizenStatusData;
    }

}