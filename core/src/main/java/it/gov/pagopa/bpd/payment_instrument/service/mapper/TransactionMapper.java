package it.gov.pagopa.bpd.payment_instrument.service.mapper;

import it.gov.pagopa.bpd.payment_instrument.publisher.model.OutgoingTransaction;
import it.gov.pagopa.bpd.payment_instrument.publisher.model.Transaction;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Class to be used to map a {@link Transaction} from an* {@link OutgoingTransaction}
 */

@Service
public class TransactionMapper {

    public OutgoingTransaction map(
            Transaction transaction) {

        OutgoingTransaction outgoingTransaction = null;

        if (transaction != null) {
            outgoingTransaction = OutgoingTransaction.builder().build();
            BeanUtils.copyProperties(transaction, outgoingTransaction);
        }

        return outgoingTransaction;

    }
}
