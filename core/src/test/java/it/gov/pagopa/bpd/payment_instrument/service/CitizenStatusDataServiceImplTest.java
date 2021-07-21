package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.CitizenStatusDataDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.CitizenStatusData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CitizenStatusDataServiceImpl.class)
public class CitizenStatusDataServiceImplTest {

    @MockBean
    private CitizenStatusDataDAO citizenStatusDataDAOMock;

    @Autowired
    private CitizenStatusDataService citizenStatusDataService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void initTest() {
        Mockito.reset(citizenStatusDataDAOMock);
    }

    @Test
    public void test_checkAndUpdate_OK() {
        CitizenStatusData citizenStatusData =
                CitizenStatusData.builder()
                .fiscalCode("fiscalCode")
                .updateDateTime(OffsetDateTime.parse("2020-04-10T16:22:45.304Z"))
                .build();
        citizenStatusData.setEnabled(false);

        CitizenStatusData persistedCitizenStatusData =
                CitizenStatusData.builder()
                        .fiscalCode("fiscalCode")
                        .updateDateTime(OffsetDateTime.parse("2020-04-09T16:22:45.304Z"))
                        .build();
        persistedCitizenStatusData.setEnabled(false);

        BDDMockito.doReturn(Optional.of(persistedCitizenStatusData))
                .when(citizenStatusDataDAOMock)
                .findById(Mockito.eq("fiscalCode"));

        BDDMockito.doReturn(persistedCitizenStatusData)
                .when(citizenStatusDataDAOMock)
                .update(Mockito.eq(citizenStatusData));

        Boolean result = citizenStatusDataService.checkAndCreate(citizenStatusData);

        Assert.assertTrue(result);
        BDDMockito.verify(citizenStatusDataDAOMock).findById(Mockito.eq("fiscalCode"));
        BDDMockito.verify(citizenStatusDataDAOMock).update(Mockito.eq(citizenStatusData));


    }

    @Test
    public void test_checkAndUpdate_OK_Outdated() {
        CitizenStatusData citizenStatusData =
                CitizenStatusData.builder()
                        .fiscalCode("fiscalCode")
                        .updateDateTime(OffsetDateTime.parse("2020-04-10T16:22:45.304Z"))
                        .build();
        citizenStatusData.setEnabled(false);

        CitizenStatusData persistedCitizenStatusData =
                CitizenStatusData.builder()
                        .fiscalCode("fiscalCode")
                        .updateDateTime(OffsetDateTime.parse("2020-04-11T16:22:45.304Z"))
                        .build();
        persistedCitizenStatusData.setEnabled(true);

        BDDMockito.doReturn(Optional.of(persistedCitizenStatusData))
                .when(citizenStatusDataDAOMock)
                .findById(Mockito.eq("fiscalCode"));

        BDDMockito.doReturn(persistedCitizenStatusData)
                .when(citizenStatusDataDAOMock)
                .update(Mockito.eq(citizenStatusData));

        Boolean result = citizenStatusDataService.checkAndCreate(citizenStatusData);

        Assert.assertFalse(result);
        BDDMockito.verify(citizenStatusDataDAOMock).findById(Mockito.eq("fiscalCode"));
        BDDMockito.verifyNoMoreInteractions(citizenStatusDataDAOMock);

    }


}
