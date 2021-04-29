package it.gov.pagopa.bpd.payment_instrument.connector.jpa;

import it.gov.pagopa.bpd.common.connector.jpa.CrudJpaDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Data Access Object to manage all CRUD operations to the database
 */
@Repository
public interface PaymentInstrumentDAO extends CrudJpaDAO<PaymentInstrument, String> {

    List<PaymentInstrument> findByFiscalCode(String fiscalCode);

    @Deprecated
    List<PaymentInstrument> findByHpanIn(List<String> idList);
//    List<PaymentInstrument>  saveAll(Iterable<PaymentInstrument> paymentInstrumentList);


    @Modifying
    @Query("update PaymentInstrument " +
            "set enabled = true, " +
            "updateDate = :updateDateTime, " +
            "updateUser = 'rollback_recesso', " +
            "status = 'ACTIVE', " +
            "deactivationDate = null " +
            "where deactivationDate >= :requestTimestamp " +
            "and fiscal_code_s = :fiscalCode")
    void reactivateForRollback(@Param("fiscalCode") String fiscalCode,
                               @Param("requestTimestamp") OffsetDateTime requestTimestamp,
                               @Param("updateDateTime") OffsetDateTime updateDateTime);

    @Query("select count(*) as count,  " +
            "bpi.channel as channel  " +
            "from PaymentInstrument bpi " +
            "where bpi.fiscalCode = :fiscalCode " +
            "and (:channel is null or bpi.channel = :channel) " +
            "and bpi.enabled = true " +
            "group by channel"
    )
    @Deprecated
    List<PaymentInstrumentConverter> getPaymentInstrument(
            @Param("fiscalCode") String fiscalCode,
            @Param("channel") String channel);

    List<PaymentInstrument> findByHpanMasterOrHpan(String hpanMaster, String hpan);

    @Query("select bpi from PaymentInstrument bpi " +
            "where bpi.par = :par "
    )
    List<PaymentInstrument> getFromPar(@Param("par") String par);
}
