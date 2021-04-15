package it.gov.pagopa.bpd.payment_instrument.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.config.ArchConfiguration;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentConverter;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import it.gov.pagopa.bpd.payment_instrument.controller.assembler.PaymentInstrumentConverterResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.controller.assembler.PaymentInstrumentHistoryResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.controller.assembler.PaymentInstrumentResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.controller.factory.PaymentInstrumentFactory;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentConverterResource;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentDTO;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentHistoryResource;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentResource;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentServiceModel;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.ArrayList;
import java.util.List;

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
    private PaymentInstrumentConverterResourceAssembler paymentInstrumentConverterResourceAssemblerMock;
    @SpyBean
    private PaymentInstrumentHistoryResourceAssembler paymentInstrumentHistoryResourceAssemblerMock;
    @SpyBean
    private PaymentInstrumentFactory paymentInstrumentFactoryMock;

    @PostConstruct
    public void configureTest() {

        List<PaymentInstrument> paymentInstrumentList = new ArrayList<>();
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setActivationDate(CURRENT_DATE_TIME);
        paymentInstrument.setStatus(PaymentInstrument.Status.ACTIVE);
        paymentInstrument.setFiscalCode("DHFIVD85M84D048L");
        paymentInstrument.setHpan("hpan");
        paymentInstrument.setHpanMaster(paymentInstrument.getHpan());
        paymentInstrumentList.add(paymentInstrument);
        PaymentInstrumentHistory pih = new PaymentInstrumentHistory();
        pih.setFiscalCode("DHFIVD85M84D048L");

        List<PaymentInstrumentConverter> converterResources = new ArrayList<>();
        PaymentInstrumentConverter converter = new PaymentInstrumentConverter() {
            @Override
            public Long getCount() {
                return 1L;
            }

            @Override
            public String getChannel() {
                return "channel";
            }
        };
        converterResources.add(converter);

        List<PaymentInstrumentHistory> pihRes = new ArrayList<>();
        PaymentInstrumentHistory resource = new PaymentInstrumentHistory();
        resource.setFiscalCode("DHFIVD85M84D048L");
        resource.setActivationDate(OffsetDateTime.now());
        pihRes.add(resource);


        doReturn(paymentInstrument)
                .when(paymentInstrumentServiceMock).find(eq("hpan"), eq("DHFIVD85M84D048L"));

        doReturn(new PaymentInstrumentServiceModel())
                .when(paymentInstrumentServiceMock).createOrUpdate(eq("hpan"), any());

        doReturn(pih)
                .when(paymentInstrumentServiceMock).checkActive(eq("hpan"), any());

        doNothing()
                .when(paymentInstrumentServiceMock).delete(eq("hpan"), Mockito.any(), Mockito.any());

        doNothing()
                .when(paymentInstrumentServiceMock).deleteByFiscalCode(eq("fiscalCode"), eq("channel"));

        doReturn(converterResources)
                .when(paymentInstrumentServiceMock).getPaymentInstrument(eq("fiscalCode"), eq("channel"));

        doReturn(pihRes)
                .when(paymentInstrumentServiceMock).findHistory(eq("DHFIVD85M84D048L"), eq("hpan"));
    }

    @Test
    public void find() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/bpd/payment-instruments/hpan?fiscalCode=DHFIVD85M84D048L")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        PaymentInstrumentResource pageResult = objectMapper.readValue(result.getResponse().getContentAsString(),
                PaymentInstrumentResource.class);
        assertNotNull(pageResult);
        verify(paymentInstrumentServiceMock).find(eq("hpan"), eq("DHFIVD85M84D048L"));
        verify(paymentInstrumentResourceAssemblerMock).toResource(any());
    }

    @Test
    public void update() throws Exception {
        PaymentInstrumentDTO paymentInstrument = new PaymentInstrumentDTO();
        paymentInstrument.setFiscalCode("DHFIVD85M84D048L");
        paymentInstrument.setActivationDate(CURRENT_DATE_TIME);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.put("/bpd/payment-instruments/hpan")
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
        verify(paymentInstrumentResourceAssemblerMock).fromServiceToResource(any(PaymentInstrumentServiceModel.class));
    }

    @Test
    public void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/bpd/payment-instruments/hpan"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        verify(paymentInstrumentServiceMock).delete(any(), any(), any());
    }

    @Test
    public void deleteByFiscalCode() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .delete("/bpd/payment-instruments/fiscal-code/fiscalCode/channel"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        verify(paymentInstrumentServiceMock).deleteByFiscalCode(any(), any());
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

    @Test
    public void rollback() throws Exception {
        OffsetDateTime date = OffsetDateTime.from(CURRENT_DATE_TIME);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        mvc.perform(MockMvcRequestBuilders.put("/bpd/payment-instruments/rollback/fiscalCode")
                .param("requestTimestamp", date.format(dateTimeFormatter)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        verify(paymentInstrumentServiceMock).reactivateForRollback(any(), any());
    }

    @Test
    public void getPaymentInstrumentNumber() throws Exception {
        PaymentInstrumentConverterResource converter = new PaymentInstrumentConverterResource();
        converter.setChannel("channel");
        converter.setCount(1L);
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/bpd/payment-instruments/number/fiscalCode?channel=channel")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        String contentString = result.getResponse().getContentAsString();
        List<PaymentInstrumentConverterResource> resource = objectMapper.readValue(
                contentString, new TypeReference<List<PaymentInstrumentConverterResource>>() {
                });

        Assert.assertNotNull(resource);
        verify(paymentInstrumentServiceMock).getPaymentInstrument(any(), any());
    }

    @Test
    public void getPaymentInstrumentHistoryDetails() throws Exception {
        PaymentInstrumentHistoryResource instrumentHistoryResource = new PaymentInstrumentHistoryResource();
        instrumentHistoryResource.setFiscalCode("DHFIVD85M84D048L");
        instrumentHistoryResource.setHpan("hpan");
        instrumentHistoryResource.setActivationDate(OffsetDateTime.now());
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/bpd/payment-instruments/DHFIVD85M84D048L/history?hpan=hpan")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        String contentString = result.getResponse().getContentAsString();
        List<PaymentInstrumentHistoryResource> resource = objectMapper.readValue(
                contentString, new TypeReference<List<PaymentInstrumentHistoryResource>>() {
                });

        Assert.assertNotNull(resource);
        verify(paymentInstrumentServiceMock).findHistory(any(), any());
        verify(paymentInstrumentHistoryResourceAssemblerMock).toResource(any());
    }
}