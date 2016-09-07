package com.sapienter.jbilling.server.customer;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.IWebServicesSessionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * Created by marcolin on 09/10/15.
 */
public class CustomerJdbcTemplateService implements CustomerService {

    private JdbcTemplate jdbcTemplate = null;
    private IWebServicesSessionBean webServicesSessionBean;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setWebServicesSessionBean(IWebServicesSessionBean webServicesSessionBean) {
        this.webServicesSessionBean = webServicesSessionBean;
    }

    @Override
    public Integer createUser(UserWS user) throws SessionInternalError {
        return webServicesSessionBean.createUser(user);
    }

    @Override
    public User resolveUserByUsername(Integer entityId, String username) {
        String sql = "select b.id, b.currency_id, b.deleted from base_user b, entity e " +
                "where b.entity_id = e.id and b.user_name = ? " +
                "and ( e.id = ? or e.parent_id = ? )";
        SqlRowSet rs = getJdbcTemplate().queryForRowSet(sql, username, entityId, entityId);
        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setCurrencyId(rs.getInt("currency_id"));
            if (rs.getInt("deleted") > 0) {
                user.setDeleted(rs.getBoolean("deleted"));
            }
            return user;
        }
        return null;
    }
}
