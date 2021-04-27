package it.gov.pagopa.bpd.payment_instrument.command;

import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.payment_instrument.model.DeletePaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.model.DeletePaymentInstrumentCommandModel;
import it.gov.pagopa.bpd.payment_instrument.service.PaymentInstrumentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Base implementation of the DeletePaymentInstrumentCommandInterface, extending Meda BaseCommand class, the command
 * represents the class interacted with at api level, hiding the multiple calls to the integration connectors
 */

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
class DeletePaymentInstrumentCommandImpl extends BaseCommand<Boolean> implements DeletePaymentInstrumentCommand {

    private DeletePaymentInstrumentCommandModel deletePaymentInstrumentCommandModel;
    private PaymentInstrumentService paymentInstrumentService;


    public DeletePaymentInstrumentCommandImpl(DeletePaymentInstrumentCommandModel deletePaymentInstrumentCommandModel) {
        this.deletePaymentInstrumentCommandModel = deletePaymentInstrumentCommandModel;
    }

    public DeletePaymentInstrumentCommandImpl(
            DeletePaymentInstrumentCommandModel deletePaymentInstrumentCommandModel,
            PaymentInstrumentService paymentInstrumentService) {
        this.deletePaymentInstrumentCommandModel = deletePaymentInstrumentCommandModel;
        this.paymentInstrumentService = paymentInstrumentService;
    }


    /**
     * Implementation of the MEDA Command doExecute method, contains the logic for the inbound transaction
     * management, calls the REST endpoint to check if it the related paymentInstrument is active, and eventually
     * sends the Transaction to the proper outbound channel. In case of an error, send a
     *
     * @return boolean to indicate if the command is succesfully executed
     */

    @SneakyThrows
    @Override
    public Boolean doExecute() {

        DeletePaymentInstrument deletePaymentInstrument = deletePaymentInstrumentCommandModel.getPayload();

        try {

            if (logger.isDebugEnabled()) {
                logger.debug("Deleting payment instrument: " +
                        deletePaymentInstrument.getHpan() + ", " +
                        deletePaymentInstrument.getFiscalCode() + ", " +
                        deletePaymentInstrument.getCancellationDate());
            }

            paymentInstrumentService.delete(deletePaymentInstrument.getHpan()
                    ,deletePaymentInstrument.getFiscalCode(),deletePaymentInstrument.getCancellationDate());

            if (logger.isDebugEnabled()) {
                logger.debug("Deleted payment instrument: " +
                        deletePaymentInstrument.getHpan() + ", " +
                        deletePaymentInstrument.getFiscalCode() + ", " +
                        deletePaymentInstrument.getCancellationDate());
            }
            return true;

        } catch (Exception e) {

            if (deletePaymentInstrument != null) {

                if (logger.isErrorEnabled()) {
                    logger.error("Error occurred while deleting the payment instrument: " +
                            deletePaymentInstrument.getHpan() + ", " +
                            deletePaymentInstrument.getFiscalCode() + ", " +
                            deletePaymentInstrument.getCancellationDate());
                    logger.error(e.getMessage(), e);
                }

            }

            throw e;

        }

    }

    @Autowired
    public void setPaymentInstrumentService(PaymentInstrumentService paymentInstrumentService) {
        this.paymentInstrumentService = paymentInstrumentService;
    }

}
