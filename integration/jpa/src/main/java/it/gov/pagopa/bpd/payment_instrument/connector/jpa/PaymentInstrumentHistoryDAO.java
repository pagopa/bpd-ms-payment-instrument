package it.gov.pagopa.bpd.payment_instrument.connector.jpa;


import it.gov.pagopa.bpd.common.connector.jpa.CrudJpaDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

/**
 * Data Access Object to manage all CRUD operations to the database
 */
@Repository
public interface PaymentInstrumentHistoryDAO extends CrudJpaDAO<PaymentInstrumentHistory, String> {

    @Query(value = "select count(1) " +
            "from PaymentInstrumentHistory pih " +
            "where pih.hpan = :hpan " +
            "and pih.activationDate <= :accountingDate " +
            "and (:accountingDate < pih.deactivationDate or pih.deactivationDate is null)"
    )
    Long countActive(@Param("hpan") String hpan, @Param("accountingDate") OffsetDateTime accountingDate);
}
