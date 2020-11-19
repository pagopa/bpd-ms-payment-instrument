package it.gov.pagopa.bpd.payment_instrument.service;

import eu.sia.meda.service.BaseService;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentHistoryDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentNotFoundException;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentDifferentChannelException;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentOnDifferentUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * @See PaymentInstrumentService
 */
@Service
class PaymentInstrumentServiceImpl extends BaseService implements PaymentInstrumentService {

    private final PaymentInstrumentDAO paymentInstrumentDAO;
    private final PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAO;

    @Value(value = "${numMaxPaymentInstr}")
    private int numMaxPaymentInstr;
    private final String appIOChannel;


    @Autowired
    public PaymentInstrumentServiceImpl(PaymentInstrumentDAO paymentInstrumentDAO,
                                        PaymentInstrumentHistoryDAO paymentInstrumentHistoryDAO,
                                        @Value("${core.PaymentInstrumentService.appIOChannel}") String appIOChannel) {
        this.paymentInstrumentDAO = paymentInstrumentDAO;
        this.paymentInstrumentHistoryDAO = paymentInstrumentHistoryDAO;
        this.appIOChannel = appIOChannel;
    }


    @Override
    public PaymentInstrument find(String hpan,String fiscalCode) {
        PaymentInstrument pi = paymentInstrumentDAO.findById(hpan).orElseThrow(() -> new PaymentInstrumentNotFoundException(hpan));

        if((pi.isEnabled() || PaymentInstrument.Status.ACTIVE.equals(pi.getStatus()))
                && fiscalCode!=null && !fiscalCode.equals(pi.getFiscalCode())){
            throw new PaymentInstrumentOnDifferentUserException(hpan);
        }

        return pi;
    }


    @Override
    public PaymentInstrument createOrUpdate(String hpan, PaymentInstrument pi) {
        final Optional<PaymentInstrument> foundPIOpt = paymentInstrumentDAO.findById(hpan);
        pi.setHpan(hpan);
        if (!foundPIOpt.isPresent()) {
//            final long count = paymentInstrumentDAO.count((root, query, criteriaBuilder) ->
//                    criteriaBuilder.equal(root.get("fiscalCode"), pi.getFiscalCode()));
//            if (count >= numMaxPaymentInstr) {
//                throw new PaymentInstrumentNumbersExceededException(
//                        PaymentInstrument.class, numMaxPaymentInstr);
//            }
            return paymentInstrumentDAO.save(pi);
        } else {
            PaymentInstrument foundPI = foundPIOpt.get();
            if (!foundPI.isEnabled()) {
                foundPI.setEnabled(true);
                foundPI.setHpan(hpan);
                foundPI.setActivationDate(pi.getActivationDate());
                foundPI.setFiscalCode(pi.getFiscalCode());
                foundPI.setStatus(PaymentInstrument.Status.ACTIVE);
                return paymentInstrumentDAO.save(pi);
            }
        }

        return pi;
    }

    @Override
    public void delete(String hpan, String fiscalCode, OffsetDateTime cancellationDate) {
        PaymentInstrument paymentInstrument = paymentInstrumentDAO.findById(hpan).orElseThrow(
                () -> new PaymentInstrumentNotFoundException(hpan));
        checkAndDelete(paymentInstrument, fiscalCode, cancellationDate);
    }

    @Override
    public void deleteByFiscalCode(String fiscalCode, String channel) {
        List<PaymentInstrument> paymentInstrumentList = paymentInstrumentDAO.findByFiscalCode(fiscalCode);

        if (paymentInstrumentList != null && !paymentInstrumentList.isEmpty()) {
            if (!channel.equals(appIOChannel)) {
                Set<String> channelSet = new HashSet<>();
                paymentInstrumentList.forEach(pi -> channelSet.add(pi.getChannel()));

                if (channelSet.size() > 1 || !channelSet.contains(channel)) {
                    throw new PaymentInstrumentDifferentChannelException(fiscalCode);
                }
            }
            paymentInstrumentList.forEach(
                    paymentInstrument -> checkAndDelete(paymentInstrument, fiscalCode, null));
        }
    }

    private void checkAndDelete(PaymentInstrument paymentInstrument,
                                String fiscalCode, OffsetDateTime cancellationDate) {

        if (fiscalCode != null && !fiscalCode.equals(paymentInstrument.getFiscalCode())) {
            throw new PaymentInstrumentOnDifferentUserException(fiscalCode);
        }

        if (paymentInstrument.isEnabled()) {
            paymentInstrument.setStatus(PaymentInstrument.Status.INACTIVE);
            paymentInstrument.setDeactivationDate(cancellationDate != null ?
                    cancellationDate :OffsetDateTime.now());
            paymentInstrument.setEnabled(false);
        }
        paymentInstrumentDAO.save(paymentInstrument);
    }




    @Override
    public boolean checkActive(String hpan, OffsetDateTime accountingDate) {
        return paymentInstrumentHistoryDAO.countActive(hpan, accountingDate.toLocalDate()) > 0;
    }

    @Override
    public void reactivateForRollback(String fiscalCode, OffsetDateTime requestTimestamp) {
        paymentInstrumentDAO.reactivateForRollback(fiscalCode, requestTimestamp);
    }

    public static void main(String[] args) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        final OffsetDateTime start = OffsetDateTime.parse("2020-10-14T18:28:17.37Z", dateTimeFormatter);
        final OffsetDateTime end = OffsetDateTime.parse("2020-10-14T18:30:15.808Z", dateTimeFormatter);
        final long between = ChronoUnit.MILLIS.between(start, end);
        System.out.println("between = " + between);
    }

}
