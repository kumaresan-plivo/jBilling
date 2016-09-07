package com.sapienter.jbilling.server.customerInspector.domain;

import com.sapienter.jbilling.server.metafields.*;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.user.db.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.Set;

@XmlRootElement(name = "metaField")
@XmlAccessorType(XmlAccessType.NONE)
public class MetaFieldField extends AbstractField {

    @XmlAttribute(required = true)
    private String name;

    @XmlAttribute
    private String accountInformationType;

    @XmlAttribute(required = true)
    protected String label;

    public String getName() {
        return name;
    }

    @Override
    public Object getValue(Integer userId) {
        if(userId != null) {
            UserDTO userDTO = new UserDAS().find(userId);
            if (this.accountInformationType!=null && !this.accountInformationType.isEmpty()) {
                for (CustomerAccountInfoTypeMetaField cai : userDTO.getCustomer().getCustomerAccountInfoTypeMetaFields()) {
                    if (cai.getAccountInfoType().getName().trim().equalsIgnoreCase(this.accountInformationType)) {
                        if(cai.getMetaFieldValue().getField().getName().equalsIgnoreCase(this.name.trim())) {
                            return cai.getMetaFieldValue().getValue();
                        }
                    }
                }
            }
            else {
                CustomerDTO customer = new CustomerDAS().find(this.getApi().getUserWS(userId).getCustomerId());
                return this.getMetaFieldValue(customer, this.getApi().getMetaFieldsForEntity(EntityType.CUSTOMER.toString()));
            }
        }

        return null;
    }

    private Object getMetaFieldValue(CustomerDTO dto, MetaFieldWS[] metafields) {
        Set<MetaField> metafieldsSet = MetaFieldBL.convertMetaFieldsToDTO(Arrays.asList(metafields), this.getCompanyId());
        MetaFieldValueWS[] values = MetaFieldBL.convertMetaFieldsToWS(metafieldsSet, dto);
        for (MetaFieldValueWS ws : values) {
            if (ws.getFieldName().trim().equalsIgnoreCase(this.name)) {
                return ws.getValue();
            }
        }

        return null;
    }

    public String getLabel() {
        return label;
    }
}