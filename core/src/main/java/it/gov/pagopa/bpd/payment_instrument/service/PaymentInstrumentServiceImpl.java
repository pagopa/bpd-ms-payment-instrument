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
import org.springframework.dao.DataIntegrityViolationException;
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
            pi.setNew(true);
            pi.setUpdatable(false);
            try {
                return paymentInstrumentDAO.save(pi);
            } catch (DataIntegrityViolationException e) {
                logger.error("An attempted insert of a instrument using the channel: "
                        + (pi.getChannel()  != null ? pi.getChannel() : "UKNOWN_CHANNEL" )+
                        " was stopped due to data integrity violation");
            }
        } else {
            PaymentInstrument foundPI = foundPIOpt.get();
            if (!foundPI.isEnabled()) {
                foundPI.setEnabled(true);
                foundPI.setHpan(hpan);
                foundPI.setActivationDate(pi.getActivationDate());
                foundPI.setFiscalCode(pi.getFiscalCode());
                foundPI.setStatus(PaymentInstrument.Status.ACTIVE);
                foundPI.setChannel(pi.getChannel());
                foundPI.setUpdatable(true);
                foundPI.setNew(false);

              try {
                    return paymentInstrumentDAO.save(foundPI);
                } catch (DataIntegrityViolationException e) {
                    logger.error("An attempted update of a instrument using the channel: "
                            + (pi.getChannel()  != null ? pi.getChannel() : "UKNOWN_CHANNEL" )+
                            " was stopped due to data integrity violation");
                }

            } else {
                if (foundPI.getFiscalCode() != null && !foundPI.getFiscalCode().equals(pi.getFiscalCode())) {
                    throw new PaymentInstrumentOnDifferentUserException(hpan);
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
            newPaymentInstrument.setNew(true);
            newPaymentInstrument.setUpdatable(false);
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
                foundPI.setNew(false);
                foundPI.setUpdatable(true);
                toSaveOrUpdate.add(foundPI);
            } else if (foundPI.getFiscalCode() != null && !foundPI.getFiscalCode().equals(pi.getFiscalCode())) {
                throw new PaymentInstrumentOnDifferentUserException(hpan);
            }
        }

        if (toSaveOrUpdate.size() > 0) {
            try {
                paymentInstrumentDAO.saveAll(toSaveOrUpdate);
            } catch (DataIntegrityViolationException e) {
                logger.error("An attempted insert of a instrument using the channel: "
                        + (pi.getChannel()  != null ? pi.getChannel() : "UKNOWN_CHANNEL" )+
                        " was stopped due to data integrity violation");
            }
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
        }

        paymentInstrument.setUpdatable(true);
        paymentInstrument.setNew(false);
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

    @Override
    public Boolean manageTokenData(TokenManagerData tokenManagerData) {

        for (TokenManagerDataCard card : tokenManagerData.getCards()) {
            managerTokenDataCard(tokenManagerData, card);
        }
        return true;

    }

    @Transactional("transactionManagerPrimary")
    private void managerTokenDataCard(TokenManagerData tokenManagerData, TokenManagerDataCard card) {

        Optional<PaymentInstrument> paymentInstrumentOpt =
                paymentInstrumentDAO.findByHpan(card.getHpan());

        if (!paymentInstrumentOpt.isPresent()) {
            log.warn("Card not found during token data update for hpan");
            return;
        }

        PaymentInstrument paymentInstrument = paymentInstrumentOpt.get();

        if (!paymentInstrument.getFiscalCode().equals(tokenManagerData.getTaxCode())) {
            log.warn("Card token data update rejected due to wrong fiscal code for hpan");
            return;
        }

        boolean toRevoke = false;

        if (paymentInstrument.getPar() == null) {
            if (card.getAction().equals("REVOKE")) {
                log.warn("Attempting to revoke a card that does not have a PAR");
                return;
            } else {
                paymentInstrument.setPar(card.getPar());
                paymentInstrument.setParActivationDate(OffsetDateTime.now());
            }
        } else {
            if (paymentInstrument.getPar().equals(card.getPar())) {
                if (card.getAction().equals("REVOKE")) {
                    if ((paymentInstrument.getParDeactivationDate() == null ||
                            paymentInstrument.getParDeactivationDate().compareTo(
                                    paymentInstrument.getParActivationDate()) <= 0) &&
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
                            paymentInstrument.getParDeactivationDate() != null &&
                            paymentInstrument.getParDeactivationDate()
                                    .compareTo(paymentInstrument.getParActivationDate()) > 0
                    ) {
                        paymentInstrument.setParActivationDate(OffsetDateTime.now());
                    }
                }
            }
        }

        if (paymentInstrument.getLastTkmUpdate() == null || paymentInstrument
                .getLastTkmUpdate().compareTo(tokenManagerData.getTimestamp()) < 0) {
            paymentInstrument.setLastTkmUpdate(tokenManagerData.getTimestamp());
            paymentInstrument.setNew(false);
            paymentInstrument.setUpdatable(true);
            paymentInstrumentDAO.update(paymentInstrument);
        }


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
                    tokenInstrument.setDeactivationDate(OffsetDateTime.now());
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

                            if (!tokenToUpdate.isEnabled() && paymentInstrument.isEnabled()) {
                                tokenToUpdate.setEnabled(true);
                                tokenToUpdate.setStatus(PaymentInstrument.Status.ACTIVE);
                                tokenToUpdate.setActivationDate(OffsetDateTime.now());
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
                                tokenToUpdate.setDeactivationDate(OffsetDateTime.now());
                            }

                            tokenToUpdate.setParActivationDate(paymentInstrument.getParActivationDate());
                            tokenToUpdate.setParDeactivationDate(paymentInstrument.getParDeactivationDate());

                            tokenToUpdate.setNew(false);
                            tokenToUpdate.setUpdatable(true);
                            tokenToUpdate.setLastTkmUpdate(tokenManagerData.getTimestamp());
                            tokensToUpdate.add(tokenToUpdate);
                        }
                    }

                } else {
                    boolean isEnabled = !htokenData.getHaction().equals("DELETE") && paymentInstrument.isEnabled();
                    PaymentInstrument tokenToInsert = new PaymentInstrument();
                    tokenToInsert.setFiscalCode(paymentInstrument.getFiscalCode());
                    tokenToInsert.setPar(paymentInstrument.getPar());
                    tokenToInsert.setHpan(htokenData.getHtoken());
                    tokenToInsert.setParActivationDate(paymentInstrument.getParActivationDate());
                    tokenToInsert.setParDeactivationDate(parDeactivationDate);
                    tokenToInsert.setEnabled(isEnabled);
                    tokenToInsert.setStatus(isEnabled ?
                            PaymentInstrument.Status.ACTIVE : PaymentInstrument.Status.INACTIVE);
                    tokenToInsert.setHpanMaster(paymentInstrument.getHpan());
                    tokenToInsert.setActivationDate(OffsetDateTime.now());
                    tokenToInsert.setDeactivationDate(isEnabled ? null : OffsetDateTime.now());
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

}
