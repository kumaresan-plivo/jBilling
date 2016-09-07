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

package com.sapienter.jbilling.server.user;

import java.sql.SQLException;
import java.util.*;

import javax.naming.NamingException;
import javax.sql.rowset.CachedRowSet;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.list.ResultList;
import com.sapienter.jbilling.server.metafields.EntityType;
import com.sapienter.jbilling.server.metafields.MetaFieldBL;
import com.sapienter.jbilling.server.metafields.MetaFieldHelper;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.user.contact.db.ContactDAS;
import com.sapienter.jbilling.server.user.contact.db.ContactDTO;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.user.permisson.db.RoleDAS;
import com.sapienter.jbilling.server.user.permisson.db.RoleDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.audit.EventLogger;
import com.sapienter.jbilling.server.util.db.CurrencyDAS;
import com.sapienter.jbilling.server.util.db.LanguageDAS;
import com.sapienter.jbilling.server.util.db.LanguageDTO;

/**
 * @author Emil
 */
public class EntityBL extends ResultList 
        implements EntitySQL {
    private CompanyDAS das = null;
    private CompanyDTO entity = null;
    private EventLogger eLogger = null;
    
    public EntityBL()  {
        init();
    }
    
    public EntityBL(Integer id)  {
        init();
        entity = das.find(id);
    }

    /*
    public EntityBL(String externalId) 
            throws FinderException, NamingException {
        init();
        entity = entityHome.findByExternalId(externalId);
    }
    */
    
    public static final CompanyWS getCompanyWS(CompanyDTO companyDto) {
    	
    	CompanyWS ws = new CompanyWS();
        ws.setId(companyDto.getId());
        ws.setCurrencyId(companyDto.getCurrencyId());
        ws.setLanguageId(companyDto.getLanguageId());
        ws.setDescription(companyDto.getDescription());
        ws.setCustomerInformationDesign(companyDto.getCustomerInformationDesign());
        ws.setUiColor(companyDto.getUiColor());

        ws.setMetaFields(MetaFieldBL.convertMetaFieldsToWS(ws.getId(), companyDto));

        ContactDTO contact = new EntityBL(new Integer(ws.getId())).getContact();

        if (contact != null) {
            ws.setContact(new ContactWS(contact.getId(),
                                         contact.getAddress1(),
                                         contact.getAddress2(),
                                         contact.getCity(),
                                         contact.getStateProvince(),
                                         contact.getPostalCode(),
                                         contact.getCountryCode(),
                                         contact.getDeleted()));
        }
        return ws;
    }

    /**
     * This method converts a List of CompanyDTO to an Array of CompanyWS.
     *
     * @param companiesDto List to convert
     */
    public static CompanyWS[] getCompaniesWS(List<CompanyDTO> companiesDto) {
        CompanyWS[] companies = new CompanyWS[companiesDto.size()];

        for (int i = 0; i < companiesDto.size(); i++) {
            companies[i] = EntityBL.getCompanyWS(companiesDto.get(i));
        }

        return companies;
    }


    
    public static final  CompanyDTO getDTO(CompanyWS ws){
        CompanyDTO dto = new CompanyDAS().find(new Integer(ws.getId()));
        dto.setCurrency(new CurrencyDAS().find(ws.getCurrencyId()));
        dto.setLanguage(new LanguageDAS().find(ws.getLanguageId()));
        dto.setDescription(ws.getDescription());
        dto.setCustomerInformationDesign(ws.getCustomerInformationDesign());
        dto.setUiColor(ws.getUiColor());

        if (ws.getMetaFields() != null) {
            if (null != dto) {
                Set<MetaField> metaFields = new HashSet<MetaField>(MetaFieldBL.getAvailableFieldsList(ws.getId(), new EntityType[]{EntityType.COMPANY}));
                MetaFieldHelper.fillMetaFieldsFromWS(metaFields, dto, ws.getMetaFields());
            }
        }
        return dto;
    }
    
    
    private void init() {
        das = new CompanyDAS();
        eLogger = EventLogger.getInstance();
    }
    
    public CompanyDTO getEntity() {
        return entity;
    }
    
    public Locale getLocale()  {
        Locale retValue = null;
        // get the language first
        Integer languageId = entity.getLanguageId();
        LanguageDTO language = new LanguageDAS().find(languageId);
        String languageCode = language.getCode();
        
        // now the country
        ContactBL contact = new ContactBL();
        contact.setEntity(entity.getId());
        String countryCode = contact.getEntity().getCountryCode();
        
        if (countryCode != null) {
            retValue = new Locale(languageCode, countryCode);
        } else {
            retValue = new Locale(languageCode);
        }

        return retValue;
    }

    public ContactDTO getContact() {
        //get company contact
        ContactBL contact = new ContactBL();
        contact.setEntity(entity.getId());
        return contact.getEntity();
    }
    
    public Integer[] getAllIDs() 
            throws SQLException, NamingException {
        List list = new ArrayList();
        
        prepareStatement(EntitySQL.listAll);
        execute();
        conn.close();
        
        while (cachedResults.next()) {
            list.add(new Integer(cachedResults.getInt(1)));
        } 
        
        Integer[] retValue = new Integer[list.size()];
        list.toArray(retValue);
        return retValue;
    }
    
    public CachedRowSet getTables() 
            throws SQLException, NamingException {
        prepareStatement(EntitySQL.getTables);
        execute();
        conn.close();
        
        return cachedResults;
    }
    
    public Integer getRootUser(Integer entityId) {
        try {
        	RoleDTO rootRole = new RoleDAS().findByRoleTypeIdAndCompanyId(Constants.TYPE_ROOT, entityId);
            prepareStatement(EntitySQL.findRoot);
            cachedResults.setInt(1, entityId);
            cachedResults.setInt(2, rootRole.getId());

            execute();
            conn.close();
            
            cachedResults.next();
            return cachedResults.getInt(1);
        } catch (Exception e) {
            throw new SessionInternalError("Root user not found for entity " +
                    entityId, EntityBL.class, e);
        } 
    }
    
    public void updateEntityAndContact(CompanyWS companyWS, Integer entityId, Integer userId) {

        CompanyDTO existingCompany=new CompanyDAS().findEntityByName(companyWS.getDescription());
        if(existingCompany!=null && existingCompany.getId()!=companyWS.getId()){
            throw new SessionInternalError("Company name should be unique", new String[]{"Company name should be unique"});
        }

        CompanyDTO dto= EntityBL.getDTO(companyWS);
            ContactWS contactWs= companyWS.getContact();
            ContactBL contactBl= new ContactBL();
            contactBl.setEntity(entityId);
            ContactDTO contact= contactBl.getEntity();
            contact.setAddress1(contactWs.getAddress1());
            contact.setAddress2(contactWs.getAddress2());
            contact.setCity(contactWs.getCity());
            contact.setCountryCode(contactWs.getCountryCode());
            contact.setPostalCode(contactWs.getPostalCode());
            contact.setStateProvince(contactWs.getStateProvince());
            contact.setCountryCode(contactWs.getCountryCode());
            new ContactDAS().save(contact);
            eLogger.auditBySystem(entityId,
                    userId, Constants.TABLE_CONTACT,
                    contact.getId(),
                    EventLogger.MODULE_WEBSERVICES,
                    EventLogger.ROW_UPDATED, null, null, null);
        new CompanyDAS().save(dto);
        eLogger.auditBySystem(entityId,
                userId, Constants.TABLE_ENTITY,
                entityId,
                EventLogger.MODULE_WEBSERVICES,
                EventLogger.ROW_UPDATED, null, null, null);
    }

    public List<CompanyWS> getChildEntities(Integer parentId) {
        List<CompanyDTO> companyDTOs = das.findChildEntities(parentId);
        List<CompanyWS> childEntities = new ArrayList<CompanyWS>();
        for(CompanyDTO entity : companyDTOs) {
            childEntities.add(getCompanyWS(entity));
        }
        return childEntities;
    }
}
