package it.gov.pagopa.bpd.payment_instrument;


import eu.sia.meda.connector.jpa.CrudJpaDAO;
import eu.sia.meda.core.controller.CrudController;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrumentHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface PaymentInstrumentHistoryDAO extends CrudJpaDAO<PaymentInstrumentHistory, String> {

    @Query(value = "select pih " +
            "from PaymentInstrumentHistory pih " +
            "where pih.hpan = :hpan " +
            "and pih.activationDate <= :accountingDate " +
            "and (:accountingDate < pih.deactivationDate or pih.deactivationDate is null)"
    )
    List<PaymentInstrumentHistory> checkActive(@Param("hpan") String hpan, @Param("accountingDate") ZonedDateTime accountingDate);
}
