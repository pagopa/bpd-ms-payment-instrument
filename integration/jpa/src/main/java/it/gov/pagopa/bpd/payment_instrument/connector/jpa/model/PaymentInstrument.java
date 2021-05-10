package it.gov.pagopa.bpd.payment_instrument.connector.jpa.model;

import it.gov.pagopa.bpd.common.connector.jpa.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;

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
    private OffsetDateTime activationDate;

    @Column(name = "cancellation_t")
    private OffsetDateTime deactivationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_c")
    private Status status;

    @Column(name = "channel_s")
    private String channel;

    public enum Status {
        ACTIVE, INACTIVE
    }

    @Column(name = "hpan_master_s")
    private String hpanMaster;

    @Column(name = "par_s")
    private String par;

    @Column(name = "par_enrollment_t")
    private OffsetDateTime parActivationDate;

    @Column(name = "par_cancellation_t")
    private OffsetDateTime parDeactivationDate;

    @Column(name = "last_tkm_update_t")
    private OffsetDateTime lastTkmUpdate;

    @Override
    protected void onUpdate() {
        super.onUpdate();
        this.setUpdateUser(this.fiscalCode);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        this.setInsertUser(this.fiscalCode);
    }

}




