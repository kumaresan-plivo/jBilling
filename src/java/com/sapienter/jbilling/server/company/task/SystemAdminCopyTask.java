package com.sapienter.jbilling.server.company.task;

import com.sapienter.jbilling.client.authentication.JBillingPasswordEncoder;
import com.sapienter.jbilling.client.util.Constants;
import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.company.event.NewAdminEvent;
import com.sapienter.jbilling.server.system.event.EventManager;
import com.sapienter.jbilling.server.user.*;
import com.sapienter.jbilling.server.user.db.*;
import com.sapienter.jbilling.server.user.permisson.db.RoleDAS;
import com.sapienter.jbilling.server.user.permisson.db.RoleDTO;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.IWebServicesSessionBean;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vivek on 10/8/15.
 */
public class SystemAdminCopyTask extends AbstractCopyTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(ConfigurationCopyTask.class));

    UserDAS userDAS = null;
    CompanyDAS companyDAS = null;

    private static final Class dependencies[] = new Class[]{};

    public Class[] getDependencies() {
        return dependencies;
    }

    public Boolean isTaskCopied(Integer entityId, Integer targetEntityId) {
        return false;
    }

    public SystemAdminCopyTask() {
        init();
    }

    private void init() {
        userDAS = new UserDAS();
        companyDAS = new CompanyDAS();
    }

    public void create(Integer entityId, Integer targetEntityId) {
        initialise(entityId, targetEntityId);  // This will create all the entities on which the current entity is dependent.
        LOG.debug("System admin creation has been started.");
        String email = Util.getSysProp(CommonConstants.SUPPORT_LEAD_EMAIL);
        Pattern pattern = Pattern.compile(CommonConstants.EMAIL_VALIDATION_REGEX);
        Matcher matcher = pattern.matcher(email);
        if(StringUtils.trimToNull(email) == null) {
            throw new SessionInternalError("Duplicate Description ",
                    new String[] { "system.admin.copy.email.null" });
        } else if(!matcher.matches()) {
            throw new SessionInternalError("Duplicate Description ",
                    new String[] { "system.admin.copy.email.validation" });
        }

        Map<String, String> credentialMap = new HashMap<String, String>();
        String username = Util.getSysProp(CommonConstants.SUPPORT_LEAD_USERNAME);
        CompanyDTO targetEntity = companyDAS.find(targetEntityId);
        String randPassword = createUser(targetEntity, username, email);
        credentialMap.put(username, randPassword);
        Map<String, String> supportAdmin = Util.getMatchingProp(CommonConstants.SUPPORT_USERS_NAMES_REGEX);
        for (Map.Entry<String, String> entry : supportAdmin.entrySet()) {
            String randPass = createUser(targetEntity, entry.getValue(), null);
            credentialMap.put(entry.getValue(), randPass);
        }

        NewAdminEvent adminEvent = new NewAdminEvent(entityId,targetEntityId, credentialMap, email, username);
        EventManager.process(adminEvent);
        LOG.debug("All System Admin has been created.");
    }

    private String createUser(CompanyDTO targetEntity, String username, String email) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(username);

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

        RoleDTO roleDTO = new RoleDAS().findByRoleTypeIdAndCompanyId(Constants.TYPE_SYSTEM_ADMIN, targetEntity.getId());
        Set<RoleDTO> roleDTOs = new HashSet<RoleDTO>();
        roleDTOs.add(roleDTO);
        userDTO.setRoles(roleDTOs);

        userDTO = userDAS.save(userDTO);

        if (StringUtils.trimToNull(email) != null) {
            createUserContact(email, targetEntity.getDescription(), userDTO.getId());
        }

        return randPassword;
    }

    private void createUserContact(String email, String organizationName, Integer userId) {
        ContactWS contactWS = new ContactWS();
        contactWS.setEmail(email);
        contactWS.setInclude(true);
        ContactDTOEx dtoEx = new ContactDTOEx(contactWS);
        contactWS.setOrganizationName(organizationName);
        IWebServicesSessionBean webServicesSessionSpringBean = Context.getBean("webServicesSession");
        new ContactBL().createForUser(dtoEx, userId, webServicesSessionSpringBean.getCallerId());
    }

}
