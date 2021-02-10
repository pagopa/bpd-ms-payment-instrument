package it.gov.pagopa.bpd.payment_instrument.connector.jpa.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(name = "bpd_payment_instrument_history")
public class PaymentInstrumentHistory implements Serializable {

    @Id
    @Column(name = "id_n")
    private String id;

    @Column(name = "hpan_s")
    private String hpan;

    @Column(name = "activation_t")
    private OffsetDateTime activationDate;

    @Column(name = "deactivation_t")
    private OffsetDateTime deactivationDate;

    @Column(name = "fiscal_code_s")
    private String fiscalCode;
}
