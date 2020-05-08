package it.gov.pagopa.bpd.payment_instrument.model.entity;

import it.gov.pagopa.bpd.common.model.entity.BaseEntity;
import it.gov.pagopa.bpd.common.model.entity.converter.UpperCaseConverter;
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

    @Convert(converter = UpperCaseConverter.class)
    @Column(name = "fiscal_code_s")
    private String fiscalCode;

    @Column(name = "enrollment_t")
    private OffsetDateTime activationDate;

    @Column(name = "cancellation_t")
    private OffsetDateTime deactivationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_c")
    private Status status;

    public enum Status {
        ACTIVE, INACTIVE
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        activationDate = OffsetDateTime.now();
    }

    public String getFiscalCode() {
        return fiscalCode != null ? fiscalCode.toUpperCase() : null;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode != null ? fiscalCode.toUpperCase() : null;
    }
}




