package it.gov.pagopa.bpd.payment_instrument.connector.jpa.model;

import it.gov.pagopa.bpd.common.connector.jpa.model.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(name = "bpd_payment_instrument_error_delete")
public class PaymentInstrumentErrorDelete extends BaseEntity {

    @Id
    @Column(name = "id_s")
    String id;

    @Column(name = "hpan_s")
    String hpan;

    @Column(name = "fiscal_code_s")
    String fiscalCode;

    @Column(name = "cancellation_date_s")
    String cancellationDate;

    @Column(name = "exception_message_s")
    String exceptionMessage;
}
