package com.sapienter.jbilling.server.order;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.mediation.JbillingMediationRecord;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by marcolin on 12/10/15.
 */
public interface OrderService {
    public static final int ORDER_PERIOD_ONCE_ID = 1;
    public static final String BEAN_NAME = "orderService";
    OrderWS getCurrentOrder(Integer userId, Date date) throws SessionInternalError;
    List<OrderWS> lastOrders(Integer userId, int numberOfOrdersToRetrieve);
    List<OrderChangeStatusWS> getOrderChangeStatusesForCompany();
    List<OrderChangeTypeWS> getOrderChangeTypesForCompany();
    MediationEventResult addMediationEvent(JbillingMediationRecord jmr);
    void undoMediation(UUID processId);
}
