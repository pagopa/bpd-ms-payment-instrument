package it.gov.pagopa.bpd.payment_instrument.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class PaymentInstrumentServiceModel {

    private String fiscalCode;

    private OffsetDateTime activationDate;

    private String channel;

    private List<String> tokenPanList;
}
