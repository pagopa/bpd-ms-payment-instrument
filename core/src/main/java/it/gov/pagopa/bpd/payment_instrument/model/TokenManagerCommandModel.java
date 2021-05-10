package it.gov.pagopa.bpd.payment_instrument.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.header.Headers;

/**
 * Model containing the inbound message data
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenManagerCommandModel {

    private TokenManagerData payload;
    private Headers headers;

}
