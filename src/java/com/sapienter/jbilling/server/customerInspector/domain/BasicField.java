package com.sapienter.jbilling.server.customerInspector.domain;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.user.UserWS;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.NumberFormat;

@XmlRootElement(name = "basic")
@XmlAccessorType(XmlAccessType.NONE)
public class BasicField extends AbstractField {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(BasicField.class));

    @XmlAttribute(required = true)
    private String name;

    @XmlAttribute(required = true)
    private Entity entity;

    @XmlAttribute(required = true)
    protected String label;

    public String getName() {
        return name;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public Object getValue(Integer userId) {
        Object objectField = null;
        if(BasicField.Entity.CUSTOMER.equals(entity)) {
            UserWS user = this.getApi().getUserWS(userId);
            try {
                Field field = UserWS.class.getDeclaredField(name);
                if(null!=field) {
                    field.setAccessible(true);
                    Object o = field.get(user);
                    if(o instanceof Number || (o instanceof String && NumberUtils.isNumber((String) o)) ) {
                        objectField = formatNumber(o);
                    }
                    else {
                        objectField = o;
                    }
                }
            } catch (Exception e) {
                LOG.info("Cannot retrieve the required property field: " + this.name);
                e.printStackTrace();
            }
            return objectField;
        }
        else if(BasicField.Entity.USER.equals(entity)) {
            UserWS user = this.getApi().getUserWS(userId);
            try {
                Field field = UserWS.class.getDeclaredField(name);
                if(null!=field) {
                    field.setAccessible(true);
                    Object o = field.get(user);
                    if(o instanceof Number || (o instanceof String && NumberUtils.isNumber((String) o)) ) {
                        objectField = formatNumber(o);
                    }
                    else {
                        objectField = o;
                    }
                }
            } catch (Exception e) {
                LOG.info("Cannot retrieve the required property field: " + this.name);
                e.printStackTrace();
            }
        }
        return objectField;
    }

    private Object formatNumber(Object o) {
        String number = "";
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(2);
        if(o instanceof Number) {
            if(o instanceof Double || o instanceof Float || o instanceof BigDecimal) {
                number = nf.format(o);
            }
            else {
                number = o.toString();
            }
        }
        else if(o instanceof String) {
            try {
                number = nf.format(nf.parseObject((String)o));
            } catch(Exception e) {
                LOG.info("Cannot parse the string object: " + o);
            }
        }
        return number;
    }

    public enum Entity {
        USER,
        CUSTOMER;
    }

    public String getLabel() {
        return label;
    }
}