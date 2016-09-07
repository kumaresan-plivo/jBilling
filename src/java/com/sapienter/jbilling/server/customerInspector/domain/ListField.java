package com.sapienter.jbilling.server.customerInspector.domain;

import com.sapienter.jbilling.server.invoice.InvoiceBL;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.payment.PaymentBL;
import com.sapienter.jbilling.server.payment.PaymentWS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.NONE)
public class ListField extends AbstractField {

    @XmlAttribute
    private Type name;

    @XmlAttribute(required = true)
    private Type type;

    @XmlAttribute(required = true)
    private String properties;

    @XmlAttribute
    private Integer limit;

    @XmlAttribute
    private String sort;

    @XmlAttribute
    private Order order;

    @XmlAttribute
    private String labels;

    @XmlAttribute
    private String moneyProperties;

    private final static Integer LIMIT = 10;
    final static String SPLIT_CHARACTER = ",";


    public Type getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String[] getProperties() {
        return null!=properties ? properties.split(SPLIT_CHARACTER) : null;
    }

    public Integer getLimit() {
        return limit != null ? limit : LIMIT;
    }

    public String getSort() {
        return sort;
    }

    public Order getOrder() {
        return order;
    }

    public String getMoneyProperties() {
        return moneyProperties;
    }

    public String[] getLabels() {
        String[] labelsArray = null;
        if(null!=this.labels) {
            labelsArray = this.labels.split(SPLIT_CHARACTER);
            labelsArray = labelsArray.length==this.getProperties().length ? labelsArray : null;
        }
        return labelsArray;
    }

    @Override
    public Object getValue(Integer userId) {
        if (this.type != null && !this.type.toString().trim().isEmpty()) {

            ListField.Order ordering = (null!=this.order && Arrays.asList(ListField.Order.values()).contains(this.order)) ? this.order : Order.ASC;

            Integer limit = (null!=this.limit && this.limit>0) ? this.limit : LIMIT;
            List<Object> entities = new ArrayList<>();
            if (type == Type.ORDER) {
                String sortAttribute = this.getPropertyName(OrderWS.class);
                entities.addAll(new OrderBL().findOrdersByUserPagedSortedByAttribute(userId,limit,0,sortAttribute,ordering,this.getApi().getCallerLanguageId()));
                return entities;
            }
            else if (type == Type.INVOICE) {
                String sortAttribute = this.getPropertyName(InvoiceWS.class);
                entities.addAll(new InvoiceBL().findInvoicesByUserPagedSortedByAttribute(userId,limit,0,sortAttribute,ordering,this.getApi().getCallerLanguageId()));
                return entities;
            }
            else if (type == Type.PAYMENT) {
                String sortAttribute = this.getPropertyName(PaymentWS.class);
                entities.addAll(new PaymentBL().findPaymentsByUserPagedSortedByAttribute(userId,limit,0,sortAttribute,ordering,this.getApi().getCallerLanguageId()));
                return entities;
            }
        }
        return null;
    }

    public enum Type {
        ORDER,
        INVOICE,
        PAYMENT
    }

    public enum Order {
        ASC,
        DESC
    }

    private String getPropertyName(Class c) {
        String propertyName = "";
        try {
            if (null!=this.sort && this.sort.trim().length()>0) {
                    propertyName = c.getDeclaredField(String.valueOf(this.sort)).getName();
            }
            else {
                propertyName = (c.equals(OrderWS.class)) ? "createDate" : "createDatetime";
            }
        } catch (Exception e) {
            propertyName = (c.equals(OrderWS.class)) ? "createDate" : "createDatetime";
        }
        return propertyName;
    }

    public boolean isValidProperty(String property, Class c) {
        boolean valid = false;
        if (null!=property && property.trim().length()>0) {
            try {
                valid = null!=c.getDeclaredField(String.valueOf(property)).getName();
            } catch (NoSuchFieldException e) {
                valid = false;
            }
        }
        else {
            valid = false;
        }
        return valid;
    }

    @Override
    public boolean isMoneyProperty(String property) {
        boolean isMoney = false;
        if(null!=this.moneyProperties && null!=property) {
            isMoney = Arrays.asList(this.moneyProperties.split(SPLIT_CHARACTER)).contains(property);
        }
        return isMoney;
    }

}