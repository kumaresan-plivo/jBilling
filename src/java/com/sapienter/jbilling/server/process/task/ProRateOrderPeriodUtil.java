/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */

package com.sapienter.jbilling.server.process.task;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.order.db.OrderChangeDTO;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.order.db.OrderProcessDAS;
import com.sapienter.jbilling.server.user.db.MainSubscriptionDTO;
import com.sapienter.jbilling.server.util.CalendarUtils;
import com.sapienter.jbilling.server.util.Constants;

public class ProRateOrderPeriodUtil {

    public static Date calculateCycleStarts (OrderDTO order, Date periodStart) {

        Date retValue = null;
        List<Integer> results = new OrderProcessDAS().findActiveInvoicesForOrder(order.getId());
        Date nextBillableDayFromOrderChanges = order.calcNextBillableDayFromChanges();

        if (!results.isEmpty() && nextBillableDayFromOrderChanges != null) {
            retValue = nextBillableDayFromOrderChanges;
            if (order.getUser().getCustomer().getMainSubscription() != null) {
                retValue = calcCycleStartDateFromMainSubscription(nextBillableDayFromOrderChanges, periodStart, order
                        .getUser()
                        .getCustomer()
                        .getMainSubscription());
            }
        } else if (order.getUser().getCustomer().getMainSubscription() != null) {
            MainSubscriptionDTO mainSubscription = order.getUser().getCustomer().getMainSubscription();
            for (OrderLineDTO line : order.getLines()) {
                for (OrderChangeDTO change : line.getOrderChanges()) {
                    Date nextBillableDayFromChange = calcCycleStartDateFromMainSubscription(
                            change.getNextBillableDate() == null ? change.getStartDate() : change.getNextBillableDate(),
                            periodStart, mainSubscription);
                    if ((retValue == null) || nextBillableDayFromChange.before(retValue)) {
                        retValue = nextBillableDayFromChange;
                    }
                }
            }
            if (retValue == null) {
                retValue = calcCycleStartDateFromMainSubscription(
                        order.getActiveSince() != null ? order.getActiveSince() : order.getCreateDate(), periodStart,
                        mainSubscription);
            }
        } else {
            retValue = periodStart;
        }
        return Util.truncateDate(retValue);
    }

    private static Date calcCycleStartDateFromMainSubscription (Date activeSince, Date periodStart,
            MainSubscriptionDTO mainSubscription) {
        Date calculatedValue = null;
        Calendar cal = new GregorianCalendar();

        Integer nextInvoiceDaysOfPeriod = mainSubscription.getNextInvoiceDayOfPeriod();
        Integer mainSubscriptionPeriodUnit = mainSubscription.getSubscriptionPeriod().getPeriodUnit().getId();
        Integer mainSubscriptionPeriodValue = mainSubscription.getSubscriptionPeriod().getValue();

        cal.setTime(activeSince);
        if (Constants.PERIOD_UNIT_WEEK.equals(mainSubscriptionPeriodUnit)) {
            cal.set(Calendar.DAY_OF_WEEK, nextInvoiceDaysOfPeriod);
        } else if (Constants.PERIOD_UNIT_SEMI_MONTHLY.equals(mainSubscriptionPeriodUnit)) {
            Date expectedStartDate = CalendarUtils.findNearestTargetDateInPastForSemiMonthly(cal,
                    nextInvoiceDaysOfPeriod);
            cal.setTime(expectedStartDate);
        } else {
            cal.set(Calendar.DAY_OF_MONTH, 1);
        }

        if (Constants.PERIOD_UNIT_MONTH.equals(mainSubscriptionPeriodUnit)) {
            // consider end of month case
            if (cal.getActualMaximum(Calendar.DAY_OF_MONTH) <= nextInvoiceDaysOfPeriod
                    && Constants.PERIOD_UNIT_MONTH.equals(mainSubscriptionPeriodUnit)) {
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else {
                cal.add(Calendar.DATE, nextInvoiceDaysOfPeriod - 1);
            }
        }

        if (!Constants.PERIOD_UNIT_SEMI_MONTHLY.equals(mainSubscriptionPeriodUnit)) {
            calculatedValue = CalendarUtils.findNearestTargetDateInPast(cal.getTime(), periodStart,
                    nextInvoiceDaysOfPeriod, mainSubscriptionPeriodUnit, mainSubscriptionPeriodValue);
        } else {
            calculatedValue = cal.getTime();
        }

        return calculatedValue;
    }
}
