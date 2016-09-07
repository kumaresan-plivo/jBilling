package com.sapienter.jbilling.server.company;

import com.sapienter.jbilling.client.authentication.JBillingPasswordEncoder;
import com.sapienter.jbilling.client.util.Constants;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.account.AccountTypeBL;
import com.sapienter.jbilling.server.company.task.*;
import com.sapienter.jbilling.server.invoice.db.InvoiceDeliveryMethodDAS;
import com.sapienter.jbilling.server.invoice.db.InvoiceDeliveryMethodDTO;
import com.sapienter.jbilling.server.item.CurrencyBL;
import com.sapienter.jbilling.server.metafields.MetaFieldBL;
import com.sapienter.jbilling.server.metafields.MetaFieldWS;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldDAS;
import com.sapienter.jbilling.server.metafields.db.ValidationRule;
import com.sapienter.jbilling.server.metafields.db.ValidationRuleDAS;
import com.sapienter.jbilling.server.order.OrderChangeStatusBL;
import com.sapienter.jbilling.server.payment.db.PaymentMethodTypeDTO;
import com.sapienter.jbilling.server.user.*;
import com.sapienter.jbilling.server.user.contact.db.ContactDAS;
import com.sapienter.jbilling.server.user.contact.db.ContactDTO;
import com.sapienter.jbilling.server.user.contact.db.ContactMapDTO;
import com.sapienter.jbilling.server.user.db.*;
import com.sapienter.jbilling.server.user.permisson.db.RoleDAS;
import com.sapienter.jbilling.server.user.permisson.db.RoleDTO;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.InternationalDescriptionWS;
import com.sapienter.jbilling.server.util.WebServicesSessionSpringBean;
import com.sapienter.jbilling.server.util.db.*;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import com.sapienter.jbilling.common.CommonConstants;
import org.apache.commons.lang.StringUtils;

import javax.naming.NamingException;
import javax.persistence.Transient;
import javax.ws.rs.HEAD;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vivek on 30/10/14.
 */
public class CopyCompanyBL {
    List<String> createEntities;
    List<AbstractCopyTask> abstractCopyTasksList=new ArrayList<AbstractCopyTask>();
    private static final Class copyTask[] = new Class[]{
            SystemAdminCopyTask.class,
            ConfigurationCopyTask.class,
            CategoryCopyTask.class,
            ProductCopyTask.class,
            PlanCopyTask.class,
            EDICopyTask.class,
            ReportCopyTask.class
    };

    public Class[] getCopyTask() {
        return copyTask;
    }

    public UserWS copyCompany(String childCompanyTemplateName, Integer entityId, List<String> importEntities, boolean isCompanyChild, boolean copyProducts, boolean copyPlans) {

        synchronized (CopyCompanyUtils.oldNewMetaFieldMap) {
            int targetEntityId=0;
            try {
                Integer parentCompanyId = 0;
//               Need to set template company id in entity Id. Because new company will be copied from template company.
                parentCompanyId = entityId;
                if (isCompanyChild) {
//                    If creating child company. We need to create it from template company.

                    CompanyDTO templateChildCompany = new CompanyDAS().findEntityByName(childCompanyTemplateName);
                    if(templateChildCompany == null) {
                        throw new SessionInternalError(
                                "Template Company does not exist ",
                                new String[]{"copy.company.child.template.not.exist," + childCompanyTemplateName.replaceAll(",", "&#44;")});
                    }
                    entityId = templateChildCompany.getId();
                }

                CompanyDTO targetEntity = createCompany(entityId, isCompanyChild, parentCompanyId);
                copyLiquibaseChangeLogs(entityId, targetEntity.getId());
                UserWS copyUserWS = createUserWithRoles(entityId, targetEntity);
                targetEntityId = targetEntity.getId();
                createCompanyContact(entityId, targetEntity, copyUserWS);

                for(Class task:getCopyTask()){
                    AbstractCopyTask abstractCopyTask=(AbstractCopyTask)task.newInstance();

                    if((!copyProducts && (abstractCopyTask instanceof CategoryCopyTask || abstractCopyTask instanceof ProductCopyTask)) ||
                    (!copyPlans && abstractCopyTask instanceof PlanCopyTask)){
                        continue;
                    }
                    abstractCopyTasksList.add(abstractCopyTask);
                    abstractCopyTask.create(entityId, targetEntityId);
                }

                copyUserWS.setEntityId(targetEntityId);
                return copyUserWS;
            } catch (Exception exception) {
                for(AbstractCopyTask copyTask:abstractCopyTasksList){
                    copyTask.cleanUp(targetEntityId);
                }
                throw new SessionInternalError(exception);
            } finally {
                CopyCompanyUtils.oldNewUserMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewCategoryMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewItemMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewAssetMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewUsagePoolMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewCurrencyExchangeMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewOrderStatusMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewOrderChangeStatusMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewOrderPeriodMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewPlanItemMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewOrderMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewAssetStatusMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewMetaFieldMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewEDITypeMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewDataTableMap = new HashMap<Integer, Integer>();
                CopyCompanyUtils.oldNewRouteRateCardMap = new HashMap<Integer, Integer>();
            }
        }
    }

