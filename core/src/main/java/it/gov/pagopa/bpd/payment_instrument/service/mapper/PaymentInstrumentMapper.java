package it.gov.pagopa.bpd.payment_instrument.service.mapper;

import it.gov.pagopa.bpd.payment_instrument.publisher.model.OutgoingPaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.PaymentInstrumentUpdate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Class to be used to map a {@link PaymentInstrumentUpdate} from an* {@link OutgoingPaymentInstrument}
 */

@Service
public class PaymentInstrumentMapper {

    public OutgoingPaymentInstrument map(
            PaymentInstrumentUpdate pi) {

        OutgoingPaymentInstrument outgoingPaymentInstrument = null;

        if (pi != null) {
            outgoingPaymentInstrument = OutgoingPaymentInstrument.builder().build();
            BeanUtils.copyProperties(pi, outgoingPaymentInstrument);
            List<String> panList = new ArrayList();
            panList.add(pi.getHpan());
            outgoingPaymentInstrument.setHashToken(panList);
        }

        return outgoingPaymentInstrument;

    }

}
