package it.gov.pagopa.bpd.payment_instrument.connector.jpa;


import eu.sia.meda.layers.connector.query.CriteriaQuery;
import it.gov.pagopa.bpd.common.connector.jpa.BaseCrudJpaDAOTest;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentErrorDelete;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;

public class PaymentInstrumentErrorDeleteDAOTest extends BaseCrudJpaDAOTest<PaymentInstrumentErrorDeleteDAO, PaymentInstrumentErrorDelete, String> {

    @Autowired
    private PaymentInstrumentErrorDeleteDAO dao;

    @Data
    private static class PaymentInstrumenErrorDeletetCriteria implements CriteriaQuery<PaymentInstrumentErrorDelete> {
        private String id;
    }

    @Override
    protected CriteriaQuery<? super PaymentInstrumentErrorDelete> getMatchAlreadySavedCriteria() {
        PaymentInstrumentErrorDeleteDAOTest.PaymentInstrumenErrorDeletetCriteria criteriaQuery = new PaymentInstrumentErrorDeleteDAOTest.PaymentInstrumenErrorDeletetCriteria();
        criteriaQuery.setId(getStoredId());

        return criteriaQuery;
    }

    @Override
    protected PaymentInstrumentErrorDeleteDAO getDao() {
        return dao;
    }

    @Override
    protected void setId(PaymentInstrumentErrorDelete entity, String id) {
        entity.setId(id);
    }

    @Override
    protected String getId(PaymentInstrumentErrorDelete entity) {
        return entity.getId();
    }

    @Override
    protected void alterEntityToUpdate(PaymentInstrumentErrorDelete entity) {
        entity.setTaxCode("alterFiscalCode");
    }

    @Override
    protected Function<Integer, String> idBuilderFn() {
        return (bias) -> "id" + bias;
    }

    @Override
    protected String getIdName() {
        return "id";
    }
}