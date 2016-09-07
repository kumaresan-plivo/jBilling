package com.sapienter.jbilling.server.mediation.converter.common;

import java.io.Serializable;

/**
 * Created by marcolin on 08/10/15.
 */
public interface MediationJob extends Serializable {
    public String getLineConverter();
    public String getRecycleJob();
    public String getResolver();
    public String getWriter();
    public String getJob();
    public boolean handleRootRateTables();
    public boolean needsInputDirectory();
}
