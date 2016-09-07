package com.sapienter.jbilling.server.process.task;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import com.sapienter.jbilling.server.user.db.CustomerAccountInfoTypeMetaField;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapienter.jbilling.client.suretax.SuretaxClient;
import com.sapienter.jbilling.client.suretax.request.LineItem;
import com.sapienter.jbilling.client.suretax.request.SuretaxRequest;
import com.sapienter.jbilling.client.suretax.response.Group;
import com.sapienter.jbilling.client.suretax.response.ItemMessage;
import com.sapienter.jbilling.client.suretax.response.SuretaxResponse;
import com.sapienter.jbilling.client.suretax.response.TaxItem;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.invoice.NewInvoiceContext;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO;
import com.sapienter.jbilling.server.invoice.db.InvoiceLineTypeDTO;
import com.sapienter.jbilling.server.invoice.db.SuretaxTransactionLogDAS;
import com.sapienter.jbilling.server.invoice.db.SuretaxTransactionLogDTO;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.PlanDAS;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.item.db.PlanItemDTO;
import com.sapienter.jbilling.server.metafields.MetaFieldHelper;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.pluggableTask.InvoiceCompositionTask;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription.Type;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.process.PeriodOfTime;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.Constants;

/**
 * This plug-in gets the tax lines from Suretax Tax engine for invoice lines in
 * an invoice. Plug-in parameters: client_number: Required. Suretax issued
 * client number. validation_key: Required. Suretax issued validation key.
 * response_group: Optional. Determines how taxes are grouped for the response.
 * Values: 00 - Taxes grouped by State (Default) 01 - Taxes grouped by State +
 * Invoice Number 02 - Tax grouped by State + Customer Number 03 - Tax grouped
 * by State + Customer Number + Invoice Number response_type: Optional. Values
 * could be 'D' or 'S'. Defaults to 'D'. D - Detailed. Tax values are returned
 * by tax type for all levels of tax (Federal, State, and Local). S - Summary.
 * Tax values are returned summarized by Federal, State and Local Taxes.
 * number_of_decimals: Optional. Number of decimals in the tax lines. Defaults
 * to 2 rollback_invoice_on_error: Optional. Whether to rollback the invoice
 * creation if an error occurs during getting of tax line from Suretax.
 */
