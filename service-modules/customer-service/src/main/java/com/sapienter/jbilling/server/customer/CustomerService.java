package com.sapienter.jbilling.server.customer;


import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.user.UserWS;

/**
 * Created by marcolin on 09/10/15.
 */
public interface CustomerService {

    public Integer createUser(UserWS user) throws SessionInternalError;
    public User resolveUserByUsername(Integer entityId, String username);
}
