package it.gov.pagopa.bpd.payment_instrument.connector.jpa.model;

import it.gov.pagopa.bpd.common.connector.jpa.model.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(name = "bpd_payment_instrument_error_token")
public class PaymentInstrumentErrorToken extends BaseEntity {

    @Id
    @Column(name = "id_s")
    String id;

    @Column(name = "token_data_s")
    String tokenData;

    @Column(name = "exception_message_s")
    String exceptionMessage;

}
