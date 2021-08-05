package it.gov.pagopa.bpd.payment_instrument.command;

import it.gov.pagopa.bpd.common.BaseTest;

/**
 * Test class for the FilterTransactionCommand method
 */

public class FilterTransactionCommandTest extends BaseTest {

//    @Rule
//    public ExpectedException expectedException = ExpectedException.none();
//    @Mock
//    PaymentInstrumentService paymentInstrumentServiceMock;
//    @Mock
//    PointTransactionPublisherService pointTransactionProducerServiceMock;
//    @Spy
//    TransactionMapper transactionMapperSpy;
//
//    @Before
//    public void initTest() {
//        Mockito.reset(
//                paymentInstrumentServiceMock,
//                pointTransactionProducerServiceMock,
//                transactionMapperSpy);
//    }
//
//    @Test
//    public void test_BDPActive() {
//
//        PaymentInstrumentHistory pih = new PaymentInstrumentHistory();
//        pih.setFiscalCode("fiscalCode");
//        Transaction transaction = getRequestObject();
//        OutgoingTransaction outgoingTransaction = transactionMapperSpy.map(transaction);
//        outgoingTransaction.setFiscalCode(pih.getFiscalCode());
//        FilterTransactionCommand filterTransactionCommand = new FilterTransactionCommandImpl(
//                TransactionCommandModel.builder().payload(transaction).build(),
//                pointTransactionProducerServiceMock,
//                paymentInstrumentServiceMock,
//                transactionMapperSpy
//        );
//
//
//        try {
//
//            BDDMockito.doReturn(pih).when(paymentInstrumentServiceMock)
//                    .checkActive(Mockito.eq(transaction.getHpan()), Mockito.eq(transaction.getTrxDate()));
//            BDDMockito.doNothing().when(pointTransactionProducerServiceMock)
//                    .publishPointTransactionEvent(Mockito.eq(outgoingTransaction));
//
//            Boolean isOk = filterTransactionCommand.execute();
//
//            Assert.assertTrue(isOk);
//            BDDMockito.verify(paymentInstrumentServiceMock, Mockito.atLeastOnce())
//                    .checkActive(Mockito.eq(transaction.getHpan()), Mockito.eq(transaction.getTrxDate()));
//            BDDMockito.verify(pointTransactionProducerServiceMock, Mockito.atLeastOnce())
//                    .publishPointTransactionEvent(Mockito.any());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Assert.fail();
//        }
//
//    }
//
//    protected FilterTransactionCommand buildCommandInstance(Transaction transaction) {
//        return new FilterTransactionCommandImpl(
//                TransactionCommandModel.builder().payload(transaction).headers(null).build(),
//                pointTransactionProducerServiceMock,
//                paymentInstrumentServiceMock
//        );
//    }
//
//    protected Transaction getRequestObject() {
//        return Transaction.builder()
//                .idTrxAcquirer("1")
//                .acquirerCode("001")
//                .trxDate(OffsetDateTime.parse("2020-04-09T16:22:45.304Z"))
//                .amount(BigDecimal.valueOf(1313.13))
//                .operationType("00")
//                .hpan("hpan")
//                .merchantId("0")
//                .circuitType("00")
//                .mcc("813")
//                .idTrxIssuer("0")
//                .amountCurrency("833")
//                .correlationId("1")
//                .acquirerId("0")
//                .terminalId("0")
//                .bin("000004")
//                .build();
//    }
//
//    @Test
//    public void test_BDPNotActive() {
//
//        Transaction transaction = getRequestObject();
//        OutgoingTransaction outgoingTransaction = transactionMapperSpy.map(transaction);
//        FilterTransactionCommand filterTransactionCommand = new FilterTransactionCommandImpl(
//                TransactionCommandModel.builder().payload(transaction).build(),
//                pointTransactionProducerServiceMock,
//                paymentInstrumentServiceMock,
//                transactionMapperSpy
//        );
//
//        try {
//
//            BDDMockito.doReturn(null).when(paymentInstrumentServiceMock)
//                    .checkActive(Mockito.eq(transaction.getHpan()), Mockito.eq(transaction.getTrxDate()));
//            BDDMockito.doNothing().when(pointTransactionProducerServiceMock)
//                    .publishPointTransactionEvent(Mockito.eq(outgoingTransaction));
//
//            Boolean isOk = filterTransactionCommand.execute();
//
//            Assert.assertTrue(isOk);
//            BDDMockito.verify(paymentInstrumentServiceMock, Mockito.atLeastOnce())
//                    .checkActive(Mockito.eq(transaction.getHpan()), Mockito.eq(transaction.getTrxDate()));
//            BDDMockito.verifyZeroInteractions(pointTransactionProducerServiceMock);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Assert.fail();
//        }
//
//    }
//
//    @SneakyThrows
//    @Test
//    public void testExecute_KO_Validation() {
//
//        Transaction transaction = getRequestObject();
//        transaction.setAcquirerCode(null);
//        FilterTransactionCommand filterTransactionCommand = buildCommandInstance(transaction);
//
//        BDDMockito.doThrow(new RuntimeException("Some Exception")).when(paymentInstrumentServiceMock)
//                .checkActive(Mockito.eq(transaction.getHpan()), Mockito.eq(transaction.getTrxDate()));
//
//        expectedException.expect(Exception.class);
//        filterTransactionCommand.execute();
//
//        BDDMockito.verifyZeroInteractions(paymentInstrumentServiceMock);
//        BDDMockito.verifyZeroInteractions(pointTransactionProducerServiceMock);
//
//    }
//
//    @Test
//    public void testExecute_KO_Null() {
//
//        FilterTransactionCommand filterTransactionCommand = buildCommandInstance(null);
//
//        try {
//
//            expectedException.expect(AssertionError.class);
//            filterTransactionCommand.execute();
//            BDDMockito.verifyZeroInteractions(paymentInstrumentServiceMock);
//            BDDMockito.verifyZeroInteractions(pointTransactionProducerServiceMock);
//            BDDMockito.verifyZeroInteractions();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Assert.fail();
//        }
//
//    }

}