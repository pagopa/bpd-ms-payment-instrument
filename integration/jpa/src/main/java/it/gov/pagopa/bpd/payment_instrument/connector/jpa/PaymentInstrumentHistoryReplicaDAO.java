package it.gov.pagopa.bpd.payment_instrument.connector.jpa;


import it.gov.pagopa.bpd.common.connector.jpa.CrudJpaDAO;
import it.gov.pagopa.bpd.common.connector.jpa.ReadOnlyRepository;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object to manage all CRUD operations to the database
 */
@ReadOnlyRepository
public interface PaymentInstrumentHistoryReplicaDAO extends CrudJpaDAO<PaymentInstrumentHistory, String> {

    @Query("select pih " +
            "from PaymentInstrumentHistory pih " +
            "where pih.fiscalCode = :fiscalCode " +
            "and (:hpan is null or pih.hpan = :hpan)"
    )
    List<PaymentInstrumentHistory> find(@Param("fiscalCode") String fiscalCode, @Param("hpan") String hpan);

    @Query(nativeQuery = true,
            value = "select * " +
                    "from bpd_payment_instrument_history pih " +
                    "where pih.hpan_s = :hpan " +
                    "and date_trunc('day',pih.activation_t) < :accountingDate " +
                    "and (:accountingDate <= date_trunc('day',pih.deactivation_t) " +
                    "or pih.deactivation_t is null)"
    )
    PaymentInstrumentHistory findActive(@Param("hpan") String hpan, @Param("accountingDate") LocalDate accountingDate);


}
