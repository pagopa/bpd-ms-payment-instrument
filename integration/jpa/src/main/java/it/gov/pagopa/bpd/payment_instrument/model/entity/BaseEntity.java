package it.gov.pagopa.bpd.payment_instrument.model.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.ZonedDateTime;

@MappedSuperclass
@Data
public abstract class BaseEntity implements Serializable {

    @Column(name = "INSERT_DATE_T")
    private ZonedDateTime insertDate;

    @Column(name = "INSERT_USER_S")
    private String insertUser;

    @Column(name = "UPDATE_DATE_T")
    private ZonedDateTime updateDate;

    @Column(name = "UPDATE_USER_S")
    private String updateUser;

    @Column(name = "ENABLED_B")
    @Where(clause = "ENABLED_B = 'TRUE'")
    private boolean enabled = true;

    @PrePersist
    protected void onCreate() {
        insertDate = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = ZonedDateTime.now();
    }
}
