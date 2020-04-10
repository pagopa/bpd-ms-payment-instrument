package it.gov.pagopa.bpd.payment_instrument.model.entity;

import it.gov.pagopa.bpd.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"hpan"}, callSuper = false)
@Table(name = "bpd_payment_instrument")
public class PaymentInstrument extends BaseEntity {

    @Id
    @Column(name = "hpan_s")
    private String hpan;

    @Column(name = "fiscal_code_s")
    private String fiscalCode;

    @Column(name = "enrollment_t")
    private ZonedDateTime activationDate;

    @Column(name = "cancellation_t")
    private ZonedDateTime cancellationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_c")
    private Status status;

    @Override
    protected void onUpdate() {
        super.onUpdate();
        activationDate = ZonedDateTime.now();
    }

    public enum Status {
        ACTIVE, INACTIVE
    }
}




