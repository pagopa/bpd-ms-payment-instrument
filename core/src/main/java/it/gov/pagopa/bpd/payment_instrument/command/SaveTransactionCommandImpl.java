package it.gov.pagopa.bpd.payment_instrument.command;

import it.gov.pagopa.bpd.payment_instrument.model.SaveTransactionCommandModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**

 * implementation of the SaveTransactionCommand interface, extends the BaseSaveTransactionCommandImpl class.
 * Defines the proper implementation, it's used in the beanFactory construction
 */

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
class SaveTransactionCommandImpl extends BaseSaveTransactionCommandImpl implements SaveTransactionCommand {

    public SaveTransactionCommandImpl(SaveTransactionCommandModel saveTransactionCommandModel, Boolean enableBPD, Boolean enableFA) {
        super(saveTransactionCommandModel, enableBPD, enableFA);
    }

    public SaveTransactionCommandImpl(
            SaveTransactionCommandModel saveTransactionCommandModel,
            Boolean enableBPD, Boolean enableFA,
            PaymentInstrumentConnectorService paymentInstrumentConnectorService,
            FaPaymentInstrumentConnectorService faPaymentInstrumentConnectorService,
            PointTransactionPublisherService pointTransactionProducerService,
            InvoiceTransactionPublisherService invoiceTransactionProducerService) {
        super(saveTransactionCommandModel,
                enableBPD, enableFA,
                paymentInstrumentConnectorService,
                faPaymentInstrumentConnectorService,
                pointTransactionProducerService,
                invoiceTransactionProducerService);
    }

    @Override
    public Boolean doExecute() {
        return super.doExecute();
    }

}
