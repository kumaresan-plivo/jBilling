package com.sapienter.jbilling.server.invoiceTemplate.report;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.common.SystemProperties;
import com.sapienter.jbilling.server.invoice.*;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO;
import com.sapienter.jbilling.server.invoiceTemplate.domain.DocDesign;
import com.sapienter.jbilling.server.invoiceTemplate.domain.SqlField;
import com.sapienter.jbilling.server.invoiceTemplate.domain.SubReportDataSource;
import com.sapienter.jbilling.server.invoiceTemplate.ui.JsonFactory;
import com.sapienter.jbilling.server.item.AssetBL;
import com.sapienter.jbilling.server.item.AssetWS;
import com.sapienter.jbilling.server.item.CurrencyBL;
import com.sapienter.jbilling.server.item.ItemTypeBL;
import com.sapienter.jbilling.server.item.db.AssetDAS;
import com.sapienter.jbilling.server.item.db.AssetDTO;
import com.sapienter.jbilling.server.item.db.ItemTypeDTO;
import com.sapienter.jbilling.server.mediation.IMediationSessionBean;
import com.sapienter.jbilling.server.mediation.JbillingMediationRecord;
import com.sapienter.jbilling.server.mediation.MediationService;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskParameterDAS;
import com.sapienter.jbilling.server.user.db.*;
import com.sapienter.jbilling.server.process.db.BillingProcessConfigurationDAS;
import com.sapienter.jbilling.server.process.db.BillingProcessConfigurationDTO;
import com.sapienter.jbilling.server.process.db.PeriodUnitDTO;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.metafields.MetaFieldBL;
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.notification.MessageDTO;
import com.sapienter.jbilling.server.notification.NotificationBL;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderProcessDTO;
import com.sapienter.jbilling.server.process.db.BillingProcessDTO;
import com.sapienter.jbilling.server.user.ContactBL;
import com.sapienter.jbilling.server.user.ContactDTOEx;
import com.sapienter.jbilling.server.user.EntityBL;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.util.*;
import com.sapienter.jbilling.server.util.search.BasicFilter;
import com.sapienter.jbilling.server.util.search.SearchCriteria;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;

import org.apache.log4j.Logger;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.sapienter.jbilling.server.invoiceTemplate.report.ReportBuildVisitor.*;
import static com.sapienter.jbilling.server.util.search.Filter.FilterConstraint.EQ;

/**
 * @author elmot
 */
public class InvoiceTemplateBL {

