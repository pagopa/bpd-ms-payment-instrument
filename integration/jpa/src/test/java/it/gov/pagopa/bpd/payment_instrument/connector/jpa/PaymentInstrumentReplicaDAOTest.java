package it.gov.pagopa.bpd.payment_instrument.connector.jpa;

import it.gov.pagopa.bpd.payment_instrument.connector.jpa.model.PaymentInstrumentHistory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
public class PaymentInstrumentReplicaDAOTest {

    private static final String EXISTING_HASH_PAN = "existing-hpan";
    private static final String EXISTING_FISCAL_CODE = "existing-fiscal-code";

    @MockBean
    private PaymentInstrumentReplicaDAO paymentInstrumentReplicaDAOMock;

    @Test
    public void find_ok() {
        List<PaymentInstrumentHistory> entity = paymentInstrumentReplicaDAOMock.find(
                EXISTING_FISCAL_CODE, EXISTING_HASH_PAN);

        Assert.assertNotNull(entity);
    }

}
