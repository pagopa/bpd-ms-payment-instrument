package it.gov.pagopa.bpd.payment_instrument.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.config.ArchConfiguration;
import it.gov.pagopa.bpd.payment_instrument.assembler.PaymentInstrumentResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.factory.PaymentInstrumentFactory;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentResource;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    private PaymentInstrumentService paymentInstrumentServiceMock;
    @SpyBean
    private PaymentInstrumentResourceAssembler paymentInstrumentResourceAssemblerMock;
    @SpyBean
    private PaymentInstrumentFactory paymentInstrumentFactoryMock;

    @PostConstruct
    public void configureTest() {
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setActivationDate(CURRENT_DATE_TIME);
        paymentInstrument.setStatus(PaymentInstrument.Status.ACTIVE);
        paymentInstrument.setFiscalCode("DHFIVD85M84D048L");

        doReturn(paymentInstrument)
                .when(paymentInstrumentServiceMock).find(eq("hpan"));

        doReturn(new PaymentInstrument())
                .when(paymentInstrumentServiceMock).createOrUpdate(eq("hpan"), eq(paymentInstrument));

        doReturn(true)
                .when(paymentInstrumentServiceMock).checkActive(eq("hpan"), any());

        doNothing()
                .when(paymentInstrumentServiceMock).delete(eq("hpan"));
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
        assertNotNull(pageResult);
        verify(paymentInstrumentServiceMock).find(eq("hpan"));
        verify(paymentInstrumentResourceAssemblerMock).toResource(any(PaymentInstrument.class));
    }

    @Test
    public void update() throws Exception {
        PaymentInstrumentDTO paymentInstrument = new PaymentInstrumentDTO();
        paymentInstrument.setFiscalCode("DHFIVD85M84D048L");
        paymentInstrument.setActivationDate(CURRENT_DATE_TIME);
        MvcResult result = (MvcResult) mvc.perform(MockMvcRequestBuilders.put("/bpd/payment-instruments/hpan")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(paymentInstrument)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        PaymentInstrumentResource pageResult = objectMapper.readValue(result.getResponse().getContentAsString(),
                PaymentInstrumentResource.class);
        assertNotNull(pageResult);
        verify(paymentInstrumentServiceMock).createOrUpdate(eq("hpan"), any());
        verify(paymentInstrumentFactoryMock).createModel(eq(paymentInstrument));
        verify(paymentInstrumentResourceAssemblerMock).toResource(any(PaymentInstrument.class));
    }

    @Test
    public void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/bpd/payment-instruments/hpan"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        verify(paymentInstrumentServiceMock).delete(any());
    }

    @Test
    public void checkActive() throws Exception {
        OffsetDateTime date = OffsetDateTime.from(CURRENT_DATE_TIME);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/bpd/payment-instruments/hpan/history/active")
                .param("accountingDate", date.format(dateTimeFormatter)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
        assertNotNull(result);
        verify(paymentInstrumentServiceMock).checkActive(eq("hpan"), any());
    }
}