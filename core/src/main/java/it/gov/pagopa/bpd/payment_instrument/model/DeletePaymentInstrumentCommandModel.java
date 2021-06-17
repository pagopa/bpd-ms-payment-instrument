package it.gov.pagopa.bpd.payment_instrument.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.header.Headers;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeletePaymentInstrumentCommandModel {

    private DeletePaymentInstrument payload;
    private Headers headers;
}