    public CompanyDTO createCompany(Integer entityId, boolean isCompanyChild, Integer parentCompanyId) {
        CompanyDAS companyDAS = new CompanyDAS();
        CompanyDTO oldCompany = companyDAS.find(entityId);
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setDescription(oldCompany.getDescription() + " copy " + System.currentTimeMillis());
        companyDTO.setCreateDatetime(new Date());
        companyDTO.setLanguage(oldCompany.getLanguage());
        companyDTO.setCurrency(oldCompany.getCurrency());
        companyDTO.setCurrencies(oldCompany.getCurrencies());
        companyDTO.setType(oldCompany.getType());
        companyDTO.setDeleted(0);
        if (isCompanyChild) {
            CompanyDTO parentCompany = companyDAS.find(parentCompanyId);
            companyDTO.setParent(parentCompany);
        }
        companyDTO = companyDAS.save(companyDTO);
        companyDTO.getCurrency().getEntities_1().add(companyDTO.getId());
        companyDTO = new CompanyDAS().save(companyDTO);
//        Set Invoice delivery Method Here. Its a constant for all account type.
        setInvoiceDeliveryMethod(companyDTO);
        return companyDTO;
    }

    private void copyLiquibaseChangeLogs(Integer oldCompany, Integer newCompany){
        CompanyDAS companyDAS = new CompanyDAS();
        companyDAS.copyLiquibaseChangeLogs(oldCompany, newCompany, "enrollment-edi-communication.xml");
        companyDAS.copyLiquibaseChangeLogs(oldCompany, newCompany, "parent-company-account-type.xml");
    }

    private void setInvoiceDeliveryMethod(CompanyDTO targetEntity) {
        InvoiceDeliveryMethodDAS invoiceDeliveryMethodDAS = new InvoiceDeliveryMethodDAS();
        List<InvoiceDeliveryMethodDTO> invoiceDeliveryMethodDTOs =  invoiceDeliveryMethodDAS.findAll();
        for(InvoiceDeliveryMethodDTO invoiceDeliveryMethodDTO : invoiceDeliveryMethodDTOs) {
            invoiceDeliveryMethodDTO.getEntities().add(targetEntity);
            invoiceDeliveryMethodDAS.save(invoiceDeliveryMethodDTO);
        }
    }

    public UserWS createUserWithRoles(Integer entityId, CompanyDTO targetEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("admin");

        String randPassword = UserBL.generatePCICompliantPassword();
        JBillingPasswordEncoder passwordEncoder = new JBillingPasswordEncoder();
        userDTO.setPassword(passwordEncoder.encodePassword(randPassword, null));

        userDTO.setDeleted(0);
        userDTO.setUserStatus(new UserStatusDAS().find(UserDTOEx.STATUS_ACTIVE));
        userDTO.setSubscriberStatus(new SubscriberStatusDAS().find(UserDTOEx.SUBSCRIBER_ACTIVE));
        userDTO.setLanguage(targetEntity.getLanguage());
        userDTO.setCurrency(targetEntity.getCurrency());
        userDTO.setCompany(targetEntity);
        userDTO.setCreateDatetime(new Date());
        userDTO.setEncryptionScheme(Integer.parseInt(Util.getSysProp(com.sapienter.jbilling.server.util.Constants.PASSWORD_ENCRYPTION_SCHEME)));

        CompanyDTO entity = new CompanyDAS().find(entityId);
        new RoleBL().createDefaultRoles(targetEntity.getLanguageId(), entity, targetEntity);

        RoleDTO roleDTO = new RoleDAS().findByRoleTypeIdAndCompanyId(Constants.TYPE_ROOT, targetEntity.getId());
        Set<RoleDTO> roleDTOs = new HashSet<RoleDTO>();
        roleDTOs.add(roleDTO);
        userDTO.setRoles(roleDTOs);

        userDTO = new UserDAS().save(userDTO);
        UserWS userWS = UserBL.getWS(new UserDTOEx(userDTO));
        userWS.setPassword(randPassword);
        return userWS;
    }

    public void createCompanyContact(Integer entityId, CompanyDTO targetEntity, UserWS user) {
        ContactDTO contact = new EntityBL(entityId).getContact();
        new ContactBL().create(new ContactDTOEx(contact), com.sapienter.jbilling.server.util.Constants.TABLE_ENTITY, targetEntity.getId(),
                user.getUserId());
    }

    public Set<MetaField> copyMetaFields(Set<MetaField> metaFields, CompanyDTO entity) {
        Set<MetaField> copyMetaFields = new HashSet<MetaField>();
        for (MetaField metaField : metaFields) {
            MetaFieldWS copyMetaFieldWS = MetaFieldBL.getWS(metaField);
            copyMetaFieldWS.setId(0);

            MetaField copyMetaField = MetaFieldBL.getDTO(copyMetaFieldWS, entity.getId());
            if (metaField.getValidationRule() != null)
                copyMetaField.setValidationRule(new ValidationRuleDAS().find(metaField.getValidationRule().getId()));

            copyMetaField = new MetaFieldDAS().save(copyMetaField);
            copyMetaFields.add(copyMetaField);
        }
        return copyMetaFields;
    }
}
