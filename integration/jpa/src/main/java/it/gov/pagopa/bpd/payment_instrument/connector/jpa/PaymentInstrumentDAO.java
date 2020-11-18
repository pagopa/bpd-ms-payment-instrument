package it.gov.pagopa.bpd.payment_instrument.connector.jpa;

import it.gov.pagopa.bpd.common.connector.jpa.CrudJpaDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Data Access Object to manage all CRUD operations to the database
 */
@Repository
public interface PaymentInstrumentDAO extends CrudJpaDAO<PaymentInstrument, String> {

    List<PaymentInstrument> findByFiscalCode(String fiscalCode);

    @Modifying
    @Query("update PaymentInstrument " +
            "set enabled = true " +
            "where deactivationDate >= :requestTimestamp " +
            "and fiscal_code_s = :fiscalCode")
    void reactivateForRollback(@Param("fiscalCode") String fiscalCode, @Param("requestTimestamp") OffsetDateTime requestTimestamp);

}
