package it.gov.pagopa.bpd.payment_instrument.command;

import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.payment_instrument.model.InboundCitizenStatusData;
import it.gov.pagopa.bpd.payment_instrument.model.ProcessCitizenUpdateEventCommandModel;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.CitizenStatusData;
import it.gov.pagopa.bpd.payment_instrument.service.mapper.CitizenStatusDataMapper;
import it.gov.pagopa.bpd.payment_instrument.service.CitizenStatusDataService;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * Class extending {@link BaseCommand<Boolean>}, implementation of {@link it.gov.pagopa.bpd.payment_instrument.command.ProcessCitizenUpdateEventCommand}.
 * The command defines the execution of the whole {@link CitizenStatusData} save processing, aggregating and hiding the
 * services used to call on the services and commands involved in the process
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ProcessCitizenUpdateEventCommandImpl extends BaseCommand<Boolean> implements ProcessCitizenUpdateEventCommand {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private final ProcessCitizenUpdateEventCommandModel processCitizenUpdateEventCommandModel;
    private CitizenStatusDataMapper citizenStatusDataMapper;
    private PaymentInstrumentService paymentInstrumentService;
    private CitizenStatusDataService statusDataService;
    private final LocalDate processDateTime;

    public ProcessCitizenUpdateEventCommandImpl(ProcessCitizenUpdateEventCommandModel processCitizenUpdateEventCommandModel) {
        this.processCitizenUpdateEventCommandModel = processCitizenUpdateEventCommandModel;
        this.processDateTime = LocalDate.now();
    }

    public ProcessCitizenUpdateEventCommandImpl(
                                      ProcessCitizenUpdateEventCommandModel processCitizenUpdateEventCommandModel,
                                      CitizenStatusDataService citizenStatusDataService,
                                      PaymentInstrumentService paymentInstrumentService,
                                      CitizenStatusDataMapper citizenStatusDataMapper) {
        this.processCitizenUpdateEventCommandModel = processCitizenUpdateEventCommandModel;
        this.statusDataService = citizenStatusDataService;
        this.processDateTime = LocalDate.now();
        this.paymentInstrumentService = paymentInstrumentService;
        this.citizenStatusDataMapper = citizenStatusDataMapper;
    }


    @SneakyThrows
    @Override
    @Transactional(transactionManager = "transactionManagerPrimary",
            propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean doExecute() {

        InboundCitizenStatusData inboundCitizenStatusData = processCitizenUpdateEventCommandModel.getPayload();

        try {

            validateRequest(inboundCitizenStatusData);

            CitizenStatusData citizenStatusData = citizenStatusDataMapper.map(inboundCitizenStatusData);
            boolean statusUpdated = statusDataService.checkAndCreate(citizenStatusData);

            if (statusUpdated && !inboundCitizenStatusData.getEnabled()) {
                paymentInstrumentService.deleteByFiscalCodeIfNotUpdated(
                        inboundCitizenStatusData.getFiscalCode(), inboundCitizenStatusData.getUpdateDateTime());
            }

            return true;

        } catch (Exception e) {

            if (inboundCitizenStatusData != null) {

                if (logger.isErrorEnabled()) {
                    logger.error("Error occured during processing");
                    logger.error(e.getMessage(), e);
                }

            }

            throw e;

        }

    }

    /**
     * Method to process a validation check for the parsed Transaction request
     *
     * @param citizenStatusData instance of CitizenStatusData, parsed from the inbound byte[] payload
     * @throws ConstraintViolationException
     */
    private void validateRequest(InboundCitizenStatusData citizenStatusData) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(citizenStatusData);
        if (constraintViolations.size() > 0) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    @Autowired
    public void setCitizenStatusDataMapper(CitizenStatusDataMapper citizenStatusDataMapper) {
        this.citizenStatusDataMapper = citizenStatusDataMapper;
    }

    @Autowired
    public void setPaymentInstrumentService(PaymentInstrumentService paymentInstrumentService) {
        this.paymentInstrumentService = paymentInstrumentService;
    }

    @Autowired
    public void setCitizenStatusDataService(CitizenStatusDataService citizenStatusDataService) {
        this.statusDataService = citizenStatusDataService;
    }

}
