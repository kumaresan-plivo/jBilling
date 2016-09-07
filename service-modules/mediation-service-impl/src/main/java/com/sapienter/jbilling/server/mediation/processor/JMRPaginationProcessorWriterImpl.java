package com.sapienter.jbilling.server.mediation.processor;

import com.sapienter.jbilling.server.mediation.JbillingMediationRecord;
import com.sapienter.jbilling.server.mediation.MetricsHelper;
import com.sapienter.jbilling.server.mediation.converter.db.DaoConverter;
import com.sapienter.jbilling.server.mediation.converter.db.JMRRepository;
import com.sapienter.jbilling.server.order.MediationEventResult;
import com.sapienter.jbilling.server.order.OrderService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by marcolin on 13/10/15.
 */
public class JMRPaginationProcessorWriterImpl implements ItemWriter<List<JbillingMediationRecord>> {
    @Autowired
    private JMRRepository jmrRepository;
    @Autowired
    private OrderService orderService;

    public void setJmrRepository(JMRRepository jmrRepository) {
        this.jmrRepository = jmrRepository;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void write(List<? extends List<JbillingMediationRecord>> listsByUser) throws Exception {
        for (List<JbillingMediationRecord> jmrsByUser: listsByUser) {
            for (JbillingMediationRecord jmr: jmrsByUser) {
                MediationEventResult mediationEventResult = orderService.addMediationEvent(jmr);
                if (mediationEventResult != null) {
                    jmr.setOrderId(mediationEventResult.getCurrentOrderId());
                    jmr.setOrderLineId(mediationEventResult.getOrderLinedId());
                    jmr.setRatedPrice(mediationEventResult.getAmountForChange());
                    jmr.setRatedCostPrice(mediationEventResult.getCostAmountForChange());
                    jmr.setStatus(JbillingMediationRecord.STATUS.PROCESSED);
                } else {
                    jmr.setStatus(JbillingMediationRecord.STATUS.NOT_BILLABLE);
                }
                sendMetric(jmr);
                updateMediationRecord(jmr);
            }
        }
    }

    public JbillingMediationRecord updateMediationRecord(JbillingMediationRecord jbillingMediationRecord) {
        return DaoConverter.getMediationRecord(
                jmrRepository.save(DaoConverter.getMediationRecordDao(jbillingMediationRecord)));
    }

    private static void sendMetric(JbillingMediationRecord callDataRecord) {
        try {
            MetricsHelper.log("Readed JMR: " + callDataRecord.toString(),
                    InetAddress.getLocalHost().toString(),
                    MetricsHelper.MetricType.ORDER_CREATED.name());
        } catch (Exception e) {}
    }
}
