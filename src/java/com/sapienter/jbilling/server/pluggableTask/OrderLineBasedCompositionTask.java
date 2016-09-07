package com.sapienter.jbilling.server.pluggableTask;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.invoice.NewInvoiceContext;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.process.PeriodOfTime;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.PreferenceBL;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class OrderLineBasedCompositionTask extends PluggableTask implements InvoiceCompositionTask {
    private static final FormatLogger LOG = new FormatLogger(
            Logger.getLogger(OrderChangeBasedCompositionTask.class));

    private String DATE_FORMAT;
    private String INVOICE_LINE_TO;
    private String INVOICE_LINE_PERIOD;
    private String INVOICE_LINE_ORDER_NUMBER;
    private String INVOICE_LINE_DELEGATED;
    private String INVOICE_LINE_DELEGATED_DUE;
    private DateTimeFormatter dateFormatter;
    private Locale locale;

    private boolean resourceBundleInitialized = false;

    public void apply(NewInvoiceContext invoiceCtx, Integer userId) throws TaskException {
        // initialize resource bundle once, if not initialized
        if (!resourceBundleInitialized) {
            initializeResourceBundleProperties(userId);
        }

        /*
         * Process each order being included in this invoice
         */
        for (NewInvoiceContext.OrderContext orderCtx : invoiceCtx.getOrders()) {

            OrderDTO order = orderCtx.order;
            BigDecimal orderContribution = BigDecimal.ZERO;

            if (Integer.valueOf(1).equals(order.getNotesInInvoice())) {
                invoiceCtx.appendCustomerNote(order.getNotes());
            }
            // Add order lines - excluding taxes
            for (OrderLineDTO orderLine : order.getLines()) {
                if (orderLine.getDeleted() == 1) {
                    continue;
                }

                for (PeriodOfTime period : orderCtx.periods) {
                    InvoiceLineDTO invoiceLine = null;
                    if (orderLine.getOrderLineType().getId() == Constants.ORDER_LINE_TYPE_ITEM ||
                            orderLine.getOrderLineType().getId() == Constants.ORDER_LINE_TYPE_DISCOUNT) {

                        String desc = null;

                        // compose order line description, this appends the period.
                        if (desc == null) {
                            // this will be invoked for ORDER_LINE_TYPE_ITEM
                            // and ORDER_LINE_TYPE_DISCOUNT.PERIODBASED discounts.
                            desc = composeDescription(orderLine, period);
                        }

                        Integer type;
                        // determine the invoice line type (one-time, recurring, line from sub-account)
                        if (userId.equals(order.getUser().getId())) {
                            if (Constants.ORDER_PERIOD_ONCE.equals(order.getPeriodId())) {
                                type = Constants.INVOICE_LINE_TYPE_ITEM_ONETIME;
                            } else {
                                type = Constants.INVOICE_LINE_TYPE_ITEM_RECURRING;
                            }
                        } else {
                            type = Constants.INVOICE_LINE_TYPE_SUB_ACCOUNT;
                        }

                        BigDecimal periodAmount = calculateAmountForPeriod(orderLine, period);

                        invoiceLine = new InvoiceLineDTO(null,
                                desc,
                                periodAmount,
                                orderLine.getPrice(),
                                orderLine.getQuantity(),
                                type,
                                0,
                                orderLine.getItemId(),
                                order.getUser().getId(),
                                orderLine.isPercentage() ? 1 : 0);
                        // link invoice line to the order that originally held the charge
                        invoiceLine.setOrder(order);
                        orderContribution = orderContribution.add(periodAmount);

                        invoiceCtx.addResultLine(invoiceLine);
                    } else if (orderLine.getOrderLineType().getId() == Constants.ORDER_LINE_TYPE_TAX) {
                        // tax items
                        int taxLineIndex = getTaxLineIndex(invoiceCtx.getResultLines(), orderLine.getDescription());
                        if (taxLineIndex >= 0) {
                            // tax already exists, add the total
                            invoiceLine = (InvoiceLineDTO) invoiceCtx.getResultLines().get(taxLineIndex);
                            BigDecimal periodAmount = orderLine.getAmount();
                            invoiceLine.setAmount(invoiceLine.getAmount().add(periodAmount));
                            orderContribution = orderContribution.add(periodAmount);
                        } else {
                            // tax has not yet been added, add a new invoice line
                            BigDecimal periodAmount = orderLine.getAmount();
                            invoiceLine = new InvoiceLineDTO(null,
                                    orderLine.getDescription(),
                                    periodAmount,
                                    orderLine.getPrice(),
                                    null,
                                    Constants.INVOICE_LINE_TYPE_TAX,
                                    0,
                                    orderLine.getItemId(),
                                    order.getUser().getId(),
                                    orderLine.isPercentage() ? 1 : 0);

                            orderContribution = orderContribution.add(periodAmount);

                            invoiceCtx.addResultLine(invoiceLine);
                        }
                    } else if (orderLine.getOrderLineType().getId() == Constants.ORDER_LINE_TYPE_PENALTY) {
                        // penalty items
                        BigDecimal periodAmount = orderLine.getAmount();
                        invoiceLine = new InvoiceLineDTO(null,
                                orderLine.getDescription(),
                                periodAmount,
                                null,
                                null,
                                Constants.INVOICE_LINE_TYPE_PENALTY,
                                0,
                                orderLine.getItemId(),
                                order.getUser().getId(),
                                orderLine.isPercentage() ? 1 : 0);

                        orderContribution = orderContribution.add(periodAmount);

                        invoiceCtx.addResultLine(invoiceLine);
                    }
                }
            }
        }


        /*
         * add delegated invoices
         */
        for (InvoiceDTO invoice : invoiceCtx.getInvoices()) {
            // the whole invoice will be added as a single line
            // The text of this line has to be i18n
            String delegatedLine = new StringBuilder(100)
                    .append(INVOICE_LINE_DELEGATED)
                    .append(' ')
                    .append(invoice.getPublicNumber())
                    .append(' ')
                    .append(INVOICE_LINE_DELEGATED_DUE)
                    .append(' ')
                    .append(dateFormatter.print(invoice.getDueDate().getTime()))
                    .toString();

            invoiceCtx.addResultLine(new InvoiceLineDTO.Builder()
                    .description(delegatedLine)
                    .amount(invoice.getBalance())
                    .type(Constants.INVOICE_LINE_TYPE_DUE_INVOICE)
                    .build());
        }
    }

    private BigDecimal calculateAmountForPeriod(OrderLineDTO orderLine, PeriodOfTime period) {
        if (orderLine.getPurchaseOrder().getProrateFlag()) {
            return calculateProRatedAmountForPeriod(orderLine.getAmount(), period);
        }
        return orderLine.getAmount();
    }

    private BigDecimal calculateProRatedAmountForPeriod(BigDecimal fullPrice, PeriodOfTime period) {

        if (period == null || fullPrice == null) {
            LOG.warn("Called with null parameters");
            return null;
        }

        // this is an amount from a one-time order, not a real period of time
        if (period == PeriodOfTime.OneTimeOrderPeriodOfTime) {
            return fullPrice;
        }

        // if this is not a fraction of a period, don't bother making any calculations
        if (period.getDaysInCycle() == period.getDaysInPeriod()) {
            return fullPrice;
        }

        BigDecimal oneDayPrice = fullPrice.divide(new BigDecimal(period.getDaysInCycle()), Constants.BIGDECIMAL_SCALE,
                Constants.BIGDECIMAL_ROUND);

        return oneDayPrice.multiply(new BigDecimal(period.getDaysInPeriod())).setScale(Constants.BIGDECIMAL_SCALE,
                Constants.BIGDECIMAL_ROUND);
    }

    /**
     * Composes the actual invoice line description based off of set entity preferences and the order period being
     * processed.
     *
     * @param order  order being processed
     * @param period period of time being processed
     * @param desc   original order line description
     * @return invoice line description
     */
    protected String composeDescription(OrderLineDTO orderLine, PeriodOfTime period) {
        OrderDTO order = orderLine.getPurchaseOrder();
        // initialize resource bundle once, if not initialized
        if (!resourceBundleInitialized) {
            initializeResourceBundleProperties(order.getBaseUserByUserId().getUserId());
        }
        StringBuilder lineDescription = new StringBuilder(1000).append(orderLine.getDescription());

        /*
         * append the billing period to the order line for non one-time orders
         */
        if (order.getOrderPeriod().getId() != Constants.ORDER_PERIOD_ONCE) {
            // period ends at midnight of the next day (E.g., Oct 1 00:00, effectivley end-of-day Sept 30th).
            // subtract 1 day from the end so the period print out looks human readable
            LocalDate start = period.getDateMidnightStart();
            LocalDate end = period.getDateMidnightEnd().minusDays(1);

            LOG.debug("Composing for period %s to %s. Using date format: %s", start, end, DATE_FORMAT);

            // now add this to the line
            lineDescription.append(' ').append(INVOICE_LINE_PERIOD).append(' ');
            lineDescription.append(dateFormatter.print(start)).append(' ');
            lineDescription.append(INVOICE_LINE_TO).append(' ');
            lineDescription.append(dateFormatter.print(end));
        }
        /*
         * optionally append the order id if the entity has the preference set
         */
        if (needAppendOrderId(order.getBaseUserByUserId().getCompany().getId())) {
            lineDescription.append(INVOICE_LINE_ORDER_NUMBER);
            lineDescription.append(' ');
            lineDescription.append(order.getId().toString());
        }
        return lineDescription.toString();
    }

    /**
     * Gets the locale for the given user.
     *
     * @param userId user to get locale for
     * @return users locale
     */
    protected Locale getLocale(Integer userId) {
        if (locale == null) {
            try {
                UserBL user = new UserBL(userId);
                locale = user.getLocale();
            } catch (Exception e) {
                throw new SessionInternalError("Exception occurred determining user locale for composition.", e);
            }
        }
        return locale;
    }

    /**
     * Returns true if the given entity wants the order ID appended to the invoice line description.
     *
     * @param entityId entity id
     * @return true if order ID should be appended, false if not.
     */
    protected boolean needAppendOrderId(Integer entityId) {
        int preferenceOrderIdInInvoiceLine = 0;
        try {
            preferenceOrderIdInInvoiceLine = PreferenceBL.getPreferenceValueAsIntegerOrZero(entityId,
                    Constants.PREFERENCE_ORDER_IN_INVOICE_LINE);
        } catch (Exception e) {
            /* use default value */
        }
        return preferenceOrderIdInInvoiceLine == 1;
    }

    private void initializeResourceBundleProperties(Integer userId) {
        LOG.debug("Initializing resource bundle properties");
        ResourceBundle bundle = ResourceBundle.getBundle("entityNotifications", getLocale(userId));

        DATE_FORMAT = bundle.getString("format.date");
        INVOICE_LINE_TO = bundle.getString("invoice.line.to");
        INVOICE_LINE_PERIOD = bundle.getString("invoice.line.period");
        INVOICE_LINE_ORDER_NUMBER = bundle.getString("invoice.line.orderNumber");
        INVOICE_LINE_DELEGATED = bundle.getString("invoice.line.delegated");
        INVOICE_LINE_DELEGATED_DUE = bundle.getString("invoice.line.delegated.due");

        dateFormatter = DateTimeFormat.forPattern(DATE_FORMAT);

        resourceBundleInitialized = true;
    }

    /**
     * Returns the index of a tax line with the matching description. Used to find an existing
     * tax line so that similar taxes can be consolidated;
     *
     * @param lines invoice lines
     * @param desc  tax line description
     * @return index of tax line
     */
    protected int getTaxLineIndex(List lines, String desc) {
        for (int f = 0; f < lines.size(); f++) {
            InvoiceLineDTO line = (InvoiceLineDTO) lines.get(f);
            if (line.getTypeId() == Constants.ORDER_LINE_TYPE_TAX) {
                if (line.getDescription().equals(desc)) {
                    return f;
                }
            }
        }
        return -1;
    }
}
