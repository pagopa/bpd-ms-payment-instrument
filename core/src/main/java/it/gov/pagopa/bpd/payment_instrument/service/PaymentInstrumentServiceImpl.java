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
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerData;
import it.gov.pagopa.bpd.payment_instrument.model.TokenManagerDataCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @See PaymentInstrumentService
 */
@Service
@Slf4j
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


//    @Override
//    public List<PaymentInstrument> find(String hpan, String fiscalCode) {
//        List<PaymentInstrument> piList = paymentInstrumentDAO.findByHpanMasterOrHpan(hpan, hpan);
//
//        if (piList != null && !piList.isEmpty() && piList.stream().anyMatch(item -> item.getHpan().equals(item.getHpanMaster()))) {
//            PaymentInstrument hpanMaster = piList.stream().filter(item -> item.getHpan().equals(item.getHpanMaster())).findFirst().get();
//            if ((hpanMaster.isEnabled() || PaymentInstrument.Status.ACTIVE.equals(hpanMaster.getStatus()))
//                    && fiscalCode != null && !fiscalCode.equals(hpanMaster.getFiscalCode())) {
//                throw new PaymentInstrumentOnDifferentUserException(hpan);
//            }
//        } else {
//            throw new PaymentInstrumentNotFoundException(hpan);
//        }
//
//        return piList;
//    }

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
    public PaymentInstrument findByPar(String par) {
        List<PaymentInstrument> piList = paymentInstrumentDAO.getFromPar(par);
        return piList.get(0);
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
                foundPI.setChannel(pi.getChannel());
                foundPI.setPar(pi.getPar());
                foundPI.setParActivationDate(pi.getParActivationDate());
                return paymentInstrumentDAO.save(foundPI);
            } else {
                if (foundPI.getFiscalCode() != null && !foundPI.getFiscalCode().equals(pi.getFiscalCode())) {
                    throw new PaymentInstrumentOnDifferentUserException(hpan);
                } else if (!foundPI.getPar().equals(pi.getPar()) && foundPI.getParActivationDate() != pi.getParActivationDate()) {
                    foundPI.setPar(pi.getPar());
                    foundPI.setParActivationDate(pi.getParActivationDate());
                    return paymentInstrumentDAO.save(foundPI);
                }
                pi = foundPI;
            }
        }
        return pi;
    }

    @Override
    @Deprecated
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
                foundPI.setChannel(pi.getChannel());
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

