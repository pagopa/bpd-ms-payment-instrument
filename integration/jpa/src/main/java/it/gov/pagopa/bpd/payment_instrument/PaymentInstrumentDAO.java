package it.gov.pagopa.bpd.payment_instrument;

import eu.sia.meda.connector.jpa.CrudJpaDAO;
import eu.sia.meda.connector.jpa.JPAConnector;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface PaymentInstrumentDAO extends CrudJpaDAO<PaymentInstrument, String> {

    @Query(
            value = "select 1 " +
                    "from bpd_test.bpd_payment_instrument_history pih " +
                    "where pih.hpan_s = :hpan " +
                    "and pih.activation_t <= :accountingDate " +
                    "and (:accountingDate < pih.deactivation_t or pih.deactivation_t isnull)",
            nativeQuery = true
    )
    Object checkActive(@Param("hpan") String hpan, @Param("accountingDate") ZonedDateTime accountingDate);

}
