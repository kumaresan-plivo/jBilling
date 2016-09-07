package com.sapienter.jbilling.server.company.task;

import au.com.bytecode.opencsv.CSVWriter;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.pricing.RateCardBL;
import com.sapienter.jbilling.server.pricing.RateCardWS;
import com.sapienter.jbilling.server.pricing.db.RateCardDAS;
import com.sapienter.jbilling.server.pricing.db.RateCardDTO;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.util.sql.JDBCUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.log4j.Logger;
import org.hibernate.ScrollableResults;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by vivek on 28/11/14.
 */
public class RateCardCopyTask extends AbstractCopyTask {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(RateCardCopyTask.class));

    RateCardDAS rateCardDAS = null;
    CompanyDAS companyDAS = null;

    private static final Class dependencies[] = new Class[]{};

    public Class[] getDependencies() {
        return dependencies;
    }

    public Boolean isTaskCopied(Integer entityId, Integer targetEntityId) {
        List<RateCardDTO> rateCardDTOs = new RateCardDAS().getRateCardsByEntity(entityId);
        return rateCardDTOs != null && !rateCardDTOs.isEmpty();
    }

    public RateCardCopyTask() {
        init();
    }

    private void init() {
        rateCardDAS = new RateCardDAS();
        companyDAS = new CompanyDAS();
    }

    public void create(Integer entityId, Integer targetEntityId) {
        initialise(entityId, targetEntityId);  // This will create all the entities on which the current entity is dependent.
        LOG.debug("create RateCardCopyTask");
        List<RateCardDTO> rateCardDTOs = rateCardDAS.getRateCardsByEntity(entityId);
        List<RateCardDTO> copyRateCardDTOs = rateCardDAS.getRateCardsByEntity(targetEntityId);
        if (copyRateCardDTOs.isEmpty()) {
            for (RateCardDTO rateCardDTO : rateCardDTOs) {
                String randomUUID = rateCardDTO.getTableName() + "_" + UUID.randomUUID().toString().replace("-", "");
                randomUUID = JDBCUtils.getDatabaseObjectName(randomUUID);
                RateCardWS rateCardWS = RateCardBL.getWS(rateCardDTO);
                rateCardWS.setId(null);
                rateCardWS.setTableName(rateCardDTO.getTableName());
                rateCardWS.getChildCompanies().remove(targetEntityId);
                rateCardWS.getChildCompanies().add(entityId);
                RateCardDTO copyRateCardDTO = RateCardBL.getDTO(rateCardWS);
                File tempFile = new File(randomUUID + ".csv");
                try {
                    tempFile = createCSVFileFromTable(copyRateCardDTO, randomUUID);
                } catch (IOException ioex) {
                    LOG.error("Caught IO Exception Here " + ioex);
                }
                rateCardWS.setTableName(randomUUID);
                createRateCard(rateCardWS, tempFile, targetEntityId);
            }
        }
        LOG.debug("RateCardCopyTask has been completed.");
    }

    /*
    * Rate Card
    */
    public Integer createRateCard(RateCardWS rateCardWs, File rateCardFile, Integer targetEntityId) {
        RateCardDTO rateCardDTO = RateCardBL.getDTO(rateCardWs);
        rateCardDTO.setCompany(new CompanyDAS().find(targetEntityId));
        rateCardDTO.setGlobal(rateCardWs.isGlobal());
        return new RateCardBL().create(rateCardDTO, rateCardFile, true);
    }

    private File createCSVFileFromTable(RateCardDTO rateCard, String randomTableName) throws IOException {
        RateCardBL rateCardService = new RateCardBL(rateCard);

        // outfile
        File file = File.createTempFile(randomTableName, ".csv");
        CSVWriter writer = new CSVWriter(new FileWriter(file), ',');

        // write csv header
        List<String> columns = rateCardService.getRateTableColumnNames();

        Boolean isPriceTermExist = Boolean.FALSE;
        int priceTermIndex = -1;
        if (columns.contains("price_term")) {

            isPriceTermExist = Boolean.TRUE;

            priceTermIndex = columns.indexOf("price_term");
            columns.remove("price_term");
        }
        writer.writeNext(columns.toArray(new String[columns.size()]));

        // read rows and write file
        ScrollableResults resultSet = null;

        try {
            resultSet = rateCardService.getRateTableRows();
            while (resultSet.next()) {
                writer.writeNext(convertToString(resultSet.get(), isPriceTermExist, priceTermIndex));
            }
        } finally {
            try { resultSet.close(); } catch (Throwable t) { LOG.error(t); }
            writer.close();
        }

        return file;
    }

    public String[] convertToString(Object[] objects, Boolean isPriceTermExist, int priceTermIndex) {
        String[] strings = new String[!isPriceTermExist ? objects.length : (objects.length - 1)];

        int i = 0;
        for (Object object : objects) {
            if (!isPriceTermExist) {
                if (object != null) {
                    Converter converter = ConvertUtils.lookup(object.getClass());
                    if (converter != null) {
                        strings[i++] = converter.convert(object.getClass(), object).toString();
                    } else {
                        strings[i++] = object.toString();
                    }
                } else {
                    strings[i++] = "";
                }
            } else if (priceTermIndex != -1 && priceTermIndex != i) {
                if (object != null) {
                    Converter converter = ConvertUtils.lookup(object.getClass());
                    if (converter != null) {
                        strings[i++] = converter.convert(object.getClass(), object).toString();
                    } else {
                        strings[i++] = object.toString();
                    }
                } else {
                    strings[i++] = "";
                }
            }
        }
        return strings;
    }
}