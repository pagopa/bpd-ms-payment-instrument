package it.gov.pagopa.bpd.payment_instrument.controller.assembler;

import it.gov.pagopa.bpd.payment_instrument.controller.model.ChannelValidationResource;
import org.springframework.stereotype.Service;

@Service
public class ChannelValidationResourceAssembler {

    public ChannelValidationResource toResource(Boolean validationChannel) {
        ChannelValidationResource resource = new ChannelValidationResource();
        resource.setIsValid(validationChannel != null ? validationChannel : false);
        return resource;
    }

}
