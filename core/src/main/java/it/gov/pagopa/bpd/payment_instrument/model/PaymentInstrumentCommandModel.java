package it.gov.pagopa.bpd.payment_instrument.model;

import it.gov.pagopa.bpd.payment_instrument.publisher.model.PaymentInstrumentUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.header.Headers;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInstrumentCommandModel {

    private PaymentInstrumentUpdate payload;
    private Headers headers;
}