//    @Override
//    public void delete(String hpan, String fiscalCode, OffsetDateTime cancellationDate) {
//
//        List<PaymentInstrument> piList = paymentInstrumentDAO.findByHpanMasterOrHpan(hpan, hpan);
//        if (piList == null || piList.isEmpty()) {
//            throw new PaymentInstrumentNotFoundException(hpan);
//        }
//
//        piList.forEach(
//                paymentInstrument -> checkAndDelete(paymentInstrument, fiscalCode, null));
//    }

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
            paymentInstrument.setNew(false);
        }
        paymentInstrumentDAO.save(paymentInstrument);
    }


    @Override
    public PaymentInstrumentHistory checkActive(String hpan, OffsetDateTime accountingDate) {
        return paymentInstrumentHistoryReplicaDAO.findActive(hpan, accountingDate.toLocalDate());
    }

    @Override
    public Optional<PaymentInstrument> findByhpan(String hpan) {
        return paymentInstrumentDAO.findById(hpan);
    }

    @Override
    public PaymentInstrumentHistory checkActivePar(String par, OffsetDateTime accountingDate) {
        return paymentInstrumentHistoryReplicaDAO.findActivePar(par, accountingDate.toLocalDate());
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

    @Override
    public Boolean manageTokenData(TokenManagerData tokenManagerData) {

        for (TokenManagerDataCard card : tokenManagerData.getCards()) {
            Optional<PaymentInstrument> paymentInstrumentOpt =
                    paymentInstrumentDAO.findByHpan(card.getHpan());

            if (!paymentInstrumentOpt.isPresent()) {
                log.warn("Card not found during token data update for hpan");
                continue;
            }

            PaymentInstrument paymentInstrument = paymentInstrumentOpt.get();

            if (!paymentInstrument.getFiscalCode().equals(tokenManagerData.getTaxCode())) {
                log.warn("Card token data update rejected due to wrong fiscal code for hpan");
                continue;
            }

            boolean toRevoke = false;

            if (paymentInstrument.getPar() == null) {
                if (card.getAction().equals("REVOKE")) {
                    log.warn("Attempting to revoke a card that does not have a PAR");
                    continue;
                } else {
                    paymentInstrument.setPar(card.getPar());
                    paymentInstrument.setParActivationDate(OffsetDateTime.now());
                }
            } else {
                if (paymentInstrument.getPar().equals(card.getPar())) {
                    if (card.getAction().equals("REVOKE")) {
                        if ((paymentInstrument.getParDeactivationDate() == null ||
                                paymentInstrument.getParDeactivationDate().compareTo(
                                        paymentInstrument.getActivationDate()) <= 0) &&
                                (paymentInstrument.getLastTkmUpdate() == null ||
                                        paymentInstrument.getLastTkmUpdate().compareTo(
                                        tokenManagerData.getTimestamp()) <= 0)
                        ) {
                            paymentInstrument.setParDeactivationDate(OffsetDateTime.now());
                            toRevoke = true;
                        }
                    } else {
                        if (paymentInstrument.getLastTkmUpdate() != null &&
                                (paymentInstrument.getLastTkmUpdate().compareTo(
                                        tokenManagerData.getTimestamp()) <= 0) &&
                                paymentInstrument.getDeactivationDate() != null &&
                                paymentInstrument.getDeactivationDate()
                                        .compareTo(paymentInstrument.getActivationDate()) > 0
                        ) {
                            paymentInstrument.setActivationDate(OffsetDateTime.now());
                        }
                    }
                }
            }

            if (paymentInstrument.getLastTkmUpdate() == null || paymentInstrument
                    .getLastTkmUpdate().compareTo(tokenManagerData.getTimestamp()) < 0) {
                paymentInstrument.setLastTkmUpdate(tokenManagerData.getTimestamp());
            }
            paymentInstrument.setNew(false);
            paymentInstrument.setUpdatable(true);
            paymentInstrumentDAO.update(paymentInstrument);

            List<PaymentInstrument> tokensToInsert = new ArrayList<>();
            List<PaymentInstrument> tokensToUpdate = new ArrayList<>();

            if (toRevoke) {
                List<PaymentInstrument> tokenInstruments = paymentInstrumentDAO.findTokensToRevoke(
                        card.getHpan(), paymentInstrument.getPar(), tokenManagerData.getTaxCode());
                tokenInstruments.forEach(tokenInstrument -> {
                    if (tokenInstrument.isEnabled() && (tokenInstrument.getLastTkmUpdate() == null ||
                            tokenInstrument.getLastTkmUpdate().compareTo(tokenManagerData.getTimestamp()) <= 0)) {
                        tokenInstrument.setEnabled(false);
                        tokenInstrument.setStatus(PaymentInstrument.Status.INACTIVE);
                        tokenInstrument.setLastTkmUpdate(tokenInstrument.getLastTkmUpdate());
                        tokenInstrument.setDeactivationDate(tokenManagerData.getTimestamp());
                        tokenInstrument.setParDeactivationDate(
                                paymentInstrument.getParDeactivationDate());
                        tokenInstrument.setUpdatable(true);
                        tokenInstrument.setNew(false);
                        tokensToUpdate.add(tokenInstrument);
                    }
                });
            } else {

                card.getHtokens().forEach(htokenData -> {

                    Optional<PaymentInstrument> tokenOpt =
                            paymentInstrumentDAO.findToken(
                                    htokenData.getHtoken(), card.getPar(), tokenManagerData.getTaxCode());
                    OffsetDateTime parDeactivationDate = paymentInstrument.getParDeactivationDate();

                    if (tokenOpt.isPresent()) {

                        PaymentInstrument tokenToUpdate = tokenOpt.get();
                        if (htokenData.getHaction().equals("INSERT_UPDATE")) {
                            if (tokenToUpdate.getLastTkmUpdate() == null ||
                                    tokenToUpdate.getLastTkmUpdate().compareTo(
                                            tokenManagerData.getTimestamp()) <= 0) {
                                tokenToUpdate.setLastTkmUpdate(tokenManagerData.getTimestamp());

                                if (!tokenToUpdate.isEnabled()) {
                                    tokenToUpdate.setEnabled(true);
                                    tokenToUpdate.setStatus(PaymentInstrument.Status.ACTIVE);
                                    tokenToUpdate.setActivationDate(tokenManagerData.getTimestamp());
                                }

                                tokenToUpdate.setParActivationDate(paymentInstrument.getParActivationDate());
                                tokenToUpdate.setParDeactivationDate(paymentInstrument.getParDeactivationDate());

                                tokenToUpdate.setNew(false);
                                tokenToUpdate.setUpdatable(true);
                                tokenToUpdate.setLastTkmUpdate(tokenManagerData.getTimestamp());
                                tokensToUpdate.add(tokenToUpdate);
                            }
                        } else {
                            if (tokenToUpdate.getLastTkmUpdate() == null ||
                                    tokenToUpdate.getLastTkmUpdate().compareTo(
                                            tokenManagerData.getTimestamp()) <= 0) {

                                tokenToUpdate.setLastTkmUpdate(tokenManagerData.getTimestamp());

                                if (tokenToUpdate.isEnabled()) {
                                    tokenToUpdate.setEnabled(false);
                                    tokenToUpdate.setStatus(PaymentInstrument.Status.INACTIVE);
                                    tokenToUpdate.setDeactivationDate(tokenManagerData.getTimestamp());
                                }

                                tokenToUpdate.setParActivationDate(paymentInstrument.getParActivationDate());
                                tokenToUpdate.setDeactivationDate(paymentInstrument.getParDeactivationDate());

                                tokenToUpdate.setNew(false);
                                tokenToUpdate.setUpdatable(true);
                                tokenToUpdate.setLastTkmUpdate(tokenManagerData.getTimestamp());
                                tokensToUpdate.add(tokenToUpdate);
                            }
                        }

                    } else {
                        PaymentInstrument tokenToInsert = new PaymentInstrument();
                        tokenToInsert.setFiscalCode(paymentInstrument.getFiscalCode());
                        tokenToInsert.setDeactivationDate(paymentInstrument.getDeactivationDate());
                        tokenToInsert.setPar(paymentInstrument.getPar());
                        tokenToInsert.setHpan(htokenData.getHtoken());
                        tokenToInsert.setParActivationDate(paymentInstrument.getParActivationDate());
                        tokenToInsert.setParDeactivationDate(parDeactivationDate);
                        tokenToInsert.setEnabled(!htokenData.getHaction().equals("DELETE"));
                        tokenToInsert.setStatus(!htokenData.getHaction().equals("DELETE") ?
                                PaymentInstrument.Status.ACTIVE :
                                PaymentInstrument.Status.INACTIVE);
                        tokenToInsert.setHpanMaster(paymentInstrument.getHpan());
                        tokenToInsert.setActivationDate(tokenManagerData.getTimestamp());
                        tokenToInsert.setDeactivationDate(!htokenData.getHaction().equals("DELETE") ?
                                null : tokenManagerData.getTimestamp());
                        tokenToInsert.setLastTkmUpdate(paymentInstrument.getLastTkmUpdate());
                        tokenToInsert.setNew(true);
                        tokenToInsert.setUpdatable(false);
                        tokenToInsert.setLastTkmUpdate(tokenManagerData.getTimestamp());
                        tokensToInsert.add(tokenToInsert);
                    }

                });
            }

            if (!tokensToInsert.isEmpty()) {
                paymentInstrumentDAO.saveAll(tokensToInsert);
            }

            if (!tokensToUpdate.isEmpty()) {
                paymentInstrumentDAO.saveAll(tokensToUpdate);
            }

        }

        return true;

    }

}
