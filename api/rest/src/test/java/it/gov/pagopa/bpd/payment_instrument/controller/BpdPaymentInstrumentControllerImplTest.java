package it.gov.pagopa.bpd.payment_instrument.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.config.ArchConfiguration;
import it.gov.pagopa.bpd.payment_instrument.assembler.PaymentInstrumentResourceAssembler;
import it.gov.pagopa.bpd.payment_instrument.command.PaymentInstrumentDAOService;
import it.gov.pagopa.bpd.payment_instrument.factory.PaymentInstrumentFactory;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.model.resource.PaymentInstrumentResource;
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

import javax.annotation.PostConstruct;
import java.util.Optional;

//@WebMvcTest(BpdPaymentInstrumentControllerImpl.class)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BpdPaymentInstrumentControllerImpl.class})
//@ContextConfiguration(classes = BpdPaymentInstrumentControllerImpl.class)
@AutoConfigureMockMvc(secure = false)
//@WebMvcTest(secure = false)
public class BpdPaymentInstrumentControllerImplTest {

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
        paymentInstrument.setHpan("prova");
        Optional<PaymentInstrument> optional = Optional.of(paymentInstrument);
        BDDMockito.doReturn(optional).when(paymentInstrumentDAOServiceMock).find(Mockito.eq("hpan"));

        BDDMockito.doReturn(new PaymentInstrument()).when(paymentInstrumentDAOServiceMock).update(Mockito.eq("test"), Mockito.eq(paymentInstrument));

        BDDMockito.doReturn(true).when(paymentInstrumentDAOServiceMock).checkActive(Mockito.eq("hpan"), Mockito.any());

        BDDMockito.doNothing().when(paymentInstrumentDAOServiceMock).delete(Mockito.eq("test"));
    }


    //    @Test
    public void find() throws Exception {
        MvcResult result = (MvcResult) mvc.perform(MockMvcRequestBuilders
                .get("/bpd/payment-instruments/hpan")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        PaymentInstrumentResource pageResult = objectMapper.readValue(result.getResponse().getContentAsString(),
                PaymentInstrumentResource.class);
        Assert.assertNotNull(pageResult);
        BDDMockito.verify(paymentInstrumentDAOServiceMock).find(Mockito.eq("hpan"));
        BDDMockito.verify(paymentInstrumentResourceAssemblerMock).toResource(Mockito.any(PaymentInstrument.class));
    }

    //    @Test
    public void update() throws Exception {
//        PaymentInstrumentDTO paymentInstrument = new PaymentInstrumentDTO();
//        MvcResult result = (MvcResult) mvc.perform(MockMvcRequestBuilders.put("/bpd/payment-instruments/hpan")
//            .contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(paymentInstrument)))
//            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
//        PaymentInstrumentResource pageResult = objectMapper.readValue(result.getResponse().getContentAsString(),
//                PaymentInstrumentResource.class);
//        Assert.assertNotNull(pageResult);
//        BDDMockito.verify(paymentInstrumentDAOServiceMock).update("test", Mockito.any());
//        BDDMockito.verify(paymentInstrumentFactoryMock).createModel(Mockito.eq(paymentInstrument));
//        BDDMockito.verify(paymentInstrumentResourceAssemblerMock).toResource(Mockito.any(PaymentInstrument.class));
    }

    @Test
    public void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/bpd/payment-instruments/hpan"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        BDDMockito.verify(paymentInstrumentDAOServiceMock).delete(Mockito.any());
    }

    //    @Test
    public void checkActive() throws Exception {
//        ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
//        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/bpd/payment-instruments/hpan/history")
//                .param("accountingDate",date.format(dateTimeFormatter)))
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
//        Assert.assertTrue(Mockito.any());
//        BDDMockito.verify(paymentInstrumentDAOServiceMock).checkActive(Mockito.eq("tets"), Mockito.any());
    }
}