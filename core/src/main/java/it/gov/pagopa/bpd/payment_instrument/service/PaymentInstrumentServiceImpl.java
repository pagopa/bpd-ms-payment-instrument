package it.gov.pagopa.bpd.payment_instrument.service;

import eu.sia.meda.service.BaseService;
import it.gov.pagopa.bpd.payment_instrument.assembler.PaymentInstrumentAssembler;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentConverter;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.PaymentInstrumentHistoryReplicaDAO;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrument;
import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentDifferentChannelException;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentNotFoundException;
import it.gov.pagopa.bpd.payment_instrument.exception.PaymentInstrumentOnDifferentUserException;
import it.gov.pagopa.bpd.payment_instrument.model.PaymentInstrumentServiceModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @See PaymentInstrumentService
 */
@Service
class PaymentInstrumentServiceImpl extends BaseService implements PaymentInstrumentService {

    private final PaymentInstrumentDAO paymentInstrumentDAO;
    private final PaymentInstrumentHistoryReplicaDAO paymentInstrumentHistoryReplicaDAO;
    private final PaymentInstrumentAssembler paymentInstrumentAssembler;

    @Value(value = "${numMaxPaymentInstr}")
    private int numMaxPaymentInstr;
    private final String appIOChannel;


    @Autowired
    public PaymentInstrumentServiceImpl(ObjectProvider<PaymentInstrumentDAO> paymentInstrumentDAO,
                                        ObjectProvider<PaymentInstrumentHistoryReplicaDAO> paymentInstrumentHistoryDAO,
                                        PaymentInstrumentAssembler paymentInstrumentAssembler,
                                        @Value("${core.PaymentInstrumentService.appIOChannel}") String appIOChannel) {
        this.paymentInstrumentDAO = paymentInstrumentDAO.getIfAvailable();
        this.paymentInstrumentHistoryReplicaDAO = paymentInstrumentHistoryDAO.getIfAvailable();
        this.paymentInstrumentAssembler = paymentInstrumentAssembler;
        this.appIOChannel = appIOChannel;
    }


    @Override
    public PaymentInstrument find(String hpan, String fiscalCode) {
        PaymentInstrument pi = paymentInstrumentDAO.findById(hpan).orElseThrow(() -> new PaymentInstrumentNotFoundException(hpan));
        if ((pi.isEnabled() || PaymentInstrument.Status.ACTIVE.equals(pi.getStatus()))
                && fiscalCode != null && !fiscalCode.equals(pi.getFiscalCode())) {
            throw new PaymentInstrumentOnDifferentUserException(hpan);
        }
        return pi;
    }

    @Override
    public PaymentInstrumentServiceModel createOrUpdate(String hpan, PaymentInstrumentServiceModel pi) {
        List<String> idList = new ArrayList<>();
        idList.add(hpan);
        if (pi.getTokenPanList() != null) {
            idList.addAll(pi.getTokenPanList());
        }
        List<PaymentInstrument> toSaveOrUpdate = new ArrayList<>();
        List<PaymentInstrument> piList = paymentInstrumentDAO.findByHpanIn(idList);
        List<String> piListHpan = piList.stream()
                .map(PaymentInstrument::getHpan)
                .collect(Collectors.toList());
        List<String> notYetEnrolledIdList = idList.stream()
                .filter(element -> !piListHpan.contains(element))
                .collect(Collectors.toList());
        for (String id : notYetEnrolledIdList) {
            PaymentInstrument newPaymentInstrument = paymentInstrumentAssembler.toResource(pi, id);
            newPaymentInstrument.setHpanMaster(hpan);
            if (newPaymentInstrument.getActivationDate() == null) {
                newPaymentInstrument.setActivationDate(OffsetDateTime.now());
            }
            toSaveOrUpdate.add(newPaymentInstrument);
        }
        for (PaymentInstrument foundPI : piList) {
            if (!foundPI.isEnabled()) {
                foundPI.setEnabled(true);
                foundPI.setActivationDate(pi.getActivationDate() != null ? pi.getActivationDate()
                        : OffsetDateTime.now());
                foundPI.setFiscalCode(pi.getFiscalCode());
                foundPI.setStatus(PaymentInstrument.Status.ACTIVE);
                foundPI.setPar(pi.getPar());
                toSaveOrUpdate.add(foundPI);
            } else if (foundPI.getFiscalCode() != null && !foundPI.getFiscalCode().equals(pi.getFiscalCode())) {
                throw new PaymentInstrumentOnDifferentUserException(hpan);
            }
        }
        if (toSaveOrUpdate.size() > 0) {
            paymentInstrumentDAO.saveAll(toSaveOrUpdate);
        }
        return pi;
    }

    @Override
    public void delete(String hpan, String fiscalCode, OffsetDateTime cancellationDate) {

        List<PaymentInstrument> piList = paymentInstrumentDAO.findByHpanMasterOrHpan(hpan, hpan);
        if (piList == null || piList.isEmpty()) {
            throw new PaymentInstrumentNotFoundException(hpan);
        }

        piList.forEach(
                paymentInstrument -> checkAndDelete(paymentInstrument, fiscalCode, null));
    }

    @Override
    public void deleteByFiscalCode(String fiscalCode, String channel) {
        List<PaymentInstrument> paymentInstrumentList = paymentInstrumentDAO.findByFiscalCode(fiscalCode);

        if (paymentInstrumentList != null && !paymentInstrumentList.isEmpty()) {
            if (!appIOChannel.equals(channel)) {
                Set<String> channelSet = new HashSet<>();
                paymentInstrumentList.stream().filter(pi -> pi.isEnabled()).forEach(pi -> channelSet.add(pi.getChannel()));

                if (channelSet.size() != 0 && (channelSet.size() > 1 || !channelSet.contains(channel))) {
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
                    cancellationDate : OffsetDateTime.now());
            paymentInstrument.setUpdateUser(fiscalCode);
            paymentInstrument.setUpdateDate(OffsetDateTime.now());
            paymentInstrument.setEnabled(false);
        }
        paymentInstrumentDAO.save(paymentInstrument);
    }


    @Override
    public PaymentInstrumentHistory checkActive(String hpan, OffsetDateTime accountingDate) {
        return paymentInstrumentHistoryReplicaDAO.findActive(hpan, accountingDate.toLocalDate());
    }

    @Override
    public void reactivateForRollback(String fiscalCode, OffsetDateTime requestTimestamp) {
        OffsetDateTime updateDateTime = OffsetDateTime.now();
        paymentInstrumentDAO.reactivateForRollback(fiscalCode, requestTimestamp, updateDateTime);
    }

    @Override
    public String getFiscalCode(String hpan) {
        PaymentInstrument pi = paymentInstrumentDAO.findById(hpan).orElseThrow(() -> new PaymentInstrumentNotFoundException(hpan));
        return pi.getFiscalCode();
    }

    @Override
    @Deprecated
    public List<PaymentInstrumentConverter> getPaymentInstrument(String fiscalCode, String channel) {
        return paymentInstrumentDAO.getPaymentInstrument(fiscalCode, channel);
    }

    @Override
    public List<PaymentInstrumentHistory> findHistory(String fiscalCode, String hpan) {
        return paymentInstrumentHistoryReplicaDAO.find(fiscalCode, hpan);
    }

}
