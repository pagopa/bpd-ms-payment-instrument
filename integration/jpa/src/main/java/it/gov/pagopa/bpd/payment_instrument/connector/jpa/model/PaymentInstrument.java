package it.gov.pagopa.bpd.payment_instrument.connector.jpa.model;

import it.gov.pagopa.bpd.common.connector.jpa.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"hpan"}, callSuper = false)
@Table(name = "bpd_payment_instrument")
public class PaymentInstrument extends BaseEntity implements Serializable,Persistable<String> {

    @Transient
    private boolean isNew = true;

    @Transient
    private boolean updatable = false;

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

    @Override
    public String getId() {
        return this.hpan;
    }


    @Override
    public boolean isNew() {
        if (!updatable) {
            return true;
        } else {
            return isNew;
        }
    }

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

}




