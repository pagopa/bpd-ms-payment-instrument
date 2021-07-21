package it.gov.pagopa.bpd.payment_instrument.connector.jpa;

import it.gov.pagopa.bpd.common.connector.jpa.CrudJpaDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.CitizenStatusData;
import org.springframework.stereotype.Repository;

@Repository
public interface CitizenStatusDataDAO extends CrudJpaDAO<CitizenStatusData, String> {}
