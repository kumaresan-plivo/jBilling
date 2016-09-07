package com.sapienter.jbilling.server.customer.mocks;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.customer.CustomerService;
import com.sapienter.jbilling.server.customer.User;
import com.sapienter.jbilling.server.user.UserWS;

/**
 * Created by marcolin on 09/10/15.
 */
public class CustomerMockService implements CustomerService {
    @Override
    public Integer createUser(UserWS user) throws SessionInternalError {
        return null;
    }

    @Override
    public User resolveUserByUsername(Integer entityId, String username) {
        if (username.indexOf("success") >= 0) {
            User user = new User();
            user.setId(1);
            user.setCurrencyId(1);
            user.setDeleted(false);
            return user;
        } else {
            return null;
        }
    }
}
