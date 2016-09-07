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

package com.sapienter.jbilling.server.user.tasks;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.notification.INotificationSessionBean;
import com.sapienter.jbilling.server.notification.MessageDTO;
import com.sapienter.jbilling.server.notification.NotificationBL;
import com.sapienter.jbilling.server.notification.NotificationNotFoundException;
import com.sapienter.jbilling.server.order.event.NewOrderEvent;
import com.sapienter.jbilling.server.pluggableTask.AutoRenewalEvent;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.usagePool.db.CustomerUsagePoolDAS;
import com.sapienter.jbilling.server.usagePool.db.CustomerUsagePoolDTO;
import com.sapienter.jbilling.server.usagePool.db.UsagePoolConsumptionActionDTO;
import com.sapienter.jbilling.server.usagePool.event.UsagePoolConsumptionNotificationEvent;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.contact.db.ContactDTO;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.user.event.NewContactEvent;
import com.sapienter.jbilling.server.util.Context;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Event custom notification task.
 *
 * @author: Panche.Isajeski
 * @since: 12/07/12
 */
public class EventBasedCustomNotificationTask extends PluggableTask implements IInternalEventsTask {

    private static final Logger LOG = Logger.getLogger(EventBasedCustomNotificationTask.class);

    private static final ParameterDescription PARAMETER_NEW_CONTACT_CUSTOM_NOTIFICATION_ID =
            new ParameterDescription("new_contact_notification_id", false, ParameterDescription.Type.INT);
    private static final ParameterDescription PARAMETER_NEW_ORDER_CUSTOM_NOTIFICATION_ID =
            new ParameterDescription("new_order_notification_id", false, ParameterDescription.Type.INT);
    private static final ParameterDescription PARAMETER_AUTO_RENEWAL_CUSTOM_NOTIFICATION_ID =
            new ParameterDescription("auto_renewal_notification_id", false, ParameterDescription.Type.INT);
    private static final ParameterDescription PARAMETER_AUTO_RENEWAL_CONFIRMATION_CUSTOM_NOTIFICATION_ID =
            new ParameterDescription("auto_renewal_confirmation_notification_id", false, ParameterDescription.Type.INT);

    //initializer for pluggable params
    // add as many event - notification parameters
    {
        descriptions.add(PARAMETER_NEW_CONTACT_CUSTOM_NOTIFICATION_ID);
        descriptions.add(PARAMETER_NEW_ORDER_CUSTOM_NOTIFICATION_ID);
        descriptions.add(PARAMETER_AUTO_RENEWAL_CUSTOM_NOTIFICATION_ID);
        descriptions.add(PARAMETER_AUTO_RENEWAL_CONFIRMATION_CUSTOM_NOTIFICATION_ID);
    }

    @SuppressWarnings("unchecked")
    private static final Class<Event>[] events = new Class[]{
        NewContactEvent.class,
        UsagePoolConsumptionNotificationEvent.class,
        NewOrderEvent.class,
        AutoRenewalEvent.class
    };

    @Override
    public void process(Event event) throws PluggableTaskException {
    	
        INotificationSessionBean notificationSession = Context.getBean(Context.Name.NOTIFICATION_SESSION);
        if (event instanceof NewContactEvent) {
            fireNewContactEventNotification((NewContactEvent) event, notificationSession);
        }
    	else if (event instanceof UsagePoolConsumptionNotificationEvent) {
        	fireCustomerUsagePoolConsumptionNotification((UsagePoolConsumptionNotificationEvent) event, notificationSession);
        }
        else if (event instanceof NewOrderEvent) {
            fireNewOrderEventNotification((NewOrderEvent) event, notificationSession);
        }
        else if (event instanceof AutoRenewalEvent) {
            fireAutoRenewalEventNotification((AutoRenewalEvent) event);
        }
    }

    @Override
    public Class<Event>[] getSubscribedEvents() {
        return events;
    }

    private boolean fireNewOrderEventNotification(NewOrderEvent newOrderEvent, INotificationSessionBean notificationSession) {
        if (parameters.get(PARAMETER_NEW_ORDER_CUSTOM_NOTIFICATION_ID.getName()) == null || newOrderEvent.getOrder() == null) {
            return false;
        }

        Integer notificationMessageTypeId = Integer.parseInt((String) parameters
                .get(PARAMETER_NEW_ORDER_CUSTOM_NOTIFICATION_ID.getName()));

        MessageDTO message = null;
        Integer userId = newOrderEvent.getOrder().getUserId();

        try {
            UserBL userBL = new UserBL(userId);
            message = new NotificationBL().getCustomNotificationMessage(
                    notificationMessageTypeId,
                    newOrderEvent.getEntityId(),
                    userId,
                    userBL.getLanguage());

        } catch (NotificationNotFoundException e) {
            LOG.debug(String.format("Custom notification id: %s does not exist for the user id %s ",
                    notificationMessageTypeId, userId));
        }

        if (message == null) {
            return false;
        }

        LOG.debug(String.format("Notifying user: %s for a new contact event", userId));
        notificationSession.notify(userId, message);
        return true;

    }