public class SureTaxCompositionTask extends PluggableTask implements
		InvoiceCompositionTask {
	private static final FormatLogger LOG = new FormatLogger(
			Logger.getLogger(SureTaxCompositionTask.class));
	public static final String SURETAX_REQUEST_URL = "Suretax Request Url";
	public static final String CLIENT_NUMBER = "Client Number";
	public static final String VALIDATION_KEY = "Validation Key";
	public static final String DATA_YEAR = "Data Year";
	public static final String DATA_MONTH = "Data Month";
	public static final String RESPONSE_GROUP = "Response Group";
	public static final String RESPONSE_TYPE = "Response Type";
	public static final String NUMBER_OF_DECIMAL = "Number of Decimals";
	public static final String ROLLBACK_INVOICE_ON_ERROR = "Rollback Invoice on Error";
	public static final String SURETAX_TRANS_ID_META_FIELD_NAME = "Suretax Response Trans Id";
	public static final String SECONDARY_ZIP_CODE_EXTN_FIELDNAME = "Secondary Zip Code Extension Field Name";
	public static final String SECONDARY_ZIP_CODE_FIELDNAME = "Secondary Zip Code Field Name";
	public static final String BILLING_ZIP_CODE_FIELDNAME = "Billing Zip Code Field Name";
	public static final String BILLING_ZIP_CODE_Extension_FIELDNAME = "Billing Zip Code Extension Field Name";
	public static final String REGULATORY_CODE_FIELDNAME = "Regulatory Code Field Name";
	public static final String SALES_TYPE_CODE_FIELDNAME = "Sales Type Code Field Name";
	public static final String TAX_EXEMPTION_CODE_FIELDNAME = "Tax Exemption Code Field Name";
	public static final String TRANSACTION_TYPE_CODE_FIELDNAME = "Transaction Type Code Field Name";
	private NewInvoiceContext invoice = null;
	SuretaxTransactionLogDAS suretaxTransactionLogDAS = null;
	ObjectMapper mapper = null;

	public SureTaxCompositionTask() {
		descriptions.add(new ParameterDescription(SURETAX_REQUEST_URL, true, Type.STR));
		descriptions.add(new ParameterDescription(CLIENT_NUMBER, true, Type.STR));
		descriptions.add(new ParameterDescription(VALIDATION_KEY, true, Type.STR));
		descriptions.add(new ParameterDescription(DATA_YEAR, false, Type.INT));
		descriptions.add(new ParameterDescription(DATA_MONTH, false, Type.INT));
		descriptions.add(new ParameterDescription(RESPONSE_GROUP, false, Type.STR));
		descriptions.add(new ParameterDescription(RESPONSE_TYPE, false, Type.STR));
		descriptions.add(new ParameterDescription(NUMBER_OF_DECIMAL, false, Type.STR));
		descriptions.add(new ParameterDescription(ROLLBACK_INVOICE_ON_ERROR, false, Type.BOOLEAN));
		descriptions.add(new ParameterDescription(SECONDARY_ZIP_CODE_EXTN_FIELDNAME, false, Type.STR));
		descriptions.add(new ParameterDescription(SECONDARY_ZIP_CODE_FIELDNAME, false, Type.STR));
		descriptions.add(new ParameterDescription(BILLING_ZIP_CODE_FIELDNAME, false, Type.STR));
		descriptions.add(new ParameterDescription(BILLING_ZIP_CODE_Extension_FIELDNAME, false, Type.STR));
		descriptions.add(new ParameterDescription(REGULATORY_CODE_FIELDNAME, false, Type.STR));
		descriptions.add(new ParameterDescription(SALES_TYPE_CODE_FIELDNAME, false, Type.STR));
		descriptions.add(new ParameterDescription(TAX_EXEMPTION_CODE_FIELDNAME, false, Type.STR));
		descriptions.add(new ParameterDescription(TRANSACTION_TYPE_CODE_FIELDNAME, false, Type.STR));
		suretaxTransactionLogDAS = new SuretaxTransactionLogDAS();
		mapper = new ObjectMapper();
	}

	/**
	 * This method is called for populating of the tax lines from the Suretax
	 * tax engine.
	 */
	@Override
	public void apply(NewInvoiceContext invoice, Integer userId)
			throws TaskException {
		this.invoice = invoice;
		// Defaults to rolling back of invoice creation on error.
		boolean rollback_invoice_on_suretax_error = true;
		try {
			rollback_invoice_on_suretax_error = getParameter(
					ROLLBACK_INVOICE_ON_ERROR, 1) == 1;
		} catch (PluggableTaskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SuretaxResponse response = null;
		SuretaxRequest suretaxRequest = null;

		boolean errorOccurred = false;
		try {
			suretaxRequest = getAssembledRequest(invoice, userId);
			// Save the suretax json request in the sure_tax_txn_log table

			LOG.debug("Total Line item size : "+suretaxRequest.itemList.size());
			if(suretaxRequest.itemList.size() == 0){
				return;
			}
			String jsonRequestString = mapper
					.writeValueAsString(suretaxRequest);

			suretaxTransactionLogDAS.save(new SuretaxTransactionLogDTO(
					suretaxRequest.clientTracking, "REQUEST",
					jsonRequestString,
					new Timestamp(System.currentTimeMillis()), null, "TAX"));
			String suretaxRequestUrl = getParameter(SURETAX_REQUEST_URL, "");
			response = new SuretaxClient().getResponse(suretaxRequest,
					suretaxRequestUrl);
		} catch (Exception e) {
			if (rollback_invoice_on_suretax_error)
				throw new TaskException(e);
			else
				errorOccurred = true;
		}

		// If the control has come here but an error had occurred then
		// the error was meant to be ignored. So, if an error had occurred
		// then just ignore the next block of code. That is exit gracefully
		if (!errorOccurred) {
			// Save the suretax json response in the sure_tax_txn_log table
			int transId = -1;
			try {
				transId = Integer.parseInt(response.transId);
			} catch (Exception e) {
				// TODO: handle exception
			}
			suretaxTransactionLogDAS.save(new SuretaxTransactionLogDTO(
					suretaxRequest.clientTracking, "RESPONSE",
					response.jsonString, new Timestamp(System
							.currentTimeMillis()), transId, "TAX"));
			if (response != null && !response.successful.equals("Y")) {
				if (rollback_invoice_on_suretax_error) {
					throw new TaskException(
							"Error while obtaining the tax lines for this invoice:"
									+ response.responseCode + ":"
									+ response.headerMessage);
				}

			} else if (response != null
					&& response.successful.equals("Y")
					&& response.headerMessage
							.contains("Success with Item errors")) {
				StringBuffer errorMessages = new StringBuffer(
						"Error messages:[");
				int count = 0;
				for (ItemMessage itemMessage : response.itemMessages) {
					if (count == 0) {
						count++;
					} else {
						errorMessages.append(",");
					}
					errorMessages.append(itemMessage.message);
				}
				errorMessages.append("]");
				if (rollback_invoice_on_suretax_error) {
					throw new TaskException(
							"Error while obtaining the tax lines for this invoice:"
									+ errorMessages.toString());
				}
			} else {
				LOG.debug("Response Code: " + response.responseCode
						+ ", Header Message:" + response.headerMessage
						+ ", Client Tracking: " + response.clientTracking
						+ ", Total tax:" + response.totalTax + ", Trans Id: "
						+ response.transId);
				OrderDTO order = ((InvoiceLineDTO) invoice.getResultLines().get(0))
						.getOrder();

				List<InvoiceLineDTO> taxLines = getTaxLines(response, order);
				for (InvoiceLineDTO taxLine : taxLines) {
					invoice.addResultLine(taxLine);
				}
				// Add the trans id in the invoice
				MetaFieldHelper.setMetaField(invoice.getEntityId(), invoice,
						SURETAX_TRANS_ID_META_FIELD_NAME, transId);
			}
		}
	}

	/**
	 * Creates a Suretax request from inputs from the invoice.
	 *
	 * @param invoice
	 *            Invoice for which tax lines need to be calculated.
	 * @return Returns instance of
	 *         com.sapienter.jbilling.client.suretax.request.SuretaxRequest
	 * @throws TaskException
	 */
	private SuretaxRequest getAssembledRequest(NewInvoiceContext invoice,
			Integer userId) throws TaskException {
		// Construct a suretax request to get the tax lines.
		SuretaxRequest suretaxRequest = new SuretaxRequest();

		// Get the pluggable task parameters here.
		String clientNumber = getParameter(CLIENT_NUMBER, "");
		String validationKey = getParameter(VALIDATION_KEY, "");
		String responseGroup = getParameter(RESPONSE_GROUP, "03");
		String responseType = getParameter(RESPONSE_TYPE, "D");
		String numberOfDecimals = getParameter(NUMBER_OF_DECIMAL, "2");
		Integer dataYear = null;
		Integer dataMonth = null;
		try {
			dataYear = getParameter(DATA_YEAR, Calendar.getInstance().get(Calendar.YEAR));
			dataMonth = getParameter(DATA_MONTH, Calendar.getInstance().get(Calendar.MONTH) + 1);
		} catch (PluggableTaskException e) {
			LOG.debug("Exception while retrieving Data Year or Data Month");
		}

		suretaxRequest.setClientNumber(clientNumber);
		suretaxRequest.setValidationKey(validationKey);
		String uniqueTrackingCode = System.currentTimeMillis() + "";
		suretaxRequest.setClientTracking(uniqueTrackingCode);
		suretaxRequest.setDataMonth(dataMonth.toString());
		suretaxRequest.setDataYear(dataYear.toString());
		suretaxRequest.setIndustryExemption("");
		suretaxRequest.setBusinessUnit("");
		suretaxRequest.setResponseGroup(responseGroup);
		suretaxRequest.setResponseType(responseType + numberOfDecimals);
		suretaxRequest.setReturnFileCode("0");
		suretaxRequest.setTotalRevenue(getTotalRevenue(invoice).floatValue());

		List<LineItem> itemList = new ArrayList<LineItem>();
		for (InvoiceLineDTO invoiceLine : (List<InvoiceLineDTO>) invoice
				.getResultLines()) {

			/* Escaping Taxs and Due Invoices */
			if (invoiceLine.getInvoiceLineType().getId() != Constants.INVOICE_LINE_TYPE_TAX && invoiceLine.getInvoiceLineType().getId() !=Constants.INVOICE_LINE_TYPE_DUE_INVOICE){

				LOG.debug("Populating itemlist for invoice line: "
						+ invoiceLine);

				// First, check if the item is a plan
				List<PlanDTO> plans = new PlanDAS().findByItemId(invoiceLine
						.getItem().getId());

				if (plans != null && plans.size() > 0) {
					LOG.debug("There are plans in the item:" + ", size:"
							+ plans.size());

					for (PlanDTO plan : plans) {

						LOG.debug("plan:" + plan.getDescription() + ", item:"
								+ plan.getItem().getDescription()
								+ ", plan items:" + plan.getPlanItems());

						LOG.debug("plan items:" + plan.getPlanItems().size());

						LineItem planSubscriptionItem = getLineItem(plan
								.getItem().getId(), invoiceLine,
								uniqueTrackingCode, plan, userId);
						LOG.debug("Line item for plan "+plan.getItem().getId()+" : "+planSubscriptionItem);
						if(planSubscriptionItem==null){
							continue;
						}
						itemList.add(planSubscriptionItem);

						if (plan.getPlanItems() != null) {
							for (PlanItemDTO planItem : plan.getPlanItems()) {
								LineItem li = getLineItem(planItem.getItem()
										.getId(), invoiceLine,
										uniqueTrackingCode, plan, userId);
								LOG.debug(" Line item for plan item "+planItem.getItem().getId()+" : "+li);
								if(li==null){
									continue;
								}
								itemList.add(li);
							}
						}
					}
				} else {

					LineItem lineItem=getLineItem(invoiceLine.getItem().getId(),
							invoiceLine, uniqueTrackingCode, null, userId);
					LOG.debug("Line item for product "+invoiceLine.getItem().getId()+": "+lineItem);
					if(lineItem==null){
						continue;
					}
					itemList.add(lineItem);
				}
			}
		}
		suretaxRequest.setItemList(itemList);
		return suretaxRequest;
	}

	private LineItem getLineItem(Integer itemId, InvoiceLineDTO invoiceLine,
			String uniqueTrackingCode, PlanDTO plan, Integer userId)
			throws TaskException {
		// Get the meta field names
		String secondaryZipCodeExtensionFieldname = getParameter(
				SECONDARY_ZIP_CODE_EXTN_FIELDNAME,
				"Secondary Zip code extension");
		String secondaryZipCodeFieldname = getParameter(
				SECONDARY_ZIP_CODE_FIELDNAME, "Secondary Zip code");
		String billingZipCodeFieldname = getParameter(
				BILLING_ZIP_CODE_FIELDNAME, "Billing Zip code extension");
        String billingZipCodeExtensionFieldname = getParameter(
                BILLING_ZIP_CODE_Extension_FIELDNAME, "Zip code");
		String regulatoryCodeFieldname = getParameter(
				REGULATORY_CODE_FIELDNAME, "Regulatory Code");
		String salesTypeCodeFieldname = getParameter(SALES_TYPE_CODE_FIELDNAME,
				"Sales Type Code");
		String taxExemptionCodeFieldname = getParameter(
				TAX_EXEMPTION_CODE_FIELDNAME, "Tax exemption code");
		String transactionTypeCodeFieldname = getParameter(
				TRANSACTION_TYPE_CODE_FIELDNAME, "Transaction Type Code");

		LineItem lineItem = new LineItem();
		lineItem.setBillToNumber(""); // TODO: need to be addressed ?
		String customerNumber = null;
		List<NewInvoiceContext.OrderContext> orders = invoice.getOrders();
		// We need to get the fresh item from the database because
		// the item in the invoiceLine doesn't yet contain meta fields.
		ItemDTO item = new ItemDAS().find(itemId);
		OrderDTO orderDTO = null;
		UserDTO invoiceToUser = null;
		MetaFieldValue<String> transTypeCode = null;
		if (plan != null) {
			transTypeCode = plan.getMetaField(transactionTypeCodeFieldname);
		} else {
			transTypeCode = item.getMetaField(transactionTypeCodeFieldname);
		}
		if (transTypeCode == null || transTypeCode.getValue() == null
				|| transTypeCode.getValue().isEmpty()) {
			/* if "Transaction Type Code" metafield is not set in the product/plan then did not create lineItem for those product/plan */
			return null;
		}
		lineItem.setTransTypeCode(transTypeCode.getValue());
		for (NewInvoiceContext.OrderContext orderCtx : orders) {
			if (orderCtx.order.getId().intValue() == invoiceLine.getOrder().getId()) {
				orderDTO = orderCtx.order;
				break;
			}
		}

		if (null == orderDTO) {
			orderDTO = orders.get(0).order;
		}

		invoiceToUser = new UserDAS().find(userId);
		customerNumber = invoiceToUser.getCustomer().getId() + "";

		lineItem.setCustomerNumber(customerNumber);
		lineItem.setInvoiceNumber("JB" + uniqueTrackingCode);
		lineItem.setLineNumber(""); // TODO: need to be addressed ?
		lineItem.setOrigNumber(""); // TODO: need to be addressed ?

		//Fetch data from customer
		List<String> metaFieldNames = new LinkedList<String>();
		metaFieldNames.add(secondaryZipCodeExtensionFieldname);
		metaFieldNames.add(secondaryZipCodeFieldname);
		metaFieldNames.add(billingZipCodeFieldname);
		metaFieldNames.add(billingZipCodeExtensionFieldname);

		Map<String, MetaFieldValue> customerMetaFieldValues = getMetaFieldValues(invoiceToUser, metaFieldNames);

		MetaFieldValue<String> p2PPlus4 = customerMetaFieldValues
                .get(secondaryZipCodeExtensionFieldname);
		if (p2PPlus4 != null) {
			lineItem.setP2PPlus4(p2PPlus4.getValue().toString());
		} else {
			lineItem.setP2PPlus4("");
		}

		MetaFieldValue<String> p2PZipcode = customerMetaFieldValues
                .get(secondaryZipCodeFieldname);
		if (p2PZipcode != null) {
			lineItem.setP2PZipcode(p2PZipcode.getValue().toString());
		} else {
			lineItem.setP2PZipcode("");
		}

		MetaFieldValue<String> plus4 = customerMetaFieldValues
                .get(billingZipCodeExtensionFieldname);

		LOG.debug("Meta fields: p2PPlus4:" + p2PPlus4 + ", p2PZipcode:"
				+ p2PZipcode + ", plus4:" + plus4);

		MetaFieldValue<String> regulatoryCode = null;
		if (plan != null) {
			regulatoryCode = plan.getMetaField(regulatoryCodeFieldname);
		} else {
			regulatoryCode = item.getMetaField(regulatoryCodeFieldname);
		}
		if (regulatoryCode == null || regulatoryCode.getValue() == null
				|| regulatoryCode.getValue().isEmpty()) {
			lineItem.setRegulatoryCode("00");
		} else {
			lineItem.setRegulatoryCode(regulatoryCode.getValue());
		}

		lineItem.setRevenue(invoiceLine.getAmount().floatValue());

		MetaFieldValue<String> salesTypeCode = orderDTO
				.getMetaField(salesTypeCodeFieldname);
		if (salesTypeCode == null || salesTypeCode.getValue() == null
				|| salesTypeCode.getValue().isEmpty()) {
			lineItem.setSalesTypeCode("R");
		} else {
			lineItem.setSalesTypeCode(salesTypeCode.getValue());
		}

		lineItem.setSeconds(invoiceLine.getQuantity() != null ? invoiceLine
				.getQuantity().intValue() : 0);
		List<String> taxExemptionCodeList = new ArrayList<String>();
		// First get the tax exemption code from the customer
		MetaFieldValue<String> taxExemptionCode = customerMetaFieldValues
                .get(taxExemptionCodeFieldname);
		LOG.debug("Tax exemption code from customer: " + taxExemptionCode);
		if (!(taxExemptionCode != null && taxExemptionCode.getValue() != null && !taxExemptionCode
				.getValue().isEmpty())) {
			if (plan != null) {
				taxExemptionCode = plan.getMetaField(taxExemptionCodeFieldname);
			} else {
				// If that was null/empty then get it from the product
				taxExemptionCode = item.getMetaField(taxExemptionCodeFieldname);
			}
			LOG.debug("Tax exemption code from product: " + taxExemptionCode);
		}
		if (taxExemptionCode == null) {
			LOG.debug("Setting tax exemption code to be 00");
			taxExemptionCodeList.add("00");
		} else {
			taxExemptionCodeList.add(taxExemptionCode.getValue());
		}
		LOG.debug("Meta fields: regulatoryCode:" + regulatoryCode
				+ ", salesTypeCode:" + salesTypeCode + ", taxExemptionCode:"
				+ taxExemptionCode);
		lineItem.setTaxExemptionCodeList(taxExemptionCodeList);
		lineItem.setTaxIncludedCode("0");

		lineItem.setTermNumber("");

		// TODO: Need to check if trans date will be current date or based on data year and data month ?
		lineItem.setTransDate("07-10-2012");

		lineItem.setUnits(invoiceLine.getQuantity() != null ? invoiceLine
				.getQuantity().intValue() : 0);
		lineItem.setUnitType("00");
		MetaFieldValue billingZipCodeValue = customerMetaFieldValues
                .get(billingZipCodeFieldname);

		String zipCode = null;
		if (billingZipCodeValue != null) zipCode = (String) billingZipCodeValue.getValue();

		String plus4Value = null;
		if (plus4 != null) plus4Value = plus4.getValue();

		if ((plus4Value == null || plus4.isEmpty()) && zipCode != null && zipCode.length() == 9) {
			plus4Value = zipCode.substring(5);
			zipCode = zipCode.substring(0, 5);
		}
		if (plus4Value != null) {
			lineItem.setPlus4(plus4Value);
		} else {
			lineItem.setPlus4("");
		}
		if (zipCode != null && !zipCode.isEmpty() && plus4Value != null
				&& !plus4Value.isEmpty()) {
			lineItem.setZipcode(zipCode);
			lineItem.setTaxSitusRule("05");
		} else if (zipCode != null && !zipCode.isEmpty()
				&& (plus4Value == null || plus4Value.isEmpty())) {
			lineItem.setZipcode(zipCode);
			lineItem.setPlus4("0000");
			lineItem.setTaxSitusRule("05");
		}
		return lineItem;
	}

	private Map<String, MetaFieldValue> getMetaFieldValues(UserDTO user, List<String> metaFieldNames) {
		Map<String, MetaFieldValue> metaFieldValues = new HashMap<String, MetaFieldValue>();
		Set<CustomerAccountInfoTypeMetaField> list = user.getCustomer().getCustomerAccountInfoTypeMetaFields();
		for (CustomerAccountInfoTypeMetaField customerAccountInfoTypeMetaField : list) {
			MetaFieldValue metaField = customerAccountInfoTypeMetaField.getMetaFieldValue();
			if (metaFieldNames.contains(metaField.getField().getName())) {

				if (metaFieldNames.remove(metaField.getField().getName()))
					metaFieldValues.put(metaField.getField().getName(), metaField);
			}
		}
		return metaFieldValues;
	}

	/**
	 * Converts the instance of SuretaxResponse object into tax lines.
	 *
	 * @param suretaxResponse
	 * @param order
	 * @return
	 */
	private List<InvoiceLineDTO> getTaxLines(SuretaxResponse suretaxResponse,
                                             OrderDTO order) {
		List<InvoiceLineDTO> taxLines = new ArrayList<InvoiceLineDTO>();
		if (suretaxResponse.successful.equals("Y")) {
			for (Group group : suretaxResponse.groupList) {
				for (TaxItem taxItem : group.taxList) {
					InvoiceLineDTO invoiceLineDTO = new InvoiceLineDTO();
					invoiceLineDTO.setAmount(new BigDecimal(taxItem.taxAmount));
					invoiceLineDTO.setDescription(taxItem.taxTypeCode + ":"
							+ taxItem.taxTypeDesc);
					invoiceLineDTO.setInvoiceLineType(new InvoiceLineTypeDTO(
							Constants.INVOICE_LINE_TYPE_TAX));
					invoiceLineDTO.setIsPercentage(0);
					invoiceLineDTO.setOrder(order);
					invoiceLineDTO.setPrice(new BigDecimal(taxItem.taxAmount));
					invoiceLineDTO.setQuantity(1);
					taxLines.add(invoiceLineDTO);
				}
			}
		}
		return taxLines;
	}

	protected BigDecimal getTotalRevenue(NewInvoiceContext invoice) {

		// calculate TOTAL to include result lines
		invoice.calculateTotal();
		BigDecimal invoiceAmountSum = invoice.getTotal();

		// Remove CARRIED BALANCE from tax calculation to avoid double taxation
		LOG.debug("Carried balance is " + invoice.getCarriedBalance());
		if (null != invoice.getCarriedBalance()) {
			invoiceAmountSum = invoiceAmountSum.subtract(invoice
					.getCarriedBalance());
		}

		// Remove TAX ITEMS from Invoice to avoid calculating tax on tax
		for (int i = 0; i < invoice.getResultLines().size(); i++) {
			InvoiceLineDTO invoiceLine = (InvoiceLineDTO) invoice
					.getResultLines().get(i);
			if (null != invoiceLine.getInvoiceLineType()
					&& invoiceLine.getInvoiceLineType().getId() == Constants.INVOICE_LINE_TYPE_TAX) {
				invoiceAmountSum = invoiceAmountSum.subtract(invoiceLine
						.getAmount());
			}
		}

		return invoiceAmountSum;
	}
}
