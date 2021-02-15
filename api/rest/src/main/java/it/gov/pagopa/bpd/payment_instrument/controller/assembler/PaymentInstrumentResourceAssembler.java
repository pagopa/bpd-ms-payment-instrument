package it.gov.pagopa.bpd.payment_instrument.controller.assembler;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentResource;
import it.gov.pagopa.bpd.payment_instrument.controller.model.TokenizedInstrument;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentServiceModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper between <PaymentInstrument> Entity class and <PaymentInstrumentResource> Resource class
 */
@Service
public class PaymentInstrumentResourceAssembler {

    public PaymentInstrumentResource toResource(List<PaymentInstrument> paymentInstrumentList) {
        PaymentInstrumentResource resource = null;

        if (paymentInstrumentList != null && !paymentInstrumentList.isEmpty()) {
            resource = new PaymentInstrumentResource();
            List<TokenizedInstrument> tokenizedInstrumentList = null;
            for (PaymentInstrument paymentInstrument : paymentInstrumentList) {
                if (paymentInstrument.getHpan().equals(paymentInstrument.getHpanMaster())) {
                    BeanUtils.copyProperties(paymentInstrument, resource);
                } else {
                    if (tokenizedInstrumentList == null) {
                        tokenizedInstrumentList = new ArrayList<>();
                    }
                    TokenizedInstrument tokenizedInstrument = new TokenizedInstrument();
                    BeanUtils.copyProperties(paymentInstrument, tokenizedInstrument);
                    tokenizedInstrument.setHashToken(paymentInstrument.getHpan());
                    tokenizedInstrumentList.add(tokenizedInstrument);
                }
            }
            resource.setTokenizedInstruments(tokenizedInstrumentList);
        }

        return resource;
    }

    public PaymentInstrumentResource fromServiceToResource(PaymentInstrumentServiceModel paymentInstrumentServiceModel) {
        PaymentInstrumentResource resource = null;

        if (paymentInstrumentServiceModel != null) {
            resource = new PaymentInstrumentResource();
            BeanUtils.copyProperties(paymentInstrumentServiceModel, resource);
        }

        return resource;
    }

}