    private boolean fireNewContactEventNotification(NewContactEvent newContactEvent,
                                                    INotificationSessionBean notificationSession) {
        if (parameters.get(PARAMETER_NEW_CONTACT_CUSTOM_NOTIFICATION_ID.getName()) == null || newContactEvent.getContactDto()  == null) {
            return false;
        }
        Integer notificationMessageTypeId = Integer.parseInt((String) parameters
                .get(PARAMETER_NEW_CONTACT_CUSTOM_NOTIFICATION_ID.getName()));

        MessageDTO message = null;
        Integer userId = newContactEvent.getContactDto().getUserId();

        try {
            UserBL userBL = new UserBL(userId);
            message = new NotificationBL().getCustomNotificationMessage(
                    notificationMessageTypeId,
                    newContactEvent.getEntityId(),
                    userId,
                    userBL.getLanguage());

        } catch (NotificationNotFoundException e) {
            LOG.debug(String.format("Custom notification id: %s does not exist for the user id %s ",
                    notificationMessageTypeId, userId));
        }

        if (message == null) {
            return false;
        }

        LOG.debug(String.format("Notifying user: %s for a new contact event", userId));
        notificationSession.notify(userId, message);
        return true;
    }
    
    private boolean fireCustomerUsagePoolConsumptionNotification(UsagePoolConsumptionNotificationEvent usagePoolConsumptionNotificationEvent,
            									INotificationSessionBean notificationSession) {
        UsagePoolConsumptionActionDTO action = usagePoolConsumptionNotificationEvent.getAction();
        Integer notificationMessageTypeId = action.getNotificationId();

		Integer customerUsagePoolId = usagePoolConsumptionNotificationEvent.getCustomerUsagePoolId();
		CustomerUsagePoolDTO customerUsagePool = new CustomerUsagePoolDAS().find(customerUsagePoolId);
		Integer userId = customerUsagePool.getCustomer().getBaseUser().getUserId();
		
		if (notificationMessageTypeId == null && userId == null) {
			return false;
		}
		
		MessageDTO message = null;
		UserDTO user = null;
		String salutation = "";
		String usagePoolName = "";
		try {
			UserBL userBL = new UserBL(userId);
			user = userBL.getEntity();
			ContactDTO contact = userBL.getEntity().getContact();
	        if (null != contact && null != contact.getFirstName() && null != contact.getLastName()) 
	            salutation = contact.getFirstName() + " " + contact.getLastName();
	        else 
	            salutation = userBL.getEntity().getUserName();
	        
	        usagePoolName = customerUsagePool.getUsagePool().getDescription(user.getLanguage().getId(), "name");
	        
			message = new NotificationBL().getCustomNotificationMessage(
			notificationMessageTypeId,
			usagePoolConsumptionNotificationEvent.getEntityId(),
			userId,
			userBL.getLanguage());
			
		} catch (NotificationNotFoundException e) {
			LOG.debug(String.format("Custom notification id: %s does not exist for the user id %s ",
					notificationMessageTypeId, userId));
		}
		if (message == null) {
			return false;
		}
        
		message.addParameter("userSalutation", salutation);
		message.addParameter("usagePoolName", usagePoolName);
		message.addParameter("percentageConsumption", action.getPercentage());
		
		LOG.debug(String.format("Notifying user: %s for a consumption notification event", userId));
		notificationSession.notify(userId, message);
		return true;
	}

    private boolean fireAutoRenewalEventNotification(AutoRenewalEvent autoRenewalEvent) {
        Map<String, Object> messageParameters = new HashMap<>();
        String notificationIdParameterName;

        if (autoRenewalEvent.isRenewalReached()) {
            notificationIdParameterName = PARAMETER_AUTO_RENEWAL_CONFIRMATION_CUSTOM_NOTIFICATION_ID.getName();
        }
        else {
            notificationIdParameterName = PARAMETER_AUTO_RENEWAL_CUSTOM_NOTIFICATION_ID.getName();
            messageParameters.put("daysBeforeNotification", autoRenewalEvent.getDaysBeforeNotification());
        }

        return this.sendNotification(Integer.valueOf(parameters.get(notificationIdParameterName)), autoRenewalEvent.getEntityId(),
                autoRenewalEvent.getCustomer().getBaseUser().getId(), autoRenewalEvent.getName(), messageParameters);
    }

    private boolean sendNotification(Integer notificationMessageTypeId, Integer entityId, Integer userId, String eventName, Map messageParameters) {
        try {
            UserDTO user = new UserDAS().findNow(userId);
            MessageDTO message = new NotificationBL().getCustomNotificationMessage(notificationMessageTypeId, entityId, userId, user.getLanguageIdField());

            if (messageParameters != null) {
                message.getParameters().putAll(messageParameters);
            }

            LOG.debug(String.format("Notifying user: %s for %s event", userId, eventName));
            ((INotificationSessionBean) Context.getBean(Context.Name.NOTIFICATION_SESSION)).notify(userId, message);
            return true;
        }
        catch (NotificationNotFoundException e) {
            LOG.debug(String.format("Custom notification id: %s does not exist for the user id %s ", notificationMessageTypeId, userId));
            return false;
        }
        catch (SessionInternalError e) {
            LOG.debug(String.format("Error sending custom notification id: %s for the user id %s ", notificationMessageTypeId, userId));
            return false;
        }
    }
}