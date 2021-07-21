package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.CitizenStatusData;

import java.util.Optional;

public interface CitizenStatusDataService {

    boolean checkAndCreate(CitizenStatusData citizenStatusData);

    Optional<CitizenStatusData> findCitizenStatusData(String fiscalCode);

}
