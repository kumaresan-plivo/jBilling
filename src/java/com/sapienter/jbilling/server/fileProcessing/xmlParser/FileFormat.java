package com.sapienter.jbilling.server.fileProcessing.xmlParser;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.ediTransaction.db.EDITypeDAS;
import com.sapienter.jbilling.server.ediTransaction.db.EDITypeDTO;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aman on 24/8/15.
 */
public class FileFormat {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(FileFormat.class));
    private FileFormat fileFormat;
    private FileStructure fileStructure;
    private EDITypeDTO ediTypeDTO;

    private static Map<Integer, FileFormat> editTypeMap = new ConcurrentHashMap<>();

    //It is a singleton class which allow to create single object for each format.
    //If object is already exist then return the same object.
    //Else call XML parser to parse the xml file and generate the

    public FileStructure getFileStructure() {
        return fileStructure;
    }

    public void setFileStructure(FileStructure fileStructure) {
        this.fileStructure = fileStructure;
    }

    public EDITypeDTO getEdiTypeDTO() {
        return ediTypeDTO;
    }

    public void setEdiTypeDTO(EDITypeDTO ediTypeDTO) {
        this.ediTypeDTO = ediTypeDTO;
    }

    private FileFormat(int ediTypeId) {
        ediTypeDTO = new EDITypeDAS().findNow(ediTypeId);
        File format = new File(FileConstants.getFormatFilePath() + File.separator + ediTypeDTO.getPath() + FileConstants.HYPHEN_SEPARATOR + ediTypeDTO.getEntity().getId() + ".xml");
        try {
            fileStructure = XMLParser.parseXML(format);
        }catch (FileNotFoundException fnfe){
            LOG.error(fnfe.getMessage());
        }catch (Exception e) {
            LOG.error(e);
        }
    }

    public static FileFormat getFileFormat(int ediTypeId){
        FileFormat fileFormat = editTypeMap.get(ediTypeId);
        if(fileFormat == null) {
            fileFormat = new FileFormat(ediTypeId);
            editTypeMap.put(ediTypeId, fileFormat);
        }
        return fileFormat;
    }

    @Override
    public String toString() {
        return "FileFormat{" +
                "fileStructure=" + fileStructure +
                ", ediTypeDTO=" + ediTypeDTO +
                '}';
    }
}
