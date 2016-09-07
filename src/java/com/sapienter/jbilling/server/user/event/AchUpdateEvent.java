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

package com.sapienter.jbilling.server.user.event;

import com.sapienter.jbilling.server.payment.db.PaymentInformationDTO;
import com.sapienter.jbilling.server.system.event.Event;

/**
 *
 * @author vikasb
 */
public class AchUpdateEvent implements Event {

    private final PaymentInformationDTO ach;
    private final Integer entityId;

    public PaymentInformationDTO getAch() {
        return ach;
    }

    public AchUpdateEvent(PaymentInformationDTO ach, Integer entityId) {
        this.ach = ach;
        this.entityId = entityId;
    }

    public String getName() {
        return "Update ACH event";
    }

    public final Integer getEntityId() {
        return entityId;
    }
}
