package com.sapienter.jbilling.server.metafields;

import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldDAS;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.payment.db.PaymentInformationDTO;
import com.sapienter.jbilling.server.payment.db.PaymentMethodTypeDAS;
import com.sapienter.jbilling.server.user.db.AccountInformationTypeDAS;
import com.sapienter.jbilling.server.user.db.AccountInformationTypeDTO;
import com.sapienter.jbilling.server.user.db.CustomerAccountInfoTypeMetaField;
import com.sapienter.jbilling.server.user.db.CustomerDTO;

import java.util.*;

/**
 * Created by marcolin on 29/10/15.
 */
public class MetaFieldExternalHelper {

    /**
     * Remove metafields from the entity with a value of null or ''
     *
     * @param customer
     */
    public static void removeEmptyAitMetaFields(CustomerDTO customer) {
        List<CustomerAccountInfoTypeMetaField> valuesToRemove = new ArrayList<CustomerAccountInfoTypeMetaField>();

        Set<CustomerAccountInfoTypeMetaField> metaFieldsSet = customer.getCustomerAccountInfoTypeMetaFields();

        for(CustomerAccountInfoTypeMetaField metaField : metaFieldsSet) {
            MetaFieldValue value = metaField.getMetaFieldValue();

            if(value.getValue() == null ||
                    value.getValue().toString().trim().isEmpty()) {
                valuesToRemove.add(metaField);
            }
        }
        metaFieldsSet.removeAll(valuesToRemove);
    }

    public static void setAitMetaField(Integer entityId, CustomerDTO entity, Integer groupId, String name, Object value){
        setAitMetaField(entityId, groupId, entity, name, value);
    }

    /**
     * Sets the value of an ait meta field in a map.
     *
     * @param entity	:	 customer entity for search/set fields
     * @param name	:	field name
     * @param value	:	field value
     * @throws IllegalArgumentException thrown if field name does not exist, or if value is of an incorrect type.
     */
    public static void setAitMetaField(Integer entityId, Integer groupId, CustomerDTO entity, String name, Object value) throws IllegalArgumentException {
        EntityType[] types = entity.getCustomizedEntityType();
        if (types == null) {
            throw new IllegalArgumentException("Meta Fields could not be specified for current entity");
        }
        MetaField fieldName = null;
        if(null != groupId){
            fieldName = new MetaFieldDAS().getFieldByNameTypeAndGroup(entityId, types, name, groupId);
        }
        if (fieldName == null) {
            throw new IllegalArgumentException("Meta Field with name " + name + " was not defined for current entity");
        }
        MetaFieldValue field = fieldName.createValue();
        try {
            field.setValue(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Incorrect type for meta field with name " + name, ex);
        }
        entity.setAitMetaField(field, groupId);
    }


    public static MetaField findPaymentMethodMetaField(String fieldName, Integer paymentMethodTypeId) {

        for (MetaField field : getPaymentMethodMetaFields(paymentMethodTypeId)) {
            if(field.getName().equals(fieldName)){
                return field;
            }
        }
        return null;
    }



    public static Set<MetaField> getPaymentMethodMetaFields(Integer paymentMetohdTypeId) {
        return new PaymentMethodTypeDAS().findNow(paymentMetohdTypeId).getMetaFields();
    }


    /**
     * Usefull method for updating ait meta fields with validation before entity saving
     *
     * @param entity    target entity
     * @param dto       dto with new data
     */
    public static void updateAitMetaFieldsWithValidation(Integer languageId, Integer entityId, Integer accountTypeId, CustomerDTO entity, MetaContent dto) {
        if (null != accountTypeId) {
            Map<Integer, List<MetaField>> groupMetaFields =
                    getAvailableAccountTypeFieldsMap(accountTypeId);

            for (Map.Entry<Integer, List<MetaField>> entry : groupMetaFields.entrySet()) {
                Integer groupId = entry.getKey();
                List<MetaField> fields = entry.getValue();

                for (MetaField field : fields) {
                    String fieldName = field.getName();
                    MetaFieldValue newValue = dto.getMetaField(fieldName, groupId);
                    if (newValue == null) {
                        newValue = dto.getMetaField(field.getId());
                    }
                    entity.setAitMetaField(entityId, groupId, fieldName,
                            newValue != null ? newValue.getValue() : null);
                }
            }
        }

        for (Map.Entry<Integer, List<MetaFieldValue>> entry : entity.getAitMetaFieldMap().entrySet()) {
            for (MetaFieldValue value : entry.getValue()) {
                MetaFieldBL.validateMetaField(languageId, value.getField(), value, entity);
            }
        }

        removeEmptyAitMetaFields(entity);
    }


    public static Map<Integer, List<MetaField>> getAvailableAccountTypeFieldsMap(Integer accountTypeId){
        Map<Integer, List<MetaField>> metaFields = new HashMap<Integer, List<MetaField>>(1);
        AccountInformationTypeDAS aitDAS = new AccountInformationTypeDAS();
        for(AccountInformationTypeDTO ait : aitDAS.getInformationTypesForAccountType(accountTypeId)){
            metaFields.put(ait.getId(), new LinkedList(ait.getMetaFields()));
        }
        return metaFields;
    }

    /**
     * Usefull method for updating meta fields with validation before entity saving
     * @param entity    target entity
     * @param dto       dto with new data
     */
    public static void updatePaymentMethodMetaFieldsWithValidation(Integer languageId, Integer entityId, Integer paymentMethodTypeId,
                                                                   PaymentInformationDTO entity, MetaContent dto) {

        for (MetaField field : getPaymentMethodMetaFields(paymentMethodTypeId)) {
            String fieldName = field.getName();
            MetaFieldValue newValue = dto.getMetaField(fieldName, null);
            if (newValue == null) {
                newValue = dto.getMetaField(field.getId());
            }

            entity.setMetaField(entityId, null, fieldName,
                    newValue != null ? newValue.getValue() : null);
        }

        // Updating and validating of ait meta fields is done in a separate method

        for (MetaFieldValue value : entity.getMetaFields()) {
            MetaFieldBL.validateMetaField(languageId, value.getField(), value, entity);
        }
    }
}
