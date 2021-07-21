package it.gov.pagopa.bpd.payment_instrument.service.mapper;

import it.gov.pagopa.bpd.payment_instrument.model.InboundCitizenStatusData;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.CitizenStatusData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Class to be used to map a {@link CitizenStatusData} from an* {@link InboundCitizenStatusData}
 */

@Service
public class CitizenStatusDataMapper {

    /**
     * @param inboundCitizenStatusData instance of an {@link InboundCitizenStatusData}, to be mapped into a {@link CitizenStatusData}
     * @return {@link CitizenStatusData} instance from the input citizenStatusData,
     */
    public CitizenStatusData map(
            InboundCitizenStatusData inboundCitizenStatusData) {

        CitizenStatusData citizenStatusData = null;

        if (inboundCitizenStatusData != null) {
            citizenStatusData = CitizenStatusData.builder().build();
            BeanUtils.copyProperties(inboundCitizenStatusData, citizenStatusData);
        }

        return citizenStatusData;

    }

}
