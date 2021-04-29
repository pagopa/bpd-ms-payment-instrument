package it.gov.pagopa.bpd.payment_instrument.model;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentErrorDelete;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.header.Headers;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeletePaymentInstrumentErrorServiceModel {

    private PaymentInstrumentErrorDelete payload;
    private Headers headers;
}
