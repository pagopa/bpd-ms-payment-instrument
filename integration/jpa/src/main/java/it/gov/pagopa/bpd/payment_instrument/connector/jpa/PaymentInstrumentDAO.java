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
    @Query(nativeQuery = true, value="update bpd_payment_instrument " +
            "set enabled_b = true, "+
            "update_date_t = :updateDateTime , "+
            "update_user_s = 'rollback_recesso', "+
            "status_c = 'ACTIVE', "+
            "cancellation_t = null "+
            "where cancellation_t >= :requestTimestamp " +
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

    @Query(nativeQuery = true, value = "SELECT * " +
            "FROM bpd_payment_instrument p " +
            "WHERE (hpan_s = :hpan OR hpan_master_s = :hpanMaster) " +
            "AND enabled_b IS TRUE")
    List<PaymentInstrument> findByHpanMasterOrHpan(@Param("hpanMaster") String hpanMaster, @Param("hpan") String hpan);
}
