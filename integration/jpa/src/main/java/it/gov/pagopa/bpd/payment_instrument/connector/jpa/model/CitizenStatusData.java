package it.gov.pagopa.bpd.payment_instrument.connector.jpa.model;

import it.gov.pagopa.bpd.common.connector.jpa.model.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"fiscalCode"}, callSuper = false)
@Table(name = "bpd_citizen_status_data")
public class CitizenStatusData extends BaseEntity implements Serializable {

    @Id
    @Column(name="fiscal_code_s")
    String fiscalCode;

    @Column(name = "update_timestamp_t")
    OffsetDateTime updateDateTime;

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
    }

}
