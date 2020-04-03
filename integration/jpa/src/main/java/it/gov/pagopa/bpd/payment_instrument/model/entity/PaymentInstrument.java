package it.gov.pagopa.bpd.payment_instrument.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"hpan"}, callSuper = false)
@Table(name = "bpd_payment_instrument", schema = "bpd_test")     //TODO verificare lo schema(senza non và)
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

    @PreUpdate
    protected void onUpdate() {
        activationDate = ZonedDateTime.now();
    }

    public enum Status {
        ACTIVE, INACTIVE
    }
}