    private final JasperDesign design;
    private final Map<String, JasperDesign> subReportsDesigns;
    private final Map<String, String> subDataSources;
    private final Map<String, JRDesignDataset> subDataSets;
    private final Map<String, SubReportDataSource> subReportDataSources;
    private JasperReport rootReport;
    private Map<String, JasperReport> subreports;
    private JasperPrint jasperPrint;

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(InvoiceTemplateBL.class));

    private static final String ITG_DYNAMIC_PARAMS = "itg_dynamic_parameters";

    public InvoiceTemplateBL(JasperDesign jasperDesign, Map<String, JasperDesign> subreports,
                             Map<String, String> subDataSources, Map<String, JRDesignDataset> subDataSets,
                             Map<String, SubReportDataSource> subReportDataSources) {
        this.design = jasperDesign;
        this.subReportsDesigns = subreports;
        this.subDataSources = subDataSources;
        this.subDataSets = subDataSets;
        this.subReportDataSources = subReportDataSources;
    }

    public Map<String, String> getSubDataSources() {
        return subDataSources;
    }

    public Map<String, JRDesignDataset> getSubDataSets() {
        return subDataSets;
    }

    public Map<String, SubReportDataSource> getSubReportDataSources() {
        return subReportDataSources;
    }

    public JasperPrint getJasperPrint() {
        return jasperPrint;
    }

    public static InvoiceTemplateBL buildDesign(String json, Map<Class<?>, Object> resources, Map<String, Class<?>> parameters, Map<String, String> dynamicParameters) throws JRException {
        DocDesign docDesign = JsonFactory.getGson().fromJson(json, DocDesign.class);
        return buildDesign(docDesign, resources, parameters, dynamicParameters);
    }

    public static InvoiceTemplateBL buildDesign(Reader json, Map<Class<?>, Object> resources, Map<String, Class<?>> parameters, Map<String, String> dynamicParameters) throws JRException {
        DocDesign docDesign = JsonFactory.getGson().fromJson(json, DocDesign.class);
        return buildDesign(docDesign, resources, parameters, dynamicParameters);
    }

    public static InvoiceTemplateBL buildDesign(DocDesign docDesign, Map<Class<?>, Object> resources, Map<String, Class<?>> parameters, Map<String, String> dynamicParameters) throws JRException {
        ReportBuildVisitor visitor = new ReportBuildVisitor(resources, parameters, dynamicParameters);
        docDesign.visit(visitor);
        return visitor.createInvoiceTemplateBL();
    }

    public void exportToPng(int pageNum, OutputStream outputStream) throws JRException, IOException {
        exportToPng(jasperPrint, pageNum, outputStream);
    }

    private static void exportToPng(JasperPrint jasperPrint, int pageNum, OutputStream outputStream) throws JRException, IOException {
        int pageWidth = jasperPrint.getPageWidth();
        int pageHeight = jasperPrint.getPageHeight();
        BufferedImage bufferedImage = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        JRGraphics2DExporter exporter = new JRGraphics2DExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRGraphics2DExporterParameter.PAGE_INDEX, pageNum);
        exporter.setParameter(JRGraphics2DExporterParameter.GRAPHICS_2D, graphics);
        exporter.exportReport();
        ImageIO.write(bufferedImage, "PNG", outputStream);
    }

    public void compile() throws JRException {
        rootReport = JasperCompileManager.compileReport(design);

        subreports = new HashMap<String, JasperReport>();
        for (Map.Entry<String, JasperDesign> entry : subReportsDesigns.entrySet()) {

            JasperDesign subReport = entry.getValue();
//            subReport.setMainDataset(design.getMainDesignDataset());
            subreports.put(entry.getKey(), JasperCompileManager.compileReport(subReport));
        }
    }

    public void debugDesignPrint() throws JRException {
        JasperCompileManager.writeReportToXmlStream(design, System.out);
        for (JasperDesign jasperDesign : subReportsDesigns.values()) {
            JasperCompileManager.writeReportToXmlStream(jasperDesign, System.out);
        }
    }

    public void jrxmlExport(OutputStream outputStream) throws IOException, JRException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        zipOutputStream.putNextEntry(new ZipEntry(design.getName() + ".jrxml"));
        JasperCompileManager.writeReportToXmlStream(design, zipOutputStream);
        zipOutputStream.finish();
        for (JasperDesign subreport : subReportsDesigns.values()) {
            zipOutputStream.putNextEntry(new ZipEntry(subreport.getName() + ".jrxml"));
            JasperCompileManager.writeReportToXmlStream(subreport, System.out);
        }
        zipOutputStream.close();
    }

    public void fill(JRDataSource invoiceDataSource, JRDataSource cdrDataSource, Map<String, JRDataSource> xDataSources, Map<String, Object> parameters, Connection connection) throws JRException {
        Map<String, Object> reportParametrs = new HashMap<String, Object>(parameters);
        reportParametrs.putAll(subreports);
        reportParametrs.put(CDR_LINES_DATASET, cdrDataSource);
        reportParametrs.put(INVOICE_LINES_DATASET, invoiceDataSource);
        reportParametrs.put(JBILLING_CONNECTION, connection);
        // use 'single row dataset' to ensure, that there will be at least one row in detail section to display sub-report
        for (int i = 1; i <= subreports.size(); i++) {
            reportParametrs.put(SINGLE_ROW_DATASET + "_" + (i + 1), new JRBeanArrayDataSource(new Object[]{new Object()}));
        }
        for (Map.Entry<String, JRDataSource> xDataSourceEntry : xDataSources.entrySet()) {
            reportParametrs.put(xDataSourceEntry.getKey(), xDataSourceEntry.getValue());
        }
        jasperPrint = JasperFillManager.fillReport(rootReport, reportParametrs, new JREmptyDataSource());
    }

    private static void exportToPdf(JasperPrint jasperPrint, OutputStream fos) throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
        exporter.exportReport();
    }

    public void exportToPdf(OutputStream outputStream) throws JRException {
        exportToPdf(jasperPrint, outputStream);
    }

    public int getPageNumber() {
        return jasperPrint == null ? -1 : jasperPrint.getPages().size();
    }

    public void debugPrintPrint(OutputStream out) throws JRException {
        JRXmlExporter jrXmlExporter = new JRXmlExporter();
        jrXmlExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        jrXmlExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
        jrXmlExporter.exportReport();
    }

    public static void generateErrorReport(String title, String message, boolean image, OutputStream outputStream) {
        InputStream resourceAsStream = InvoiceTemplateBL.class.getResourceAsStream("errorReport.jrxml");

        try {
            JasperReport errorReport = JasperCompileManager.compileReport(resourceAsStream);
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("ERROR_TITLE", title);
            params.put("ERROR_MSG", message);
            JasperPrint errorPrint = JasperFillManager.fillReport(errorReport, params, new JREmptyDataSource());
            if (image) {
                exportToPng(errorPrint, 0, outputStream);
            } else {
                exportToPdf(errorPrint, outputStream);
            }
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }

    public static InvoiceTemplateBL createInvoiceTemplateBL(InvoiceTemplateDTO selected, InvoiceDTO invoice) throws JRException, SQLException {
        ArrayList<InvoiceLineEnvelope> invoiceLines = new ArrayList<InvoiceLineEnvelope>();
        Map<String, Object> invoiceParameters = invoice != null ? fillData(invoice) : new HashMap<String, Object>();
        Locale locale = (Locale) invoiceParameters.get(JRParameter.REPORT_LOCALE);
        Map<Class<?>, Object> resources = new HashMap<Class<?>, Object>();
        if (selected != null) {
            java.util.List<InvoiceTemplateFileDTO> files = (java.util.List<InvoiceTemplateFileDTO>) InvokerHelper.invokeMethod(InvoiceTemplateFileDTO.class, "findAllByTemplate", selected);

            Map<String, InvoiceTemplateFileDTO> filesMap = new HashMap<String, InvoiceTemplateFileDTO>();
            for (InvoiceTemplateFileDTO file : files) {
                filesMap.put(file.getName(), file);
            }
            resources.put(InvoiceTemplateFileDTO.class, filesMap);
        }
        Map<String, Object> sqlFieldParameters = processSqlFields(selected.getTemplateJson(), invoiceParameters);
        Map<String, Class<?>> xParameters = new HashMap<String, Class<?>>();
        for (Map.Entry<String, Object> entry : invoiceParameters.entrySet()) {
            if (entry.getKey().startsWith("__")) {
                xParameters.put(entry.getKey(), entry.getValue().getClass());
            }
        }
        for (Map.Entry<String, Object> entry : sqlFieldParameters.entrySet()) {
            xParameters.put(entry.getKey(), entry.getValue().getClass());
        }
        invoiceParameters.putAll(sqlFieldParameters);

        Map<String, String> dynamicParameters = null;
        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if(requestAttributes != null) {
            dynamicParameters = (HashMap<String, String>)requestAttributes.getAttribute(ITG_DYNAMIC_PARAMS, RequestAttributes.SCOPE_SESSION);
        }
        InvoiceTemplateBL invoiceTemplate = InvoiceTemplateBL.buildDesign(selected.getTemplateJson(), resources, xParameters, dynamicParameters != null ? dynamicParameters : new HashMap<String, String>());
        BigDecimal sub_total = BigDecimal.ZERO;
        final Set assetSet = new HashSet();
        final String defaultAssetIdLabel = "Identifier"; //g.message(code: 'asset.detail.identifier').toString();

        if (invoice != null) {
            final ItemTypeBL itemTypeBL = new ItemTypeBL();
            final AssetDAS assetDAS = new AssetDAS();
            for (InvoiceLineDTO invoiceLineDTO : invoice.getInvoiceLines()) {
                if (invoiceLineDTO.getInvoiceLineType() != null && invoiceLineDTO.getInvoiceLineType().getId() != Constants.INVOICE_LINE_TYPE_TAX) {
                    Set<AssetEnvelope> invoiceLineAssets = new TreeSet<AssetEnvelope>(new Comparator<AssetEnvelope>() {
                        @Override
                        public int compare(AssetEnvelope o1, AssetEnvelope o2) {
                            return o1.getIdentifier().compareTo(o2.getIdentifier());
                        }
                    });

                    for (OrderProcessDTO op : invoice.getOrderProcesses()) {
                        for (OrderLineDTO l : op.getPurchaseOrder().getLines()) {
                            if (l.getAssets().size() > 0) {
                                ItemTypeDTO itemType = l.getItem().findItemTypeWithAssetManagement();

                                for (AssetDTO asset : l.getAssets()) {
                                    invoiceLineAssets.add(new AssetEnvelope(AssetBL.getWS(asset), itemType));
                                }
                            }
                        }
                    }

                    invoiceLines.add(new InvoiceLineEnvelope(invoiceLineDTO, invoiceLineAssets, defaultAssetIdLabel));
                    assetSet.addAll(invoiceLineAssets);
                    sub_total = sub_total.add(invoiceLineDTO.getAmount());
                }
            }
        }
        invoiceParameters.put("sub_total", ConvertUtils.formatMoney(sub_total, locale));
        final JRBeanCollectionDataSource beanDataSource = new JRBeanCollectionDataSource(invoiceLines, false);
        List<CdrEnvelope> cdrs = collectCdrs(invoice, locale, defaultAssetIdLabel);
        final JRBeanCollectionDataSource eventsDataSource = new JRBeanCollectionDataSource(cdrs, false);

        final AssetsCollector assetsCollector = new AssetsCollector(assetSet, defaultAssetIdLabel);
        final Map<String, JRDataSource> xDataSources = new HashMap<String, JRDataSource>();

        for (Map.Entry<String, String> entry : invoiceTemplate.getSubDataSources().entrySet()) {
            String name = entry.getKey();
            String origin = entry.getValue();
            if (origin.equals(INVOICE_LINES_DATASET)) {
                xDataSources.put(name, new JRBeanCollectionDataSource(beanDataSource.getData(), false));

            }
            else if (origin.equals(CDR_LINES_DATASET)) {
                xDataSources.put(name, new JRBeanCollectionDataSource(eventsDataSource.getData(), false));

            }
            else if (origin.equals(ASSETS_DATASET)) {
                final JRDesignDataset dataSet = invoiceTemplate.getSubDataSets().get(name);
                for (String f : assetsCollector.getFields()) {
                    for (final JRField field : dataSet.getFields()) {
                        if (field.getName().equals(f)) {
                            return invoiceTemplate; // <-- nasty way to exclude duplicates
                        }
                    }
                    JRDesignField field = new JRDesignField();
                    field.setName(f);
                    field.setValueClass(String.class);
                    field.setDescription(f);
                    try {
                        dataSet.addField(field);
                    } catch (JRException e) {
                        throw new RuntimeException(e);
                    }
                }

                Collection<String> fieldNames = new ArrayList<String>();
                for (JRField f : dataSet.getFields()) {
                    fieldNames.add(f.getName());
                }
                assetsCollector.ensureFields(fieldNames);

                xDataSources.put(name, new JRMapCollectionDataSource((List)assetsCollector.getData()));
            }
        }

        ComboPooledDataSource dataSource = Context.getBean("dataSource");
        for (Map.Entry<String, SubReportDataSource> entry : invoiceTemplate.getSubReportDataSources().entrySet()) {
            xDataSources.put(entry.getKey(), entry.getValue().toJRDataSource());
        }

        invoiceTemplate.compile();

        Connection connection = dataSource.getConnection();

        try {
            invoiceTemplate.fill(beanDataSource, eventsDataSource, xDataSources, invoiceParameters, connection);
        } finally {
            connection.close();
        }

        return invoiceTemplate;
    }

    /**
     * Copy of com.sapienter.jbilling.server.notification.NotificationBL.generatePaperInvoiceNew
     */
    private static Map<String, Object> fillData(InvoiceDTO invoice) {
        NotificationBL notification = new NotificationBL();
        InvoiceBL invoiceBl = new InvoiceBL(invoice);
        Integer entityId = invoiceBl.getEntity().getBaseUser().
                getEntity().getId();
        // the language doesn't matter when getting a paper invoice
        MessageDTO message = notification.getInvoicePaperMessage(
                entityId, null, invoiceBl.getEntity().getBaseUser().
                        getLanguageIdField(), invoiceBl.getEntity());

        String message1 = message.getContent()[0].getContent();
        String message2 = message.getContent()[1].getContent();
        ContactBL contact = new ContactBL();
        contact.setInvoice(invoice.getId());
        ContactDTOEx to = contact.getEntity() != null ? contact.getDTO() : null;
        if (to == null) to = new ContactDTOEx();
        if (to.getUserId() == null) {
            to.setUserId(invoice.getBaseUser().getUserId());
        }
        UserDTO user = invoiceBl.getEntity().getBaseUser();
        entityId = user.getEntity().getId();
        contact.setEntity(entityId);
        ContactDTOEx from = contact.getDTO();
        if (from.getUserId() == null) {
            from.setUserId(new EntityBL().getRootUser(entityId));
        }

        Locale locale = (new UserBL(invoice.getUserId())).getLocale();
        Map<String, Object> parameters = new HashMap<String, Object>();

        IWebServicesSessionBean webServicesSession = Context.getBean("webServicesSession");

        fillMetaFields("receiver", webServicesSession.getUserWS(to.getUserId()).getMetaFields(), parameters);
        fillMetaFields("owner", webServicesSession.getUserWS(from.getUserId()).getMetaFields(), parameters);

        Collection<MetaFieldValueWS> metaFieldValueWSes = new LinkedList<MetaFieldValueWS>();
        for (MetaFieldValue mfv : invoice.getMetaFields()) {
            metaFieldValueWSes.add(MetaFieldBL.getWS(mfv));
        }
        fillMetaFields("invoice", metaFieldValueWSes, parameters);

        // invoice data
        parameters.put(InvoiceParameters.INVOICE_ID.getName(), invoice.getId());
        parameters.put(InvoiceParameters.INVOICE_NUMBER.getName(), invoice.getPublicNumber());
        parameters.put(InvoiceParameters.INVOICE_USER_ID.getName(), invoice.getUserId());
        parameters.put(InvoiceParameters.INVOICE_CREATE_DATETIME.getName(), Util.formatDate(invoice.getCreateDatetime(), invoice.getUserId()));
        parameters.put(InvoiceParameters.INVOICE_DUE_DATE.getName(), Util.formatDate(invoice.getDueDate(), invoice.getUserId()));

        if (invoice.getDueDate().after(new Date())) {
            parameters.put(InvoiceParameters.PAYMENT_DUE_IN.getName(), new Integer(Math.abs(Days.daysBetween(new LocalDate(invoice.getDueDate().getTime()), new LocalDate(new Date().getTime())).getDays())).toString());
        } else {
            parameters.put(InvoiceParameters.PAYMENT_DUE_IN.getName(), new String("0"));
        }

        BillingProcessDTO bp = invoice.getBillingProcess();
        if (bp == null) {
            bp = invoice.getInvoice() == null ? null : invoice.getBillingProcess();
        }
        if (bp == null) {
            parameters.put(InvoiceParameters.BILLING_PERIOD_START_DATE.getName(), Util.formatDate(invoice.getCreateDatetime(), invoice.getUserId()));
            parameters.put(InvoiceParameters.BILLING_DATE.getName(), Util.formatDate(invoice.getCreateDatetime(), invoice.getUserId()));
            parameters.put(InvoiceParameters.BILLING_PERIOD_END_DATE.getName(),
                    Util.formatDate(getBillingPeriodDate(invoice.getCreateDatetime(), invoice.getBaseUser().getCustomer().getMainSubscription()),
                            invoice.getUserId()));
        } else {
            BillingProcessConfigurationDTO bpConf = new BillingProcessConfigurationDAS().findByEntity(bp.getEntity());
            Date cusNextInvDateMinPeriod = getBillingPeriodDate(invoice.getBaseUser().getCustomer().getNextInvoiceDate(), invoice.getBaseUser().getCustomer().getMainSubscription(),true);
            if(bpConf.getInvoiceDateProcess() == 1){
                parameters.put(InvoiceParameters.BILLING_DATE.getName(), Util.formatDate( cusNextInvDateMinPeriod, invoice.getUserId()));
            }else{
                parameters.put(InvoiceParameters.BILLING_DATE.getName(), Util.formatDate(bp.getBillingDate(), invoice.getUserId()));
            }
            parameters.put(InvoiceParameters.BILLING_PERIOD_START_DATE.getName(), Util.formatDate(cusNextInvDateMinPeriod, invoice.getUserId()));
            parameters.put(InvoiceParameters.BILLING_PERIOD_END_DATE.getName(),
                    Util.formatDate(getBillingPeriodEndDate(cusNextInvDateMinPeriod, bp.getPeriodUnit()), invoice.getUserId()));
        }

        // owner and receiver data
        parameters.put(InvoiceParameters.OWNER_COMPANY.getName(), printable(from.getOrganizationName()));
        parameters.put(InvoiceParameters.OWNER_STREET_ADDRESS.getName(), getAddress(from));
        parameters.put(InvoiceParameters.OWNER_ZIP.getName(), printable(from.getPostalCode()));
        parameters.put(InvoiceParameters.OWNER_CITY.getName(), printable(from.getCity()));
        parameters.put(InvoiceParameters.OWNER_STATE.getName(), printable(from.getStateProvince()));
        parameters.put(InvoiceParameters.OWNER_COUNTRY.getName(), printable(from.getCountryCode()));
        parameters.put(InvoiceParameters.OWNER_PHONE.getName(), getPhoneNumber(from));
        parameters.put(InvoiceParameters.OWNER_EMAIL.getName(), printable(from.getEmail()));

        parameters.put(InvoiceParameters.RECEIVER_COMPANY.getName(), printable(to.getOrganizationName()));
        parameters.put(InvoiceParameters.RECEIVER_NAME.getName(), printable(to.getFirstName(), to.getLastName()));
        parameters.put(InvoiceParameters.RECEIVER_STREET_ADDRESS.getName(), getAddress(to));
        parameters.put(InvoiceParameters.RECEIVER_ZIP.getName(), printable(to.getPostalCode()));
        parameters.put(InvoiceParameters.RECEIVER_CITY.getName(), printable(to.getCity()));
        parameters.put(InvoiceParameters.RECEIVER_STATE.getName(), printable(to.getStateProvince()));
        parameters.put(InvoiceParameters.RECEIVER_COUNTRY.getName(), printable(to.getCountryCode()));
        parameters.put(InvoiceParameters.RECEIVER_PHONE.getName(), getPhoneNumber(to));
        parameters.put(InvoiceParameters.RECEIVER_EMAIL.getName(), printable(to.getEmail()));
        parameters.put(InvoiceParameters.RECEIVER_ID.getName(), printable(String.valueOf(to.getId())));

        // symbol of the currency
        CurrencyBL currency = new CurrencyBL(invoice.getCurrency().getId());
        String symbol = currency.getEntity().getSymbol();
        if (symbol.length() >= 4 && symbol.charAt(0) == '&' &&
                symbol.charAt(1) == '#') {
            // this is an html symbol
            // remove the first two digits
            symbol = symbol.substring(2);
            // remove the last digit (;)
            symbol = symbol.substring(0, symbol.length() - 1);
            // convert to a single char
            Character ch = new Character((char)
                    Integer.valueOf(symbol).intValue());
            symbol = ch.toString();
        }
        parameters.put(InvoiceParameters.CURRENCY_SYMBOL.getName(), symbol);

        // text coming from the notification parameters
        parameters.put(InvoiceParameters.MESSAGE_1.getName(), message1);
        parameters.put(InvoiceParameters.MESSAGE_2.getName(), message2);
        parameters.put(InvoiceParameters.CUSTOMER_NOTES.getName(), "HST: 884725441");
        //todo: change this static value

        // invoice notes stripped of html line breaks
        String notes = invoice.getCustomerNotes();
        if (notes != null) {
            notes = notes.replaceAll("<br/>", "\r\n");
        }
        parameters.put(InvoiceParameters.INVOICE_NOTES.getName(), notes);

        // tax calculated
        BigDecimal taxTotal = new BigDecimal(0);
        String tax_price = "";
        String tax_amount = "";
        String product_code;
        java.util.List<InvoiceLineDTO> lines = new ArrayList<InvoiceLineDTO>(invoice.getInvoiceLines());
        // Temp change: sort is leading to NPE
        //Collections.sort(lines, new InvoiceLineComparator());
        for (InvoiceLineDTO line : lines) {
            // process the tax, if this line is one
            if (line.getInvoiceLineType() != null && // for headers/footers
                    line.getInvoiceLineType().getId() ==
                            Constants.INVOICE_LINE_TYPE_TAX) {
                // update the total tax variable
                taxTotal = taxTotal.add(line.getAmount());
                product_code = line.getItem() != null ? line.getItem().getInternalNumber() : line.getDescription();
                tax_price += product_code + " " + line.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " %\n";
                tax_amount += line.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\n";
            }
        }
        tax_price = (tax_price.equals("")) ? "0.00 %" : tax_price.substring(0, tax_price.lastIndexOf("\n"));
        tax_amount = (tax_amount.equals("")) ? "0.00" : tax_amount.substring(0, tax_amount.lastIndexOf("\n"));
        parameters.put(InvoiceParameters.SALES_TAX.getName(), taxTotal);
        parameters.put(InvoiceParameters.TAX_PRICE.getName(), tax_price);
        parameters.put(InvoiceParameters.TAX_AMOUNT.getName(), tax_amount);

        // this parameter help in filter out tax items from invoice lines
        parameters.put(InvoiceParameters.INVOICE_LINE_TAX_ID.getName(), Constants.INVOICE_LINE_TYPE_TAX);
        parameters.put(InvoiceParameters.TOTAL.getName(), ConvertUtils.formatMoney(invoice.getTotal(), locale));
        //payment term calculated
        parameters.put(InvoiceParameters.PAYMENT_TERMS.getName(), new Long((invoice.getDueDate().getTime() - invoice.getCreateDatetime().getTime()) / (24 * 60 * 60 * 1000)).toString());

        // set report locale
        parameters.put(JRParameter.REPORT_LOCALE, locale);
        parameters.put(InvoiceParameters.FORMAT_UTIL.getName(), new FormatUtil(locale, symbol));

        parameters.putAll(createAitDynamicParameters(invoice, entityId));

        Boolean useHbase = false;
        try {
            String property = SystemProperties.getSystemProperties().get(CommonConstants.ITG_USE_HBASE);
            useHbase = Boolean.valueOf(property);
        } catch (Exception ex) {
            LOG.debug("Exception occurred while fetching the property value from jbilling.properties : " + ex);
        }

        if (useHbase) {
            populateMediationRecordLines(invoice);
        }

        return parameters;
    }

    public static Map<String, Object> createAitDynamicParameters(InvoiceDTO invoice, Integer entityId) {
        // Set up dynamic AIT fields as parameters to send out to the ITG.
        Map<String, Object> dynamicParameters = new HashMap<String, Object>();
        for (CustomerAccountInfoTypeMetaField customerAitMetaField : invoice.getBaseUser().getCustomer().getCustomerAccountInfoTypeMetaFields()) {
            String parameterName = customerAitMetaField.getMetaFieldValue().getField().getName().toLowerCase().replaceAll("[/!@#\\\\$%&\\\\*()\\s]", "_");
            String parameterValue = customerAitMetaField.getMetaFieldValue().getValue().toString();
            dynamicParameters.put(parameterName, parameterValue);
        }

        if (PreferenceBL.getPreferenceValueAsBoolean(entityId, Constants.PREFERENCE_ITG_INVOICE_NOTIFICATION)) {
            ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
            if(requestAttributes != null) {
                requestAttributes.setAttribute(ITG_DYNAMIC_PARAMS, dynamicParameters, RequestAttributes.SCOPE_SESSION);
            }
        }

        return dynamicParameters;
    }

    public static void populateMediationRecordLines(InvoiceDTO invoice) {
        //TODO MODULARIZATION: WHAT IS HAPPENING HERE???
//
//        //Clear the MediationRecordLines for the invoice
//        MediationRecordLineDAS mediationRecordLineDAS = new MediationRecordLineDAS();
//
//        List<MediationRecordLineDTO> mediationRecordLineDTOs = mediationRecordLineDAS.findByInvoice(invoice);
//
//        for(MediationRecordLineDTO mediationRecordLineDTO : mediationRecordLineDTOs){
//            mediationRecordLineDAS.delete(mediationRecordLineDTO);
//        }
//
//        // Retrieve MediationRecordLines for the invoice and store them in the DB.
//        List<MediationRecordLineDTO> mediationRecordLines = retrieveMediationRecordLinesForInvoice(invoice);
//        for (MediationRecordLineDTO mediationRecordLine : mediationRecordLines) {
//            mediationRecordLineDAS.save(new CommonMediationRecordLineDTO(mediationRecordLine));
//        }
    }

    private static List<JbillingMediationRecord> retrieveMediationRecordLinesForInvoice(InvoiceDTO invoiceDTO) {
        MediationService mediationService = Context.getBean("mediationService");
        List<JbillingMediationRecord> eventsForInvoice = new ArrayList<>();
        if (invoiceDTO != null && invoiceDTO.getOrderProcesses() != null) {
            invoiceDTO.getOrderProcesses().stream().filter(orderProcessDTO -> orderProcessDTO.getPurchaseOrder() != null)
                    .map(orderProcessDTO -> orderProcessDTO.getPurchaseOrder().getId())
                    .forEach(orderId -> eventsForInvoice.addAll(mediationService.getMediationRecordsForOrder(orderId)));
        }
        return eventsForInvoice;
    }

    private static List<CdrEnvelope> collectCdrs(InvoiceDTO invoice, Locale locale, String defaultAssetIdLabel) {
        List<CdrEnvelope> cdrs = new ArrayList<>();
        Boolean useHbase = false;
        try {
            String property = SystemProperties.getSystemProperties().get(CommonConstants.ITG_USE_HBASE);
            useHbase = Boolean.valueOf(property);
        } catch (Exception ex) {
            LOG.debug("Exception occurred while fetching the property value from jbilling.properties : " + ex);
        }
        //TODO MODULARIZATION: WE SHOULD IMPLEMENT THIS
//        if (useHbase) {
//            if (invoice == null) return null;
//            Set<Integer> foundOrdersId = new TreeSet<Integer>();
//            java.util.List<OrderDTO> ordersToScan = new ArrayList<OrderDTO>();
//            for (OrderProcessDTO orderProcessDTO : invoice.getOrderProcesses()) {
//                ordersToScan.add(orderProcessDTO.getPurchaseOrder());
//            }
//            //Scan order hierarchy
//            for (int i = 0; i < ordersToScan.size(); i++) {
//                OrderDTO orderDTO = ordersToScan.get(i);
//                int orderId = orderDTO.getId();
//                if (!foundOrdersId.contains(orderId)) {
//                    foundOrdersId.add(orderId);
//                }
//                if (orderDTO.getParentOrder() != null && !foundOrdersId.contains(orderDTO.getParentOrder().getId())) {
//                    ordersToScan.add(orderDTO.getParentOrder());
//                }
//                for (OrderDTO childOrder : orderDTO.getChildOrders()) {
//                    if (!foundOrdersId.contains(childOrder.getId())) {
//                        ordersToScan.add(childOrder);
//                    }
//                }
//            }
//            final HBaseCallDataRecordDAS das = new HBaseCallDataRecordDAS();
//            Map<MediationRecordLineDTO, Record> mediationRecords = new LinkedHashMap<MediationRecordLineDTO, Record>();
//            for (OrderDTO order : ordersToScan) {
//                final List<MediationRecordLineDTO> mediationRecordLines = ((IMediationSessionBean) Context.getBean("mediationSession")).getMediationRecordLinesForOrder(order.getId());
//                for (final MediationRecordLineDTO mrl : mediationRecordLines) {
//                    final java.util.List<Record> cdr = das.get(mrl.getCdrHBaseKey());
//                    mediationRecords.put(mrl, cdr.isEmpty() ? null : cdr.get(0));
//                }
//            }
//
//            cdrs = ConvertUtils.collectCdrs(mediationRecords, locale, defaultAssetIdLabel);
//        }

        return cdrs;
    }

    private static void fillMetaFields(String prefix, MetaFieldValueWS[] metaFields, Map<String, Object> parameters) {
        fillMetaFields(prefix, metaFields != null ? Arrays.asList(metaFields) : null, parameters);
    }

    private static void fillMetaFields(String prefix, Collection<MetaFieldValueWS> metaFields, Map<String, Object> parameters) {
        if (metaFields != null) {
            for (MetaFieldValueWS mfv : metaFields) {
                String name = mfv.getFieldName().replace('.', '_').replace(' ', '_');
                String value = mfv.getValue() == null ? "" : String.valueOf(mfv.getValue());
                parameters.put("__" + prefix + "__" + name, value);
            }
        }
    }

    public static String printable(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    public static String printable(String str, String str2) {
        StringBuilder builder = new StringBuilder();

        if (str != null) builder.append(str).append(' ');
        if (str2 != null) builder.append(str2);

        return builder.toString();
    }

    public static String getPhoneNumber(ContactDTOEx contact){
        if(contact.getPhoneCountryCode()!=null && contact.getPhoneAreaCode()!=null && (contact.getPhoneNumber()!=null && !contact.getPhoneNumber().trim().equals("")))
            return  contact.getPhoneCountryCode()+"-"+contact.getPhoneAreaCode()+"-"+contact.getPhoneNumber();
        else
            return "";
    }

    public static String getAddress(ContactDTOEx contact){
        return printable(contact.getAddress1())+((contact.getAddress2()!=null && !contact.getAddress2().trim().equals(""))?("\n"+contact.getAddress2()):(""));
    }

    public static Date getBillingPeriodDate(Date startDate, MainSubscriptionDTO mainSubscription, boolean subtract){
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        if(mainSubscription.getSubscriptionPeriod().getPeriodUnit().getId() == PeriodUnitDTO.SEMI_MONTHLY){
            cal.add(GregorianCalendar.DAY_OF_MONTH, mainSubscription.getSubscriptionPeriod().getValue() *
                    (subtract ? -15 : 15));
        } else {
            cal.add(MapPeriodToCalendar.map(mainSubscription.getSubscriptionPeriod().getPeriodUnit().getId()),
                    mainSubscription.getSubscriptionPeriod().getValue() * (subtract ? -1 : 1));
        }

        return cal.getTime();
    }

    public static Date getBillingPeriodDate(Date startDate, MainSubscriptionDTO mainSubscription){
        return getBillingPeriodDate(startDate, mainSubscription, false);
    }

    public static Date getBillingPeriodEndDate(Date startDate, PeriodUnitDTO unitDTO){
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(MapPeriodToCalendar.map(unitDTO.getId()), 1);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    // parse the query to find the parameters and execute the query
    public static Map<String, Object> processSqlFields(String templateJson, Map<String, Object> parameters) {
        DocDesign docDesign = JsonFactory.getGson().fromJson(templateJson, DocDesign.class);
        List<SqlField> sqlFields = docDesign.getSqlFields();
        Map<String, Object> sqlFieldParameters = new HashMap<String, Object>();
        for (SqlField sqlField : sqlFields) {
            LOG.debug("Processing the SQL field  : "+sqlField.getName());
            try{
                Object output = sqlField.setAsParameter(parameters);
                if(output!=null) sqlFieldParameters.put(sqlField.getName(),output);
            } catch (Exception exception){
                LOG.error(exception);
                throw new RuntimeException(exception);
            }
        }
        return sqlFieldParameters;
    }

    public static InvoiceTemplateDTO createNewTemplateWithVersion(InvoiceTemplateDTO newTemplate,
                                                                  String name,
                                                                  String json,
                                                                  Integer entityId,
                                                                  Integer userId){
        InvoiceTemplateDAS das = new InvoiceTemplateDAS();
        InvoiceTemplateVersionDAS versionDAS = new InvoiceTemplateVersionDAS();

        newTemplate.setName(name);
        newTemplate.setEntity(new CompanyDTO(entityId));

        newTemplate = das.save(newTemplate);
        das.flush();

        InvoiceTemplateVersionDTO versionDTO = new InvoiceTemplateVersionDTO(newTemplate.getId()+".1",
                null, new Date(), json);
        versionDTO.setUserId(userId);
        versionDTO = versionDAS.save(versionDTO);
        versionDAS.flush();

        versionDTO.setInvoiceTemplate(newTemplate);
        newTemplate.getInvoiceTemplateVersions().add(versionDTO);
        versionDAS.save(versionDTO);
        versionDAS.flush();

        return das.save(newTemplate);
    }

    public static InvoiceTemplateVersionDTO getTemplateVersionForInvoice(InvoiceTemplateDTO dto){
        LinkedList<InvoiceTemplateVersionDTO> versionDTOs = InvoiceTemplateVersionBL.sortDTOByVersionNumber(dto.getInvoiceTemplateVersions());
        return InvoiceTemplateVersionBL.getVersionForInvoice(versionDTOs);
    }

    public static void setTemplateVersionForInvoice(InvoiceTemplateDTO invoiceTemplate){

        if(invoiceTemplate == null){
            throw new SessionInternalError("Could not resolve InvoiceTemplate",
                    new String[]{"download.invoice.template.not.set"});
        }

        InvoiceTemplateVersionDTO versionDTO = InvoiceTemplateBL.getTemplateVersionForInvoice(invoiceTemplate);
        if(versionDTO != null){
            invoiceTemplate.setTemplateJson(versionDTO.getId());
        }
    }

    /**
     * if template is not configured anywhere then 0 is returned
     * else 1 for AccountType, 2 for Customer and 3 for PaperInvoiceNotificationTask
     * */
    public static Integer getTemplateConfigurationPlace(InvoiceTemplateDTO templateDTO){
        return (new AccountTypeDAS().countAllByInvoiceTemplate(templateDTO.getId()) > 0) ? 1
                : ((new CustomerDAS().countAllByInvoiceTemplate(templateDTO.getId()) > 0) ? 2
                : (new PluggableTaskParameterDAS().isInvoiceTemplateConfigured(templateDTO.getId()) ? 3: 0));

    }

    public static Boolean validateTemplateForDelete(InvoiceTemplateDTO templateDTO){
        Integer configurationPlace;
        if((configurationPlace = getTemplateConfigurationPlace(templateDTO)) != 0){
            throw new SessionInternalError("Invoice Template is already being used.",
                    new String[]{"invoice.template.in.use.for."+configurationPlace+".message,"+templateDTO.getId()});
        }
        return Boolean.TRUE;
    }
}
