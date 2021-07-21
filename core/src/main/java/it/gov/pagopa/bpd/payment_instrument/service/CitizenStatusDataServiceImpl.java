package it.gov.pagopa.bpd.payment_instrument.service;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.CitizenStatusDataDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.CitizenStatusData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @See CitizenStatusDataService
 */
@Service
@Slf4j
public class CitizenStatusDataServiceImpl implements CitizenStatusDataService {

    private final CitizenStatusDataDAO citizenStatusDataDAO;

    @Autowired
    public CitizenStatusDataServiceImpl(
            ObjectProvider<CitizenStatusDataDAO> citizenStatusDataDAO) {
        this.citizenStatusDataDAO = citizenStatusDataDAO.getIfAvailable();
    }

    @Override
    public boolean checkAndCreate(CitizenStatusData citizenStatusData) {

        Optional<CitizenStatusData> savedCitizenStatusDataOpt =
                citizenStatusDataDAO.findById(citizenStatusData.getFiscalCode());

        if (savedCitizenStatusDataOpt.isPresent() &&
                savedCitizenStatusDataOpt.get().getUpdateDateTime().compareTo(
                        citizenStatusData.getUpdateDateTime()) >= 0) {
            log.warn("Encountered an update event that is outdated");
            return false;
        }

        citizenStatusDataDAO.update(citizenStatusData);

        return true;
    }

    @Override
    public Optional<CitizenStatusData> findCitizenStatusData(String fiscalCode) {
        return citizenStatusDataDAO.findById(fiscalCode);
    }
    
}
