package it.gov.pagopa.bpd.payment_instrument.connector.jpa;


import it.gov.pagopa.bpd.common.connector.jpa.CrudJpaDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * Data Access Object to manage all CRUD operations to the database
 */
@Repository
public interface PaymentInstrumentHistoryDAO extends CrudJpaDAO<PaymentInstrumentHistory, String> {

    @Query(nativeQuery = true,
            value = "select count(1) " +
                    "from bpd_payment_instrument_history pih " +
                    "where pih.hpan_s = :hpan " +
                    "and date_trunc('day',pih.activation_t) < :accountingDate " +
                    "and (:accountingDate <= date_trunc('day',pih.deactivation_t) " +
                    "or pih.deactivation_t is null)"
    )
    Long countActive(@Param("hpan") String hpan, @Param("accountingDate") LocalDate accountingDate);
}
