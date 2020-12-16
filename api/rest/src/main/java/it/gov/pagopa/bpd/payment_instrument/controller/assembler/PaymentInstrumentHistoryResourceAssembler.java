package it.gov.pagopa.bpd.payment_instrument.controller.assembler;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentHistoryResource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper between <PaymentInstrumentHistory> Entity class and <PaymentInstrumentHistoryResource> Resource class
 */
@Service
public class PaymentInstrumentHistoryResourceAssembler {

    public List<PaymentInstrumentHistoryResource> toResource(List<PaymentInstrumentHistory> paymentInstrument) {
        List<PaymentInstrumentHistoryResource> resource = null;

        if (paymentInstrument != null) {
            resource = new ArrayList<PaymentInstrumentHistoryResource>();
            for (PaymentInstrumentHistory pih : paymentInstrument) {
                PaymentInstrumentHistoryResource pir = new PaymentInstrumentHistoryResource();
                BeanUtils.copyProperties(pih, pir);
                resource.add(pir);
            }
        }

        return resource;
    }
}
