/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

 This file is part of jbilling.

 jbilling is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jbilling is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sapienter.jbilling.client.authentication;

import com.sapienter.jbilling.client.authentication.model.EncryptedLicense;
import com.sapienter.jbilling.client.authentication.model.User;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.user.IUserSessionBean;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.user.permisson.db.PermissionDTO;
import com.sapienter.jbilling.server.user.permisson.db.RoleDTO;
import com.sapienter.jbilling.server.util.Constants;

import grails.plugin.springsecurity.SpringSecurityService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * AuthenticationUserService
 *
 * @author Brian Cowdery
 * @since 20/07/11
 */
@Transactional
public class AuthenticationUserService implements UserService {
	

    private jbilling.UserService userService;
    private SpringSecurityService springSecurityService;

    public AuthenticationUserService() {
    }

    public void setUserService (jbilling.UserService userService) {
        this.userService = userService;
    }

    public void setSpringSecurityService(SpringSecurityService springSecurityService) {
        this.springSecurityService = springSecurityService;
    }

    public User getUser(String username, Integer entityId) {
        return userService.getUser(username, entityId, (CompanyUserDetails) springSecurityService.getPrincipal());
    }

    public boolean isLockoutEnforced(UserDTO user) {
        return userService.isLockoutEnforced(user);
    }

    public Collection<GrantedAuthority> getAuthorities(String username, Integer entityId) {
        return userService.getAuthorities(username, entityId);
    }

    public EncryptedLicense getLicense(Integer entityId) {
        String licenseKey = Util.getSysProp("license_key");
        String licenseeName = Util.getSysProp("licensee");

        return new EncryptedLicense(licenseKey, licenseeName);
    }
    
    /**
     * method to save new encrypted password and encryption scheme for a user
     */
    public void saveUser(String userName, Integer entityId, String newPasswordEncoded, Integer newScheme){
        userService.saveUser(userName, entityId, newPasswordEncoded, newScheme);
    }
    
    /**
     * check if user's encryption scheme and jbilling.properties encryption scheme is same or different
     * @param entityId
     * @param userName
     * @return
     */
    public Boolean isEncryptionSchemeSame(Integer entityId, String userName, Integer schemeId){
    	UserBL bl = new UserBL(userName, entityId);
    	return bl.isEncryptionSchemeSame(schemeId);
    }
    
    /**
     * get encryption scheme of the user
     * @param userName
     * @param entityId
     * @return
     */
    public Integer getEncryptionSchemeOfUser(Integer entityId, String userName){
    	UserBL bl = new UserBL(userName, entityId);
    	return bl.getEncryptionSchemeOfUser(userName, entityId);
    }
}
