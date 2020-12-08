package it.gov.pagopa.bpd.payment_instrument.controller.assembler;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentConverter;
import it.gov.pagopa.bpd.payment_instrument.controller.model.PaymentInstrumentConverterResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentInstrumentConverterResourceAssembler {

    public List<PaymentInstrumentConverterResource> toResource(List<PaymentInstrumentConverter> paymentInstrument) {
        List<PaymentInstrumentConverterResource> resource = null;

        if (paymentInstrument != null) {
            resource = new ArrayList<PaymentInstrumentConverterResource>();
            for (PaymentInstrumentConverter pi : paymentInstrument) {
                PaymentInstrumentConverterResource pir = new PaymentInstrumentConverterResource();
                pir.setChannel(pi.getChannel());
                pir.setCount(pi.getCount());
                resource.add(pir);
            }
        }
        return resource;
    }
}
