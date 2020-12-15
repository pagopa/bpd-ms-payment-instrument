package it.gov.pagopa.bpd.payment_instrument.assembler;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentServiceModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Mapper between <AwardPeriod> Entity class and <AwardPeriodServiceModel> Resource class
 */
@Service
public class PaymentInstrumentAssembler {

    public PaymentInstrument toResource(PaymentInstrumentServiceModel paymentInstrumentServiceModel, String id) {
        PaymentInstrument model = null;

        if (paymentInstrumentServiceModel != null) {
            model = new PaymentInstrument();
            BeanUtils.copyProperties(paymentInstrumentServiceModel, model);
            model.setStatus(PaymentInstrument.Status.ACTIVE);
            model.setHpan(id);
        }
        return model;
    }

}