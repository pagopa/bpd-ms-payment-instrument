package it.gov.pagopa.bpd.payment_instrument;


import eu.sia.meda.connector.jpa.CrudJpaDAO;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrumentHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Data Access Object to manage all CRUD operations to the database
 */
@Repository
public interface PaymentInstrumentHistoryDAO extends CrudJpaDAO<PaymentInstrumentHistory, String> {

    @Query(value = "select pih " +
            "from PaymentInstrumentHistory pih " +
            "where pih.hpan = :hpan " +
            "and pih.activationDate <= :accountingDate " +
            "and (:accountingDate < pih.deactivationDate or pih.deactivationDate is null)"
    )
    List<PaymentInstrumentHistory> checkActive(@Param("hpan") String hpan, @Param("accountingDate") OffsetDateTime accountingDate);
}
