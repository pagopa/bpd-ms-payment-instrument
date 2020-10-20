package it.gov.pagopa.bpd.payment_instrument.service;


import it.gov.pagopa.bpd.payment_instrument.publisher.model.Transaction;

/**
 * public interface for the PointTransactionPublisherService
 */

public interface PointTransactionPublisherService {

    /**
     * Method that has the logic for publishing a Transaction to the point-processor outbound channel,
     * calling on the appropriate connector
     *
     * @param transaction Transaction instance to be published
     */
    void publishPointTransactionEvent(Transaction transaction);

}
