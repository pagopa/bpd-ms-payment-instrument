package it.gov.pagopa.bpd.payment_instrument;


import eu.sia.meda.layers.connector.query.CriteriaQuery;
import it.gov.pagopa.bpd.common.BaseCrudJpaDAOTest;
import it.gov.pagopa.bpd.payment_instrument.model.entity.PaymentInstrument;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;

public class PaymentInstrumentDAOTest extends BaseCrudJpaDAOTest<PaymentInstrumentDAO, PaymentInstrument, String> {

    @Data
    private static class PaymentInstrumentCriteria implements CriteriaQuery<PaymentInstrument> {
        private String hpan;
    }

    @Autowired
    private PaymentInstrumentDAO dao;

    @Override
    protected CriteriaQuery<? super PaymentInstrument> getMatchAlreadySavedCriteria() {
        PaymentInstrumentDAOTest.PaymentInstrumentCriteria criteriaQuery = new PaymentInstrumentDAOTest.PaymentInstrumentCriteria();
        criteriaQuery.setHpan(getStoredId());

        return criteriaQuery;
    }

    @Override
    protected PaymentInstrumentDAO getDao() {
        return dao;
    }

    @Override
    protected void setId(PaymentInstrument entity, String id) {
        entity.setHpan(id);
    }

    @Override
    protected String getId(PaymentInstrument entity) {
        return entity.getHpan();
    }

    @Override
    protected void alterEntityToUpdate(PaymentInstrument entity) {
        entity.setStatus(PaymentInstrument.Status.INACTIVE);
    }

    @Override
    protected Function<Integer, String> idBuilderFn() {
        return (bias) -> "hpan" + bias;
    }

    @Override
    protected String getIdName() {
        return "hpan";
    }
}