package com.sapienter.jbilling.server.company.task;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.company.CopyCompanyBL;
import com.sapienter.jbilling.server.company.CopyCompanyUtils;
import com.sapienter.jbilling.server.metafields.EntityType;
import com.sapienter.jbilling.server.metafields.MetaFieldType;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldDAS;
import com.sapienter.jbilling.server.payment.db.PaymentMethodTemplateDAS;
import com.sapienter.jbilling.server.payment.db.PaymentMethodTemplateDTO;
import com.sapienter.jbilling.server.payment.db.PaymentMethodTypeDAS;
import com.sapienter.jbilling.server.payment.db.PaymentMethodTypeDTO;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.util.Constants;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by vivek on 5/11/14.
 */
public class PaymentMethodTypeCopyTask extends AbstractCopyTask {
    PaymentMethodTypeDAS paymentMethodTypeDAS = null;
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PaymentMethodTypeCopyTask.class));

    private static final Class dependencies[] = new Class[]{
            MetaFieldsCopyTask.class,
            PaymentMethodTemplateCopyTask.class
    };

    public Class[] getDependencies() {
        return dependencies;
    }

    public Boolean isTaskCopied(Integer entityId, Integer targetEntityId) {
        List<PaymentMethodTypeDTO> copyPaymentMethodTypeList = new PaymentMethodTypeDAS().findAllByEntity(targetEntityId);
        return copyPaymentMethodTypeList != null && !copyPaymentMethodTypeList.isEmpty();
    }

    public PaymentMethodTypeCopyTask() {
        init();
    }

    private void init() {
        paymentMethodTypeDAS = new PaymentMethodTypeDAS();
    }


    public void create(Integer entityId, Integer targetEntityId) {
        initialise(entityId, targetEntityId);  // This will create all the entities on which the current entity is dependent.
        LOG.debug("Create PaymentMethodTypeCopyTask");
        copyPaymentMethodType(entityId, targetEntityId);
        LOG.debug("PaymentMethodTypeCopyTask has been completed.");
    }

    private void copyPaymentMethodType(Integer entityId, Integer targetEntityId) {

        List<PaymentMethodTypeDTO> paymentMethodTypeDTOList = paymentMethodTypeDAS.findAllByEntity(entityId);
        CompanyDTO targetEntity = new CompanyDAS().find(targetEntityId);
            /*We assume that the payment method name is unique per company*/
        for (PaymentMethodTypeDTO paymentMethodTypeDTO : paymentMethodTypeDTOList) {
            if (paymentMethodTypeDAS.findByMethodName(paymentMethodTypeDTO.getMethodName(), targetEntityId).isEmpty()) {
                PaymentMethodTypeDTO copyPaymentMethodTypeDTO = new PaymentMethodTypeDTO();
                copyPaymentMethodTypeDTO.setEntity(targetEntity);
                copyPaymentMethodTypeDTO.setIsRecurring(paymentMethodTypeDTO.getIsRecurring());
                copyPaymentMethodTypeDTO.setMethodName(paymentMethodTypeDTO.getMethodName());
                copyPaymentMethodTypeDTO.setPaymentMethodTemplate(paymentMethodTypeDTO.getPaymentMethodTemplate());
                copyPaymentMethodTypeDTO.setAllAccountType(paymentMethodTypeDTO.isAllAccountType());

                Set<MetaField> copyMetaFields = new CopyCompanyBL().copyMetaFields(paymentMethodTypeDTO.getMetaFields(), targetEntity);
                copyPaymentMethodTypeDTO.setMetaFields(copyMetaFields);
                copyPaymentMethodTypeDTO = paymentMethodTypeDAS.save(copyPaymentMethodTypeDTO);

                CopyCompanyUtils.oldNewPaymentMethodTypeMap.put(paymentMethodTypeDTO.getId(), copyPaymentMethodTypeDTO.getId());

            }
        }
    }

}