package it.gov.pagopa.bpd.payment_instrument.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.config.ArchConfiguration;
import it.gov.pagopa.bpd.payment_instrument.assembler.PaymentInstrumentResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.factory.PaymentInstrumentFactory;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentResource;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentDAOService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BpdPaymentInstrumentControllerImpl.class})
@AutoConfigureMockMvc(secure = false)
@EnableWebMvc
public class BpdPaymentInstrumentControllerImplTest {

    public static final OffsetDateTime CURRENT_DATE_TIME = OffsetDateTime.now(ZoneOffset.UTC);


    @Autowired
    protected MockMvc mvc;
    protected ObjectMapper objectMapper = new ArchConfiguration().objectMapper();
    @MockBean
    private PaymentInstrumentDAOService paymentInstrumentDAOServiceMock;
    @SpyBean
    private PaymentInstrumentResourceAssembler paymentInstrumentResourceAssemblerMock;
    @SpyBean
    private PaymentInstrumentFactory paymentInstrumentFactoryMock;

    @PostConstruct
    public void configureTest() {
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setHpan("hpan");
        paymentInstrument.setActivationDate(CURRENT_DATE_TIME);
        paymentInstrument.setStatus(PaymentInstrument.Status.ACTIVE);

        BDDMockito.doReturn(Optional.of(paymentInstrument)).when(paymentInstrumentDAOServiceMock).find(Mockito.eq("hpan"));

        BDDMockito.doReturn(new PaymentInstrument()).when(paymentInstrumentDAOServiceMock).update(Mockito.eq("hpan"), Mockito.eq(paymentInstrument));

        BDDMockito.doReturn(true).when(paymentInstrumentDAOServiceMock).checkActive(Mockito.eq("hpan"), Mockito.any());

        BDDMockito.doNothing().when(paymentInstrumentDAOServiceMock).delete(Mockito.eq("hpan"));
    }

    @Test
    public void find() throws Exception {
        MvcResult result = (MvcResult) mvc.perform(MockMvcRequestBuilders
                .get("/bpd/payment-instruments/hpan")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        PaymentInstrumentResource pageResult = objectMapper.readValue(result.getResponse().getContentAsString(),
                PaymentInstrumentResource.class);
        Assert.assertNotNull(pageResult);
        BDDMockito.verify(paymentInstrumentDAOServiceMock).find(Mockito.eq("hpan"));
        BDDMockito.verify(paymentInstrumentResourceAssemblerMock).toResource(Mockito.any(PaymentInstrument.class));
    }

    @Test
    public void update() throws Exception {
        PaymentInstrumentDTO paymentInstrument = new PaymentInstrumentDTO();
        paymentInstrument.setActivationDate(CURRENT_DATE_TIME);
        MvcResult result = (MvcResult) mvc.perform(MockMvcRequestBuilders.put("/bpd/payment-instruments/hpan")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(paymentInstrument)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        PaymentInstrumentResource pageResult = objectMapper.readValue(result.getResponse().getContentAsString(),
                PaymentInstrumentResource.class);
        Assert.assertNotNull(pageResult);
        BDDMockito.verify(paymentInstrumentDAOServiceMock).update(Mockito.eq("hpan"), Mockito.any());
        BDDMockito.verify(paymentInstrumentFactoryMock).createModel(Mockito.eq(paymentInstrument));
        BDDMockito.verify(paymentInstrumentResourceAssemblerMock).toResource(Mockito.any(PaymentInstrument.class));
    }

    @Test
    public void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/bpd/payment-instruments/hpan"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        BDDMockito.verify(paymentInstrumentDAOServiceMock).delete(Mockito.any());
    }

    @Test
    public void checkActive() throws Exception {
        OffsetDateTime date = OffsetDateTime.from(CURRENT_DATE_TIME);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/bpd/payment-instruments/hpan/history")
                .param("accountingDate", date.format(dateTimeFormatter)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
        Assert.assertNotNull(result);
        BDDMockito.verify(paymentInstrumentDAOServiceMock).checkActive(Mockito.eq("hpan"), Mockito.any());
    }
}