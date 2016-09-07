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

package com.sapienter.jbilling.server.customerEnrollment.db;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.account.AccountTypeBL;
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentCommentWS;
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentStatus;
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentWS;
import com.sapienter.jbilling.server.customerEnrollment.event.EnrollmentCompletionEvent;
import com.sapienter.jbilling.server.customerEnrollment.event.ValidateEnrollmentEvent;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.metafields.MetaFieldBL;
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.metafields.db.MetaFieldGroup;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.EntityType;
import com.sapienter.jbilling.server.process.event.SureAddressEvent;
import com.sapienter.jbilling.server.system.event.EventManager;
import com.sapienter.jbilling.server.user.db.AccountInformationTypeDTO;
import com.sapienter.jbilling.server.user.db.AccountTypeDTO;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.user.db.UserDAS;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author Emil
 */
public class CustomerEnrollmentBL {

    private CustomerEnrollmentDTO customerEnrollmentDTO = null;
    private CustomerEnrollmentDAS customerEnrollmentDAS = null;
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(CustomerEnrollmentBL.class));

    public CustomerEnrollmentBL(){
        init();
    }

    public CustomerEnrollmentBL(Integer enrollmentId){
        init();
        customerEnrollmentDAS.find(enrollmentId);
    }

    private  void init(){
        customerEnrollmentDAS=new CustomerEnrollmentDAS();
    }


    public CustomerEnrollmentWS getWS(CustomerEnrollmentDTO dto) {
        if (dto == null) {
            dto = this.customerEnrollmentDTO;
        }
        CustomerEnrollmentWS ws = new CustomerEnrollmentWS();
        ws.setId(dto.getId());
        ws.setAccountTypeId(dto.getAccountType().getId());
        ws.setDeleted(dto.getDeleted());
        ws.setBulkEnrollment(dto.isBulkEnrollment());
        ws.setBrokerId(dto.getBrokerId());
        ws.setCompanyName(dto.getCompany().getDescription());

        ws.setAccountTypeName(dto.getAccountType().getDescription());

        ws.setCreateDatetime(dto.getCreateDatetime());
        List <CustomerEnrollmentCommentWS> customerEnrollmentCommentWSList =new ArrayList<CustomerEnrollmentCommentWS>();

        CustomerEnrollmentCommentBL customerEnrollmentCommentBL=new CustomerEnrollmentCommentBL();

        if(dto.getComments()!=null){
            for(CustomerEnrollmentCommentDTO customerEnrollmentCommentDTO:dto.getComments()){
                customerEnrollmentCommentWSList.add(customerEnrollmentCommentBL.getWS(customerEnrollmentCommentDTO));
            }
        }

        ws.setCustomerEnrollmentComments(customerEnrollmentCommentWSList.toArray(new CustomerEnrollmentCommentWS[customerEnrollmentCommentWSList.size()]));
        ws.setMetaFields(convertCustomerEnrollmentMetaFieldsToWS(dto.getCompany().getId(), new AccountTypeBL(dto.getAccountType().getId()).getAccountType(), dto));
        ws.setAccountNumber((String) ws.getMetaFieldValue(FileConstants.CUSTOMER_ACCOUNT_KEY));
        ws.setEntityId(dto.getCompany().getId());
        ws.setStatus(dto.getStatus());
        if(dto.getUser() != null) {
            ws.setCustomerId(dto.getUser().getId());
        }

        if(dto.getParentCustomer()!=null){
            ws.setParentUserId(dto.getParentCustomer().getUserId());
        }
        if(dto.getParentEnrollment()!=null){
            ws.setParentEnrollmentId(dto.getParentEnrollment().getId());
        }
        ws.setMessage(dto.getMessage());
        return ws;
    }

    public CustomerEnrollmentDTO getDTO(CustomerEnrollmentWS ws) throws SessionInternalError{

        CustomerEnrollmentDTO dto = new CustomerEnrollmentDTO();
        if(ws.getId() > 0 ){
            dto.setId(ws.getId());
            dto.setVersionNum(customerEnrollmentDAS.findNow(ws.getId()).getVersionNum());
        }
        dto.setDeleted(ws.getDeleted());
        dto.setBulkEnrollment(ws.isBulkEnrollment());
        dto.setBrokerId(ws.getBrokerId());
        MetaFieldBL.fillMetaFieldsFromWS(ws.getEntityId(), dto, ws.getMetaFields());
        dto.setAccountType(new AccountTypeBL(ws.getAccountTypeId()).getAccountType());
        if(ws.getCustomerId() != null && ws.getCustomerId() > 0) {
            dto.setUser(new UserDAS().find(ws.getCustomerId()));
        }


        if(ws.getCustomerEnrollmentComments()!=null){
            CustomerEnrollmentCommentBL customerEnrollmentCommentBL=new CustomerEnrollmentCommentBL();
            for(CustomerEnrollmentCommentWS customerEnrollmentCommentWS:ws.getCustomerEnrollmentComments()){
                dto.getComments().add(customerEnrollmentCommentBL.getDTO(customerEnrollmentCommentWS));
            }
        }
        if(ws.getParentUserId()!=null){
            dto.setParentCustomer(new UserDAS().find(ws.getParentUserId()));
        }

        if(ws.getParentEnrollmentId()!=null){
            dto.setParentEnrollment(new CustomerEnrollmentDAS().find(ws.getParentEnrollmentId()));
        }
        dto.setCompany(new CompanyDAS().find(ws.getEntityId()));

        dto.setCreateDatetime(ws.getCreateDatetime());
        dto.setMetaFields(new LinkedList<MetaFieldValue>(dto.getMetaFields()));
        dto.setStatus(ws.getStatus());
        dto.setMessage(ws.getMessage());
        return dto;
    }

    public Integer save(CustomerEnrollmentDTO customerEnrollmentDTO) {
        Set<CustomerEnrollmentCommentDTO> customerEnrollmentCommentDTOs=customerEnrollmentDTO.getComments();
        customerEnrollmentDTO.setComments(null);
        CustomerEnrollmentDTO customerEnrollment=customerEnrollmentDAS.save(customerEnrollmentDTO);
        LOG.debug("customer enrollment  has been saved.");
        if(customerEnrollment.getStatus()!=null && customerEnrollment.getStatus().equals(CustomerEnrollmentStatus.VALIDATED)) {
            EnrollmentCompletionEvent event = new EnrollmentCompletionEvent(customerEnrollment.getCompany().getId(), customerEnrollment.getId());
            EventManager.process(event);
        }
        for(CustomerEnrollmentCommentDTO customerEnrollmentCommentDTO:customerEnrollmentCommentDTOs){
            customerEnrollmentCommentDTO.setCustomerEnrollment(customerEnrollment);
            new CustomerEnrollmentCommentDAS().save(customerEnrollmentCommentDTO);
        }
        return customerEnrollment.getId();
    }

    public void delete(Integer customerEnrollmentId){
        LOG.debug("customerEnrollmentDAS.countChildCompanies(customerEnrollmentId)   " + customerEnrollmentDAS.countChildCompanies(customerEnrollmentId));
        if(customerEnrollmentDAS.countChildCompanies(customerEnrollmentId) > 0) {
            throw new SessionInternalError(
                    "You can not delete this enrollment as it's all child did not enrolled.",
                    new String[] { "CustomerEnrollmentDTO,label,customer.enrollment.not.delete.label" });
        }
        CustomerEnrollmentDTO customerEnrollmentDTO=customerEnrollmentDAS.find(customerEnrollmentId);
        customerEnrollmentDTO.setDeleted(1);
        customerEnrollmentDAS.save(customerEnrollmentDTO);
    }

    public Long countByAccountType(Integer accountType){
        return customerEnrollmentDAS.countByAccountType(accountType);
    }

    public CustomerEnrollmentWS getCustomerEnrollmentWS(Integer customerEnrollmentId) {
        if (customerEnrollmentId == null) {
            return null;
        }
        CustomerEnrollmentDTO customerEnrollmentDTO = new CustomerEnrollmentDAS().findNow(customerEnrollmentId);
        CustomerEnrollmentWS customerEnrollmentWS = null;
        if(customerEnrollmentDTO != null) {
            customerEnrollmentWS = getWS(customerEnrollmentDTO);
        }
        return customerEnrollmentWS;
    }
    public void validateEnrollment(CustomerEnrollmentDTO customerEnrollmentDTO) throws SessionInternalError{
        List<String> errorMessages = new ArrayList<>();

        for (MetaFieldValue metaFieldValue : customerEnrollmentDTO.getMetaFields()){
            try {
                MetaFieldBL.validateMetaField(customerEnrollmentDTO.getCompany().getLanguageId(), metaFieldValue.getField(), metaFieldValue, customerEnrollmentDTO);
            }
            catch (SessionInternalError e){
                Collections.addAll(errorMessages, e.getErrorMessages());
            }
        }

        if (errorMessages.size() > 0) {
            SessionInternalError sessionInternalError = new SessionInternalError();
            sessionInternalError.setErrorMessages(errorMessages.toArray(new String[errorMessages.size()]));
            throw sessionInternalError;
        }

        LOG.debug("Validating is any non drop customer/enrollment is exist in the system for the account type");
        EventManager.process(new ValidateEnrollmentEvent(customerEnrollmentDTO.getCompany().getId(), customerEnrollmentDTO));

        //If server validation is passed then call third party api for validate address
        EventManager.process(new SureAddressEvent(customerEnrollmentDTO.getCompany().getParent().getId(), customerEnrollmentDTO));

    }


    public MetaFieldValueWS[] convertCustomerEnrollmentMetaFieldsToWS(Integer entityId, AccountTypeDTO accountType, CustomerEnrollmentDTO entity) {

        Set<AccountInformationTypeDTO> infoTypes = accountType.getInformationTypes();

        Set<MetaField> availableMetaFields=new HashSet<MetaField>();

        for(AccountInformationTypeDTO informationTypeDTO:infoTypes){
            availableMetaFields.addAll(informationTypeDTO.getMetaFields());
        }
        //code for getting all ait metafield
        MetaFieldValueWS[] result = new MetaFieldValueWS[]{};
        if (availableMetaFields.size()>0) {
            result = new MetaFieldValueWS[availableMetaFields.size()];
            Integer groupId=null;
            int i = 0;
            for (MetaField field : availableMetaFields) {
                for(MetaFieldGroup metaFieldGroup:field.getMetaFieldGroups()){
                    if(metaFieldGroup.getEntityType()== EntityType.ACCOUNT_TYPE){
                        groupId=metaFieldGroup.getId();
                        break;
                    }
                }
                MetaFieldValue value = entity.getMetaField(field.getName(), groupId);
                if (value == null) {
                    value = field.createValue();
                }
                MetaFieldValueWS metaFieldValueWS = MetaFieldBL.getWS(value, groupId);
                result[i++] = metaFieldValueWS;
            }
        }
        return result;
    }
}
