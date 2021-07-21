package it.gov.pagopa.bpd.payment_instrument.command;

import eu.sia.meda.core.command.Command;

/**
 * Interface extending {@link Command<Boolean>}, defines the command,
 * to be used for inbound {@link it.gov.pagopa.bpd.payment_instrument.model.InboundCitizenStatusData} to be processed
 *
 * @see ProcessCitizenUpdateEventCommandImpl
 */

public interface ProcessCitizenUpdateEventCommand extends Command<Boolean> {
}
