package com.sapienter.jbilling.server.mediation.converter.db;

import com.sapienter.jbilling.server.mediation.ConversionResult;
import com.sapienter.jbilling.server.mediation.JbillingMediationErrorRecord;
import com.sapienter.jbilling.server.mediation.JbillingMediationRecord;

/**
 * Created by marcolin on 09/10/15.
 */
public class DaoConverter {

    public static JbillingMediationErrorRecord getMediationErrorRecord(JbillingMediationErrorRecordDao dao) {
        return new JbillingMediationErrorRecord(
                dao.getjBillingCompanyId(), dao.getMediationCfgId(),
                dao.getRecordKey(), dao.getErrorCodes(), dao.getPricingFields(), dao.getProcessId(),
                dao.getStatus());
    }

    public static JbillingMediationErrorRecordDao  getMediationErrorRecordDao(ConversionResult result) {
        JbillingMediationErrorRecord error = result.getErrorRecord();
        return new JbillingMediationErrorRecordDao(
                error.getjBillingCompanyId(), error.getMediationCfgId(),
                error.getRecordKey(), error.getErrorCodes(), error.getPricingFields(), error.getProcessId(),
                error.getStatus());
    }

    public static JbillingMediationRecord getMediationRecord(JbillingMediationRecordDao dao) {
        return new JbillingMediationRecord(
                JbillingMediationRecord.STATUS.valueOf(dao.getStatus().name()),
                JbillingMediationRecord.TYPE.valueOf(dao.getType().name()),
                dao.getjBillingCompanyId(), dao.getMediationCfgId(), dao.getRecordKey(),
                dao.getUserId(), dao.getEventDate(), dao.getQuantity(), dao.getDescription(),
                dao.getCurrencyId(), dao.getItemId(), dao.getOrderId(), dao.getOrderLineId(),
                dao.getPricingFields(), dao.getRatedPrice(), dao.getRatedCostPrice(), dao.getProcessId());
    }

    public static JbillingMediationRecordDao getMediationRecordDao(ConversionResult result) {
        return getMediationRecordDao(result.getRecordCreated());
    }

    public static JbillingMediationRecordDao getMediationRecordDao(JbillingMediationRecord created) {
        return new JbillingMediationRecordDao(
                JbillingMediationRecordDao.STATUS.valueOf(created.getStatus().name()),
                JbillingMediationRecordDao.TYPE.valueOf(created.getType().name()),
                created.getjBillingCompanyId(), created.getMediationCfgId(), created.getRecordKey(),
                created.getUserId(), created.getEventDate(), created.getQuantity(), created.getDescription(),
                created.getCurrencyId(), created.getItemId(), created.getOrderId(), created.getOrderLineId(),
                created.getPricingFields(), created.getRatedPrice(), created.getRatedCostPrice(),  created.getProcessId());
    }
}
