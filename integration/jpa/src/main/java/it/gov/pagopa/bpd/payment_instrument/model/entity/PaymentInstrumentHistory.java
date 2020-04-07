package it.gov.pagopa.bpd.payment_instrument.model.entity;

import it.gov.pagopa.bpd.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"hpan"}, callSuper = false)
@Table(name = "bpd_payment_instrument_history", schema = "bpd_test")
public class PaymentInstrumentHistory implements Serializable {

    @Id
    @Column(name = "id_n")
    private String id;

    @Column(name = "hpan_s")
    private String hpan;

    @Column(name = "activation_t")
    private ZonedDateTime activationDate;

    @Column(name = "deactivation_t")
    private ZonedDateTime deactivationDate;
}
