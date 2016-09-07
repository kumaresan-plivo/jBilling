package com.sapienter.jbilling.server.company.task;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.report.db.ReportDAS;
import com.sapienter.jbilling.server.report.db.ReportDTO;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.util.Context;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

/**
 * @author Javier Rivero
 * @since 19/01/16.
 */
public class ReportCopyTask extends AbstractCopyTask{
    ReportDAS reportDAS = null;
    CompanyDAS companyDAS = null;
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(ReportCopyTask.class));
    private static final Class dependencies[] = new Class[]{};

    private void init() {
        reportDAS = new ReportDAS();
        companyDAS = new CompanyDAS();
    }

    public ReportCopyTask(){
        init();
    }

    @Override
    public void create(Integer entityId, Integer targetEntityId) {
        initialise(entityId, targetEntityId);  // This will create all the entities on which the current entity is dependent.
        CompanyDTO targetCompany = companyDAS.find(targetEntityId);
        CompanyDTO company = companyDAS.find(entityId);
        List<ReportDTO> reports = reportDAS.findAllReportsByCompany(company);
        List<ReportDTO> copyReports = reportDAS.findAllReportsByCompany(targetCompany);
        JdbcTemplate jdbcTemplate = Context.getBean(Context.Name.JDBC_TEMPLATE);

        if (copyReports.isEmpty()) {
            for (ReportDTO report : reports) {
                copyReports.add(report);
                String query = "INSERT INTO entity_report_map (report_id, entity_id) VALUES (" + report.getId() +
                        ", " + targetCompany.getId() + ")";
                jdbcTemplate.execute(query);
            }
        }

        LOG.debug("PluginCopyTask has been completed");

    }

    @Override
    public Boolean isTaskCopied(Integer entityId, Integer targetEntityId) {
        CompanyDTO targetCompany = companyDAS.find(targetEntityId);
        List<ReportDTO> reportDTOs = reportDAS.findAllReportsByCompany(targetCompany);
        return reportDTOs != null && !reportDTOs.isEmpty();
    }

    @Override
    public Class[] getDependencies() {
        return dependencies;
    }
}
