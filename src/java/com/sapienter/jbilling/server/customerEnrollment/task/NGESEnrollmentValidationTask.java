package com.sapienter.jbilling.server.customerEnrollment.task;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentStatus;
import com.sapienter.jbilling.server.customerEnrollment.db.CustomerEnrollmentDAS;
import com.sapienter.jbilling.server.customerEnrollment.db.CustomerEnrollmentDTO;
import com.sapienter.jbilling.server.customerEnrollment.event.ValidateEnrollmentEvent;
import com.sapienter.jbilling.server.ediTransaction.IEDITransactionBean;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.user.db.*;
import com.sapienter.jbilling.server.util.Context;
import org.apache.log4j.Logger;

/**
 * Created by neeraj on 23/02/15.
 */
public class NGESEnrollmentValidationTask extends PluggableTask implements IInternalEventsTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(NGESEnrollmentValidationTask.class));

    private static final Class<Event> events[] = new Class[]{
            ValidateEnrollmentEvent.class
    };

    //initializer for pluggable params
    {
    }


    @Override
    public Class<Event>[] getSubscribedEvents() {
        return events;
    }

    IEDITransactionBean ediTransactionBean = Context.getBean(Context.Name.EDI_TRANSACTION_SESSION);
    @Override
    public void process(Event event) throws PluggableTaskException {

        if (!(event instanceof ValidateEnrollmentEvent)) {
            return;
        }
        CustomerEnrollmentDTO enrollmentDTO=((ValidateEnrollmentEvent) event).getEnrollmentDTO();
        ediTransactionBean.isCustomerExistForAccountNumber(enrollmentDTO);

    }


}
