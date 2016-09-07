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

/*
 * Created on Jan 27, 2005
 * One session bean to expose as a single web service, thus, one wsdl
 */
package com.sapienter.jbilling.server.util;

import com.sapienter.jbilling.common.*;
import com.sapienter.jbilling.server.item.*;
import com.sapienter.jbilling.server.item.db.*;
import com.sapienter.jbilling.server.process.db.*;
import com.sapienter.jbilling.server.util.db.*;
import com.sapienter.jbilling.server.mediation.*;
import com.sapienter.jbilling.server.payment.*;

import grails.plugin.springsecurity.SpringSecurityService;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.sapienter.jbilling.server.payment.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.sql.rowset.CachedRowSet;

import com.sapienter.jbilling.common.*;
import com.sapienter.jbilling.server.item.*;
import com.sapienter.jbilling.server.item.db.*;
import com.sapienter.jbilling.server.util.credentials.EmailResetPasswordService;
import com.sapienter.jbilling.server.util.db.*;
import com.sapienter.jbilling.server.order.*;
import com.sapienter.jbilling.server.order.db.*;
import com.sapienter.jbilling.server.pricing.cache.MatchingFieldType;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sapienter.jbilling.CustomerNoteDAS;
import com.sapienter.jbilling.client.authentication.CompanyUserDetails;
import com.sapienter.jbilling.common.*;
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentWS;
import com.sapienter.jbilling.server.customerEnrollment.db.CustomerEnrollmentBL;
import com.sapienter.jbilling.server.customerEnrollment.db.CustomerEnrollmentDTO;
import com.sapienter.jbilling.server.ediTransaction.*;
import com.sapienter.jbilling.server.ediTransaction.db.*;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.fileProcessing.fileGenerator.FlatFileGenerator;
import com.sapienter.jbilling.server.fileProcessing.fileGenerator.IFileGenerator;
import com.sapienter.jbilling.server.fileProcessing.fileParser.FlatFileParser;
import com.sapienter.jbilling.server.fileProcessing.xmlParser.FileFormat;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.account.AccountInformationTypeBL;
import com.sapienter.jbilling.server.account.AccountTypeBL;
import com.sapienter.jbilling.server.company.CopyCompanyBL;

import com.sapienter.jbilling.server.diameter.*;
import com.sapienter.jbilling.server.discount.DiscountBL;
import com.sapienter.jbilling.server.discount.DiscountLineWS;
import com.sapienter.jbilling.server.discount.DiscountWS;
import com.sapienter.jbilling.server.discount.db.DiscountDAS;
import com.sapienter.jbilling.server.discount.db.DiscountDTO;
import com.sapienter.jbilling.server.discount.db.DiscountLineDTO;
import com.sapienter.jbilling.server.entity.InvoiceLineDTO;
import com.sapienter.jbilling.server.filter.Filter;
import com.sapienter.jbilling.server.filter.FilterConstraint;
import com.sapienter.jbilling.server.invoice.IInvoiceSessionBean;
import com.sapienter.jbilling.server.invoice.InvoiceBL;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.invoice.NewInvoiceContext;
import com.sapienter.jbilling.server.invoice.db.*;
import com.sapienter.jbilling.server.item.*;
import com.sapienter.jbilling.server.item.batch.AssetImportConstants;
import com.sapienter.jbilling.server.item.db.*;
import com.sapienter.jbilling.server.mediation.*;
import com.sapienter.jbilling.server.mediation.converter.common.steps.MediationStepResult;
import com.sapienter.jbilling.server.mediation.db.MediationConfiguration;
import com.sapienter.jbilling.server.mediation.db.MediationConfigurationDAS;
import com.sapienter.jbilling.server.mediation.task.IMediationProcess;
import com.sapienter.jbilling.server.metafields.*;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldDAS;
import com.sapienter.jbilling.server.metafields.db.MetaFieldGroup;
import com.sapienter.jbilling.server.notification.INotificationSessionBean;
import com.sapienter.jbilling.server.notification.MessageDTO;
import com.sapienter.jbilling.server.notification.NotificationBL;
import com.sapienter.jbilling.server.order.*;
import com.sapienter.jbilling.server.order.db.*;
import com.sapienter.jbilling.server.order.event.OrderPreAuthorizedEvent;
import com.sapienter.jbilling.server.order.validator.IsNotEmptyOrDeletedValidator;
import com.sapienter.jbilling.server.order.validator.OrderHierarchyValidator;
import com.sapienter.jbilling.server.payment.*;
import com.sapienter.jbilling.server.payment.db.*;
import com.sapienter.jbilling.server.payment.event.PaymentSuccessfulEvent;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.*;
import com.sapienter.jbilling.server.pricing.*;
import com.sapienter.jbilling.server.pricing.cache.MatchingFieldType;
import com.sapienter.jbilling.server.pricing.db.*;
import com.sapienter.jbilling.server.process.*;
import com.sapienter.jbilling.server.process.task.IScheduledTask;
import com.sapienter.jbilling.server.process.task.ScheduledTask;
import com.sapienter.jbilling.server.provisioning.*;
import com.sapienter.jbilling.server.provisioning.db.ProvisioningCommandDAS;
import com.sapienter.jbilling.server.provisioning.db.ProvisioningCommandDTO;
import com.sapienter.jbilling.server.security.JBCrypto;
import com.sapienter.jbilling.server.system.event.EventManager;
import com.sapienter.jbilling.server.usagePool.CustomerUsagePoolBL;
import com.sapienter.jbilling.server.usagePool.CustomerUsagePoolWS;
import com.sapienter.jbilling.server.usagePool.UsagePoolBL;
import com.sapienter.jbilling.server.usagePool.UsagePoolWS;
import com.sapienter.jbilling.server.usagePool.db.CustomerUsagePoolDAS;
import com.sapienter.jbilling.server.usagePool.db.CustomerUsagePoolDTO;
import com.sapienter.jbilling.server.usagePool.db.UsagePoolDAS;
import com.sapienter.jbilling.server.usagePool.db.UsagePoolDTO;
import com.sapienter.jbilling.server.user.*;
import com.sapienter.jbilling.server.user.db.*;
import com.sapienter.jbilling.server.user.partner.*;
import com.sapienter.jbilling.server.user.partner.db.*;
import com.sapienter.jbilling.server.util.audit.EventLogger;
import com.sapienter.jbilling.server.util.credentials.EmailResetPasswordService;
import com.sapienter.jbilling.server.util.csv.CsvExporter;
import com.sapienter.jbilling.server.util.csv.Exportable;
import com.sapienter.jbilling.server.util.csv.ExportableMap;
import com.sapienter.jbilling.server.util.db.*;
import com.sapienter.jbilling.server.util.search.SearchCriteria;
import com.sapienter.jbilling.server.util.search.SearchResultString;
import com.sapienter.jbilling.tools.JArrays;
import grails.plugin.springsecurity.SpringSecurityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.naming.NamingException;
import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.common.base.Splitter;

import static com.sapienter.jbilling.server.order.CancellationFeeType.FLAT;
import static com.sapienter.jbilling.server.order.CancellationFeeType.PERCENTAGE;

@Transactional(propagation = Propagation.REQUIRED)
public class WebServicesSessionSpringBean implements IWebServicesSessionBean {

	private static final FormatLogger LOG = new FormatLogger(
			Logger.getLogger(WebServicesSessionSpringBean.class));

	private SpringSecurityService springSecurityService;

	public SpringSecurityService getSpringSecurityService() {
		if (springSecurityService == null)
			this.springSecurityService = Context
					.getBean(Context.Name.SPRING_SECURITY_SERVICE);
		return springSecurityService;
	}

	public void setSpringSecurityService(
			SpringSecurityService springSecurityService) {
		this.springSecurityService = springSecurityService;
	}

	/*
	 * Returns the user ID of the authenticated user account making the web
	 * service call.
	 * 
	 * @return caller user ID
	 */
	@Transactional(readOnly = true)
	public Integer getCallerId() {
		CompanyUserDetails details = (CompanyUserDetails) getSpringSecurityService()
				.getPrincipal();
		if(details == null)
			return null;
		return details.getUserId();
	}

	/**
	 * Returns the company ID of the authenticated user account making the web
	 * service call.
	 *
	 * @return caller company ID
	 */
	@Transactional(readOnly = true)
	public Integer getCallerCompanyId() {
		CompanyUserDetails details = (CompanyUserDetails) getSpringSecurityService()
				.getPrincipal();
		return details.getCompanyId();
	}

	/**
	 * Returns the language ID of the authenticated user account making the web
	 * service call.
	 *
	 * @return caller language ID
	 */
	@Transactional(readOnly = true)
	public Integer getCallerLanguageId() {
		CompanyUserDetails details = (CompanyUserDetails) getSpringSecurityService()
				.getPrincipal();
		return details.getLanguageId();
	}

	/**
	 * Returns the currency ID of the authenticated user account making the web
	 * service call.
	 *
	 * @return caller currency ID
	 */
	public Integer getCallerCurrencyId() {
		CompanyUserDetails details = (CompanyUserDetails) getSpringSecurityService()
				.getPrincipal();
		return details.getCurrencyId();
	}

	// todo: reorganize methods and reformat code. should match the structure of
	// the interface to make things readable.

	/*
	 * Invoices
	 */
	@Transactional(readOnly = true)
	public InvoiceWS getInvoiceWS(Integer invoiceId)
			throws SessionInternalError {
		if (invoiceId == null) {
			return null;
		}
		InvoiceDTO invoice = new InvoiceDAS().find(invoiceId);

		if (invoice.getDeleted() == 1) {
			return null;
		}

		InvoiceWS wsDto = InvoiceBL.getWS(invoice);
		if (null != invoice.getInvoiceStatus()) {
			wsDto.setStatusDescr(invoice.getInvoiceStatus().getDescription(
					getCallerLanguageId()));
		}
		return wsDto;
	}

	@Transactional(readOnly = true)
	public InvoiceWS[] getAllInvoicesForUser(Integer userId) {
		IInvoiceSessionBean invoiceBean = Context
				.getBean(Context.Name.INVOICE_SESSION);
		Set<InvoiceDTO> invoices = invoiceBean.getAllInvoices(userId);

		List<InvoiceWS> ids = new ArrayList<InvoiceWS>(invoices.size());
		for (InvoiceDTO invoice : invoices) {
			InvoiceWS wsdto = InvoiceBL.getWS(invoice);
			if (null != invoice.getInvoiceStatus())
				wsdto.setStatusDescr(invoice.getInvoiceStatus().getDescription(
						getCallerLanguageId()));

			ids.add(wsdto);
		}
		return ids.toArray(new InvoiceWS[ids.size()]);
	}

	@Transactional(readOnly = true)
	public InvoiceWS[] getAllInvoices() {

		List<InvoiceDTO> invoices = new InvoiceDAS().findAll();

		List<InvoiceWS> ids = new ArrayList<InvoiceWS>(invoices.size());
		for (InvoiceDTO invoice : invoices) {
			InvoiceWS wsdto = InvoiceBL.getWS(invoice);
			if (null != invoice.getInvoiceStatus())
				wsdto.setStatusDescr(invoice.getInvoiceStatus().getDescription(
						getCallerLanguageId()));

			ids.add(wsdto);
		}
		return ids.toArray(new InvoiceWS[ids.size()]);
	}

	public boolean notifyInvoiceByEmail(Integer invoiceId) {
		INotificationSessionBean notificationSession = (INotificationSessionBean) Context
				.getBean(Context.Name.NOTIFICATION_SESSION);
        
        boolean emailInvoice;
        try{
            emailInvoice = notificationSession.emailInvoice(invoiceId); 
        } catch (SessionInternalError sie) {
          throw sie;
        } catch (Exception e){
            LOG.warn("Exception in web service: notifying invoice by email", e);
            emailInvoice = false;
        }
        return emailInvoice;
    }

	public boolean notifyPaymentByEmail(Integer paymentId) {
		INotificationSessionBean notificationSession = (INotificationSessionBean) Context
				.getBean(Context.Name.NOTIFICATION_SESSION);
		return notificationSession.emailPayment(paymentId);
	}

	@Transactional(readOnly = true)
	public Integer[] getAllInvoices(Integer userId) {
		IInvoiceSessionBean invoiceBean = Context
				.getBean(Context.Name.INVOICE_SESSION);
		Set<InvoiceDTO> invoices = invoiceBean.getAllInvoices(userId);

		List<Integer> ids = new ArrayList<Integer>(invoices.size());
		for (InvoiceDTO invoice : invoices)
			ids.add(invoice.getId());
		return ids.toArray(new Integer[ids.size()]);
	}

	@Transactional(readOnly = true)
	public InvoiceWS getLatestInvoice(Integer userId)
			throws SessionInternalError {
		InvoiceWS retValue = null;
		try {
			if (userId == null) {
				return null;
			}
			InvoiceBL bl = new InvoiceBL();
			Integer invoiceId = bl.getLastByUser(userId);
			if (invoiceId != null) {
				retValue = bl.getWS(new InvoiceDAS().find(invoiceId));
			}
			return retValue;
		} catch (Exception e) { // needed because the sql exception :(
			LOG.error("Exception in web service: getting latest invoice"
					+ " for user " + userId, e);
			throw new SessionInternalError("Error getting latest invoice");
		}
	}

	@Transactional(readOnly = true)
	public Integer[] getLastInvoices(Integer userId, Integer number)
			throws SessionInternalError {
		if (userId == null || number == null) {
			return null;
		}

		InvoiceBL bl = new InvoiceBL();
		return bl.getManyWS(userId, number);
	}

	@Transactional(readOnly = true)
	public Integer[] getInvoicesByDate(String since, String until)
			throws SessionInternalError {
		try {
			Date dSince = com.sapienter.jbilling.common.Util.parseDate(since);
			Date dUntil = com.sapienter.jbilling.common.Util.parseDate(until);
			if (since == null || until == null) {
				return null;
			}

			Integer entityId = getCallerCompanyId();

			InvoiceBL invoiceBl = new InvoiceBL();
			return invoiceBl.getInvoicesByCreateDateArray(entityId, dSince,
					dUntil);
		} catch (Exception e) { // needed for the SQLException :(
			LOG.error("Exception in web service: getting invoices by date"
					+ since + until, e);
			throw new SessionInternalError("Error getting last invoices");
		}
	}

	/**
	 * Returns the invoices for the user within the given date range.
	 */
	@Transactional(readOnly = true)
	public Integer[] getUserInvoicesByDate(Integer userId, String since,
			String until) throws SessionInternalError {
		if (userId == null || since == null || until == null) {
			return null;
		}

		Date dSince = com.sapienter.jbilling.common.Util.parseDate(since);
		Date dUntil = com.sapienter.jbilling.common.Util.parseDate(until);

		InvoiceBL invoiceBl = new InvoiceBL();

		Integer[] results = invoiceBl.getUserInvoicesByDate(userId, dSince,
				dUntil);

		return results;
	}

	/**
	 * Returns an array of IDs for all unpaid invoices under the given user ID.
	 *
	 * @param userId
	 *            user IDs
	 * @return array of un-paid invoice IDs
	 */
	@Transactional(readOnly = true)
	public Integer[] getUnpaidInvoices(Integer userId) {
		try {
			CachedRowSet rs = new InvoiceBL().getPayableInvoicesByUser(userId);

			Integer[] invoiceIds = new Integer[rs.size()];
			int i = 0;
			while (rs.next())
				invoiceIds[i++] = rs.getInt(1);

			rs.close();
			return invoiceIds;

		} catch (SQLException e) {
			throw new SessionInternalError(
					"Exception occurred querying payable invoices.");
		} catch (Exception e) {
			throw new SessionInternalError(
					"An un-handled exception occurred querying payable invoices.");
		}
	}

	@Transactional(readOnly = true)
	public InvoiceWS[] getUserInvoicesPage(Integer userId, Integer limit,
			Integer offset) throws SessionInternalError {
		if (userId == null || limit == null || offset == null) {
			return null;
		}

		List<InvoiceDTO> invoicesPaged = new InvoiceBL().getListInvoicesPaged(
				getCallerCompanyId(), userId, limit, offset);

		if (invoicesPaged == null) {
			return new InvoiceWS[0];
		}

		List<InvoiceWS> invoicesWS = new ArrayList<InvoiceWS>(
				invoicesPaged.size());
		for (InvoiceDTO invoice : invoicesPaged) {
			InvoiceWS wsdto = InvoiceBL.getWS(invoice);
			if (null != invoice.getInvoiceStatus())
				wsdto.setStatusDescr(invoice.getInvoiceStatus().getDescription(
						getCallerLanguageId()));

			invoicesWS.add(wsdto);
		}
		return invoicesWS.toArray(new InvoiceWS[invoicesWS.size()]);

	}

	/**
	 * Generates and returns the paper invoice PDF for the given invoiceId.
	 *
	 * @param invoiceId
	 *            invoice to generate PDF for
	 * @return PDF invoice bytes
	 * @throws SessionInternalError
	 */
	@Transactional(readOnly = true)
	public byte[] getPaperInvoicePDF(Integer invoiceId)
			throws SessionInternalError {
		IInvoiceSessionBean invoiceSession = (IInvoiceSessionBean) Context
				.getBean(Context.Name.INVOICE_SESSION);

		UserWS user = getUserWS(getCallerId());

		if (Constants.TYPE_CUSTOMER.equals(user.getMainRoleId())) {
			try {
				if (!getInvoiceWS(invoiceId).getUserId().equals(
						user.getUserId())) {
					LOG.warn(String
							.format("Invalid access to download Invoice ID %s by user %s",
									invoiceId, user.getUserId()));
					throw new Exception();
				}
			} catch (Exception e) {
				SessionInternalError sie = new SessionInternalError(
						"Invalid Invoice ID or the user does not own this Invoice.");
				sie.setErrorMessages(new String[] { "InvoiceDTO,id,invoice.error.invalid.download,"
						+ invoiceId });
				throw sie;
			}
		}

		return invoiceSession.getPDFInvoice(invoiceId);
	}

	/**
	 * Un-links a payment from an invoice, effectivley making the invoice
	 * "unpaid" by removing the payment balance.
	 *
	 * If either invoiceId or paymentId parameters are null, no operation will
	 * be performed.
	 *
	 * @param invoiceId
	 *            target Invoice
	 * @param paymentId
	 *            payment to be unlink
	 */
	public void removePaymentLink(Integer invoiceId, Integer paymentId) {
		if (invoiceId == null || paymentId == null) {
			return;
		}

		PaymentBL paymentBL = new PaymentBL(paymentId);

		// check if the payment is a refund , if it is do not allow it
		if (paymentBL.getEntity().getIsRefund() == 1) {
			LOG.debug(
					"This payment id %s is a refund so we cannot unlink it from the invoice",
					paymentId);
			throw new SessionInternalError(
					"This payment is a refund and hence cannot be unlinked from any invoice",
					new String[] { "PaymentWS,unlink,validation.error.payment.unlink" });
		}

		// if the payment has been refunded
		// #A Partially refunded Payment can be unlinked from an Invoice.
		/*
		 * if(paymentBL.ifRefunded()) { throw new SessionInternalError(
		 * "This payment has been refunded and hence cannot be unlinked from the invoice"
		 * , new String[]
		 * {"PaymentWS,unlink,validation.error.delete.refunded.payment"}); }
		 */

		boolean result = paymentBL.unLinkFromInvoice(invoiceId);
		if (!result) {
			throw new SessionInternalError("Unable to find the Invoice Id "
					+ invoiceId + " linked to Payment Id " + paymentId);
		}
	}

	/**
	 * Applies an existing payment to an invoice.
	 *
	 * If either invoiceId or paymentId parameters are null, no operation will
	 * be performed.
	 *
	 * @param invoiceId
	 *            target invoice
	 * @param paymentId
	 *            payment to apply
	 */
	public void createPaymentLink(Integer invoiceId, Integer paymentId) {

		LOG.debug("In createPaymentLink...");
		if (invoiceId == null || paymentId == null) {
			throw new SessionInternalError("Payment link chain missing!");
		}

		Integer entityId = getCallerCompanyId();

		PaymentWS payment = getPayment(paymentId);

		// Guard against npe
		if (payment == null) {
			LOG.debug("No payment found for paymentId: %d.", paymentId);
			throw new SessionInternalError("Payment not found!");
		}
		// Check if the payment owing user is from the same entity as the caller
		// user.
		Integer userId = payment.getOwningUserId();
		UserDTO user = new UserDAS().find(userId);
		if (null == user) {
			LOG.debug("No owning user for payment id: %d", paymentId);
			throw new SessionInternalError(
					"There is not user for the supplied payment.");
		}
		Integer userCompanyId = user.getEntity().getId();
		if (!entityId.equals(userCompanyId)) {
			LOG.debug(
					"Payment owing user entity id: %d not equals with invoking user entity id: %d",
					userCompanyId, entityId);
			throw new SessionInternalError(
					"Can not create link for non owing payment!!");
		}

		// Check if the invoice for the invoice id has the same entity id as the
		// caller entity id.
		InvoiceDTO invoice = findInvoice(invoiceId);
		if (null == invoice) {
			LOG.debug("No invoice found invoice id: %d", invoiceId);
			throw new SessionInternalError("Invoice not found!!");
		}
		if (!entityId.equals(invoice.getBaseUser().getEntity().getId())) {
			LOG.debug(
					"Invoice entity id: %d not equals with invoking user entity id: %d",
					userCompanyId, entityId);
			throw new SessionInternalError(
					"Can not create link for non owing invoice!!");
		}

		IPaymentSessionBean session = Context
				.getBean(Context.Name.PAYMENT_SESSION);
		session.applyPayment(paymentId, invoiceId);
	}

	public void removeAllPaymentLinks(Integer paymentId)
			throws SessionInternalError {

		if (paymentId == null) {
			return;
		}

		PaymentBL bl = new PaymentBL(paymentId);
		if (bl.getEntity() == null) {
			return;
		}

		Iterator it = bl.getEntity().getInvoicesMap().iterator();
		while (it.hasNext()) {
			PaymentInvoiceMapDTO map = (PaymentInvoiceMapDTO) it.next();
			boolean result = bl.unLinkFromInvoice(map.getInvoiceEntity()
					.getId());
			if (!result) {
				throw new SessionInternalError("Unable to find the Invoice Id "
						+ map.getInvoiceEntity().getId()
						+ " linked to Payment Id " + paymentId);
			}
			bl = new PaymentBL(paymentId);
			it = bl.getEntity().getInvoicesMap().iterator();
		}
	}

	/**
	 * Deletes an invoice
	 * 
	 * @param invoiceId
	 *            The id of the invoice to delete
	 */
	public void deleteInvoice(Integer invoiceId) {
		IInvoiceSessionBean session = Context
				.getBean(Context.Name.INVOICE_SESSION);
		session.delete(invoiceId, getCallerId());
	}

	/**
	 * Saves an invoiceWS instance from legacy data without linking to any
	 * available order with special comments of legacy mark.
	 *
	 * @param invoiceWS
	 *            The instance of desired invoiceWS
	 */
	public Integer saveLegacyInvoice(InvoiceWS invoiceWS) {
		IInvoiceSessionBean session = Context
				.getBean(Context.Name.INVOICE_SESSION);

		NewInvoiceContext newInvoiceDTO = new NewInvoiceContext();

		UserDTO userDTO = new UserDAS().find(invoiceWS.getUserId());
		newInvoiceDTO.setBaseUser(userDTO);
		CurrencyDTO currency = new CurrencyDAS()
				.find(invoiceWS.getCurrencyId());
		newInvoiceDTO.setCurrency(currency);
		newInvoiceDTO.setCreateDatetime(invoiceWS.getCreateDatetime());
		newInvoiceDTO.setDueDate(invoiceWS.getDueDate());
		newInvoiceDTO.setTotal(new BigDecimal(invoiceWS.getTotal()));
		if (invoiceWS.getPaymentAttempts() != null) {
			newInvoiceDTO.setPaymentAttempts(invoiceWS.getPaymentAttempts());
		}
		if (invoiceWS.getStatusId() != null) {
			InvoiceStatusDTO invoiceStatusDTO = new InvoiceStatusDAS()
					.find(invoiceWS.getStatusId());
			newInvoiceDTO.setInvoiceStatus(invoiceStatusDTO);
		}
		newInvoiceDTO.setToProcess(invoiceWS.getToProcess());
		newInvoiceDTO.setBalance(new BigDecimal(invoiceWS.getBalance()));
		newInvoiceDTO.setCarriedBalance(invoiceWS.getCarriedBalanceAsDecimal());
		if (invoiceWS.getInProcessPayment() != null) {
			newInvoiceDTO.setInProcessPayment(invoiceWS.getInProcessPayment());
		}
		newInvoiceDTO.setIsReview(invoiceWS.getIsReview() == null ? 0
				: invoiceWS.getIsReview()); // set fake value if null
		newInvoiceDTO.setDeleted(invoiceWS.getDeleted());
		newInvoiceDTO
				.setCustomerNotes((invoiceWS.getCustomerNotes() == null ? ""
						: (invoiceWS.getCustomerNotes() + " "))
						+ "This invoice is migrated from legacy system.");
		newInvoiceDTO.setPublicNumber(invoiceWS.getNumber());
		newInvoiceDTO.setLastReminder(invoiceWS.getLastReminder());
		newInvoiceDTO.setOverdueStep(invoiceWS.getOverdueStep());
		newInvoiceDTO.setCreateTimestamp(invoiceWS.getCreateTimeStamp());

		// if create date time is given then we can assume that that
		// is the billing date, otherwise we will fake the billing date
		newInvoiceDTO
				.setBillingDate(null != invoiceWS.getCreateDatetime() ? invoiceWS
						.getCreateDatetime() : new Date());

		for (InvoiceLineDTO invoiceLineDTO : invoiceWS.getInvoiceLines()) {

			com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO dbInvoiceLineDTO = new com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO();

			if (invoiceLineDTO.getId() != null) {
				dbInvoiceLineDTO.setId(invoiceLineDTO.getId());
			}
			ItemDTO itemDTO = new ItemDAS().find(invoiceLineDTO.getItemId());
			dbInvoiceLineDTO.setItem(itemDTO);
			dbInvoiceLineDTO.setAmount(invoiceLineDTO.getAmountAsDecimal());
			dbInvoiceLineDTO.setQuantity(invoiceLineDTO.getQuantityAsDecimal());
			dbInvoiceLineDTO.setPrice(invoiceLineDTO.getPriceAsDecimal());
			dbInvoiceLineDTO.setDeleted(invoiceLineDTO.getDeleted());
			dbInvoiceLineDTO.setDescription(invoiceLineDTO.getDescription());
			dbInvoiceLineDTO.setSourceUserId(invoiceLineDTO.getSourceUserId());
			dbInvoiceLineDTO.setIsPercentage(invoiceLineDTO.getPercentage());
			dbInvoiceLineDTO.setInvoiceLineType(new InvoiceLineTypeDTO(3)); // Due
																			// invoice
																			// line
																			// type

			newInvoiceDTO.getResultLines().add(dbInvoiceLineDTO);
		}

		InvoiceDTO newInvoice = session.create(getCompany().getId(),
				invoiceWS.getUserId(), newInvoiceDTO);
		return newInvoice.getId();
	}

	/**
	 * Saves a paymentWS instance from legacy data without linking to any
	 * available invoice with special comments of legacy mark.
	 *
	 * @param paymentWS
	 *            The instance of desired paymentWS
	 */
	public Integer saveLegacyPayment(PaymentWS paymentWS) {

		IPaymentSessionBean session = Context
				.getBean(Context.Name.PAYMENT_SESSION);

		if (paymentWS.getPaymentNotes() != null) {
			paymentWS.setPaymentNotes(paymentWS.getPaymentNotes()
					+ " This payment is migrated from legacy system.");
		} else {
			paymentWS
					.setPaymentNotes("This invoice is migrated from legacy system.");
		}

		PaymentDTOEx paymentDTOEx = new PaymentDTOEx(paymentWS);

		return session.saveLegacyPayment(paymentDTOEx);
	}

	/**
	 * Saves an orderWS instance from legacy data used in migration tool.
	 *
	 * @param orderWS
	 *            The instance of desired orderWS
	 * @return id of saved orderWS object
	 */
	public Integer saveLegacyOrder(OrderWS orderWS) {

		UserWS userWS = getUserWS(orderWS.getUserId());
		OrderBL orderBL = new OrderBL();

		return orderBL.create(userWS.getEntityId(), null,
				orderBL.getDTO(orderWS));
	}

	/**
	 * Deletes an Item
	 * 
	 * @param itemId
	 *            The id of the item to delete
	 */
	public void deleteItem(Integer itemId) throws SessionInternalError {

		ItemBL bl = new ItemBL(itemId);
		// only root entity can delete global item/category, otherwise owning
		// entity can
		CompanyDAS companyDas = new CompanyDAS();
		if (bl.getEntity().isGlobal()) {
			if (!companyDas.isRoot(getCallerCompanyId())) {
				String[] errors = new String[] { "ItemTypeWS,global,validation.only.root.can.delete.global.item" };
				throw new SessionInternalError(
						"validation.only.root.can.delete.global.item", errors);
			}
		}

		if (!getCallerCompanyId().equals(bl.getEntity().getEntityId())) {
			String[] errors = new String[] { "ItemTypeWS,entity,validation.only.owner.can.delete.item" };
			throw new SessionInternalError(
					"validation.only.owner.can.delete.item", errors);
		}

		// todo - item may be in use, prevent deletion

		IItemSessionBean itemSession = (IItemSessionBean) Context
				.getBean(Context.Name.ITEM_SESSION);
		itemSession.delete(getCallerId(), itemId);
		LOG.debug("Deleted Item Id %s", itemId);
	}

	/**
	 * Deletes an Item Category
	 * 
	 * @param itemCategoryId
	 *            The id of the Item Category to delete
	 */
	public void deleteItemCategory(Integer itemCategoryId)
			throws SessionInternalError {

		ItemTypeBL bl = new ItemTypeBL(itemCategoryId);

		// only root entity can delete global item/category, otherwise owning
		// entity can
		CompanyDAS companyDas = new CompanyDAS();
		if (bl.getEntity().isGlobal()) {
			if (!companyDas.isRoot(getCallerCompanyId())) {
				String[] errors = new String[] { "ItemTypeWS,global,validation.only.root.can.delete.global.category" };
				throw new SessionInternalError(
						"validation.only.root.can.delete.global.category",
						errors);
			}
		}

		if (!getCallerCompanyId().equals(bl.getEntity().getEntityId())) {
			String[] errors = new String[] { "ItemTypeWS,entity,validation.only.owner.can.delete.category" };
			throw new SessionInternalError(
					"validation.only.owner.can.delete.category", errors);
		}

		// todo - catetgory may be in use, prevent deletion

		bl.delete(getCallerId());
	}

	/**
	 * List all item categories for the given entity It includes global and
	 * child entity product categories too
	 * 
	 * @param entityId
	 *            : company id for which item types will be retrieved
	 * @return : List of item categories
	 */
	@Transactional(readOnly = true)
	public ItemTypeWS[] getAllItemCategoriesByEntityId(Integer entityId) {
		ItemTypeBL bl = new ItemTypeBL();
		return bl.getItemCategoriesByEntity(entityId);
	}

	/**
	 * List all items for a given entity
	 */
	@Transactional(readOnly = true)
	public ItemDTOEx[] getAllItemsByEntityId(Integer entityId) {
		ItemBL bl = new ItemBL();
		List<ItemDTOEx> ws = bl.getAllItemsByEntity(entityId);
		return ws.toArray(new ItemDTOEx[ws.size()]);
	}

	/**
	 * Generates invoices for orders not yet invoiced for this user. Optionally
	 * only allow recurring orders to generate invoices. Returns the ids of the
	 * invoices generated.
	 */
	public Integer[] createInvoice(Integer userId, boolean onlyRecurring) {
		return createInvoiceWithDate(userId, null, null, null, onlyRecurring);
	}

	/**
	 * Generates an invoice for a customer using an explicit billing date & due
	 * date period.
	 *
	 * If the billing date is left blank, the invoice will be generated for
	 * today.
	 *
	 * If the due date period unit or value is left blank, then the due date
	 * will be calculated from the order period, or from the customer due date
	 * period if set.
	 *
	 * @param userId
	 *            user id to generate an invoice for.
	 * @param billingDate
	 *            billing date for the invoice generation run
	 * @param dueDatePeriodId
	 *            due date period unit
	 * @param dueDatePeriodValue
	 *            due date period value
	 * @param onlyRecurring
	 *            only include recurring orders? false to include all orders in
	 *            invoice.
	 * @return array of generated invoice ids.
	 */
	public Integer[] createInvoiceWithDate(Integer userId, Date billingDate,
			Integer dueDatePeriodId, Integer dueDatePeriodValue,
			boolean onlyRecurring) {

		UserDTO user = new UserDAS().find(userId);
		BillingProcessConfigurationDTO config = new BillingProcessConfigurationDAS()
				.findByEntity(user.getCompany());

		// Create a mock billing process object, because the method
		// we are calling was meant to be called by the billing process.
		BillingProcessDTO billingProcess = new BillingProcessDTO();
		billingProcess.setId(0);
		billingProcess.setEntity(user.getCompany());
		billingProcess.setBillingDate(billingDate != null ? billingDate
				: new Date());
		billingProcess.setIsReview(0);
		billingProcess.setRetriesToDo(0);

		// optional target due date
		TimePeriod dueDatePeriod = null;
		if (dueDatePeriodId != null && dueDatePeriodValue != null) {
			dueDatePeriod = new TimePeriod();
			dueDatePeriod.setUnitId(dueDatePeriodId);
			dueDatePeriod.setValue(dueDatePeriodValue);
			LOG.debug("Using provided due date %s", dueDatePeriod);
		}

		// generate invoices
		InvoiceDTO[] invoices = new BillingProcessBL().generateInvoice(
				billingProcess, dueDatePeriod, user, false, onlyRecurring,
				getCallerId());
		// generate invoices should return an empty array instead of null... bad
		// design :(
		if (invoices == null)
			return new Integer[0];

		// build the list of generated ID's and return
		List<Integer> invoiceIds = new ArrayList<Integer>(invoices.length);
		for (InvoiceDTO invoice : invoices) {
			invoiceIds.add(invoice.getId());
		}
		return invoiceIds.toArray(new Integer[invoiceIds.size()]);
	}

	public Integer applyOrderToInvoice(Integer orderId, InvoiceWS invoiceWs) {
		if (orderId == null)
			throw new SessionInternalError("Order id cannot be null.");

		// validate order to be processed
		OrderDTO order = new OrderDAS().find(orderId);
		if (order == null
				|| !OrderStatusFlag.INVOICE.equals(order.getOrderStatus()
						.getOrderStatusFlag())) {
			LOG.debug("Order must exist and be active to generate an invoice.");
			return null;
		}

		// create an invoice template that contains the meta field values
		NewInvoiceContext template = new NewInvoiceContext();
		MetaFieldBL.fillMetaFieldsFromWS(getCallerCompanyId(), template,
				invoiceWs.getMetaFields());

		LOG.debug("Updating invoice with order: %s", orderId);
		LOG.debug("Invoice WS: %s", invoiceWs);
		LOG.debug("Invoice template fields: %s", template.getMetaFields());

		// update the invoice
		try {
			BillingProcessBL process = new BillingProcessBL();
			InvoiceDTO invoice = process.generateInvoice(order.getId(),
					invoiceWs.getId(), template, getCallerId());
			return invoice != null ? invoice.getId() : null;

		} catch (SessionInternalError e) {
			throw e;
		} catch (Exception e) {
			LOG.debug("apply order to invoice. ", e);
			throw new SessionInternalError(
					"Error while generating a new invoice", e);
		}
	}

	/**
	 * Generates a new invoice for an order, or adds the order to an existing
	 * invoice.
	 *
	 * @param orderId
	 *            order id to generate an invoice for
	 * @param invoiceId
	 *            optional invoice id to add the order to. If null, a new
	 *            invoice will be created.
	 * @return id of generated invoice, null if no invoice generated.
	 * @throws SessionInternalError
	 *             if user id or order id is null.
	 */
	public Integer createInvoiceFromOrder(Integer orderId, Integer invoiceId)
			throws SessionInternalError {
		if (orderId == null)
			throw new SessionInternalError("Order id cannot be null.");

		// validate order to be processed
		OrderDTO order = new OrderDAS().find(orderId);
		if (order == null
				|| !OrderStatusFlag.INVOICE.equals(order.getOrderStatus()
						.getOrderStatusFlag())) {
			LOG.debug("Order must exist and be active to generate an invoice.");
			return null;
		}
		// Set the pessimistic lock (select for update) for a order DTO to
		// ensure no any concurrent process can update it while creating new
		// invoice
		new OrderDAS().getHibernateTemplate().lock(order, LockMode.UPGRADE);
		// create new invoice, or add to an existing invoice
		InvoiceDTO invoice;
		if (invoiceId == null) {
			LOG.debug("Creating a new invoice for order %s", order.getId());
			invoice = doCreateInvoice(order.getId());
			if (null == invoice) {
				throw new SessionInternalError(
						"Invoice could not be generated. The purchase order may not have any applicable periods to be invoiced.");
			}
		} else {
			LOG.debug("Adding order %s to invoice %s", order.getId(), invoiceId);
			IBillingProcessSessionBean process = (IBillingProcessSessionBean) Context
					.getBean(Context.Name.BILLING_PROCESS_SESSION);
			invoice = process.generateInvoice(order.getId(), invoiceId, null,
					getCallerId());
		}

		return invoice == null ? null : invoice.getId();
	}

	/*
	 * USERS
	 */
	/**
	 * Creates a new user. The user to be created has to be of the roles
	 * customer or partner. The username has to be unique, otherwise the
	 * creating won't go through. If that is the case, the return value will be
	 * null.
	 * 
	 * @param newUser
	 *            The user object with all the information of the new user. If
	 *            contact or credit card information are present, they will be
	 *            included in the creation although they are not mandatory.
	 * @return The id of the new user, or null if non was created
	 */
	public Integer createUser(UserWS newUser) throws SessionInternalError {
		return createUserWithCompanyId(newUser, getCallerCompanyId());
	}

	public Integer createUserWithCompanyId(UserWS newUser, Integer companyId)
			throws SessionInternalError {

		LOG.debug("Entering createUser()");

		newUser.setUserId(0);
		Integer entityId = companyId;
		UserBL bl = new UserBL();
		UserDTO parentUser = new UserDAS().findNow(newUser.getParentId());
		Integer parentCompanyId = new CompanyDAS().getParentCompanyId(entityId);
		int parentChildCustomerPreference = PreferenceBL.getPreferenceValueAsIntegerOrZero(companyId, Constants.PREFERENCE_PARENT_CHILD_CUSTOMER_HIERARCHY);
		if (parentChildCustomerPreference == 1) {
			if (!(bl.exists(newUser.getParentId(), entityId) || (parentCompanyId != null && bl.exists(newUser.getParentId(), parentCompanyId)))) {
				throw new SessionInternalError(
						"There doesn't exist a parent with the supplied id."
								+ newUser.getParentId(),
						new String[]{"UserWS,parentId,validation.error.parent.does.not.exist"});
			}
		} else if (!bl.exists(newUser.getParentId(), entityId)) {
			throw new SessionInternalError(
					"There doesn't exist a parent with the supplied id."
							+ newUser.getParentId(),
					new String[]{"UserWS,parentId,validation.error.parent.does.not.exist"});
		}

		if (null != parentUser && parentUser.getCustomer().getIsParent() == 0) {
			throw new SessionInternalError(
					"The selected parent id {0} is not set to allow sub-accounts."
							+ newUser.getParentId(),
					new String[] { "UserWS,parentId,validation.error.not.allowed.parentId,"
							+ newUser.getParentId() });
		}

		LOG.debug("Checking if user with name already exist");

        if (bl.exists(newUser.getUserName(), entityId)) {
            throw new SessionInternalError("User already exists with username " + newUser.getUserName(),
                                            new String[] { "UserWS,userName,validation.error.user.already.exists" });
        }

        LOG.debug("Checking if user with email already exist");
        //forcing unique email in the systems for all users
        if (forceUniqueEmails(entityId)) {
            List<String> emails = new ArrayList<String>();

            if (0 != newUser.getMainRoleId().compareTo(Constants.TYPE_CUSTOMER)) {
                MetaFieldDAS metaFieldDAS = new MetaFieldDAS();

				if(newUser.getMetaFields() != null) {
					for (MetaFieldValueWS value : newUser.getMetaFields()) {
						if (null != value.getFieldName() && null != value.getGroupId()) {
							MetaField field = metaFieldDAS.getFieldByNameAndGroup(
									entityId, value.getFieldName(), value.getGroupId());

							if (field.getFieldUsage() == MetaFieldType.EMAIL) {
								emails.add(value.getStringValue());
							}
						}
					}
				}
            } else if (null != newUser.getContact()) {
                emails.add(newUser.getContact().getEmail());
            }

            for (String email : emails) {
                if (new UserBL().findUsersByEmail(email, entityId).size() > 0) {
                    throw new SessionInternalError("User already exists with email " + email,
                            new String[]{"ContactWS,email,validation.error.email.already.exists"});
                }
            }
        }
        
        if (0 == newUser.getMainRoleId().compareTo(Constants.TYPE_CUSTOMER)){
            if(null == newUser.getAccountTypeId()){
                throw new SessionInternalError("Customer users must have account type id defined.",
                        new String[] {"UserWS,accountTypeId,validation.error.account.type.not.defined"});
            }
            
            AccountTypeDTO accountType = new AccountTypeDAS()
                    .find(newUser.getAccountTypeId(), entityId);

            if(null == accountType){
                throw new SessionInternalError("Customer users must have account type that exists.",
                        new String[] {"UserWS,accountTypeId,validation.error.account.type.not.exist"});
            }
        }

		// The Payment Instruments must be unique
		validateUniquePaymentInstruments(newUser.getPaymentInstruments());

		ContactBL cBl = new ContactBL();
		UserDTOEx dto = new UserDTOEx(newUser, entityId);

		Integer userId;
		try {
			LOG.debug("Creating user");
			userId = bl.create(dto, getCallerId());
			// if the user is not customer do not create
			// a contatct for that user
			if (newUser.getContact() != null
					&& 0 != newUser.getMainRoleId().compareTo(
							Constants.TYPE_CUSTOMER)) {
				newUser.getContact().setId(0);
				cBl.createForUser(new ContactDTOEx(newUser.getContact()),
						userId, getCallerId());
			}

			if (newUser.getCustomerNotes() != null) {
				for (CustomerNoteWS customerNotes : JArrays.toArrayList(newUser
						.getCustomerNotes())) {
					customerNotes.setCustomerId(UserBL.getUserEntity(userId)
							.getCustomer().getId());
					createCustomerNote(customerNotes);
				}
			}
			bl.createCredentialsFromDTO(dto);
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}
		return userId;
	}

	/**
	 * Creates a reseller customer with company information
	 * 
	 * @param newUser
	 *            : an instance of UserWS
	 * @return : id of the created reseller
	 */
	public Integer createReseller(UserWS newUser, Integer parentId) {
		newUser.setUserId(0);
		Integer entityId = parentId;
		UserBL bl = new UserBL();

		ContactBL cBl = new ContactBL();
		UserDTOEx dto = new UserDTOEx(newUser, entityId);
		Integer userId;
		try {
			userId = bl.create(dto, getCallerId());
			// if the user is not customer do not create
			// a contatct for that user
			if (newUser.getContact() != null
					&& 0 != newUser.getMainRoleId().compareTo(
							Constants.TYPE_CUSTOMER)) {
				newUser.getContact().setId(0);
				cBl.createForUser(new ContactDTOEx(newUser.getContact()),
						userId, getCallerId());
			}
			bl.createCredentialsFromDTO(dto);
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}
		return userId;
	}

	private boolean forceUniqueEmails(Integer entityId) {
		int preferenceForceUniqueEmails = 0;
		try {
			preferenceForceUniqueEmails = PreferenceBL
					.getPreferenceValueAsIntegerOrZero(entityId,
							Constants.PREFERENCE_FORCE_UNIQUE_EMAILS);
		} catch (EmptyResultDataAccessException e) {
			// default will be used
		}
		return 1 == preferenceForceUniqueEmails;
	}

	public void deleteUser(Integer userId) throws SessionInternalError {
		UserBL bl = new UserBL();
		if (getCallerId().equals(userId)) {
			throw new SessionInternalError("User cannot delete itself");
		}
		Integer executorId = getCallerId();
		bl.set(userId);
		bl.delete(executorId);
	}

    public void initiateTermination(Integer userId, String reasonCode, Date terminationDate) throws SessionInternalError {
        IEDITransactionBean ediTransactionBean = Context.getBean(Context.Name.EDI_TRANSACTION_SESSION);
        ediTransactionBean.initiateTermination(userId, reasonCode, terminationDate);
    }


	/**
	 * Returns true if a user exists with the given user name, false if not.
	 *
	 * @param userName
	 *            user name
	 * @return true if user exists, false if not.
	 */
	public boolean userExistsWithName(String userName) {
		return new UserBL().exists(userName, getCallerCompanyId());
	}

	/**
	 * Returns true if a user with the given ID exists and is accessible by the
	 * caller, false if not.
	 *
	 * @param userId
	 *            user id
	 * @return true if user exists, false if not.
	 */
	public boolean userExistsWithId(Integer userId) {
		return new UserBL().exists(userId, getCallerCompanyId());
	}

	public void updateUserContact(Integer userId, ContactWS contact)
			throws SessionInternalError {
		// todo: support multiple WS method param validations through
		// WSSecurityMethodMapper
		UserBL userBL = new UserBL(userId);
		Integer entityId = getCallerCompanyId();

		if (forceUniqueEmails(entityId) && null != contact.getEmail()
				&& (userBL.isEmailUsedByOthers(contact.getEmail()))) {
			throw new SessionInternalError(
					"User already exists with email " + contact.getEmail(),
					new String[] { "ContactWS,email,validation.error.email.already.exists" });
		}

		// update the contact
		ContactBL cBl = new ContactBL();
		cBl.updateForUser(new ContactDTOEx(contact), userId, getCallerId());
	}

	/**
	 * @param user
	 */
	public void updateUser(UserWS user) {
		updateUserWithCompanyId(user, getCallerCompanyId());
	}

	public void updateUserWithCompanyId(UserWS user, Integer entityId)
			throws SessionInternalError {

		// TODO commenting validate user for create/edit customer grails impl. -
		// vikasb
		// validateUser(user);

		UserBL bl = new UserBL(user.getUserId());

		// get the entity
		Integer executorId = getCallerId();

		// convert user WS to a DTO that includes customer data
		UserDTOEx dto = new UserDTOEx(user, entityId);
		if (dto.getCustomer() != null) {
			if (dto.getCustomer().getParent() != null
					&& dto.getCustomer().getParent().getId() == dto.getId()) {
				throw new SessionInternalError(
						"The parent id cannot be the same as user id for this customer.",
						new String[] { "UserWS,parentId,validation.error.parent.customer.id.same" });
			}

			if (dto.getCustomer().getParent() != null) {

				UserDTO parentUser = new UserDAS().findNow(dto.getCustomer()
						.getParent().getId());
				if (null != parentUser
						&& parentUser.getCustomer().getIsParent() == 0) {
					throw new SessionInternalError(
							"The selected parent id {0} is not set to allow sub-accounts."
									+ dto.getCustomer().getParent().getId(),
							new String[] { "UserWS,parentId,validation.error.not.allowed.parentId,"
									+ dto.getCustomer().getParent().getId() });
				}
			}

			if (dto.getCustomer().getParent() != null) {
				if (!new UserBL().okToAddAsParent(dto.getId(), dto
						.getCustomer().getParent().getId())) {
					throw new SessionInternalError(
							"Cannot set the parent to the Customer's own child in account hierarchy.",
							new String[] { "UserWS,parentId,customer.error.hierachy" });
				}
			}
		}

		// The Payment Instruments must be unique
		validateUniquePaymentInstruments(user.getPaymentInstruments());

		// update the user info and customer data
		bl.getEntity().touch();

		String changedPassword = user.getPassword();
		if (null != changedPassword && !changedPassword.trim().isEmpty()) {

			Integer methodId = bl.getEntity().getEncryptionScheme();
			boolean matches = JBCrypto.passwordsMatch(methodId, bl.getEntity()
					.getPassword(), changedPassword);

			if (matches) {
				// password is not changed and in attempts to update password we
				// must use different
				throw new SessionInternalError(
						"The new password must be different from the previous one",
						new String[] { "UserWS,password,validation.error.password.same.as.previous" });
			} else {
				// password changed so do additional validation on the new
				// password

				if (!user.getPassword().matches(
						Constants.PASSWORD_PATTERN_4_UNIQUE_CLASSES)) {
					throw new SessionInternalError(
							"User's password must match required conditions.",
							new String[] { "UserWS,password,validation.error.password.size,8,40" });
				}
			}
		}

		bl.update(executorId, dto);

		// now update the contact info
		if (user.getContact() != null
				&& 0 != user.getMainRoleId().compareTo(Constants.TYPE_CUSTOMER)) {
			String email = user.getContact().getEmail();
			if (forceUniqueEmails(entityId) && null != email
					&& (bl.isEmailUsedByOthers(email))) {
				throw new SessionInternalError(
						"User already exists with email " + email,
						new String[] { "ContactWS,email,validation.error.email.already.exists" });
			}

			ContactDTOEx contact = new ContactDTOEx(user.getContact());
			new ContactBL().updateForUser(contact, user.getUserId(),
					getCallerId());
		}
		for (CustomerNoteWS customerNotes : JArrays.toArrayList(user
				.getCustomerNotes())) {
			customerNotes.setCustomerId(UserBL.getUserEntity(user.getUserId())
					.getCustomer().getId());
			createCustomerNote(customerNotes);
		}
	}

	/**
	 * Retrieves a user with its contact and credit card information.
	 * 
	 * @param userId
	 *            The id of the user to be returned
	 */
	@Transactional(readOnly = true)
	public UserWS getUserWS(Integer userId) throws SessionInternalError {
		UserBL bl = new UserBL(userId);
		return bl.getUserWS();
	}

	/**
	 * Retrieves all the contacts of a user
	 * 
	 * @param userId
	 *            The id of the user to be returned
	 */
	@Transactional(readOnly = true)
	public ContactWS[] getUserContactsWS(Integer userId)
			throws SessionInternalError {
		ContactWS[] dtos = null;
		ContactBL contact = new ContactBL();
		List result = contact.getAll(userId);
		dtos = new ContactWS[result.size()];
		for (int f = 0; f < result.size(); f++) {
			dtos[f] = ContactBL.getContactWS((ContactDTOEx) result.get(f));
		}

		return dtos;
	}

	/**
	 * Retrieves the user id for the given username
	 */
	@Transactional(readOnly = true)
	public Integer getUserId(String username) throws SessionInternalError {
		if (username == null || username.trim().isEmpty()) {
			return null;
		}

		UserDAS das = new UserDAS();
		UserDTO dto = das.findByUserName(username, getCallerCompanyId());
		if (dto == null) {
			return null;
		} else {
			return dto.getId();
		}
	}

	/**
	 * Retrieves user by the user's email. This is only valid if Jbilling is
	 * configured to force unique emails per user/customers in the company. If
	 * unique emails are not forced then an exception is thrown and in such case
	 * this method should not be used.
	 *
	 * @param email
	 *            - email of the user
	 * @return ID of the user with given email
	 * @throws SessionInternalError
	 */
	@Transactional(readOnly = true)
	public Integer getUserIdByEmail(String email) throws SessionInternalError {
		if (null == email || 0 == email.trim().length()) {
			throw new SessionInternalError(
					"User email can not be null or empty");
		}

		Integer entityId = getCallerCompanyId();

		if (forceUniqueEmails(entityId)) {
			UserBL userDas = new UserBL();
			List<UserDTO> users = userDas.findUsersByEmail(email, entityId);
			if (null == users || 0 == users.size()) {
				return null;
			} else if (1 == users.size()) {
				return users.iterator().next().getId();
			} else {
				throw new SessionInternalError(
						"Multiple users found with the same email.");
			}
		} else {
			throw new SessionInternalError(
					"Not configured to force unique emails per users.");
		}
	}

	/**
	 * Retrieves an array of users in the required status
	 */
	@Transactional(readOnly = true)
	public Integer[] getUsersInStatus(Integer statusId)
			throws SessionInternalError {
		return getUsersByStatus(statusId, true);
	}

	/**
	 * Retrieves an array of users in the required status
	 */
	@Transactional(readOnly = true)
	public Integer[] getUsersNotInStatus(Integer statusId)
			throws SessionInternalError {
		return getUsersByStatus(statusId, false);
	}

	/**
	 * Retrieves an array of users in the required status
	 */
	@Transactional(readOnly = true)
	public Integer[] getUsersByStatus(Integer statusId, boolean in)
			throws SessionInternalError {
		try {
			UserBL bl = new UserBL();
			CachedRowSet users = bl.getByStatus(getCallerCompanyId(), statusId,
					in);
			LOG.debug("got collection. Now converting");
			Integer[] ret = new Integer[users.size()];
			int f = 0;
			while (users.next()) {
				ret[f] = users.getInt(1);
				f++;
			}
			users.close();
			return ret;
		} catch (Exception e) { // can't remove because of SQLException :(
			throw new SessionInternalError(e);
		}
	}

	/**
	 * Creates a user, then an order for it, an invoice out the order and tries
	 * the invoice to be paid by an online payment This is ... the mega call !!!
	 */
	public CreateResponseWS create(UserWS user, OrderWS order,
			OrderChangeWS[] orderChanges) throws SessionInternalError {

		CreateResponseWS retValue = new CreateResponseWS();

		// the user first
		final Integer userId = createUser(user);
		retValue.setUserId(userId);

		if (userId == null) {
			return retValue;
		}

		// the order and (if needed) invoice
		order.setUserId(userId);
		validateLines(order);

		/*
		 * #7899 - The order being created is evaluated for subscription lines,
		 * if order is containing any subscription products then internal
		 * account and order are created for each subscription line, if all the
		 * lines are subscription lines then main order is not created and we
		 * get only internal accounts and orders
		 */
		List<OrderChangeWS> changes = JArrays.toArrayList(orderChanges);
		createSubscriptionAccountAndOrder(order.getUserId(), order, false,
				changes);
		orderChanges = changes != null ? changes
				.toArray(new OrderChangeWS[changes.size()]) : null;

		Integer orderId = null;
		InvoiceDTO invoice = null;
		if (order.getOrderLines().length > 0) {
			orderId = doCreateOrder(order, orderChanges, true).getId();
			invoice = doCreateInvoice(orderId);
		}

		retValue.setOrderId(orderId);

		if (invoice != null) {
			retValue.setInvoiceId(invoice.getId());

			// find credit card
			PaymentInformationDTO creditCardInstrument = getCreditCard(userId);

			// the payment, if we have a credit card
			if (creditCardInstrument != null) {
				PaymentDTOEx payment = doPayInvoice(invoice,
						creditCardInstrument);
				PaymentAuthorizationDTOEx result = null;
				if (payment != null) {
					result = new PaymentAuthorizationDTOEx(payment
							.getAuthorization().getOldDTO());
					result.setResult(new Integer(payment.getPaymentResult()
							.getId()).equals(Constants.RESULT_OK));
				}
				retValue.setPaymentResult(result);
				retValue.setPaymentId(null != payment ? payment.getId() : null);
			}
		} else {
			throw new SessionInternalError("Invoice expected for order: "
					+ orderId);
		}

		return retValue;
	}

	@Transactional(readOnly = true)
	public PartnerWS getPartner(Integer partnerId) throws SessionInternalError {
		IUserSessionBean userSession = Context
				.getBean(Context.Name.USER_SESSION);
		PartnerDTO dto = userSession.getPartnerDTO(partnerId);

		return PartnerBL.getWS(dto);
	}

	public Integer createPartner(UserWS newUser, PartnerWS partner)
			throws SessionInternalError {
		UserBL bl = new UserBL();
		newUser.setUserId(0);
		Integer entityId = getCallerCompanyId();

		if (bl.exists(newUser.getUserName(), entityId)) {
			throw new SessionInternalError(
					"User already exists with username "
							+ newUser.getUserName(),
					new String[] { "UserWS,userName,validation.error.user.already.exists" });
		}

		PartnerDTO partnerDto = PartnerBL.getPartnerDTO(partner);
		MetaFieldBL.fillMetaFieldsFromWS(entityId, partnerDto,
				newUser.getMetaFields());

		UserDTOEx dto = new UserDTOEx(newUser, entityId);
		dto.setPartner(partnerDto);

		Integer userId = bl.create(dto, getCallerId());

		ContactBL cBl = new ContactBL();
		if (newUser.getContact() != null) {
			newUser.getContact().setId(0);
			cBl.createForUser(new ContactDTOEx(newUser.getContact()), userId,
					getCallerId());
		}

		bl.createCredentialsFromDTO(dto);
		return bl.getDto().getPartner().getId();

	}

	public void updatePartner(UserWS user, PartnerWS partner)
			throws SessionInternalError {
		IUserSessionBean userSession = Context
				.getBean(Context.Name.USER_SESSION);
		Integer entityId = getCallerCompanyId();

		if (user != null) {
			UserDTOEx userDto = new UserDTOEx(user, entityId);
			userSession.update(getCallerId(), userDto);
		}

		if (partner != null) {
			PartnerDTO partnerDto = PartnerBL.getPartnerDTO(partner);

			if (user != null) {
				MetaFieldBL.fillMetaFieldsFromWS(entityId, partnerDto,
						user.getMetaFields());
			}

			userSession.updatePartner(getCallerId(), partnerDto);
		}
	}

	public void deletePartner(Integer partnerId) throws SessionInternalError {
		PartnerBL bl = new PartnerBL(partnerId);
		bl.delete(getCallerId());
	}

	/**
	 * Return the UserCode objects linked to a user.
	 *
	 * @param userId
	 * @return
	 * @throws SessionInternalError
	 */
	@Transactional(readOnly = true)
    public UserCodeWS[] getUserCodesForUser(Integer userId) throws SessionInternalError {
		List<UserCodeDTO> userCodes = new UserBL().getUserCodesForUser(userId);
		return UserBL.convertUserCodeToWS(userCodes);
	}

	/**
	 * Create a UserCode
	 * 
	 * @param userCode
	 * @return
	 * @throws SessionInternalError
	 */
	public Integer createUserCode(UserCodeWS userCode)
			throws SessionInternalError {
		UserBL userBL = new UserBL();
		return userBL.createUserCode(userCode);
	}

	/**
	 * Update a UserCode
	 * 
	 * @param userCode
	 * @return
	 * @throws SessionInternalError
	 */
	public void updateUserCode(UserCodeWS userCode) throws SessionInternalError {
		UserBL userBL = new UserBL();
		userBL.updateUserCode(userCode);
	}

	/**
	 * Return ids of objects of the specified type linked to the User Code.
	 *
	 * @param userCode
	 * @return
	 */
	private Integer[] getAssociatedObjectsByUserCodeAndType(String userCode,
			UserCodeObjectType objectType) {
		List<Integer> objectIds = new UserBL()
				.getAssociatedObjectsByUserCodeAndType(userCode, objectType);
		return objectIds.toArray(new Integer[objectIds.size()]);
	}

	/**
	 * Return ids of customers linked to the User Code.
	 *
	 * @param userCode
	 * @return
	 */
	@Transactional(readOnly = true)
	public Integer[] getCustomersByUserCode(String userCode)
			throws SessionInternalError {
		return getAssociatedObjectsByUserCodeAndType(userCode,
                UserCodeObjectType.CUSTOMER);
	}

	/**
	 * Return ids of orders linked to the User Code.
	 *
	 * @param userCode
	 * @return
	 */
	@Transactional(readOnly = true)
	public Integer[] getOrdersByUserCode(String userCode)
			throws SessionInternalError {
		return getAssociatedObjectsByUserCodeAndType(userCode,
                UserCodeObjectType.ORDER);
	}

	/**
	 * Return ids of objects of the specified type linked to the user.
	 *
	 * @param userId
	 * @param objectType
	 * @return
	 */
	private Integer[] getAssociatedObjectsByUserAndType(int userId,
			UserCodeObjectType objectType) throws SessionInternalError {
		List<Integer> objectIds = new UserBL()
				.getAssociatedObjectsByUserAndType(userId, objectType);
		return objectIds.toArray(new Integer[objectIds.size()]);
	}

	/**
	 * Return ids of customers linked to the user through a user code.
	 *
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
    public Integer[] getCustomersLinkedToUser(Integer userId) throws SessionInternalError {
        return getAssociatedObjectsByUserAndType(userId, UserCodeObjectType.CUSTOMER);
	}

	/**
	 * Return ids of orders linked to the user through a user code.
	 *
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
    public Integer[] getOrdersLinkedToUser(Integer userId) throws SessionInternalError {
        return getAssociatedObjectsByUserAndType(userId, UserCodeObjectType.ORDER);
	}

	/**
	 * Retrieves all item categories that are being used for specific partner
	 * Item category is considered to belong to a partner it it has at least one
	 * item that belongs to the provided partner The partner belonging is
	 * determined by a metafield for each item. If the metafield value matches
	 * the provided partner name the item is considered to be used by the
	 * partner
	 *
	 * @param partner
	 * @param parentCategoriesOnly
	 *            - if set to true it will take into consideration the
	 *            parent-child category relation and will only include
	 *            parent(top) categories
	 * @return
	 */
	@Transactional(readOnly = true)
	public ItemTypeWS[] getItemCategoriesByPartner(String partner,
			boolean parentCategoriesOnly) {

		if (null == partner) {
			throw new SessionInternalError(
					"Null value for partner is not allowed");
		}

		return new ItemTypeBL().getItemCategoriesByPartner(partner,
                parentCategoriesOnly);
	}

	/**
	 * Uses the parent-child item category relation to determine the child item
	 * types from the provided parent item Type Id
	 *
	 * @param itemTypeId
	 *            - id of the parent category
	 * @return Child categories for the provided parent categories
	 */
	@Transactional(readOnly = true)
	public ItemTypeWS[] getChildItemCategories(Integer itemTypeId) {

		if (null == itemTypeId) {
			throw new SessionInternalError(
					"Null value for itemTypeId is not allowed");
		}

		if (null == (new ItemTypeDAS()).findNow(itemTypeId)) {
			LOG.debug("Category with the given ID does not exist");
			return null;
		}

		return new ItemTypeBL().getChildItemCategories(itemTypeId);
	}

	/**
	 * Retrieves addon item defined for the provided itemId Item is considered
	 * to be an Addon item if it belongs to a category: ADDON-productCode where
	 * the product code is retrieved from the provided itemId
	 *
	 * @param itemId
	 * @return
	 */
	@Transactional(readOnly = true)
	public ItemDTOEx[] getAddonItems(Integer itemId) {

		if (null == itemId) {
			throw new SessionInternalError(
					"Null value for itemId is not allowed");
		}

		ItemDTO item = new ItemDAS().findNow(itemId);
		if (null == item) {
			throw new SessionInternalError("Item with given id does not exist");
		}

		String addonCategoryName = "ADDON-" + item.getInternalNumber();

		ItemTypeDTO addonCategory = new ItemTypeDAS().findByDescription(
				getCallerCompanyId(), addonCategoryName);

		if (null == addonCategory) {
			LOG.debug("Addon category with description " + addonCategoryName
                    + " does not exist");
			return null;
		}

		return new ItemBL().getAllItemsByType(addonCategory.getId(),
				getCallerCompanyId());
	}

	/**
	 * Pays given invoice, using the first credit card available for invoice'd
	 * user.
	 *
	 * @return <code>null</code> if invoice has not positive balance, or if user
	 *         does not have credit card
	 * @return resulting authorization record. The payment itself can be found
	 *         by calling getLatestPayment
	 */
	// this method does not start a transaction since transaction
	// during payment processing is managed manually
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public PaymentAuthorizationDTOEx payInvoice(Integer invoiceId)
			throws SessionInternalError {
		LOG.debug("In payInvoice..");
		if (invoiceId == null) {
			LOG.debug("Invoice id null.");
			throw new SessionInternalError("Can not pay null invoice");
		}

		final InvoiceDTO invoice = findInvoice(invoiceId);
		if (null == invoice) {
			LOG.debug("No invoice found invoice id: %d", invoiceId);
			throw new SessionInternalError("Invoice not found!!");
		}
		if (!getCallerCompanyId().equals(
				invoice.getBaseUser().getEntity().getId())) {
			throw new SessionInternalError(
					"Applying invoices from another entity not supported!!");
		}
		PaymentInformationDTO creditCardInstrument = getCreditCard(invoice
                .getBaseUser().getUserId());
		if (creditCardInstrument == null) {
			return null;
		}

		PaymentDTOEx payment = doPayInvoice(invoice, creditCardInstrument);

		PaymentAuthorizationDTOEx result = null;
		if (payment != null) {
			result = new PaymentAuthorizationDTOEx(payment.getAuthorization()
					.getOldDTO());
			result.setResult(new Integer(payment.getPaymentResult().getId())
					.equals(Constants.RESULT_OK));
		}

		return result;
	}

	/*
	 * ORDERS
	 */
	/**
	 * @return the information of the payment aurhotization, or NULL if the user
	 *         does not have a credit card
	 */
	public PaymentAuthorizationDTOEx createOrderPreAuthorize(OrderWS order,
			OrderChangeWS[] orderChanges) throws SessionInternalError {

		PaymentAuthorizationDTOEx retValue = null;
		// start by creating the order. It'll do the checks as well
		Integer orderId = createOrder(order, orderChanges);

		Integer userId = order.getUserId();
		PaymentInformationDTO cc = getCreditCard(userId);
		UserBL user = new UserBL();
		Integer entityId = user.getEntityId(userId);
		OrderDAS das = new OrderDAS();
		OrderDTO dbOrder = das.find(orderId);
		if (cc != null) {
			PaymentInformationBL piBl = new PaymentInformationBL();

			try {
				retValue = piBl.validatePreAuthorization(entityId, userId, cc,
						dbOrder.getTotal(), dbOrder.getCurrencyId(),
						getCallerId());
			} catch (PluggableTaskException e) {
				throw new SessionInternalError("doing validation",
						WebServicesSessionSpringBean.class, e);
			}
		}

		// order has been pre-authorized. Informing the tasks
		if (retValue != null && retValue.getResult().equals(Boolean.TRUE)) {
			EventManager
					.process(new OrderPreAuthorizedEvent(entityId, dbOrder));
		}

		return retValue;
	}

	/**
	 * When a plan order is created, multiple orders can get created and these
	 * are linked together by primaryOrderId. This is a Function that returns
	 * all the linked orders if the primary order id is provided.
	 * 
	 * @param primaryOrderId
	 * @return List<OrderWS> - The list of linked orders including the primary
	 *         order itself.
	 */
	@Transactional(readOnly = true)
	public OrderWS[] getLinkedOrders(Integer primaryOrderId)
			throws SessionInternalError {
		List<OrderDTO> linkedOrders = new OrderDAS()
				.findByPrimaryOrderId(primaryOrderId);
		if (null == linkedOrders) {
			// If no linked orders are found, return an empty array.
			return new OrderWS[0];
		}

		List<OrderWS> orders = new ArrayList<OrderWS>();

		OrderBL bl = null;
		for (OrderDTO dto : linkedOrders) {
			bl = new OrderBL(dto);
			orders.add(bl.getWS(getCallerLanguageId()));
		}

		return orders.toArray(new OrderWS[orders.size()]);
	}

	public Integer createOrder(OrderWS order, OrderChangeWS[] orderChanges)
			throws SessionInternalError {
		setOrderOnOrderChanges(order, orderChanges);
		validateLines(order);
		/*
		 * #7899 - The order being created is evaluated for subscription lines,
		 * if order is containing any subscription products then internal
		 * account and order are created for each subscription line
		 */
		List<OrderChangeWS> changes = JArrays.toArrayList(orderChanges);
		createSubscriptionAccountAndOrder(order.getUserId(), order, false,
				changes);
		orderChanges = changes != null ? changes
				.toArray(new OrderChangeWS[changes.size()]) : null;

		// If order only contained subscription lines then now order do not have
		// any lines left, no need to create order with no lines
		if ((order.getOrderLines() != null && order.getOrderLines().length > 0)
				|| (null != orderChanges && orderChanges.length > 0)) {
			OrderWS ows = doCreateOrder(order, orderChanges, true);
			Integer orderId = ows != null ? ows.getId() : null;
			return orderId;
		}

		return null;
	}

	/**
	 * Update the given order, or create it if it doesn't already exist.
	 *
	 * @param order
	 *            order to update or create
	 * @return order id
	 * @throws SessionInternalError
	 */
	public Integer createUpdateOrder(OrderWS order, OrderChangeWS[] orderChanges)
			throws SessionInternalError {
		IOrderSessionBean orderSession = Context
				.getBean(Context.Name.ORDER_SESSION);
		setOrderOnOrderChanges(order, orderChanges);
		validateOrder(order, orderChanges);
		validateLines(order);

		if (order.getId() != null) {
			validateActiveSinceDate(order);
		}

		/*
		 * #7899 - The order being created is evaluated for subscription lines,
		 * if order is containing any subscription products then internal
		 * account and order are created for each subscription line
		 */
		List<OrderChangeWS> changes = JArrays.toArrayList(orderChanges);
		createSubscriptionAccountAndOrder(order.getUserId(), order, false,
				changes);
		orderChanges = changes != null ? changes
				.toArray(new OrderChangeWS[changes.size()]) : null;

		// if order has some lines left (that are non subscription) then create
		// the order
		if (order.getOrderLines().length > 0 || orderChanges.length > 0) {
			// do some transformation from WS to DTO
			Map<OrderWS, OrderDTO> wsToDtoOrdersMap = new HashMap<OrderWS, OrderDTO>();
			Map<OrderLineWS, OrderLineDTO> wsToDtoLinesMap = new HashMap<OrderLineWS, OrderLineDTO>();
			OrderBL orderBL = new OrderBL();
			OrderDTO dto = orderBL.getDTO(order, wsToDtoOrdersMap,
					wsToDtoLinesMap);
			OrderDTO rootOrder = OrderHelper.findRootOrderIfPossible(dto);
			List<OrderChangeDTO> changeDtos = new LinkedList<OrderChangeDTO>();
			List<Integer> deletedChanges = new LinkedList<Integer>();
			convertOrderChangeWsToDto(orderChanges, changeDtos, deletedChanges,
					wsToDtoOrdersMap, wsToDtoLinesMap);
			validateDiscountLines(rootOrder, changeDtos);
			Integer rootOrderId = orderSession.createUpdate(
					getCallerCompanyId(), getCallerId(), getCallerLanguageId(),
					rootOrder, changeDtos, deletedChanges);
			return wsToDtoOrdersMap.get(order).getId();
		}

		return null;
	}

    private void setOrderOnOrderChanges(OrderWS order, OrderChangeWS[] orderChanges) {
        for (OrderChangeWS orderChange: orderChanges) {
            if (orderChange.getOrderId() == null && orderChange.getOrderWS() == null) {
                orderChange.setOrderWS(order);
            }
        }
    }

    private void validateUpdateOrder(OrderWS order) {
        //cannot edit FINISHED orders
        if ( null != order.getId() && order.getId().intValue() > 0 ) {

            OrderDTO dbOrder= new OrderDAS().findNow(order.getId());

            if ( dbOrder.getOrderStatus().getOrderStatusFlag().equals(OrderStatusFlag.FINISHED) ) {
                throw new SessionInternalError("An order whose status is FINISHED, is non-editable.",
                        new String[]{"validation.error.finished.order.status"});
            }
        }
    }

    @Transactional( propagation = Propagation.REQUIRED, readOnly = true )
    public OrderWS rateOrder(OrderWS order, OrderChangeWS[] orderChanges)
            throws SessionInternalError {

        return doCreateOrder(order, orderChanges, false);
    }

	public OrderWS[] rateOrders(OrderWS orders[], OrderChangeWS[] orderChanges)
			throws SessionInternalError {

		if (orders == null || orders.length == 0) {
			LOG.debug("Call to rateOrders without orders to rate");
			return null;
		}

		OrderWS retValue[] = new OrderWS[orders.length];
		for (int index = 0; index < orders.length; index++) {
			OrderWS currentOrder = orders[index];
			List<OrderChangeWS> currentOrderChanges = new LinkedList<OrderChangeWS>();
			if (orderChanges != null) {
				LinkedHashSet<OrderWS> currentOrders = OrderHelper
						.findAllChildren(currentOrder);
				currentOrders.add(currentOrder);
				// find order changes for current order
				for (OrderChangeWS orderChange : orderChanges) {
					if (orderChange.getOrderWS() != null) {
						if (currentOrders.contains(orderChange.getOrderWS())) {
							currentOrderChanges.add(orderChange);
						}
					} else if (orderChange.getOrderId() != null) {
						for (OrderWS childOrder : currentOrders) {
							if (orderChange.getOrderId().equals(
									childOrder.getId())) {
								currentOrderChanges.add(orderChange);
							}
						}
					}
				}
			}
			retValue[index] = doCreateOrder(currentOrder,
					currentOrderChanges
							.toArray(new OrderChangeWS[currentOrderChanges
									.size()]), false);
		}
		return retValue;
	}

	/**
	 * Calculate diff in OrderChange objects between two plans for order on
	 * effective date. Difference will be calculated according to rules of
	 * SwapMethod selected
	 *
	 * @param order
	 *            Order to change plan
	 * @param existingPlanItemId
	 *            Existed plan item ID
	 * @param swapPlanItemId
	 *            Target plan item ID
	 * @param method
	 *            Swap method. Possible values: - DEFAULT swap method will
	 *            calculate the order changes as the existing plan was removed
	 *            and the new plan item was added to the order - DIFF swap
	 *            method will calculate the difference between the existing plan
	 *            item and swap plan item and generate order changes only for
	 *            that difference.
	 * @param effectiveDate
	 *            Target date to apply changes
	 * @return array of order changes to be applied to swap existingPlan to
	 *         targetPlan
	 */
	@Override
	public OrderChangeWS[] calculateSwapPlanChanges(OrderWS order,
			Integer existingPlanItemId, Integer swapPlanItemId,
			SwapMethod method, Date effectiveDate) {
		OrderLineWS existedPlanLine = OrderHelper.find(order.getOrderLines(),
				existingPlanItemId);
		if (existedPlanLine == null) {
			return new OrderChangeWS[] {};
		}
		Map<Integer, BigDecimal> itemsQuantityMap = PlanBL
				.calculateItemDiffQuantityForPlans(existingPlanItemId,
						swapPlanItemId);

		List<OrderChangeWS> result = new LinkedList<OrderChangeWS>();
		OrderChangeStatusDTO applyOrderChangeStatus = new OrderChangeStatusDAS()
				.findApplyStatus(getCallerCompanyId());
		OrderChangeTypeWS defaultChangeType = getOrderChangeTypeById(Constants.ORDER_CHANGE_TYPE_DEFAULT);
		for (Integer itemId : itemsQuantityMap.keySet()) {
			BigDecimal diffQuantity = itemsQuantityMap.get(itemId);
			int scale = diffQuantity.scale();
			BigDecimal quantityCoef = BigDecimal.ONE;
			if (itemId.equals(existingPlanItemId)
					|| itemId.equals(swapPlanItemId)) {
				quantityCoef = existedPlanLine.getQuantityAsDecimal();
			}
			BigDecimal subtractQuantity = diffQuantity
					.compareTo(BigDecimal.ZERO) < 0 ? diffQuantity.multiply(
					quantityCoef).setScale(scale, RoundingMode.HALF_UP)
					: BigDecimal.ZERO;
			BigDecimal addQuantity = diffQuantity.compareTo(BigDecimal.ZERO) > 0 ? diffQuantity
					.multiply(quantityCoef).setScale(scale,
							RoundingMode.HALF_UP) : BigDecimal.ZERO;
			OrderLineWS line = OrderHelper.find(order.getOrderLines(), itemId);
			if (line != null && SwapMethod.DEFAULT.equals(method)) {
				addQuantity = addQuantity.add(line.getQuantityAsDecimal().add(
						subtractQuantity)); // subtract quantity is negative
											// number
				subtractQuantity = line.getQuantityAsDecimal().negate();
			}
			if (line != null
					&& subtractQuantity.compareTo(BigDecimal.ZERO) != 0) {
				OrderChangeWS subtractChange = OrderChangeBL.buildFromLine(
						line, order, applyOrderChangeStatus.getId());
				subtractChange.setOrderChangeTypeId(defaultChangeType.getId());
				subtractChange.setQuantityAsDecimal(subtractQuantity);
				subtractChange.setStartDate(effectiveDate);
				result.add(subtractChange);
			}
			if (addQuantity.compareTo(BigDecimal.ZERO) != 0) {
				OrderLineWS lineForAdd = null;
				if (line != null && SwapMethod.DIFF.equals(method)) {
					lineForAdd = line;
				}
				if (lineForAdd == null) {
					lineForAdd = new OrderLineWS();
					lineForAdd.setOrderId(order.getId());
					lineForAdd.setItemId(itemId);
					lineForAdd.setQuantityAsDecimal(addQuantity);
					lineForAdd.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
					lineForAdd.setUseItem(true);
					lineForAdd.setAssetIds(new Integer[] {});
					ItemDTO item = new ItemDAS().find(itemId);
					lineForAdd.setDescription(item
							.getDescription(getCallerLanguageId()));
					PriceModelDTO priceModelDto = item.getPrice(effectiveDate,
							getCallerCompanyId());
					if (priceModelDto != null
							&& priceModelDto.getRate() != null) {
						scale = priceModelDto.getRate().scale();
						lineForAdd.setPrice(lineForAdd.getQuantityAsDecimal()
								.multiply(priceModelDto.getRate())
								.setScale(scale, RoundingMode.HALF_UP));
					}
				}
				OrderChangeWS addChange = OrderChangeBL.buildFromLine(
						lineForAdd, order, applyOrderChangeStatus.getId());
				addChange.setOrderChangeTypeId(defaultChangeType.getId());
				addChange.setQuantityAsDecimal(addQuantity);
				addChange.setStartDate(effectiveDate);
				result.add(addChange);
			}
		}

		OrderChangeWS addPlanChange = null;
		for (OrderChangeWS change : result) {
			if (change.getItemId() != null
					&& change.getItemId().equals(swapPlanItemId)) {
				addPlanChange = change;
				break;
			}
		}
		if (addPlanChange != null) {
			List<OrderChangePlanItemWS> orderChangePlanItems = new LinkedList<OrderChangePlanItemWS>();
			Iterator<OrderChangeWS> changesIterator = result.iterator();
			while (changesIterator.hasNext()) {
				OrderChangeWS change = changesIterator.next();
				if ((change.getOrderLineId() == null || change.getOrderLineId() <= 0)
						&& !change.equals(addPlanChange)) {
					changesIterator.remove();
					OrderChangePlanItemWS changePlanItemWS = new OrderChangePlanItemWS();
					changePlanItemWS.setItemId(change.getItemId());
					changePlanItemWS.setDescription(change.getDescription());
					orderChangePlanItems.add(changePlanItemWS);
				}
			}
			addPlanChange.setOrderChangePlanItems(orderChangePlanItems
					.toArray(new OrderChangePlanItemWS[orderChangePlanItems
							.size()]));
		}
		return result.toArray(new OrderChangeWS[result.size()]);
	}

	public void updateItem(ItemDTOEx item) {
		// do validation
		validateItem(item);
		updateItem(item, false);
	}

	public void updateItem(ItemDTOEx item, boolean isPlan) {
		// check if all descriptions are to delete
		List<InternationalDescriptionWS> descriptions = item.getDescriptions();
		boolean noDescriptions = true;
		for (InternationalDescriptionWS description : descriptions) {
			if (!description.isDeleted()) {
				noDescriptions = false;
				break;
			}
		}
		if (noDescriptions) {
			throw new SessionInternalError(
					"Must have a description",
					new String[] { "ItemDTOEx,descriptions,validation.error.is.required" });
		}

		Integer executorId = getCallerId();
		Integer languageId = getCallerLanguageId();

		// do some transformation from WS to DTO :(
		ItemBL itemBL = new ItemBL(item.getId());
		// Set the creator entity id to the one stored in DB to prevent stealing
		// of the Item when editing as a child for example.
		item.setEntityId(itemBL.getEntity().getEntityId());
		ItemDTO dto = itemBL.getDTO(item);
		validateAssetManagementForItem(dto, itemBL.getEntity());
		new PlanBL().validateContainingPlans(dto);

		// Set description to null
		dto.setDescription(null);
		IItemSessionBean itemSession = (IItemSessionBean) Context
				.getBean(Context.Name.ITEM_SESSION);
		itemSession.update(executorId, dto, languageId, isPlan);

		// save-delete descriptions
		for (InternationalDescriptionWS description : descriptions) {
			if (description.getLanguageId() != null) {
				if (description.isDeleted()) {
					dto.deleteDescription(description.getLanguageId());
				} else {
					dto.setDescription(description.getContent(),
							description.getLanguageId());
				}
			}
		}
	}

	/**
	 * Validate an ItemDTOEx before saving
	 *
	 * @param item
	 */
	private void validateItem(ItemDTOEx item) {

		// item may be shared - company hierarchies
		if (item.isGlobal()) {
			if (null == item.getEntityId()) {
				item.setEntityId(getCallerCompanyId());
			}
			item.setEntities(Collections.<Integer> emptyList());
		} else {
			if (CollectionUtils.isEmpty(item.getEntities())) {
				List<Integer> list = new ArrayList<Integer>(1);
				list.add(getCallerCompanyId());
				item.setEntities(list);
			}
		}

		Integer[] mandatoryItems = item
				.getMandatoryDependencyIdsOfType(ItemDependencyType.ITEM);
		validateItemMandatoryDependenciesCycle(item.getId(),
				JArrays.toArrayList(mandatoryItems));
		// check if all descriptions are to delete
		List<InternationalDescriptionWS> descriptions = item.getDescriptions();
		boolean noDescriptions = true;
		for (InternationalDescriptionWS description : descriptions) {
			if (!description.isDeleted() && StringUtils.isNotEmpty(description.getContent().trim())) {
				noDescriptions = false;
				break;
			}
		}
		if (noDescriptions) {
			throw new SessionInternalError(
					"Must have a description",
					new String[] { "ItemDTOEx,descriptions,validation.error.is.required" });
		}

		if (item.getOrderLineMetaFields() != null) {
			for (MetaFieldWS field : item.getOrderLineMetaFields()) {
				if (field.getDataType().equals(DataType.SCRIPT)
						&& (null == field.getFilename() || field.getFilename()
								.isEmpty())) {
					throw new SessionInternalError(
							"Script Meta Fields must define filename",
							new String[] { "ItemDTOEx,orderLineMetaFields,product.validation.orderLineMetaFields.script.no.file,"
									+ field.getName() });
				}
			}
		}

		// validate dependency quantities
		ItemDependencyDTOEx[] dependencies = item.getDependencies();
		if (dependencies != null) {
			for (ItemDependencyDTOEx dependency : dependencies) {
				if (dependency.getMaximum() != null) {
					if (dependency.getMaximum() < dependency.getMinimum()) {
						throw new SessionInternalError(
								"Maximum quantity must be more than minimum",
								new String[] { "ItemDTOEx,dependencies,product.validation.dependencies.max.lessthan.min" });
					}
				}
			}
		}
	}

	/**
	 * Creates the given Order in jBilling, generates an Invoice for the same.
	 * Returns the generated Invoice ID
	 */
	public Integer createOrderAndInvoice(OrderWS order,
			OrderChangeWS[] orderChanges) throws SessionInternalError {
		validateLines(order);
		Integer orderId = doCreateOrder(order, orderChanges, true).getId();
		InvoiceDTO invoice = doCreateInvoice(orderId);
		return invoice == null ? null : invoice.getId();
	}

	/**
	 * This method will update all orders in hierarchy provided. Deletion orders
	 * from hierarchy is not possible in this method, user deleteOrder()
	 * instead. If some order is not provided in hierarchy, it will not be
	 * changed at all, references for this order from other orders will not be
	 * changed too
	 * 
	 * @param order
	 *            order with hierarchy
	 * @throws SessionInternalError
	 */
	public void updateOrder(OrderWS order, OrderChangeWS[] orderChanges) {
		updateOrder(order, orderChanges, getCallerCompanyId());
	}

	public void updateOrder(OrderWS order, OrderChangeWS[] orderChanges,
			Integer entityId) throws SessionInternalError {
		validateOrder(order, orderChanges);
		validateActiveSinceDate(order);

		// do some transformation from WS to DTO
		Map<OrderWS, OrderDTO> wsToDtoOrdersMap = new HashMap<OrderWS, OrderDTO>();
		Map<OrderLineWS, OrderLineDTO> wsToDtoLinesMap = new HashMap<OrderLineWS, OrderLineDTO>();
		OrderBL orderBL = new OrderBL();
		OrderDTO dto = orderBL.getDTO(order, wsToDtoOrdersMap, wsToDtoLinesMap);
		OrderDTO rootOrder = OrderHelper.findRootOrderIfPossible(dto);
		List<OrderChangeDTO> changeDtos = new LinkedList<OrderChangeDTO>();
		List<Integer> deletedChanges = new LinkedList<Integer>();
		convertOrderChangeWsToDto(orderChanges, changeDtos, deletedChanges,
				wsToDtoOrdersMap, wsToDtoLinesMap);
		try {
			orderBL.update(rootOrder, changeDtos, deletedChanges, entityId,
					getCallerId(), getCallerLanguageId());
		} catch (SessionInternalError e) {
			LOG.error("WS - updateOrder", e);
			throw e;
		} catch (Exception e) {
			LOG.error("WS - updateOrder", e);
			throw new SessionInternalError("Error updating order", e);
		}
	}

	public void updateOrders(OrderWS[] orders, OrderChangeWS[] orderChanges)
			throws SessionInternalError {
		for (OrderWS order : orders) {
			List<OrderChangeWS> currentOrderChanges = new LinkedList<OrderChangeWS>();
			if (orderChanges != null) {
				LinkedHashSet<OrderWS> currentOrders = OrderHelper
						.findAllChildren(order);
				currentOrders.add(order);
				// find order changes for current order
				for (OrderChangeWS orderChange : orderChanges) {
					if (orderChange.getOrderWS() != null) {
						if (currentOrders.contains(orderChange.getOrderWS())) {
							currentOrderChanges.add(orderChange);
						}
					} else if (orderChange.getOrderId() != null) {
						for (OrderWS childOrder : currentOrders) {
							if (orderChange.getOrderId().equals(
									childOrder.getId())) {
								currentOrderChanges.add(orderChange);
							}
						}
					}
				}
			}
			updateOrder(order,
					currentOrderChanges
							.toArray(new OrderChangeWS[currentOrderChanges
									.size()]));
		}
	}

	@Transactional(readOnly = true)
	public OrderWS getOrder(Integer orderId) throws SessionInternalError {
		// get the info from the caller
		Integer languageId = getCallerLanguageId();
		// now get the order. Avoid the proxy since this is for the client
		OrderDAS das = new OrderDAS();
		OrderDTO order = das.findNow(orderId);
		if (order == null) { // not found
			return null;
		}
		OrderBL bl = new OrderBL(order);
		if (order.getDeleted() == 1) {
			LOG.debug("Returning deleted order %s", orderId);
		}
		return bl.getWS(languageId);
	}

	@Transactional(readOnly = true)
	public Integer[] getOrderByPeriod(Integer userId, Integer periodId)
			throws SessionInternalError {
		if (userId == null || periodId == null) {
			return null;
		}
		// now get the order
		OrderBL bl = new OrderBL();
		return bl.getByUserAndPeriod(userId, periodId);
	}

	@Transactional(readOnly = true)
	public OrderLineWS getOrderLine(Integer orderLineId)
			throws SessionInternalError {
		// now get the order
		OrderBL bl = new OrderBL();
		OrderLineWS retValue = null;

		retValue = bl.getOrderLineWS(orderLineId);

		return retValue;
	}

	public void updateOrderLine(OrderLineWS line) throws SessionInternalError {
		// now get the order
		OrderBL bl = new OrderBL();
		bl.updateOrderLine(line, getCallerId());
	}

	@Transactional(readOnly = true)
	public OrderWS getLatestOrder(Integer userId) throws SessionInternalError {
		if (userId == null) {
			throw new SessionInternalError("User id can not be null");
		}
		OrderWS retValue = null;

		// now get the order
		OrderBL bl = new OrderBL();
		Integer orderId = bl.getLatest(userId);
		if (orderId != null) {
			bl.set(orderId);
			retValue = bl.getWS(getCallerLanguageId());
		}
		return retValue;
	}

	@Transactional(readOnly = true)
	public Integer[] getLastOrders(Integer userId, Integer number)
			throws SessionInternalError {
		if (userId == null || number == null) {
			return null;
		}
		UserBL userbl = new UserBL();

		OrderBL order = new OrderBL();
		return order.getListIds(userId, number, userbl.getEntityId(userId));
	}

	@Transactional(readOnly = true)
	public OrderWS[] getUserOrdersPage(Integer user, Integer limit,
			Integer offset) throws SessionInternalError {

		List<OrderDTO> userOrdersPaged = new OrderBL().getListOrdersPaged(
				getCallerCompanyId(), user, limit, offset);

		if (null == userOrdersPaged) {
			return new OrderWS[0];
		}

		OrderWS[] ordersWs = new OrderWS[userOrdersPaged.size()];
		OrderBL bl = null;
		for (OrderDTO dto : userOrdersPaged) {
			bl = new OrderBL(dto);
			ordersWs[userOrdersPaged.indexOf(dto)] = bl
					.getWS(getCallerLanguageId());
		}

		return ordersWs;

	}

	@Transactional(readOnly = true)
	public Integer[] getLastOrdersPage(Integer userId, Integer limit,
			Integer offset) throws SessionInternalError {
		if (userId == null || limit == null || offset == null) {
			return null;
		}
		UserBL userbl = new UserBL();

		OrderBL order = new OrderBL();
		return order.getListIds(userId, limit, offset,
				userbl.getEntityId(userId));
	}

	@Transactional(readOnly = true)
	public Integer[] getOrdersByDate(Integer userId, Date since, Date until) {
		if (userId == null || since == null || until == null) {
			return null;
		}
		UserBL userbl = new UserBL();
		OrderBL order = new OrderBL();
		return order.getListIdsByDate(userId, since, until,
				userbl.getEntityId(userId));
	}

	public String deleteOrder(Integer id) throws SessionInternalError {
		// now get the order
		OrderBL bl = new OrderBL();
		bl.setForUpdate(id);
		if(getOrder(id).getGeneratedInvoices().length >0){
			throw new SessionInternalError("Error on delete Order ", new String[] { "order.have.invoice.cannot.be.delete,"+id});
		}
		String orderIds = bl.delete(getCallerId());
		return orderIds;
	}

	/**
	 * Returns the current order (order collecting current one-time charges) for
	 * the period of the given date and the given user. Returns null for users
	 * with no main subscription order.
	 */
	@Transactional(readOnly = true)
	public OrderWS getCurrentOrder(Integer userId, Date date) {
		OrderWS retValue = null;
		// get the info from the caller
		Integer languageId = getCallerLanguageId();

		// now get the current order
		OrderBL bl = new OrderBL();
		if (bl.getCurrentOrder(userId, date) != null) {
			retValue = bl.getWS(languageId);
		}

		return retValue;
	}

	/**
	 * Updates the uesr's current one-time order for the given date. Returns the
	 * updated current order. Throws an exception for users with no main
	 * subscription order.
	 */
	public OrderWS updateCurrentOrder(Integer userId, OrderLineWS[] lines,
			String pricing, Date eventDate, String eventDescription) {
		try {
			UserBL userbl = new UserBL(userId);

			// check if user has main subscription order
			if (userbl.getEntity().getCustomer().getMainSubscription() == null) {
				throw new SessionInternalError(
						"No main subscription order for userId: " + userId);
			}

			// get currency from the user
			Integer currencyId = userbl.getCurrencyId();

			// get language from the caller
			Integer languageId = getCallerLanguageId();

			// pricing fields
			List<CallDataRecord> records = null;
			PricingField[] fieldsArray = PricingField
					.getPricingFieldsValue(pricing);
			if (fieldsArray != null) {
                CallDataRecord record = new CallDataRecord();
				for (PricingField field : fieldsArray) {
					record.addField(field, false); // don't care about isKey
				}
				records = new ArrayList<CallDataRecord>(1);
				records.add(record);
			}

			List<OrderLineDTO> diffLines = null;
			OrderBL bl = new OrderBL();
			if (lines != null) {
				// get the current order
				bl.set(OrderBL.getOrCreateCurrentOrder(userId, eventDate,
						currencyId, true));
				List<OrderLineDTO> oldLines = OrderLineBL.copy(bl.getDTO()
						.getLines());

				// add the line to the current order
				for (OrderLineWS line : lines) {
					bl.addItem(line.getItemId(), line.getQuantityAsDecimal(),
							languageId, userId, getCallerCompanyId(),
							currencyId, records, eventDate);
				}

				// process lines to update prices and details from the source
				// items
				bl.processLines(bl.getDTO(), languageId, getCallerCompanyId(),
						userId, currencyId, pricing);
				diffLines = OrderLineBL.diffOrderLines(oldLines, bl.getDTO()
						.getLines());

				// generate NewQuantityEvents
				bl.checkOrderLineQuantities(oldLines, bl.getDTO().getLines(),
						getCallerCompanyId(), bl.getDTO().getId(), true);

			} else if (records != null) {
				// Since there are no lines, run the mediation process
				// rules to create them.
                //TODO MODULARIZATION ISSUE 1: Check if needed, this method should be deprecated
                PluggableTaskManager<IMediationProcess> tm = new PluggableTaskManager<IMediationProcess>(
						getCallerCompanyId(),
						Constants.PLUGGABLE_TASK_MEDIATION_PROCESS);
				IMediationProcess processTask = tm.getNextClass();

				MediationStepResult result = new MediationStepResult();
				result.setUserId(userId);
				result.setCurrencyId(currencyId);
				result.setEventDate(eventDate);
                result.setPersist(true);
				for (CallDataRecord record : records) {
					processTask.process(record, result);
				}
				// the mediation process might not have anything for you...
				if (result.getCurrentOrder() == null) {
					LOG.debug(
							"Call to updateOrder did not resolve to a current order lines = %s fields= %s",
							Arrays.toString(lines),
							Arrays.toString(fieldsArray));
					return null;
				}
				bl.set((OrderDTO) result.getCurrentOrder());
                //TODO MODULARIZATION: Check if needed, this method should be deprecated
			} else {
				throw new SessionInternalError("Both the order lines and "
						+ "pricing fields were null. At least one of either "
						+ "must be provided.");
			}


			// save the event
			// assign to record DONE and BILLABLE status
            //TODO MODULARIZATION: Check if needed, this method should be deprecated
//			IMediationSessionBean mediation = (IMediationSessionBean) Context
//					.getBean(Context.Name.MEDIATION_SESSION);
//			mediation.saveEventRecordLines(new ArrayList<OrderLineDTO>(
//					diffLines), getCallerCompanyId(), userbl.getEntity()
//					.getId(), null,
//					Constants.MEDIATION_RECORD_STATUS_DONE_AND_BILLABLE, String
//							.valueOf(eventDate.getTime()), eventDate,
//					eventDescription, null, null);
            //TODO MODULARIZATION: Check if needed, this method should be deprecated
			// return the updated order
			return bl.getWS(languageId);

		} catch (Exception e) {
			LOG.error("WS - getCurrentOrder", e);
			throw new SessionInternalError("Error updating current order", e);
		}
	}

	@Transactional(readOnly = true)
	public OrderWS[] getUserSubscriptions(Integer userId)
			throws SessionInternalError {
		if (userId == null)
			throw new SessionInternalError("User Id cannot be null.");

		List<OrderDTO> subscriptions = new OrderDAS()
				.findByUserSubscriptions(userId);
		if (null == subscriptions) {
			return new OrderWS[0];
		}
		OrderWS[] orderArr = new OrderWS[subscriptions.size()];
		OrderBL bl = null;
		for (OrderDTO dto : subscriptions) {
			bl = new OrderBL(dto);
			orderArr[subscriptions.indexOf(dto)] = bl
					.getWS(getCallerLanguageId());
		}

		return orderArr;
	}

	public boolean updateOrderPeriods(OrderPeriodWS[] orderPeriods)
			throws SessionInternalError {
		// IOrderSessionBean orderSession =
		// Context.getBean(Context.Name.ORDER_SESSION);

		List<OrderPeriodDTO> periodDtos = new ArrayList<OrderPeriodDTO>(
				orderPeriods.length);
		OrderPeriodDAS periodDas = new OrderPeriodDAS();
		OrderPeriodDTO periodDto = null;
		for (OrderPeriodWS periodWS : orderPeriods) {
			if (null != periodWS.getId()) {
				periodDto = periodDas.find(periodWS.getId());
			}
			if (null == periodDto) {
				periodDto = new OrderPeriodDTO();
				periodDto.setCompany(new CompanyDAS()
						.find(getCallerCompanyId()));
				// periodDto.setVersionNum(new Integer(0));
			}
			periodDto.setValue(periodWS.getValue());
			if (null != periodWS.getPeriodUnitId()) {
				periodDto.setUnitId(periodWS.getPeriodUnitId().intValue());
			}
			periodDto = periodDas.save(periodDto);
			if (periodWS.getDescriptions() != null
					&& periodWS.getDescriptions().size() > 0) {
				periodDto.setDescription(((InternationalDescriptionWS) periodWS
						.getDescriptions().get(0)).getContent(),
						((InternationalDescriptionWS) periodWS
								.getDescriptions().get(0)).getLanguageId());
			}
			LOG.debug("Converted to DTO: %s", periodDto);
			periodDas.flush();
			periodDas.clear();
			// periodDtos.add(periodDto);
			periodDto = null;
		}
		// orderSession.setPeriods(getCallerLanguageId(), periodDtos.toArray(new
		// OrderPeriodDTO[periodDtos.size()]));
		return true;
	}

	public boolean updateOrCreateOrderPeriod(OrderPeriodWS orderPeriod)
			throws SessionInternalError {

		Integer entityId = getCallerCompanyId();

		/*
		 * TODO - Instead of below, We should use Hibernate Validator @Size on
		 * 'content' field of InternationalDescriptionWS.java
		 */
		if (orderPeriod.getDescriptions() != null
				&& orderPeriod.getDescriptions().size() > 0) {
			int descriptionLength = orderPeriod.getDescriptions().get(0)
					.getContent().length();
			if (descriptionLength < 1 || descriptionLength > 4000) {
				throw new SessionInternalError(
						"Description should be between 1 and 4000 characters long");
			}
		}

		OrderPeriodDAS periodDas = new OrderPeriodDAS();
		OrderPeriodDTO periodDto = null;
		if (null != orderPeriod.getId()) {
			periodDto = periodDas.find(orderPeriod.getId());
		}

		if (null == periodDto) {
			periodDto = new OrderPeriodDTO();
			periodDto.setCompany(new CompanyDAS().find(entityId));
			// periodDto.setVersionNum(new Integer(0));
		}
		periodDto.setValue(orderPeriod.getValue());
		if (null != orderPeriod.getPeriodUnitId()) {
			periodDto.setUnitId(orderPeriod.getPeriodUnitId().intValue());
		}
		periodDto = periodDas.save(periodDto);
		if (orderPeriod.getDescriptions() != null
				&& orderPeriod.getDescriptions().size() > 0) {
			periodDto.setDescription(((InternationalDescriptionWS) orderPeriod
					.getDescriptions().get(0)).getContent(),
					((InternationalDescriptionWS) orderPeriod.getDescriptions()
							.get(0)).getLanguageId());
		}
		LOG.debug("Converted to DTO: %s", periodDto);
		periodDas.flush();
		periodDas.clear();
		return true;
	}

	public boolean deleteOrderPeriod(Integer periodId)
			throws SessionInternalError {
		try {
			// now get the order
			OrderBL bl = new OrderBL();
			return new Boolean(bl.deletePeriod(periodId));
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}
	}

	/*
	 * Account Type
	 */
	public boolean updateAccountType(AccountTypeWS accountType)
			throws SessionInternalError {
		Integer entityId = getCallerCompanyId();
		PaymentInformationDAS paymentInformationDAS = new PaymentInformationDAS();
		AccountTypeWS existing = AccountTypeBL.getWS(
				new AccountTypeDAS().find(accountType.getId()));
		Integer[] existingPaymentMethodTypeIds = existing
				.getPaymentMethodTypeIds();
		existingPaymentMethodTypeIds = (null == existingPaymentMethodTypeIds) ? new Integer[0]
				: existingPaymentMethodTypeIds;
		Integer[] newPaymentMethodTypeIds = accountType
				.getPaymentMethodTypeIds();
		newPaymentMethodTypeIds = (null == newPaymentMethodTypeIds) ? new Integer[0]
				: newPaymentMethodTypeIds;
		List<Integer> removedPaymentMethodType = (List<Integer>) CollectionUtils
				.subtract(Arrays.asList(existingPaymentMethodTypeIds),
						Arrays.asList(newPaymentMethodTypeIds));
		for (Integer paymentMethodType : removedPaymentMethodType) {
			long l = paymentInformationDAS
					.findByAccountTypeAndPaymentMethodType(accountType.getId(),
							paymentMethodType);
			if (l > 0) {
				throw new SessionInternalError(
						"",
						new String[] { "AccountTypeWS,paymentMethod,validation.error.payment.inUse" });
			}
		}

		AccountTypeDTO accountTypeDTO = AccountTypeBL.getDTO(accountType,entityId);
		LOG.debug("Payments: " + accountTypeDTO.getPaymentMethodTypes());
		new AccountTypeBL().update(accountTypeDTO);

		if (accountType.getDescriptions() != null
				&& accountType.getDescriptions().size() > 0) {

			for (InternationalDescriptionWS desc : accountType
					.getDescriptions()) {
				// verify if description is non empty and unique
				if (desc.getContent().trim().isEmpty()) {
					String[] errmsgs = new String[1];
					errmsgs[0] = "AccountTypeWS,descriptions,accountTypeWS.error.blank.name";
					throw new SessionInternalError(
							"There is an error in  data.", errmsgs);
				} else if (!new AccountTypeBL().isAccountTypeUnique(entityId,
						desc.getContent(), false)) {
					String[] errmsgs = new String[1];
					errmsgs[0] = "AccountTypeWS,descriptions,accountTypeWS.error.unique.name";
					throw new SessionInternalError(
							"There is an error in  data.", errmsgs);
				}
				accountTypeDTO.setDescription(desc.getContent(),
						desc.getLanguageId());
			}
			BigDecimal creditLimit = accountType.getCreditLimitAsDecimal();
			BigDecimal notification1 = accountType
					.getCreditNotificationLimit1AsDecimal();
			if (creditLimit != null && notification1 != null
					&& !(creditLimit.compareTo(notification1) >= 0)) {
				String[] errmsgs = new String[1];
				errmsgs[0] = "AccountTypeWS,creditNotificationLimit1,accountTypeWS.error.credit.limit";
				throw new SessionInternalError("There is an error in  data.",
						errmsgs);
			}
			BigDecimal notification2 = accountType
					.getCreditNotificationLimit2AsDecimal();
			if (creditLimit != null && notification2 != null
					&& !(creditLimit.compareTo(notification2) >= 0)) {
				String[] errmsgs = new String[1];
				errmsgs[0] = "AccountTypeWS,creditNotificationLimit2,accountTypeWS.error.credit.limit";
				throw new SessionInternalError("There is an error in  data.",
						errmsgs);
			}
		}
		return true;
	}

	public Integer createAccountType(AccountTypeWS accountType)
			throws SessionInternalError {
		Integer entityId = getCallerCompanyId();
		AccountTypeDTO accountTypeDTO = AccountTypeBL.getDTO(accountType,entityId);
		accountTypeDTO = new AccountTypeBL().create(accountTypeDTO);

		if (accountType.getDescriptions() != null
				&& accountType.getDescriptions().size() > 0) {

			for (InternationalDescriptionWS desc : accountType
					.getDescriptions()) {
				if (desc.getContent().trim().isEmpty()) {
					String[] errmsgs = new String[1];
					errmsgs[0] = "AccountTypeWS,descriptions,accountTypeWS.error.blank.name";
					throw new SessionInternalError(
							"There is an error in  data.", errmsgs);
				} else if (!new AccountTypeBL().isAccountTypeUnique(entityId,
						desc.getContent(), true)) {
					String[] errmsgs = new String[1];
					errmsgs[0] = "AccountTypeWS,descriptions,accountTypeWS.error.unique.name";
					throw new SessionInternalError(
							"There is an error in  data.", errmsgs);

				}
				accountTypeDTO.setDescription(desc.getContent(),
						desc.getLanguageId());
			}
			BigDecimal creditLimit = accountType.getCreditLimitAsDecimal();
			BigDecimal notification1 = accountType
					.getCreditNotificationLimit1AsDecimal();
			if (creditLimit != null && notification1 != null
					&& !(creditLimit.compareTo(notification1) >= 0)) {
				String[] errmsgs = new String[1];
				errmsgs[0] = "AccountTypeWS,creditNotificationLimit1,accountTypeWS.error.credit.limit";
				throw new SessionInternalError("There is an error in  data.",
						errmsgs);
			}
			BigDecimal notification2 = accountType
					.getCreditNotificationLimit2AsDecimal();
			if (creditLimit != null && notification2 != null
					&& !(creditLimit.compareTo(notification2) >= 0)) {
				String[] errmsgs = new String[1];
				errmsgs[0] = "AccountTypeWS,creditNotificationLimit2,accountTypeWS.error.credit.limit";
				throw new SessionInternalError("There is an error in  data.",
						errmsgs);
			}
		}

		return accountTypeDTO.getId();
	}

	public boolean deleteAccountType(Integer accountTypeId)
			throws SessionInternalError {
		try {
			AccountTypeBL bl = new AccountTypeBL(accountTypeId);
			return bl.delete();
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}
	}

	@Transactional(readOnly = true)
	public AccountTypeWS getAccountType(Integer accountTypeId)
			throws SessionInternalError {

		AccountTypeDAS das = new AccountTypeDAS();
		AccountTypeDTO accountTypeDTO = das.findNow(accountTypeId);
		if (accountTypeId == null || accountTypeDTO == null) { // not found
			return null;
		}
		AccountTypeBL bl = new AccountTypeBL(accountTypeId);
		return bl.getWS(accountTypeDTO.getLanguageId());
	}

	@Transactional(readOnly = true)
	public AccountTypeWS[] getAllAccountTypes() throws SessionInternalError {
		AccountTypeDAS das = new AccountTypeDAS();
		List<AccountTypeDTO> types = das.findAll(getCallerCompanyId());
		AccountTypeWS[] wsTypes = new AccountTypeWS[types.size()];
		for (int i = 0; i < types.size(); i++) {
			wsTypes[i] = AccountTypeBL.getWS(types.get(i));
		}
		return wsTypes;
	}

	@Transactional(readOnly = true)
	public AccountInformationTypeWS[] getInformationTypesForAccountType(
			Integer accountTypeId) {

		List<AccountInformationTypeDTO> accountInformationTypes = new AccountInformationTypeBL()
				.getAccountInformationTypes(accountTypeId);

		if (accountInformationTypes == null) {
			return new AccountInformationTypeWS[0];
		}

		List<AccountInformationTypeWS> informationTypesWS = new ArrayList<AccountInformationTypeWS>(
				accountInformationTypes.size());

		if (accountInformationTypes.size() > 0) {
			for (AccountInformationTypeDTO ait : accountInformationTypes) {
				AccountInformationTypeWS accountInformationTypeWS =AccountInformationTypeBL.getWS(ait);
				informationTypesWS.add(accountInformationTypeWS);
			}
		}
		return informationTypesWS
				.toArray(new AccountInformationTypeWS[informationTypesWS.size()]);
	}

	public Integer createAccountInformationType(
			AccountInformationTypeWS accountInformationType) {
		if (accountInformationType.getMetaFields() != null) {
			for (MetaFieldWS field : accountInformationType.getMetaFields()) {
				if (field.getDataType().equals(DataType.SCRIPT)
						&& (null == field.getFilename() || field.getFilename()
						.isEmpty())) {
					throw new SessionInternalError(
							"Script Meta Fields must define filename",
							new String[] { "AccountInformationTypeWS,metaFields,metafield.validation.filename.required" });
				}
			}
		}
		AccountInformationTypeDTO dto = AccountInformationTypeBL.getDTO(accountInformationType,getCallerCompanyId());
		Map<Integer, List<Integer>> dependencyMetaFieldMap = AccountInformationTypeBL.getMetaFieldDependency(accountInformationType);
		dto = new AccountInformationTypeBL().create(dto, dependencyMetaFieldMap);

		return dto.getId();
	}

	public void updateAccountInformationType(
			AccountInformationTypeWS accountInformationType) {

		if (accountInformationType.getMetaFields() != null) {
			for (MetaFieldWS field : accountInformationType.getMetaFields()) {
				if (field.getDataType().equals(DataType.SCRIPT)
						&& (null == field.getFilename() || field.getFilename()
						.isEmpty())) {
					throw new SessionInternalError(
							"Script Meta Fields must define filename",
							new String[] { "AccountInformationTypeWS,metaFields,metafield.validation.filename.required" });
				}
			}
		}

		AccountInformationTypeDTO dto = AccountInformationTypeBL.getDTO(accountInformationType,getCallerCompanyId());
		Map<Integer, List<Integer>> dependencyMetaFieldMap = AccountInformationTypeBL.getMetaFieldDependency(accountInformationType);
		new AccountInformationTypeBL().update(dto, dependencyMetaFieldMap);
	}

	public boolean deleteAccountInformationType(Integer accountInformationTypeId) {
		try {
			AccountInformationTypeBL bl = new AccountInformationTypeBL(
					accountInformationTypeId);
			if (AccountInformationTypeBL.checkUseForNotifications(bl
					.getAccountInformationType())) {
				throw new SessionInternalError(
						"Account information type is being used for notifications",
						new String[] { "config.account.information.type.delete.failure" });
			}
			return bl.delete();
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}
	}

	@Transactional(readOnly = true)
	public AccountInformationTypeWS getAccountInformationType(
			Integer accountInformationTypeId) {

		AccountInformationTypeDAS das = new AccountInformationTypeDAS();
		AccountInformationTypeDTO accountInformationType = das
				.findNow(accountInformationTypeId);
		if (accountInformationTypeId == null || accountInformationType == null) {
			return null;
		}
		AccountInformationTypeBL bl = new AccountInformationTypeBL(
				accountInformationTypeId);
		return bl.getWS();
	}

	/*
	 * PAYMENT
	 */


	public Integer createPayment(PaymentWS payment) {
		return applyPayment(payment, null);
	}

	public Integer[] createPayments(PaymentWS[] payments) {
		Integer[] paymentIds = new Integer[payments.length];

		for (int i = 0; i < payments.length; i++) {
			paymentIds[i] = applyPayment(payments[i], null);
		}

		return paymentIds;
	}

	public void updatePayment(PaymentWS payment) {

		Integer entityId = getCallerCompanyId();
		if (payment == null) {
			throw new SessionInternalError("Can not update null payment!!");
		}
		Integer userId = payment.getOwningUserId();
		UserDTO user = new UserDAS().find(userId);
		if (null == user) {
			LOG.debug("No owning user for payment id: %d", payment.getId());
			throw new SessionInternalError(
					"There is not user for the supplied payment.");
		}
		Integer userCompanyId = user.getEntity().getId();
		if (!userCompanyId.equals(entityId)) {
			LOG.debug(
					"Payment owing entity id: %d not equals with invoking entity id: %d",
					userCompanyId, entityId);
			throw new SessionInternalError(
					"Updating another entity's payments not supported!!");
		}

		PaymentDTOEx dto = new PaymentDTOEx(payment);
		PaymentBL paymentBL = new PaymentBL(payment.getId());
		// check if payment has been refunded
		if (paymentBL.ifRefunded()) {
			throw new SessionInternalError(
					"This payment has been refunded and hence cannot be updated.",
					new String[] { "validation.error.update.refunded.payment" });
		}
		paymentBL.update(getCallerId(), dto);
	}

	public void deletePayment(Integer paymentId) throws SessionInternalError {

		PaymentBL paymentBL = new PaymentBL(paymentId);

		// check if the payment is a refund , if it is do not allow it
		if (paymentBL.getEntity().getIsRefund() == 1) {
			LOG.debug("This payment %s is a refund so we cannot delete it.",
					paymentId);
			throw new SessionInternalError("A Refund cannot be deleted",
					new String[] { "validation.error.delete.refund.payment" });
		}

		// check if payment has been refunded
		if (paymentBL.ifRefunded()) {
			throw new SessionInternalError(
					"This payment has been refunded and hence cannot be deleted.",
					new String[] { "validation.error.delete.refunded.payment" });
		}

		paymentBL.delete();
	}

	/**
	 * Enters a payment and applies it to the given invoice. This method DOES
	 * NOT process the payment but only creates it as 'Entered'. The entered
	 * payment will later be processed by the billing process.
	 *
	 * Invoice ID is optional. If no invoice ID is given the payment will be
	 * applied to the payment user's account according to the configured entity
	 * preferences.
	 *
	 * @param payment
	 *            payment to apply
	 * @param invoiceId
	 *            invoice id
	 * @return created payment id
	 * @throws SessionInternalError
	 */
	public Integer applyPayment(PaymentWS payment, Integer invoiceId)
			throws SessionInternalError {
		// payment.setIsRefund(0);
		LOG.debug("In applyPayment...");
		Integer entityId = getCallerCompanyId();
		// Guard against npe
		if (payment == null) {
			LOG.debug("Supplied Payment is null.");
			throw new SessionInternalError("Can not apply null payment!!");
		}
		// Check if the payment owing user is from the same entity as the caller
		// user.
		Integer userId = payment.getOwningUserId();
		UserDTO user = new UserDAS().find(userId);
		if (null == user) {
			LOG.debug("No owning user for payment id: %d", payment.getId());
			throw new SessionInternalError(
					"There is not user for the supplied payment.");
		}
		Integer userCompanyId = user.getEntity().getId();
		if (!entityId.equals(userCompanyId)) {
			LOG.debug(
					"Payment owing user entity id: %d not equals with invoking user entity id: %d",
					userCompanyId, entityId);
			throw new SessionInternalError(
					"Can not apply payments from another entity!!");
		}

		// Check if the invoice for the invoice id has the same entity id as the
		// caller entity id.
		if (invoiceId != null) {
			InvoiceDTO invoice = findInvoice(invoiceId);
			if (null == invoice) {
				LOG.debug("No invoice found invoice id: %d", invoiceId);
				throw new SessionInternalError("Invoice not found!!");
			}
			if (!entityId.equals(invoice.getBaseUser().getEntity().getId())) {
				LOG.debug(
						"Invoice entity id: %d not equals with invoking user entity id: %d",
						userCompanyId, entityId);
				throw new SessionInternalError(
						"Applying invoices from another entity not supported!!");
			}
		}

		// apply validations for refund payments
		if (payment.getIsRefund() == 1) {
			// check for validations
			if (!PaymentBL.validateRefund(payment)) {
				throw new SessionInternalError(
						"Either refund payment was not linked to any payment or the refund amount is in-correct.",
						new String[] { "PaymentWS,paymentId,validation.error.apply.without.payment.or.different.linked.payment.amount" });
			}
		}

		// can not check payment id, more than 0 zero instruments should be
		// checked
		if (payment.getPaymentInstruments().size() < 1) {
			throw new SessionInternalError(
					"Cannot apply a payment without a payment method.",
					new String[] { "PaymentWS,paymentMethodId,validation.error.apply.without.method" });
		}

		IPaymentSessionBean session = (IPaymentSessionBean) Context
				.getBean(Context.Name.PAYMENT_SESSION);
		LOG.debug("payment has %s", payment);
		return session.applyPayment(new PaymentDTOEx(payment), invoiceId,
				getCallerId());
	}

	/**
	 * Processes a payment and applies it to the given invoice. This method will
	 * actively processes the payment using the configured payment plug-in.
	 *
	 * Payment is optional when an invoice ID is provided. If no payment is
	 * given, the payment will be processed using the invoiced user's configured
	 * "automatic payment" instrument.
	 *
	 * Invoice ID is optional. If no invoice ID is given the payment will be
	 * applied to the payment user's account according to the configured entity
	 * preferences.
	 *
	 * @param payment
	 *            payment to process
	 * @param invoiceId
	 *            invoice id
	 * @return payment authorization from the payment processor
	 */
	// this method does not start a transaction since transaction
	// during payment processing is managed manually
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public PaymentAuthorizationDTOEx processPayment(PaymentWS payment,
			Integer invoiceId) {
		LOG.debug("In process payment");
		Integer entityId = getCallerCompanyId();
		if (payment == null && invoiceId != null) {
			LOG.debug("Payment is null, requesting Payment for Invoice ID %s",
					invoiceId);
			return payInvoice(invoiceId);
		}
		// Guard against npe
		if (payment == null) {
			LOG.debug("Supplied Payment is null.");
			throw new SessionInternalError(
					"Payment processing parameters not found!");
		}

		Integer userId = payment.getOwningUserId();
		UserDTO user = new UserDAS().find(userId);
		if (null == user) {
			LOG.debug("No owning user for payment id: %d", payment.getId());
			throw new SessionInternalError(
					"There is not user for the supplied payment.");
		}

		Integer userCompanyId = user.getEntity().getId();
		if (!userCompanyId.equals(entityId)) {
			LOG.debug(
					"Payment owing entity id: %d not equals with invoking entity id: %d",
					userCompanyId, entityId);
			throw new SessionInternalError(
					"Processing another entity's payments not supported!!");
		}
		// apply validations for refund payment
		if (payment.getIsRefund() == 1) {
			if (!PaymentBL.validateRefund(payment)) {
				throw new SessionInternalError(
						"Either refund payment was not linked to any payment or the refund amount is in-correct",
						new String[] { "PaymentWS,paymentId,validation.error.apply.without.payment.or.different.linked.payment.amount" });
			}
		}

		LOG.debug("before dto conversion: " + payment.getPaymentInstruments());
		PaymentDTOEx dto = new PaymentDTOEx(payment);
		LOG.debug("after dto conversion: " + dto.getPaymentInstruments());
		// payment without Credit Card or ACH, fetch the users primary payment
		// instrument for use
		if (payment.getPaymentInstruments().size() < 1) {
			LOG.debug("processPayment() called without payment method, fetching users automatic payment instrument.");
			PaymentDTOEx instrument;
			try {
				instrument = PaymentBL.findPaymentInstrument(entityId,
						payment.getUserId());

			} catch (PluggableTaskException e) {
				throw new SessionInternalError(
						"Exception occurred fetching payment info plug-in.",
						new String[] { "PaymentWS,baseUserId,validation.error.no.payment.instrument" });

			} catch (TaskException e) {
				throw new SessionInternalError(
						"Exception occurred with plug-in when fetching payment instrument.",
						new String[] { "PaymentWS,baseUserId,validation.error.no.payment.instrument" });
			}

			if (instrument == null
					|| instrument.getPaymentInstruments() == null
					|| instrument.getPaymentInstruments().size() < 1) {
				throw new SessionInternalError(
						"User " + payment.getUserId()
								+ "does not have a default payment instrument.",
						new String[] { "PaymentWS,baseUserId,validation.error.no.payment.instrument" });
			}

			dto.setPaymentInstruments(instrument.getPaymentInstruments());
		}

		// TODO: multiple payment instruments method can not be set
		// populate payment method based on the payment instrument
		// LOG.debug("Payment method before: %s", payment.getMethodId());
		// if (null == dto.getPaymentMethod()) {
		// if (dto.getCreditCard() != null) {
		// dto.setPaymentMethod(new
		// PaymentMethodDTO(dto.getCreditCard().getCcType()));

		// } else if (dto.getAch() != null) {
		// dto.setPaymentMethod(new
		// PaymentMethodDTO(Constants.PAYMENT_METHOD_ACH));
		// }
		// }
		// LOG.debug("Payment method after %s",
		// dto.getPaymentMethod().getDescription());

		// process payment
		IPaymentSessionBean session = (IPaymentSessionBean) Context
				.getBean(Context.Name.PAYMENT_SESSION);
		Integer result = session.processAndUpdateInvoice(dto, invoiceId,
				entityId, getCallerId());
		LOG.debug("paymentBean.processAndUpdateInvoice() Id= %s", result);

		PaymentAuthorizationDTOEx auth = null;
		if (dto != null && dto.getAuthorization() != null) {
			LOG.debug("PaymentAuthorizationDTO Id = %s", dto.getAuthorization()
					.getId());
			auth = new PaymentAuthorizationDTOEx(dto.getAuthorization()
					.getOldDTO());
			LOG.debug("PaymentAuthorizationDTOEx Id =%s", auth.getId());
			auth.setResult(result.equals(Constants.RESULT_OK));

		} else {
			auth = new PaymentAuthorizationDTOEx();
			auth.setPaymentId(dto.getId());
			auth.setResult(result.equals(Constants.RESULT_FAIL));
		}
		return auth;
	}

	public PaymentAuthorizationDTOEx[] processPayments(PaymentWS[] payments,
			Integer invoiceId) {
		PaymentAuthorizationDTOEx[] paymentAuthorizations = new PaymentAuthorizationDTOEx[payments.length];

		for (int i = 0; i < payments.length; i++) {
			paymentAuthorizations[i] = processPayment(payments[i], invoiceId);
		}

		return paymentAuthorizations;
	}

	/*
	 * Validate credit card information using pre-Auth call to the Payment
	 * plugin for level 3 (non-Javadoc)
	 * 
	 * Level 1 - Simple checks on Credit Card Number, name, and mod10 Level 2 -
	 * Address and Security Code validation Level 3 - Check number against a
	 * payment gateway using pre-auth transaction
	 */
	// public CardValidationWS
	// validateCreditCard(com.sapienter.jbilling.server.entity.CreditCardDTO
	// creditCard, ContactWS contact, int level) {
	// CardValidationWS validation = new CardValidationWS(level);
	//
	// /*
	// Level 1 validations (default), card has a name & number, number passes
	// mod10 luhn check
	// */
	//
	// if (StringUtils.isBlank(creditCard.getName())) {
	// validation.addError("Credit card name is missing.", 1);
	// }
	//
	// if (StringUtils.isBlank(creditCard.getNumber())) {
	// validation.addError("Credit card number is missing.", 1);
	//
	// } else {
	// if (creditCard.getNumber().matches("^\\D+$")) {
	// validation.addError("Credit card number is not a valid number.", 1);
	// }
	//
	// if
	// (!com.sapienter.jbilling.common.Util.luhnCheck(creditCard.getNumber())) {
	// validation.addError("Credit card mod10 validation failed.", 1);
	// }
	// }
	//
	//
	// /*
	// Level 2 validations, card has an address & a valid CVV security code
	// */
	// if (level > 1) {
	// if (StringUtils.isBlank(contact.getAddress1())) {
	// validation.addError("Customer address is missing.", 2);
	// }
	//
	// if (StringUtils.isBlank(creditCard.getSecurityCode())) {
	// validation.addError("Credit card CVV security code is missing.", 2);
	//
	// } else {
	// if (creditCard.getSecurityCode().matches("^\\D+$")) {
	// validation.addError("Credit card CVV security code is not a valid number.",
	// 2);
	// }
	// }
	// }
	//
	//
	// /*
	// Level 3 validations, attempted live pre-authorization against payment
	// gateway
	// */
	// if (level > 2) {
	// PaymentAuthorizationDTOEx auth = null;
	//
	// try {
	// // entity id, user id, credit card, amount, currency id, executor
	// auth = new CreditCardBL().validatePreAuthorization(getCallerCompanyId(),
	// new ContactBL(contact.getId()).getEntity().getUserId(),
	// new CreditCardDTO(creditCard),
	// new BigDecimal("0.01"),
	// 1,
	// getCallerId());
	// } catch (PluggableTaskException e) {
	// // log plug-in exception and ignore
	// LOG.error("Exception occurred processing pre-authorization", e);
	// } catch (NamingException e){
	// LOG.error("Exception occurred processing pre-authorization", e);
	// }
	//
	// if (auth == null || !auth.getResult()) {
	// validation.addError("Credit card pre-authorization failed.", 3);
	// }
	// validation.setPreAuthorization(auth);
	// }
	//
	// return validation;
	// }

	@Transactional(readOnly = true)
	public PaymentWS getPayment(Integer paymentId) throws SessionInternalError {
		// get the info from the caller
		Integer languageId = getCallerLanguageId();

		PaymentBL bl = new PaymentBL(paymentId);
		return PaymentBL.getWS(bl.getDTOEx(languageId));
	}
  
	@Transactional(readOnly = true)
	public PaymentWS getLatestPayment(Integer userId)
			throws SessionInternalError {
		PaymentWS retValue = null;
		// get the info from the caller
		Integer languageId = getCallerLanguageId();

		PaymentBL bl = new PaymentBL();
		Integer paymentId = bl.getLatest(userId);
		if (paymentId != null) {
			bl.set(paymentId);
			retValue = PaymentBL.getWS(bl.getDTOEx(languageId));
		}
		return retValue;
	}

	@Transactional(readOnly = true)
	public Integer[] getLastPayments(Integer userId, Integer number)
			throws SessionInternalError {
		return getLastPaymentsPage(userId, number, 0);
	}

	public Integer[] getLastPaymentsPage(Integer userId, Integer limit,
			Integer offset) throws SessionInternalError {
		if (userId == null || limit == null || offset == null) {
			return null;
		}
		Integer languageId = getCallerLanguageId();

		PaymentBL payment = new PaymentBL();
		return payment.getManyWS(userId, limit, offset, languageId);
	}

	@Transactional(readOnly = true)
	public Integer[] getPaymentsByDate(Integer userId, Date since, Date until)
			throws SessionInternalError {
		if (userId == null || since == null || until == null) {
			return null;
		}

		PaymentBL payment = new PaymentBL();
		return payment.getListIdsByDate(userId, since, until);
	}

	@Transactional(readOnly = true)
	public PaymentWS getUserPaymentInstrument(Integer userId)
			throws SessionInternalError {
		return getUserPaymentInstrument(userId, getCallerCompanyId());
	}

	@Transactional(readOnly = true)
	public PaymentWS getUserPaymentInstrument(Integer userId, Integer entityId)
			throws SessionInternalError {
		PaymentDTOEx instrument;
		try {
			instrument = PaymentBL.findPaymentInstrument(entityId, userId);
		} catch (PluggableTaskException e) {
			throw new SessionInternalError(
					"Exception occurred fetching payment info plug-in.", e);
		} catch (TaskException e) {
			throw new SessionInternalError(
					"Exception occurred with plug-in when fetching payment instrument.",
					e);
		}

		if (instrument == null) {
			return null;
		}
		// PaymentDTOEx paymentDTOEx = new PaymentDTOEx(instrument);
		LOG.debug("Instruments are: "
				+ instrument.getPaymentInstruments().size());
		LOG.debug("Instrument payment method is: "
				+ instrument.getPaymentInstruments().iterator().next()
						.getPaymentMethod().getId());
		instrument.setUserId(userId);
		return PaymentBL.getWS(instrument);
	}

	@Transactional(readOnly = true)
	public PaymentWS[] getUserPaymentsPage(Integer userId, Integer limit,
			Integer offset) throws SessionInternalError {

		List<PaymentDTO> paymentsPaged = new PaymentBL().findUserPaymentsPaged(
				getCallerCompanyId(), userId, limit, offset);

        if (paymentsPaged == null) {
            return new PaymentWS[0];
        }

        List<PaymentWS> paymentsWs = new ArrayList<PaymentWS>(
                paymentsPaged.size());
        PaymentBL bl = null;
        for (PaymentDTO dto : paymentsPaged) {
            bl = new PaymentBL(dto.getId());
            PaymentWS wsdto = PaymentBL.getWS(bl
                    .getDTOEx(getCallerLanguageId()));
            paymentsWs.add(wsdto);
        }

        return paymentsWs.toArray(new PaymentWS[paymentsWs.size()]);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueByUser(Integer userId)
            throws SessionInternalError {
        return new PaymentDAS().findTotalRevenueByUser(userId, null, null);
    }

    /**
     * Update the given order, or create it if it doesn't already exist.
     *
     * @param order order to update or create
     * @return order id
     * @throws SessionInternalError
     */
    public Integer copyCreateUpdateOrder(OrderWS order, OrderChangeWS[] orderChanges, CompanyDTO targetEntity, UserDTO newUser) throws SessionInternalError {
        IOrderSessionBean orderSession = Context.getBean(Context.Name.ORDER_SESSION);
        setOrderOnOrderChanges(order, orderChanges);
        validateOrder(order, orderChanges);
        validateLines(order);

	/*#7899 - The order being created is evaluated for subscription lines, if order is containing any subscription products
                  then internal account and order are created for each subscription line
        */
        List<OrderChangeWS> changes = JArrays.toArrayList(orderChanges);
    	createSubscriptionAccountAndOrder(order.getUserId(), order, false, changes);
    	orderChanges = changes != null ? changes.toArray(new OrderChangeWS[changes.size()]) : null;

	//if order has some lines left (that are non subscription) then create the order
        if(order.getOrderLines().length > 0 || orderChanges.length > 0) {
            // do some transformation from WS to DTO
            Map<OrderWS, OrderDTO> wsToDtoOrdersMap = new HashMap<OrderWS, OrderDTO>();
            Map<OrderLineWS, OrderLineDTO> wsToDtoLinesMap = new HashMap<OrderLineWS, OrderLineDTO>();
            OrderBL orderBL = new OrderBL();
            OrderDTO dto = orderBL.getDTO(order, wsToDtoOrdersMap, wsToDtoLinesMap);

            OrderDTO rootOrder = OrderHelper.findRootOrderIfPossible(dto);
            List<OrderChangeDTO> changeDtos = new LinkedList<OrderChangeDTO>();
            List<Integer> deletedChanges = new LinkedList<Integer>();
            convertOrderChangeWsToDto(orderChanges, changeDtos, deletedChanges, wsToDtoOrdersMap, wsToDtoLinesMap);

            Integer rootOrderId = orderSession.createUpdate(targetEntity.getId(), newUser.getId(), targetEntity.getLanguageId(), rootOrder, changeDtos, deletedChanges);
            return wsToDtoOrdersMap.get(order).getId();
        }

    	return null;
    }



	/*
	 * ITEM
	 */
	public Integer createItem(ItemDTOEx item) throws SessionInternalError {

		// Get all descriptions to save-delete them afterwards.
		List<InternationalDescriptionWS> descriptions = item.getDescriptions();
		
		validateItem(item);

		ItemBL itemBL = new ItemBL();

		// Set the creator entity id before creating the DTO object.
		item.setEntityId(getCallerCompanyId());

		ItemDTO dto = itemBL.getDTO(item);

		// Set description to null
		dto.setDescription(null);

		// get the info from the caller
		Integer languageId = getCallerLanguageId();

		validateAssetManagementForItem(dto, null);

		// call the creation
		Integer id = null;

		dto.setGlobal(item.isGlobal());

		id = itemBL.create(dto, languageId);

		dto = itemBL.getEntity();

		// save-delete descriptions
		for (InternationalDescriptionWS description : descriptions) {
			if (description.getLanguageId() != null
					&& description.getContent() != null) {
				if (description.isDeleted()) {
					dto.deleteDescription(description.getLanguageId());
				} else {
					dto.setDescription(description.getContent(),
							description.getLanguageId());
				}
			}
		}
		return id;
	}

	/*
	 * ITEM FOR PLAN
	 */
	public Integer createItem(ItemDTOEx item, boolean isPlan)
			throws SessionInternalError {
		// check if all descriptions are to delete
		List<InternationalDescriptionWS> descriptions = item.getDescriptions();
		boolean noDescriptions = true;
		for (InternationalDescriptionWS description : descriptions) {
			if (!description.isDeleted()) {
				noDescriptions = false;
				break;
			}
		}
		if (noDescriptions) {
			throw new SessionInternalError(
					"Must have a description",
					new String[] { "ItemDTOEx,descriptions,validation.error.is.required" });
		}

		item.setEntityId(getCallerCompanyId());

		ItemBL itemBL = new ItemBL();
		ItemDTO dto = itemBL.getDTO(item);

		// Set description to null
		dto.setDescription(null);

		// get the info from the caller
		Integer languageId = getCallerLanguageId();
		Integer entityId = getCallerCompanyId();
		dto.setEntity(new CompanyDTO(entityId));

		// call the creation
		Integer id = itemBL.create(dto, languageId, isPlan);

		dto = itemBL.getEntity();

		// save-delete descriptions
		for (InternationalDescriptionWS description : descriptions) {
			if (description.getLanguageId() != null
					&& description.getContent() != null) {
				if (description.isDeleted()) {
					dto.deleteDescription(description.getLanguageId());
				} else {
					dto.setDescription(description.getContent(),
							description.getLanguageId());
				}
			}
		}

		return id;
	}

	/**
	 * Retrieves an array of items for the caller's entity.
	 * 
	 * @return an array of items from the caller's entity
	 */
	@Transactional(readOnly = true)
	public ItemDTOEx[] getAllItems() throws SessionInternalError {
		return getAllItemsByEntityId(getCallerCompanyId());
	}

	/**
	 * Implementation of the User Transitions List webservice. This accepts a
	 * start and end date as arguments, and produces an array of data containing
	 * the user transitions logged in the requested time range.
	 * 
	 * @param from
	 *            Date indicating the lower limit for the extraction of
	 *            transition logs. It can be <code>null</code>, in such a case,
	 *            the extraction will start where the last extraction left off.
	 *            If no extractions have been done so far and this parameter is
	 *            null, the function will extract from the oldest transition
	 *            logged.
	 * @param to
	 *            Date indicatin the upper limit for the extraction of
	 *            transition logs. It can be <code>null</code>, in which case
	 *            the extraction will have no upper limit.
	 * @return UserTransitionResponseWS[] an array of objects containing the
	 *         result of the extraction, or <code>null</code> if there is no
	 *         data thas satisfies the extraction parameters.
	 */
	public UserTransitionResponseWS[] getUserTransitions(Date from, Date to)
			throws SessionInternalError {

		UserTransitionResponseWS[] result = null;
		Integer last = null;
		// Obtain the current entity and language Ids

		UserBL user = new UserBL();
		Integer callerId = getCallerId();
		Integer entityId = getCallerCompanyId();
		EventLogger evLog = EventLogger.getInstance();

		if (from == null) {
			last = evLog.getLastTransitionEvent(entityId);
		}

		if (last != null) {
			result = user.getUserTransitionsById(entityId, last, to);
		} else {
			result = user.getUserTransitionsByDate(entityId, from, to);
		}

		if (result == null) {
			LOG.info("Data retrieved but resultset is null");
		} else {
			LOG.info("Data retrieved. Result size = %s", result.length);
		}

		// Log the last value returned if there was any. This happens always,
		// unless the returned array is empty.
		if (result != null && result.length > 0) {
			LOG.info("Registering transition list event");
			evLog.audit(callerId, null, Constants.TABLE_EVENT_LOG, callerId,
					EventLogger.MODULE_WEBSERVICES,
					EventLogger.USER_TRANSITIONS_LIST,
					result[result.length - 1].getId(), result[0].getId()
							.toString(), null);
		}
		return result;
	}

	/**
	 * @return UserTransitionResponseWS[] an array of objects containing the
	 *         result of the extraction, or <code>null</code> if there is no
	 *         data thas satisfies the extraction parameters.
	 */
	public UserTransitionResponseWS[] getUserTransitionsAfterId(Integer id)
			throws SessionInternalError {

		UserTransitionResponseWS[] result = null;
		// Obtain the current entity and language Ids

		UserBL user = new UserBL();
		Integer callerId = getCallerId();
		Integer entityId = getCallerCompanyId();
		EventLogger evLog = EventLogger.getInstance();

		result = user.getUserTransitionsById(entityId, id, null);

		if (result == null) {
			LOG.debug("Data retrieved but resultset is null");
		} else {
			LOG.debug("Data retrieved. Result size = %s", result.length);
		}

		// Log the last value returned if there was any. This happens always,
		// unless the returned array is empty.
		if (result != null && result.length > 0) {
			LOG.debug("Registering transition list event");
			evLog.audit(callerId, null, Constants.TABLE_EVENT_LOG, callerId,
					EventLogger.MODULE_WEBSERVICES,
					EventLogger.USER_TRANSITIONS_LIST,
					result[result.length - 1].getId(), result[0].getId()
							.toString(), null);
		}
		return result;
	}

	@Transactional(readOnly = true)
	public ItemDTOEx getItem(Integer itemId, Integer userId, String pricing) {
		PricingField[] fields = PricingField.getPricingFieldsValue(pricing);
		Integer entityId = getCallerCompanyId();

		ItemBL helper = new ItemBL(itemId);
		List<PricingField> f = JArrays.toArrayList(fields);
		helper.setPricingFields(f);

		Integer languageId = getCallerLanguageId();

		// use the currency of the given user if provided, otherwise
		// default to the currency of the caller (admin user)
		Integer currencyId = (userId != null ? new UserBL(userId)
				.getCurrencyId() : getCallerCurrencyId());

		ItemDTOEx retValue = helper.getWS(helper.getDTO(languageId, userId,
				entityId, currencyId));

		// get descriptions
		retValue.setDescriptions(getAllItemDescriptions(retValue.getId()));
		return retValue;
	}

	@Transactional(readOnly = true)
	public DiscountWS getDiscountWS(Integer discountId) {
		DiscountBL discountBl = new DiscountBL(discountId);
		DiscountWS discountWS = discountBl.getWS(discountBl.getEntity());
		discountWS.setDescriptions(getAllDiscountDescriptions(discountId));
		return discountWS;
	}

	public void deleteDiscount(Integer discountId) throws SessionInternalError {
		DiscountBL bl = new DiscountBL(discountId);
		bl.delete();
	}

	private List<InternationalDescriptionWS> getAllDiscountDescriptions(
			int discountId) {
		JbillingTableDAS tableDas = Context
				.getBean(Context.Name.JBILLING_TABLE_DAS);
		JbillingTable table = tableDas.findByName(Constants.TABLE_DISCOUNT);

		InternationalDescriptionDAS descriptionDas = (InternationalDescriptionDAS) Context
				.getBean(Context.Name.DESCRIPTION_DAS);
		Collection<InternationalDescriptionDTO> descriptionsDTO = descriptionDas
				.findAll(table.getId(), discountId, "description");

		List<InternationalDescriptionWS> descriptionsWS = new ArrayList<InternationalDescriptionWS>();
		for (InternationalDescriptionDTO descriptionDTO : descriptionsDTO) {
			descriptionsWS.add(DescriptionBL.getInternationalDescriptionWS(descriptionDTO));
		}
		return descriptionsWS;
	}

	@Transactional(readOnly = true)
	public UsagePoolWS getUsagePoolWS(Integer usagePoolId) {
		UsagePoolBL usagePoolBl = new UsagePoolBL(usagePoolId);
		UsagePoolWS usagePoolWS = usagePoolBl.getWS(usagePoolBl.getEntity());
		usagePoolWS.setNames(getAllUsagePoolNames(usagePoolId));
		return usagePoolWS;
	}

	private List<InternationalDescriptionWS> getAllUsagePoolNames(
			int usagePoolId) {
		JbillingTableDAS tableDas = Context
				.getBean(Context.Name.JBILLING_TABLE_DAS);
		JbillingTable table = tableDas.findByName(Constants.TABLE_USAGE_POOL);

		InternationalDescriptionDAS descriptionDas = (InternationalDescriptionDAS) Context
				.getBean(Context.Name.DESCRIPTION_DAS);
		Collection<InternationalDescriptionDTO> descriptionsDTO = descriptionDas
				.findAll(table.getId(), usagePoolId, "name");

		List<InternationalDescriptionWS> names = new ArrayList<InternationalDescriptionWS>();
		for (InternationalDescriptionDTO descriptionDTO : descriptionsDTO) {
			names.add(DescriptionBL.getInternationalDescriptionWS(descriptionDTO));
		}
		LOG.debug("names: " + names);
		return names;
	}

	public Integer createUsagePool(UsagePoolWS ws) {
		Integer languageId = getCallerLanguageId();
		return new UsagePoolBL().createOrUpdate(ws, languageId);
	}

	public void updateUsagePool(UsagePoolWS ws) {
		Integer languageId = getCallerLanguageId();
		new UsagePoolBL().createOrUpdate(ws, languageId);
	}

	@Transactional(readOnly = true)
	public UsagePoolWS[] getAllUsagePools() {
		Integer entityId = getCallerCompanyId();
		List<UsagePoolDTO> usagePools = new UsagePoolDAS()
				.findByEntityId(entityId);
		List<UsagePoolWS> usagePoolWSs = new ArrayList<UsagePoolWS>();
		for (UsagePoolDTO usagePool : usagePools) {
			UsagePoolWS ws = UsagePoolBL.getUsagePoolWS(usagePool);
			ws.setNames(getAllUsagePoolNames(usagePool.getId()));
			usagePoolWSs.add(ws);

		}
		return usagePoolWSs.toArray(new UsagePoolWS[usagePoolWSs.size()]);
	}

	public boolean deleteUsagePool(Integer usagePoolId) {
		boolean retVal = false;
		UsagePoolBL usagePoolBl = new UsagePoolBL(usagePoolId);
		retVal = usagePoolBl.delete(usagePoolId);
		return retVal;
	}

	@Transactional(readOnly = true)
	public UsagePoolWS[] getUsagePoolsByPlanId(Integer planId) {
		PlanDTO planDto = new PlanDAS().find(planId);
		List<UsagePoolWS> usagePoolWSs = new ArrayList<UsagePoolWS>();
		for (UsagePoolDTO usagePoolDto : planDto.getUsagePools()) {
			UsagePoolWS ws = UsagePoolBL.getUsagePoolWS(usagePoolDto);
			ws.setNames(getAllUsagePoolNames(usagePoolDto.getId()));
			usagePoolWSs.add(ws);
		}
		Collections.sort(usagePoolWSs, new Comparator<UsagePoolWS>() {
			public int compare(UsagePoolWS usagePool1, UsagePoolWS usagePool2) {
				return usagePool1.getId() - usagePool2.getId();
			}
		});
		return usagePoolWSs.toArray(new UsagePoolWS[usagePoolWSs.size()]);
	}

	private List<InternationalDescriptionWS> getAllItemDescriptions(int itemId) {
		JbillingTableDAS tableDas = Context
				.getBean(Context.Name.JBILLING_TABLE_DAS);
		JbillingTable table = tableDas.findByName(Constants.TABLE_ITEM);

		InternationalDescriptionDAS descriptionDas = (InternationalDescriptionDAS) Context
				.getBean(Context.Name.DESCRIPTION_DAS);
		Collection<InternationalDescriptionDTO> descriptionsDTO = descriptionDas
				.findAll(table.getId(), itemId, "description");

		List<InternationalDescriptionWS> descriptionsWS = new ArrayList<InternationalDescriptionWS>();
		for (InternationalDescriptionDTO descriptionDTO : descriptionsDTO) {
			descriptionsWS.add(DescriptionBL.getInternationalDescriptionWS(descriptionDTO));
		}
		return descriptionsWS;
	}

	public Integer createItemCategory(ItemTypeWS itemType)
			throws SessionInternalError {
		
		if (itemType.getAssetMetaFields() != null) {
			for (MetaFieldWS field : itemType.getAssetMetaFields()) {
				if (field.getDataType().equals(DataType.SCRIPT)
						&& (null == field.getFilename() || field.getFilename()
								.isEmpty())) {
					throw new SessionInternalError(
							"Script Meta Fields must define filename",
							new String[] { "ItemTypeWS,assetMetaFields,metafield.validation.filename.required" });
				}
			}
		}

		Integer entityId = getCallerCompanyId();
		if (!itemType.isGlobal()
				&& CollectionUtils.isEmpty(itemType.getEntities())) {
			ArrayList ents = new ArrayList();
			ents.add(entityId);
			itemType.setEntities(ents);
		}

		AssetStatusBL assetStatusBL = new AssetStatusBL();

		ItemTypeDTO dto = new ItemTypeDTO();
		dto.setDescription(itemType.getDescription());
		dto.setOrderLineTypeId(itemType.getOrderLineTypeId());
		dto.setParent(new ItemTypeDAS().find(itemType.getParentItemTypeId()));
		dto.setGlobal(itemType.isGlobal());
		dto.setOnePerOrder(itemType.isOnePerOrder());
		dto.setOnePerCustomer(itemType.isOnePerCustomer());
		dto.setEntity(new CompanyDAS().find(getCallerCompanyId()));

		List<Integer> entities = new ArrayList<Integer>(0);

		CompanyDAS companyDAS = new CompanyDAS();
		if (!itemType.isGlobal()) {
			entities.addAll(itemType.getEntities());
		}

		dto.setEntities(AssetBL.convertToCompanyDTO(itemType.getEntities()));

		dto.setAllowAssetManagement(itemType.getAllowAssetManagement());
		dto.setAssetIdentifierLabel(itemType.getAssetIdentifierLabel());
		dto.setAssetStatuses(assetStatusBL.convertAssetStatusDTOExes(itemType
				.getAssetStatuses()));

		// Assign asset meta fields to the company that created the category.
		dto.setAssetMetaFields(MetaFieldBL.convertMetaFieldsToDTO(
				itemType.getAssetMetaFields(), getCallerCompanyId()));

		ItemTypeBL.fillMetaFieldsFromWS(dto, itemType);

		validateAssetMetaFields(new HashSet<MetaField>(0),
				dto.getAssetMetaFields());
		validateItemCategoryStatuses(dto);

		entities = new ArrayList<Integer>(0);
		entities.add(getCallerCompanyId());
		entities.addAll(companyDAS.getChildEntitiesIds(getCallerCompanyId()));

		ItemTypeBL itemTypeBL = new ItemTypeBL();
		itemTypeBL.setCallerCompanyId(getCallerCompanyId());
		// Check if the category already exists to throw an error to the user.
		if (itemTypeBL.existsGlobal(getCallerCompanyId(), dto.getDescription())
				|| itemTypeBL.exists(entities, dto.getDescription())) {
			throw new SessionInternalError(
					"The product category already exists with name "
							+ dto.getDescription(),
					new String[] { "ItemTypeWS,name,validation.error.category.already.exists" });
		}

		// a subscription product must allow asset management
		if (dto.getOrderLineTypeId() == Constants.ORDER_LINE_TYPE_SUBSCRIPTION
				&& dto.getAllowAssetManagement() != 1) {
			throw new SessionInternalError(
					"Subscription product category must allow asset management",
					new String[] { "ItemTypeWS,allowAssetManagement,validation.error.subscription.category.asset.management" });
		}

		itemTypeBL.create(dto);

		// we need ids to create descriptions. Can only do it after flush
		for (AssetStatusDTO statusDTO : itemTypeBL.getEntity()
				.getAssetStatuses()) {
			statusDTO.setDescription(statusDTO.getDescription(),
					Constants.LANGUAGE_ENGLISH_ID);
		}

		return itemTypeBL.getEntity().getId();
	}

	public void updateItemCategory(ItemTypeWS itemType)
			throws SessionInternalError {

		if (itemType.getAssetMetaFields() != null) {
			for (MetaFieldWS field : itemType.getAssetMetaFields()) {
				if (field.getDataType().equals(DataType.SCRIPT)
						&& (null == field.getFilename() || field.getFilename()
								.isEmpty())) {
					throw new SessionInternalError(
							"Script Meta Fields must define filename",
							new String[] { "ItemTypeWS,assetMetaFields,metafield.validation.filename.required" });
				}
			}
		}

		UserBL bl = new UserBL(getCallerId());
		Integer executorId = bl.getEntity().getUserId();

		ItemTypeBL itemTypeBL = new ItemTypeBL(itemType.getId(),
				getCallerCompanyId());
		if (!itemType.isGlobal()
				&& CollectionUtils.isEmpty(itemType.getEntities())) {
			List<Integer> ents = new ArrayList<Integer>();
			ents.add(getCallerCompanyId());
			itemType.setEntities(ents);
		}

		AssetStatusBL assetStatusBL = new AssetStatusBL();

		ItemTypeDTO dto = new ItemTypeDTO();
		dto.setDescription(itemType.getDescription());
		dto.setGlobal(itemType.isGlobal());
		dto.setOrderLineTypeId(itemType.getOrderLineTypeId());
		dto.setAllowAssetManagement(itemType.getAllowAssetManagement());
		dto.setAssetIdentifierLabel(itemType.getAssetIdentifierLabel());
		dto.setAssetStatuses(assetStatusBL.convertAssetStatusDTOExes(itemType
				.getAssetStatuses()));
		dto.setOnePerCustomer(itemType.isOnePerCustomer());
		dto.setOnePerOrder(itemType.isOnePerOrder());
		dto.setEntity(new CompanyDAS().find(itemType.getEntityId()));

		List<Integer> entities = new ArrayList<Integer>(0);

		CompanyDAS companyDAS = new CompanyDAS();
		if (!itemType.isGlobal()) {
			entities.addAll(itemType.getEntities());
		}
		dto.setEntities(AssetBL.convertToCompanyDTO(itemType.getEntities()));

		// Assign asset meta fields to the company that created the category.
		dto.setAssetMetaFields(MetaFieldBL.convertMetaFieldsToDTO(
				itemType.getAssetMetaFields(), itemType.getEntityId()));

		ItemTypeBL.fillMetaFieldsFromWS(dto, itemType);

		// check if global category has been marked non-global when their are
		// metafields for children
		if (companyDAS.isRoot(getCallerCompanyId()) // is caller company root?
				&& (itemTypeBL.getEntity().isGlobal() && !itemType.isGlobal()) // has
																				// visibility
																				// of
																				// itemType
																				// decreased?
				&& ItemTypeBL.isChildMetaFieldPresent(itemType,
						getCallerCompanyId())) {
			throw new SessionInternalError(
					"Cannot decrease visibility when child metafields are set",
					new String[] { "ItemTypeWS,global,metafield.validation.global.changed" });
		}

		// validate statuses and meta fields
		validateItemCategoryStatuses(dto);
		validateAssetMetaFields(itemTypeBL.getEntity().getAssetMetaFields(),
				dto.getAssetMetaFields());

		entities = new ArrayList<Integer>(0);
		entities.add(getCallerCompanyId());
		entities.addAll(companyDAS.getChildEntitiesIds(getCallerCompanyId()));

		// make sure that item category names are unique. If the name was
		// changed, then check
		// that the new name isn't a duplicate of an existing category.
		if (!itemTypeBL.getEntity().getDescription()
				.equalsIgnoreCase(itemType.getDescription())) {
			if (itemTypeBL.existsGlobal(getCallerCompanyId(),
					dto.getDescription())
					|| itemTypeBL.exists(entities, dto.getDescription())) {
				throw new SessionInternalError(
						"The product category already exists with name "
								+ dto.getDescription(),
						new String[] { "ItemTypeWS,name,validation.error.category.already.exists" });
			}
		}

		// a subscription product must allow asset management
		if (dto.getOrderLineTypeId() == Constants.ORDER_LINE_TYPE_SUBSCRIPTION
				&& dto.getAllowAssetManagement() != 1) {
			throw new SessionInternalError(
					"Subscription product category must allow asset management",
					new String[] { "ItemTypeWS,allowAssetManagement,validation.error.subscription.category.asset.management" });
		}

		// if the type changed from not allowing asset management to allowing it
		// we check that there is not a
		// product already linked to the category which is already linked
		// another category which allows asset management
		if (dto.getAllowAssetManagement() == 1
				&& itemTypeBL.getEntity().getAllowAssetManagement() == 0) {
			List<Integer> typeIds = itemTypeBL
					.findAllTypesLinkedThroughProduct(itemType.getId());
			if (typeIds.size() > 1) {
				throw new SessionInternalError(
						"The category is linked to a product which can already do asset management",
						new String[] { "ItemTypeWS,allowAssetManagement,product.category.validation.multiple.linked.assetmanagement.types.error" });
			}
		}

		// if the type changed from allowing asset management to not allowing it
		// we check that there is not a
		// product linked to it which has asset management enabled
		if (dto.getAllowAssetManagement() == 0
				&& itemTypeBL.getEntity().getAllowAssetManagement() == 1) {
			for (ItemDTO item : itemTypeBL.getEntity().getItems()) {
				if (item.getAssetManagementEnabled() == 1) {
					throw new SessionInternalError(
							"The category is linked to a product which can already do asset management",
							new String[] { "ItemTypeWS,allowAssetManagement,product.category.validation.product.assetmanagement.enabled" });
				}
			}
		}

		itemTypeBL.update(executorId, dto);

		// we need ids to create descriptions. Can only do it after flush
		for (AssetStatusDTO statusDTO : dto.getAssetStatuses()) {
			statusDTO.setDescription(statusDTO.getDescription(),
					Constants.LANGUAGE_ENGLISH_ID);
		}
	}

	/**
	 * Validation for AssetDTO MetaFields
	 * 
	 * @param currentMetaFields
	 *            - current list of meta fields attached to the asset
	 * @param newMetaFields
	 *            - new meta fields that will be attached to the asset
	 * @throws SessionInternalError
	 */
	private void validateAssetMetaFields(
			Collection<MetaField> currentMetaFields,
			Collection<MetaField> newMetaFields) throws SessionInternalError {
		MetaFieldBL metaFieldBL = new MetaFieldBL();
		Map currentMetaFieldMap = new HashMap(currentMetaFields.size() * 2);
		Set names = new HashSet(currentMetaFields.size() * 2);

		// collect the current meta fields
		for (MetaField dto : currentMetaFields) {
			currentMetaFieldMap.put(dto.getId(), dto);
		}

		// loop through the new metaFields
		for (MetaField metaField : newMetaFields) {
			if (names.contains(metaField.getName())) {
				throw new SessionInternalError(
						"Meta field names must be unique ["
								+ metaField.getName() + "]",
						new String[] { "MetaFieldWS,name,metaField.validation.name.unique,"
								+ metaField.getName() });
			}
			names.add(metaField.getName());

			// if it is already in the DB validate the changes
			if (metaField.getId() > 0) {
				MetaField currentMetaField = (MetaField) currentMetaFieldMap
						.get(metaField.getId());

				// if the type change we have to make sure it is not already
				// used
				boolean checkUsage = !currentMetaField.getDataType().equals(
						metaField.getDataType());
				if (checkUsage
						&& MetaFieldBL.isMetaFieldUsed(EntityType.ASSET,
								metaField.getId())) {
					throw new SessionInternalError(
							"Data Type may not be changes is meta field is used ["
									+ metaField.getName() + "]",
							new String[] { "MetaFieldWS,dataType,metaField.validation.type.change.not.allowed,"
									+ metaField.getName() });
				}
			}
		}
	}

	private Integer zero2null(Integer var) {
		if (var != null && var.intValue() == 0) {
			return null;
		} else {
			return var;
		}
	}

	private Date zero2null(Date var) {
		if (var != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(var);
			if (cal.get(Calendar.YEAR) == 1) {
				return null;
			}
		}

		return var;

	}

	private void validateUser(UserWS newUser) throws SessionInternalError {
		// do the validation
		if (newUser == null) {
			throw new SessionInternalError("Null parameter");
		}
		// C# sends a 0 when it is null ...
		newUser.setCurrencyId(zero2null(newUser.getCurrencyId()));
		newUser.setPartnerRoleId(zero2null(newUser.getPartnerRoleId()));
		newUser.setParentId(zero2null(newUser.getParentId()));
		newUser.setMainRoleId(zero2null(newUser.getMainRoleId()));
		newUser.setLanguageId(zero2null(newUser.getLanguageId()));
		newUser.setStatusId(zero2null(newUser.getStatusId()));

		// todo: additional hibernate validations
		// additional validation
		if (newUser.getMainRoleId().equals(Constants.TYPE_CUSTOMER)
				|| newUser.getMainRoleId().equals(Constants.TYPE_PARTNER)) {
		} else {
			throw new SessionInternalError("Valid user roles are customer (5) "
					+ "and partner (4)");
		}
		if (newUser.getCurrencyId() != null
				&& newUser.getCurrencyId().intValue() <= 0) {
			throw new SessionInternalError("Invalid currency code");
		}
		if (newUser.getStatusId().intValue() <= 0) {
			throw new SessionInternalError("Invalid status code");
		}
	}

	/**
	 * Validate all orders in hierarchy and order changes
	 * 
	 * @param order
	 *            orders hierarchy for validation
	 * @param orderChanges
	 *            order changes for validation
	 * @throws SessionInternalError
	 *             if validation was failed
	 */
	private void validateOrder(OrderWS order, OrderChangeWS[] orderChanges)
			throws SessionInternalError {
		Map<OrderWS, Boolean> ordersWithChanges = new HashMap<OrderWS, Boolean>();
		if (orderChanges != null) {
			for (OrderChangeWS change : orderChanges) {
				validateOrderChange(order, change);
				OrderWS changeOrder;
				if (change.getOrderId() != null) {
					changeOrder = OrderHelper.findOrderInHierarchy(order,
							change.getOrderId());
				} else {
					changeOrder = change.getOrderWS();
				}
				if (changeOrder != null) {
					ordersWithChanges.put(changeOrder, true);
				}
			}
		}
		validateOrder(order, new HashSet<OrderWS>(), ordersWithChanges);
		validateUpdateOrder(order);
	}

    public void validateDiscountLines(OrderDTO order, List<OrderChangeDTO> orderChanges) throws SessionInternalError{
    	 
        if(!order.hasDiscountLines()){
            return;
        }
 
        for(DiscountLineDTO discountLine : order.getDiscountLines()){
            discountLine.setPurchaseOrder(order);
            DiscountDTO discount = discountLine.getDiscount();
 
            if(discountLine.isOrderLevelDiscount()){
                if (discount.getStartDate() != null &&
                        //discount should not be active compared to order
                        discount.getStartDate().after(order.getActiveSince())) {
                    throw new SessionInternalError("Discount Start Date is in future w.r.t. order's active since date.",
                            new String []{ "DiscountWS,startDate,discount.startDate.in.future," +
                                    discount.getCode() + "," + com.sapienter.jbilling.server.util.Util.formatDate
                                            (discount.getStartDate(), order.getUserId()) });
                }
 
                if (discount.getEndDate() != null &&
                        // Discount should be active till order active since date
                        discount.getEndDate().before(order.getActiveSince())) {
                    throw new SessionInternalError("Discount End Date is in before the order active since date.",
                            new String []{ "DiscountWS,endDate,discount.endDate.in.before," +
                                    discount.getCode() + "," + com.sapienter.jbilling.server.util.Util.formatDate
                                            (discount.getEndDate(), order.getUserId()) });
                }
 
            }else if(discountLine.isProductLevelDiscount()){
                OrderChangeDTO orderChange = null;
                ItemDTO item = discountLine.getItem();
 
                for(OrderChangeDTO changeDTO :orderChanges){
                    if(changeDTO.getItem().getId() == item.getId()){
                        orderChange = changeDTO;
                        break;
                    }
                }
 
                if ((discount.getStartDate() != null && orderChange != null) &&
                        discount.getStartDate().after(orderChange.getStartDate())) {
                    throw new SessionInternalError("Discount Start Date is in future w.r.t. order change's start-date of item.",
                            new String []{ "DiscountWS,startDate,discount.startDate.in.future.item," +
                                    discount.getCode() + "," + com.sapienter.jbilling.server.util.Util.formatDate
                                    (discount.getStartDate(), order.getUserId())+","+item.getDescription() });
                }
 
                if ((discount.getEndDate() != null && orderChange != null) &&
                        discount.getEndDate().before(orderChange.getStartDate())) {
                    throw new SessionInternalError("Discount End Date is in before the order-change start date of item.",
                            new String []{ "DiscountWS,endDate,discount.endDate.in.before.item," +
                                    discount.getCode() + "," + com.sapienter.jbilling.server.util.Util.formatDate
                                    (discount.getEndDate(), order.getUserId())+","+item.getDescription() });
                }
 
            }else if(discountLine.isPlanItemLevelDiscount()){
                OrderChangeDTO orderChange = null;
                PlanItemDTO planItem  = discountLine.getPlanItem();
 
                    orderChangeLoop:
                    for(OrderChangeDTO changeDTO :orderChanges){
                        Set<OrderChangePlanItemDTO> orderChangePlanItems = changeDTO.getOrderChangePlanItems();
                        for(OrderChangePlanItemDTO orderChangePlanItem: orderChangePlanItems){
                            if(orderChangePlanItem.getItem().getId() == planItem.getItem().getId()){
                                orderChange = changeDTO;
                                break orderChangeLoop;
                            }
                        }
                    }
 
                if ((discount.getStartDate() != null && orderChange != null) &&
                        discount.getStartDate().after(orderChange.getStartDate())) {
                    throw new SessionInternalError("Discount Start Date is in future w.r.t. order change's start-date of plan item.",
                            new String []{ "DiscountWS,startDate,discount.startDate.in.future.plan.item," +
                                    discount.getCode() + "," + com.sapienter.jbilling.server.util.Util.formatDate
                                    (discount.getStartDate(), order.getUserId())+","+planItem.getItem().getDescription()+
                                    ","+planItem.getPlan().getItem().getDescription() });
                }
 
                if ((discount.getEndDate() != null && orderChange != null) &&
                        discount.getEndDate().before(orderChange.getStartDate())) {
                    throw new SessionInternalError("Discount End Date is in before the order-change start date of plan item.",
                            new String []{ "DiscountWS,endDate,discount.endDate.in.before.plan.item," +
                                    discount.getCode() + "," + com.sapienter.jbilling.server.util.Util.formatDate
                                    (discount.getEndDate(), order.getUserId())+","+planItem.getItem().getDescription()+
                                    ","+planItem.getPlan().getItem().getDescription()});
                }
            }
        }
    }
	
	private void validateOrderChange(OrderWS order, OrderChangeWS orderChange) {

		List<Integer> invoicesForOrder = new OrderProcessDAS().findActiveInvoicesForOrder(order.getId());
        Integer LastBillingProcessId = null;
        try {
            LastBillingProcessId = new BillingProcessBL().getLast(getCallerCompanyId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        BillingProcessDTO lastBillingProcess = new BillingProcessDAS().find(LastBillingProcessId);

		if (orderChange.getStartDate() != null
				&& com.sapienter.jbilling.common.Util.truncateDate(
						orderChange.getStartDate()).before(
						com.sapienter.jbilling.common.Util.truncateDate(order
								.getActiveSince()))) {
			String error = "OrderChangeWS,startDate,validation.error.incorrect.start.date";
			throw new SessionInternalError(
					String.format(
							"Order ActiveSince %s, Incorrect start date %s for order change",
							order.getActiveSince(), orderChange.getStartDate()),
					new String[] { error });
		}

		if (orderChange.getStartDate() != null
				&& null != order.getActiveUntil()
				&& !com.sapienter.jbilling.common.Util.truncateDate(
						order.getActiveUntil()).after(
						com.sapienter.jbilling.common.Util
								.truncateDate(orderChange.getStartDate()))) {
			String error = "OrderChangeWS,startDate,validation.error.incorrect.start.date.expiry";
			throw new SessionInternalError(
					String.format(
							"Order Active Until %s, Incorrect start date %s for order change",
							order.getActiveUntil(), orderChange.getStartDate()),
					new String[] { error });
		}

        if (invoicesForOrder != null && invoicesForOrder.size() > 0) {
            Date endOfProcessPeriod = BillingProcessBL.getEndOfProcessPeriod(lastBillingProcess);
            if (order.getBillingTypeId().equals(Constants.ORDER_BILLING_POST_PAID)){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endOfProcessPeriod);
                calendar.set(Calendar.MONTH, endOfProcessPeriod.getMonth() - 1);
                endOfProcessPeriod = calendar.getTime();
            }

            if(orderChange.getStartDate() != null && com.sapienter.jbilling.common.Util.truncateDate(
                endOfProcessPeriod).after(com.sapienter.jbilling.common.Util.truncateDate(
                    orderChange.getStartDate()))) {
                    String error = "validation.error.incorrect.effective.date";
                    throw new SessionInternalError(
                        String.format(
                            "Order ActiveSince %s, Incorrect start date %s for order change",
                             order.getActiveSince(), orderChange.getStartDate()),
                             new String[] { error });
                }
        }

		if (orderChange.getItemId() == null) {
			String error = "OrderChangeWS,itemId,validation.error.is.required";
			throw new SessionInternalError("Item is required for order change",
					new String[] { error });
		}
		if (orderChange.getUserAssignedStatusId() == null) {
			String error = "OrderChangeWS,userAssignedStatus,validation.error.is.required";
			throw new SessionInternalError(
					"User assigned status is required for order change",
					new String[] { error });
		}
		if (orderChange.getOrderId() == null
				&& orderChange.getOrderWS() == null) {
			String error = "OrderChangeWS.order.validation.error.is.required";
			throw new SessionInternalError("OrderChange validation error",
					new String[] { error });
		}
        ItemDTO itemDTO = new ItemBL(orderChange.getItemId()).getEntity();
		MetaFieldBL.validateMetaFields(itemDTO.getEntity().getLanguageId(), itemDTO.getOrderLineMetaFields(),
                orderChange.getMetaFields());
	}

	private void validateOrder(OrderWS order, Set<OrderWS> alreadyValidated,
			Map<OrderWS, Boolean> ordersWithChanges)
			throws SessionInternalError {
		if (alreadyValidated.contains(order))
			return;
		// prevent cycles in initial hierarchy if exists
		alreadyValidated.add(order);

		LOG.debug("Validating order: " + order);
		if (order == null) {
			throw new SessionInternalError("Null parameter");
		}

		boolean orderCreate = order.getId() == null;

		
		if (order.getOrderLines().length == 1) {
			
			ItemDTO checkItem = new ItemDAS().find(order.getOrderLines()[0].getItemId());
			if (null != checkItem.getPrice(new Date(), getCallerCompanyId()) && checkItem.getPrice(new Date(), getCallerCompanyId()).getType() == PriceModelStrategy.LINE_PERCENTAGE) {
				throw new SessionInternalError(
						"Order can not create for line percentage product",
						new String[] { "validation.error.order.linePercentage.product" });
			}
		}

		if (order.getUserCode() != null && order.getUserCode().length() > 0) {
			UserBL userBL = new UserBL();
			if (userBL.findUserCodeForIdentifier(order.getUserCode(),
					getCallerCompanyId()) == null) {
				throw new SessionInternalError(
						"Order validation failed. User Code does not exist",
						new String[] { "OrderWS,userCode,validation.error.userCode.not.exist,"
								+ order.getUserCode() });
			}
		}

		for (OrderLineWS orderLineWs : order.getOrderLines()) {
			if (orderLineWs.getChildLines() != null
					&& orderLineWs.getChildLines().length == 1
					&& orderLineWs.getChildLines()[0].isPercentage())
				throw new SessionInternalError(
						"Line percentage item can not added as a sub order",
						new String[] { "OrderLineWS,itemId,validation.order.line.not.added.line.percentage.item,"
								+ orderLineWs.getItemId() });
		}

		if (order.getCancellationMinimumPeriod() != null || (order.getCancellationFeeType() != null && !order.getCancellationFeeType().trim().isEmpty())) {
			List<String> errmsgsList = new ArrayList<>();
			if (order.getCancellationMinimumPeriod() == null) {
				errmsgsList.add("OrderWS,cancellationMinimumPeriod,cancellationFee.minimum.empty");
			}

			if (order.getCancellationFeeType() == null || order.getCancellationFeeType().trim().isEmpty()) {
				errmsgsList.add("OrderWS,cancellationFeeType,cancellationFee.type.empty");
			}

			if (order.getCancellationFeeType().equalsIgnoreCase(FLAT.getCancellationFeeType()) && order.getCancellationFee() == null) {
				errmsgsList.add("OrderWS,cancellationFee,cancellationFee.fee.empty");

			}
			if (order.getCancellationFeeType().equalsIgnoreCase(PERCENTAGE.getCancellationFeeType()) && order.getCancellationFeePercentage() == null) {
				errmsgsList.add("OrderWS,cancellationFeePercentage,cancellationFee.fee.percentage.empty");
			}
			if (errmsgsList.size() > 0)
				throw new SessionInternalError("Error setting cancellation Fee field(s)", errmsgsList.toArray(new String[errmsgsList.size()]));
		}

		validateProrating(order);
		// meta fields validation
		MetaFieldBL.validateMetaFields(getCallerLanguageId(), getCallerCompanyId(), EntityType.ORDER,
				order.getMetaFields());

		order.setUserId(zero2null(order.getUserId()));
		order.setPeriod(zero2null(order.getPeriod()));
		order.setBillingTypeId(zero2null(order.getBillingTypeId()));

		// Setup a default order status if there isnt one
		if (order.getOrderStatusWS() == null
				|| order.getOrderStatusWS().getId() == null) {
			OrderStatusWS os = new OrderStatusWS();
			// #7853 - If no order statuses are configured thro' the
			// configuration menu an exception is shown on the 'create order'
			// UI.
			// Following exception handling added to take care of the issue.
			try {
				os.setId(new OrderStatusDAS().getDefaultOrderStatusId(
						OrderStatusFlag.INVOICE, getCallerCompanyId()));
			} catch (Exception e) {
				throw new SessionInternalError(
						"Order validation failed. No order status found for the order",
						new String[] { "OrderWS,orderStatus,No order status found for the order" });
			}
			order.setOrderStatusWS(os);
		}

		order.setCurrencyId(zero2null(order.getCurrencyId()));
		order.setNotificationStep(zero2null(order.getNotificationStep()));
		order.setDueDateUnitId(zero2null(order.getDueDateUnitId()));
		order.setPrimaryOrderId(zero2null(order.getPrimaryOrderId()));
		// Bug Fix: 1385: Due Date may be zero
		// order.setDueDateValue(zero2null(order.getDueDateValue()));
		order.setDfFm(zero2null(order.getDfFm()));
		order.setAnticipatePeriods(zero2null(order.getAnticipatePeriods()));
		order.setActiveSince(zero2null(order.getActiveSince()));
		order.setActiveUntil(zero2null(order.getActiveUntil()));
		order.setNextBillableDay(zero2null(order.getNextBillableDay()));
		order.setLastNotified(null);

		// CXF seems to pass empty array as null
		if (order.getOrderLines() == null) {
			LOG.debug("Order Lines == null");
			order.setOrderLines(new OrderLineWS[0]);
		}
		if (order.getChildOrders() == null) {
			order.setChildOrders(null);
		}

		// todo: additional hibernate validations
		// the lines
		if (orderCreate && !ordersWithChanges.containsKey(order)) {
			IsNotEmptyOrDeletedValidator validator = new IsNotEmptyOrDeletedValidator();
			if (!validator.isValid(order.getOrderLines(), null)) {
				throw new SessionInternalError(
						"Order validation failed",
						new String[] { "OrderWS,orderLines,validation.error.empty.lines" });
			}
		}

		for (int f = 0; f < order.getOrderLines().length; f++) {
			OrderLineWS line = order.getOrderLines()[f];
			if (line.getUseItem() == null) {
				line.setUseItem(false);
			}
			line.setItemId(zero2null(line.getItemId()));
			String error = "";
			// if use the item, I need the item id
			if (line.getUseItem()) {
				if (line.getItemId() == null
						|| line.getItemId().intValue() == 0) {
					error += "OrderLineWS: if useItem == true the itemId is required - ";
				}
				/*
				 * if (line.getQuantityAsDecimal() == null ||
				 * BigDecimal.ZERO.compareTo(line.getQuantityAsDecimal()) == 0)
				 * { error +=
				 * "OrderLineWS: if useItem == true the quantity is required - "
				 * ; }
				 */
			} else {
				// I need the amount and description
				if (line.getAmount() == null) {
					error += "OrderLineWS: if useItem == false the item amount "
							+ "is required - ";
				}
				if (line.getDescription() == null
						|| line.getDescription().length() == 0) {
					error += "OrderLineWS: if useItem == false the description "
							+ "is required - ";
				}
			}

			// validate meta fields
			if (line.getItemId() != null) {
                ItemDTO item = new ItemBL(line.getItemId())
                        .getEntity();
				MetaFieldBL.validateMetaFields(item.getEntity().getLanguageId(), item.getOrderLineMetaFields(), line
                        .getMetaFields());
			}

			// validation needed only during order creation
			if (orderCreate) {
				// check if the number of assets equals the quantity
				PlanDAS planDAS = new PlanDAS();
				// if order line is not a plan
				List<PlanDTO> plans = planDAS.findByItemId(line.getItemId());
				if (plans.size() == 0) {
					if (line.getDeleted() == 0
							&& line.getAssetIds() != null
							&& line.getAssetIds().length > 0
							&& Math.abs(line.getAssetIds().length
									- new BigDecimal(line.getQuantity())
											.floatValue()) > 0.0001) {
						error += "OrderLineWS: number of assets != quantity - ";
					}
				}

			}

			ItemBL itemBL = new ItemBL(line.getItemId());
			ItemDTO item = itemBL.getEntity();
			if (item != null) {
				UserBL userBL = new UserBL(order.getUserId());
				if (!item.isStandardAvailability()
						&& !item.getAccountTypeAvailability().contains(
								userBL.getAccountType())) {

					error += "OrderLineWS: The item is not available for the selected customer";

				}
			}

			if (error.length() > 0) {
				LOG.debug("Error occurred processing order lines");
				throw new SessionInternalError(error);
			}
		}
		if (order.getParentOrder() != null) {
			validateOrder(order.getParentOrder(), alreadyValidated,
					ordersWithChanges);
		}
		if (order.getChildOrders() != null) {
			for (OrderWS childOrder : order.getChildOrders()) {
				validateOrder(childOrder, alreadyValidated, ordersWithChanges);
			}
		}
	}

	private void validateHierarchy(OrderDTO orderDTO) {
		OrderHierarchyValidator validator = new OrderHierarchyValidator();
		validator.buildHierarchy(orderDTO);
		String error = validator.validate();
		if (error != null) {
			throw new SessionInternalError(
					"Error in order hierarchy: " + error,
					new String[] { error });
		}
	}

	private InvoiceDTO doCreateInvoice(Integer orderId) {
		try {
			BillingProcessBL process = new BillingProcessBL();
			InvoiceDTO invoice = process.generateInvoice(orderId, null,
					getCallerId());
			// LOG.debug("Invoice=== " +invoice);
			return invoice;
		} catch (Exception e) {
			LOG.debug("WS - create invoice:", e);
			throw new SessionInternalError(
					"Error while generating a new invoice");
		}
	}

	private PaymentDTOEx doPayInvoice(InvoiceDTO invoice,
			PaymentInformationDTO creditCardInstrument)
			throws SessionInternalError {

		if (invoice.getBalance() == null
				|| BigDecimal.ZERO.compareTo(invoice.getBalance()) >= 0) {
			LOG.warn("Can not pay invoice: %s, balance: %s", invoice.getId(),
					invoice.getBalance());
			return null;
		}

		IPaymentSessionBean payment = (IPaymentSessionBean) Context
				.getBean(Context.Name.PAYMENT_SESSION);
		PaymentDTOEx paymentDto = new PaymentDTOEx();
		paymentDto.setIsRefund(0);
		paymentDto.setAmount(invoice.getBalance());
		paymentDto.setCurrency(new CurrencyDAS().find(invoice.getCurrency()
				.getId()));
		paymentDto.setUserId(invoice.getBaseUser().getUserId());

		creditCardInstrument.setPaymentMethod(new PaymentMethodDAS()
				.find(com.sapienter.jbilling.common.Util
						.getPaymentMethod(new PaymentInformationBL()
								.getStringMetaFieldByType(creditCardInstrument,
										MetaFieldType.PAYMENT_CARD_NUMBER))));

		paymentDto.getPaymentInstruments().clear();
		paymentDto.getPaymentInstruments().add(creditCardInstrument);

		paymentDto.setPaymentDate(new Date());

		// make the call
		payment.processAndUpdateInvoice(paymentDto, invoice.getId(),
				getCallerId());

		return paymentDto;
	}

	/**
	 * Conveniance method to find a credit card
	 */
	private PaymentInformationDTO getCreditCard(Integer userId) {
		LOG.debug("Finding credit card for user [%s]", userId);
		if (userId == null) {
			return null;
		}

		PaymentInformationDTO result = null;
		try {
			UserBL user = new UserBL(userId);
			Integer entityId = user.getEntityId(userId);
			if (user.getDto().getPaymentInstruments().size() > 0) {
				// find it
				PaymentDTOEx paymentDto = PaymentBL.findPaymentInstrument(
						entityId, userId);
				// it might have a credit card, but it might not be valid or
				// just not found by the plug-in
				if (paymentDto != null) {
					LOG.debug("Found payment [%s] instruments", paymentDto
							.getPaymentInstruments().size());
					result = new PaymentInformationBL()
							.findCreditCard(paymentDto.getPaymentInstruments());
					LOG.debug("Found credit card %s",
							result != null ? result.getId() : result);
				}
			}
		} catch (Exception e) { // forced by checked exceptions :(
			LOG.error("WS - finding a credit card", e);
			throw new SessionInternalError(
					"Error finding a credit card for user: " + userId);
		}

		return result;
	}

	private OrderWS doCreateOrder(OrderWS order, OrderChangeWS[] orderChanges,
			boolean create) throws SessionInternalError {
		LOG.debug("Entering doCreateOrder()");
		validateOrder(order, orderChanges);
		// do some transformation from WS to DTO
		Map<OrderWS, OrderDTO> wsToDtoOrdersMap = new HashMap<OrderWS, OrderDTO>();
		Map<OrderLineWS, OrderLineDTO> wsToDtoLinesMap = new HashMap<OrderLineWS, OrderLineDTO>();
		OrderBL orderBL = new OrderBL();
		OrderDTO dto = orderBL.getDTO(order, wsToDtoOrdersMap, wsToDtoLinesMap);
		OrderDTO rootOrder = OrderHelper.findRootOrderIfPossible(dto);
		List<OrderChangeDTO> orderChangeDtos = new LinkedList<OrderChangeDTO>();
		convertOrderChangeWsToDto(orderChanges, orderChangeDtos, null,
				wsToDtoOrdersMap, wsToDtoLinesMap);

		Date onDate = com.sapienter.jbilling.common.Util
				.truncateDate(new Date());
		OrderDTO targetOrder = OrderBL.updateOrdersFromDto(null, rootOrder);
		Map<OrderLineDTO, OrderChangeDTO> appliedChanges = OrderChangeBL
				.applyChangesToOrderHierarchy(targetOrder, orderChangeDtos,
						onDate, true, getCallerCompanyId());

		// get the info from the caller
		Integer executorId = getCallerId();
		Integer entityId = getCallerCompanyId();
		UserBL userBl = new UserBL(order.getUserId());
		Integer languageId = userBl.getEntity().getLanguageIdField();

		// linked set to preserve hierarchy order in collection, from root to
		// child
		LinkedHashSet<OrderDTO> ordersForUpdate = OrderHelper
				.findOrdersInHierarchyFromRootToChild(targetOrder);

		// process the lines and let the items provide the order line details
		LOG.debug("Processing order lines");
		// recalculate from root order to child orders
		for (OrderDTO updatedOrder : ordersForUpdate) {
			OrderBL bl = new OrderBL();
			List<PricingField> pricingFields = updatedOrder.getPricingFields();
			bl.processLines(
					updatedOrder,
					languageId,
					entityId,
					updatedOrder.getBaseUserByUserId().getId(),
					updatedOrder.getCurrencyId(),
					updatedOrder.getPricingFields() != null ? PricingField
							.setPricingFieldsValue(pricingFields
									.toArray(new PricingField[pricingFields
											.size()])) : null);
			bl.set(updatedOrder);
			bl.recalculate(entityId);
		}
		OrderDTO inputOrder = wsToDtoOrdersMap.get(order);
		OrderWS resultWs = null;
		if (create) {
			LOG.debug("creating order");

			// validate final hierarchy
			validateHierarchy(targetOrder);
			validateDiscountLines(rootOrder, orderChangeDtos);
			Integer id = orderBL.create(entityId, executorId, inputOrder,
					appliedChanges);
			orderBL.validateDiscountLines();
			// save order changes
			OrderChangeBL orderChangeBL = new OrderChangeBL();
			orderChangeBL.updateOrderChanges(entityId, orderChangeDtos,
					new LinkedList<Integer>(), onDate);

			orderBL.set(id);
			resultWs = orderBL.getWS(languageId);
		} else {
			orderBL.set(inputOrder);
			resultWs = getWSFromOrder(orderBL, languageId);
		}
		// create discount order lines in case of amount and percentage
		// discounts
		// applied at order level or line level, adds to order.orderLines
		createDiscountOrderLines(resultWs, languageId);

		resultWs = ratePlanBundledItems(resultWs, userBl.getEntity());
		return resultWs;
	}

	/**
	 * Convert input array of order change ws objects to dto object, collect
	 * deleted changes ids
	 * 
	 * @param orderChanges
	 *            input order change ws objects
	 * @param changeDtos
	 *            output order change dto objects
	 * @param deletedChanges
	 *            output deleted order change ids
	 * @param wsToDtoOrdersMap
	 *            map from ws to dto for orders
	 * @param wsToDtoLinesMap
	 *            map from ws to dto for order lines
	 */
	private void convertOrderChangeWsToDto(OrderChangeWS[] orderChanges,
			List<OrderChangeDTO> changeDtos, List<Integer> deletedChanges,
			Map<OrderWS, OrderDTO> wsToDtoOrdersMap,
			Map<OrderLineWS, OrderLineDTO> wsToDtoLinesMap) {
		if (orderChanges != null) {
			// process parent changes before child
			Arrays.sort(orderChanges, new Comparator<OrderChangeWS>() {
				public int compare(OrderChangeWS left, OrderChangeWS right) {
					if (left.getParentOrderChange() == null
							&& left.getParentOrderChangeId() == null
							&& (right.getParentOrderChange() != null || right
									.getParentOrderChangeId() != null))
						return -1;
					if ((left.getParentOrderChange() != null || left
							.getParentOrderChangeId() != null)
							&& right.getParentOrderChange() == null
							&& right.getParentOrderChangeId() == null)
						return 1;
					return 0;
				}
			});
			Map<OrderChangeWS, OrderChangeDTO> wsToDtoChangesMap = new HashMap<OrderChangeWS, OrderChangeDTO>();
			for (OrderChangeWS change : orderChanges) {
				if (change.getId() != null && change.getDelete() > 0) {
					if (deletedChanges != null) {
						deletedChanges.add(change.getId());
					}
				} else {
					OrderChangeDTO orderChange = OrderChangeBL.getDTO(change,
							wsToDtoChangesMap, wsToDtoOrdersMap,
							wsToDtoLinesMap);
					orderChange.setUser(new UserDAS().find(getCallerId()));
					orderChange.setStatus(null);
					changeDtos.add(orderChange);
				}
			}
		}
	}

	/**
	 * This function will set adjusted price on order lines post discount. It
	 * will be used to update orderWs.lines array which excludes any lines for
	 * plan bundle items and will contain lines for products and plan
	 * subscription item.
	 * 
	 * @param order
	 * @param languageId
	 * @return
	 */
	private void createDiscountOrderLines(OrderWS order, Integer languageId) {

		// TODO List<OrderLineWS> discountOrderLines = new
		// ArrayList<OrderLineWS>(0);
		BigDecimal adjustedTotal = order.getTotalAsDecimal();

		for (DiscountLineWS discountLine : order.getDiscountLines()) {
			DiscountDTO discount = new DiscountBL(discountLine.getDiscountId())
					.getEntity();
			if (discount != null
					&& (discount.isAmountBased() || discount
							.isPercentageBased())) {
				if (discountLine.isOrderLevelDiscount()
						|| discountLine.isProductLevelDiscount()) {

					BigDecimal discountAmount = BigDecimal.ZERO;

					if (discountLine.isProductLevelDiscount()) {

						ItemDTO itemDto = new ItemDAS().find(discountLine
								.getItemId());
						// need to pick up line level amount to find out
						// discount amount
						for (OrderLineWS orderLine : order.getOrderLines()) {
							if (orderLine.getItemId() == itemDto.getId()) {
								// as 1st iteration, we go by taking 1st line
								// with matching item
								if (discount.isPercentageBased()) {
									discountAmount = orderLine
											.getAmountAsDecimal()
											.multiply(discount.getRate())
											.divide(new BigDecimal(100));
								} else {
									// amount based
									discountAmount = discount.getRate();
								}
								BigDecimal adjustedPrice = orderLine
										.getAmountAsDecimal().subtract(
												discountAmount);
								adjustedPrice = adjustedPrice.setScale(
										Constants.BIGDECIMAL_SCALE_STR,
										Constants.BIGDECIMAL_ROUND);
								orderLine.setAdjustedPrice(adjustedPrice);
								break;
							}
						}

					} else if (discountLine.isOrderLevelDiscount()) {

						if (discount.isPercentageBased()) {
							discountAmount = order.getTotalAsDecimal()
									.multiply(discount.getRate())
									.divide(new BigDecimal(100));
						} else {
							// amount based
							discountAmount = discount.getRate();
						}
						// 4328 - discount amount is set in negative
						discountLine.setDiscountAmount(discountAmount.negate()
								.toString());
					}

					// keep decrementing adjustedTotal for each discount at
					// order/product line level
					adjustedTotal = adjustedTotal.subtract(discountAmount);
					adjustedTotal = adjustedTotal.setScale(
							Constants.BIGDECIMAL_SCALE_STR,
							Constants.BIGDECIMAL_ROUND);
					order.setAdjustedTotal(adjustedTotal);
				}
			}
		}
	}

	/**
	 * This function will get the OrderWS[] populated in the OrderWS parameter
	 * for bundleItems. It will look at planBundleItems from OrderWS, loop on
	 * the same and rate each line looking at the item rate or percentage.
	 * 
	 * @param ws
	 * @return
	 */
	private OrderWS ratePlanBundledItems(OrderWS ws, UserDTO user) {

		List<OrderLineWS> retValue = new ArrayList<OrderLineWS>();

		OrderLineWS temp = null;
		for (OrderLineWS line : ws.getOrderLines()) {

			ItemDTO item = new ItemDAS().find(line.getItemId());

			if (item != null && item.hasPlanItems()) {

				// determined that this is plan
				List<PlanItemDTO> planBundledItems = new PlanDAS()
						.findPlanByItemId(line.getItemId()).getPlanItems();
				for (PlanItemDTO planItem : planBundledItems) {

					ItemDTO bundleItem = planItem.getItem();

					OrderLineWS orderLine = new OrderLineWS();
					orderLine.setItemId(planItem.getItem().getId());
					BigDecimal bundleItemQuantity = planItem.getBundle()
							.getQuantity()
							.multiply(line.getQuantityAsDecimal());
					orderLine.setQuantity(bundleItemQuantity);

					bundleItem = new ItemBL(bundleItem.getId()).getDTO(user
							.getLanguageIdField(), user.getId(), user
							.getCompany().getId(), user.getCurrencyId());

					// First we take all plan items with rate.
					PriceModelDTO priceModelDTO = bundleItem.getPrice(
							new Date(), getCallerCompanyId());
					if (priceModelDTO == null
							|| !(priceModelDTO.getType() == PriceModelStrategy.LINE_PERCENTAGE)) {

						BigDecimal bundleItemPrice = null != planItem.getPrice(
								new Date()).getRate() ? planItem.getPrice(
								new Date()).getRate() : (null != bundleItem
								.getPrice() ? bundleItem.getPrice()
								: BigDecimal.ZERO);
						BigDecimal bundleItemAmount = bundleItemPrice
								.multiply(bundleItemQuantity);
						orderLine.setPrice(bundleItemPrice);
						orderLine.setAmount(bundleItemAmount);

						BigDecimal adjustedPrice = bundleItemAmount;
						// one bundle item id can have multiple discounts, so
						// check all discount lines and adjust the price
						if (ws.hasDiscountLines()) {

							for (DiscountLineWS dline : ws.getDiscountLines()) {
								if (dline.isPlanItemLevelDiscount()) {
									if (dline.getPlanItemId().intValue() == planItem
											.getId().intValue()) {
										DiscountDTO discount = new DiscountDAS()
												.find(dline.getDiscountId());
										if (discount.isAmountBased()) {
											adjustedPrice = adjustedPrice
													.subtract(discount
															.getRate()
															.multiply(
																	bundleItemQuantity));
											orderLine
													.setAdjustedPrice(adjustedPrice
															.setScale(
																	Constants.BIGDECIMAL_SCALE_STR,
																	Constants.BIGDECIMAL_ROUND)
															.toString());
										} else if (discount.isPercentageBased()) {
											adjustedPrice = adjustedPrice
													.subtract((bundleItemAmount
															.multiply(discount
																	.getRate()
																	.divide(new BigDecimal(
																			100)))));
											orderLine
													.setAdjustedPrice(adjustedPrice
															.setScale(
																	Constants.BIGDECIMAL_SCALE_STR,
																	Constants.BIGDECIMAL_ROUND)
															.toString());
										}
									}
								}
							}
						}

					} else {
						// percentage item, only set the price, we dont have
						// amount and hence adjusted price here.
						orderLine.setPrice(bundleItem.getPrice());
					}

					retValue.add(orderLine);
				}
			}
		}

		OrderLineWS[] bundleItems = new OrderLineWS[1];
		ws.setPlanBundledItems(retValue.toArray(bundleItems));
		return ws;
	}

	private OrderWS getWSFromOrder(OrderBL bl, Integer languageId) {
		OrderWS retValue = bl.getWS(languageId);
		// todo: clear invoices to fit original code of current method to
		// OrderBL.convertToWS
		// possible we can remove this array cleaning
		retValue.setGeneratedInvoices(new InvoiceWS[] {});
		return retValue;
	}

	private InvoiceDTO findInvoice(Integer invoiceId) {
		final InvoiceDTO invoice;
		invoice = new InvoiceBL(invoiceId).getEntity();
		return invoice;
	}

	@Transactional(readOnly = true)
	// TODO: This method is not secured or in a jUnit test
	public InvoiceWS getLatestInvoiceByItemType(Integer userId,
			Integer itemTypeId) throws SessionInternalError {
		InvoiceWS retValue = null;
		try {
			if (userId == null) {
				return null;
			}
			InvoiceBL bl = new InvoiceBL();
			Integer invoiceId = bl.getLastByUserAndItemType(userId, itemTypeId);
			if (invoiceId != null) {
				retValue = bl.getWS(new InvoiceDAS().find(invoiceId));
			}
			return retValue;
		} catch (Exception e) { // forced by SQLException
			LOG.error("Exception in web service: getting latest invoice"
					+ " for user " + userId, e);
			throw new SessionInternalError("Error getting latest invoice");
		}
	}

	/**
	 * Return 'number' most recent invoices that contain a line item with an
	 * item of the given item type.
	 */
	@Transactional(readOnly = true)
	public Integer[] getLastInvoicesByItemType(Integer userId,
			Integer itemTypeId, Integer number) throws SessionInternalError {
		if (userId == null || itemTypeId == null || number == null) {
			return null;
		}

		InvoiceBL bl = new InvoiceBL();
		return bl.getManyByItemTypeWS(userId, itemTypeId, number);
	}

	@Transactional(readOnly = true)
	public OrderWS getLatestOrderByItemType(Integer userId, Integer itemTypeId)
			throws SessionInternalError {
		if (userId == null) {
			throw new SessionInternalError("User id can not be null");
		}
		if (itemTypeId == null) {
			throw new SessionInternalError("itemTypeId can not be null");
		}
		OrderWS retValue = null;
		// get the info from the caller
		Integer languageId = getCallerLanguageId();

		// now get the order
		OrderBL bl = new OrderBL();
		Integer orderId = bl.getLatestByItemType(userId, itemTypeId);
		if (orderId != null) {
			bl.set(orderId);
			retValue = bl.getWS(languageId);
		}
		return retValue;
	}

	@Transactional(readOnly = true)
	public Integer[] getLastOrdersByItemType(Integer userId,
			Integer itemTypeId, Integer number) throws SessionInternalError {
		if (userId == null || number == null) {
			return null;
		}
		OrderBL order = new OrderBL();
		return order.getListIdsByItemType(userId, itemTypeId, number);
	}

	@Transactional(readOnly = true)
	public String isUserSubscribedTo(Integer userId, Integer itemId) {
		OrderDAS das = new OrderDAS();
		BigDecimal quantity = das.findIsUserSubscribedTo(userId, itemId);
		return quantity != null ? quantity.toString() : null;
	}

	@Transactional(readOnly = true)
	public Integer[] getUserItemsByCategory(Integer userId, Integer categoryId) {
		Integer[] result = null;
		OrderDAS das = new OrderDAS();
		result = das.findUserItemsByCategory(userId, categoryId);
		return result;
	}
	
	@Transactional(readOnly = true)
	public ItemDTOEx[] getItemByCategory(Integer itemTypeId) {
		return new ItemBL().getAllItemsByType(itemTypeId, getCallerCompanyId());
	}

	@Transactional(readOnly = true)
	public ItemTypeWS getItemCategoryById(Integer id) {
		ItemTypeBL itemTypeBL = new ItemTypeBL();
		ItemTypeDTO itemTypeDTO = itemTypeBL.getById(id, getCallerCompanyId(),
				true);
		return ItemTypeBL.toWS(itemTypeDTO);
	}

	@Transactional(readOnly = true)
	public ItemTypeWS[] getAllItemCategories() {
		return getAllItemCategoriesByEntityId(getCallerCompanyId());
	}

	public ValidatePurchaseWS validatePurchase(Integer userId, Integer itemId,
			String fields) {
		Integer[] itemIds = null;
		if (itemId != null) {
			itemIds = new Integer[] { itemId };
		}

		String[] fieldsArray = null;
		if (fields != null) {
			fieldsArray = new String[] { fields };
		}

		return doValidatePurchase(userId, itemIds, fieldsArray);
	}

	public ValidatePurchaseWS validateMultiPurchase(Integer userId,
			Integer[] itemIds, String[] fields) {

		return doValidatePurchase(userId, itemIds, fields);
	}

	private ValidatePurchaseWS doValidatePurchase(Integer userId,
			Integer[] itemIds, String[] fields) {

		if (userId == null || (itemIds == null && fields == null)) {
			return null;
		}

		UserBL user = new UserBL(userId);

		List<List<PricingField>> fieldsList = null;
		if (fields != null) {
			fieldsList = new ArrayList<List<PricingField>>(fields.length);
			for (int i = 0; i < fields.length; i++) {
				fieldsList.add(JArrays.toArrayList(PricingField
						.getPricingFieldsValue(fields[i])));
			}
		}

		List<Integer> itemIdsList = null;
		List<BigDecimal> prices = new ArrayList<BigDecimal>();
		List<ItemDTO> items = new ArrayList<ItemDTO>();

		if (itemIds != null) {
			itemIdsList = JArrays.toArrayList(itemIds);
		} else if (fields != null) {
			itemIdsList = new LinkedList<Integer>();

			for (List<PricingField> pricingFields : fieldsList) {
				try {
					// Since there is no item, run the mediation process rules
					// to create line/s.

                    //TODO MODULARIZATION: THIS LOGIC LAUNCH OLD MEDIATION 2 JOBS...
					// fields need to be in records
					CallDataRecord record = new CallDataRecord();
					for (PricingField field : pricingFields) {
						record.addField(field, false); // don't care about isKey
					}
					PluggableTaskManager<IMediationProcess> tm = new PluggableTaskManager<>(
							getCallerCompanyId(),
							Constants.PLUGGABLE_TASK_MEDIATION_PROCESS);

					IMediationProcess processTask = tm.getNextClass();

					MediationStepResult result = new MediationStepResult();
					result.setUserId(user.getEntity().getUserId());
					result.setCurrencyId(user.getEntity().getCurrencyId());
					result.setEventDate(new Date());
                    result.setPersist(false);

					processTask.process(record, result);

					// from the lines, get the items and prices
					for (Object lineObject : result.getDiffLines()) {
                        OrderLineDTO line = (OrderLineDTO) lineObject;
						items.add(new ItemBL(line.getItemId()).getEntity());
						prices.add(line.getAmount());
					}
                    //TODO MODULARIZATION: THIS LOGIC LAUNCH OLD MEDIATION 2 JOBS...
				} catch (Exception e) {
					// log stacktrace
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					pw.close();
					LOG.error("Validate Purchase error: %s\n%s",
							e.getMessage(), sw.toString());

					ValidatePurchaseWS result = new ValidatePurchaseWS();
					result.setSuccess(false);
					result.setAuthorized(false);
					result.setQuantity(BigDecimal.ZERO);
					result.setMessage(new String[] { "Error: " + e.getMessage() });

					return result;
				}
			}
		} else {
			return null;
		}

		// find the prices first
		// this will do nothing if the mediation process was uses. In that case
		// the itemIdsList will be empty
		int itemNum = 0;
		for (Integer itemId : itemIdsList) {
			ItemBL item = new ItemBL(itemId);

			if (fieldsList != null && !fieldsList.isEmpty()) {
				int fieldsIndex = itemNum;
				// just get first set of fields if only one set
				// for many items
				if (fieldsIndex > fieldsList.size()) {
					fieldsIndex = 0;
				}
				item.setPricingFields(fieldsList.get(fieldsIndex));
			}

			// todo: validate purchase should include the quantity purchased for
			// validations
			BigDecimal price = item.getPrice(userId, BigDecimal.ONE,
					getCallerCompanyId());
			// if the price can not be determined than use 0 since it will
			// contribute to the total amount of purchase and will not affect
			// validation
			prices.add(null != price ? price : BigDecimal.ZERO);
			items.add(item.getEntity());
			itemNum++;
		}

		ValidatePurchaseWS ret = new UserBL(userId).validatePurchase(items,
				prices, fieldsList);
		return ret;
	}

	/**
	 * Return the item id for the product with the productCode, if this is
	 * visible from the company who made the call or if it is a global product
	 * 
	 * @param productCode
	 * @return
	 * @throws SessionInternalError
	 */
	@Transactional(readOnly = true)
	public Integer getItemID(String productCode) throws SessionInternalError {
		Integer companyId = getCallerCompanyId();
		Integer parentCompany = new CompanyDAS().getParentCompanyId(companyId);
		ItemDAS itemDAS = new ItemDAS();
		ItemDTO itemFound = itemDAS.findItemByInternalNumber(productCode,
				companyId);
		if (itemFound != null
				&& itemDAS.isProductVisibleToCompany(itemFound.getId(),
						companyId, parentCompany)) {
			return itemFound.getId();
		}
		return null;
	}

	@Transactional(readOnly = true)
	public Integer getAuthPaymentType(Integer userId)
			throws SessionInternalError {

		IUserSessionBean sess = (IUserSessionBean) Context
				.getBean(Context.Name.USER_SESSION);
		return sess.getAuthPaymentType(userId);
	}

	public void setAuthPaymentType(Integer userId, Integer autoPaymentType,
			boolean use) throws SessionInternalError {

		IUserSessionBean sess = (IUserSessionBean) Context
				.getBean(Context.Name.USER_SESSION);
		sess.setAuthPaymentType(userId, autoPaymentType, use);
	}

	@Transactional(readOnly = true)
	public AgeingWS[] getAgeingConfiguration(Integer languageId)
			throws SessionInternalError {
		try {
			IBillingProcessSessionBean processSession = (IBillingProcessSessionBean) Context
					.getBean(Context.Name.BILLING_PROCESS_SESSION);
			AgeingDTOEx[] dtoArr = processSession.getAgeingSteps(
					getCallerCompanyId(), getCallerLanguageId(), languageId);
			AgeingWS[] wsArr = new AgeingWS[dtoArr.length];
			AgeingBL bl = new AgeingBL();
			for (int i = 0; i < wsArr.length; i++) {
				wsArr[i] = bl.getWS(dtoArr[i]);
			}
			return wsArr;
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}
	}

	public void saveAgeingConfiguration(AgeingWS[] steps, Integer languageId)
			throws SessionInternalError {
		AgeingBL bl = new AgeingBL();
		AgeingDTOEx[] dtoList = new AgeingDTOEx[steps.length];
		for (int i = 0; i < steps.length; i++) {
			dtoList[i] = bl.getDTOEx(steps[i]);
		}
		IBillingProcessSessionBean processSession = (IBillingProcessSessionBean) Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		processSession
				.setAgeingSteps(getCallerCompanyId(), languageId, dtoList);
	}

	/*
	 * Billing process
	 */
	@Transactional(readOnly = true)
	public boolean isBillingRunning(Integer entityId) {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		return processBean.isBillingRunning(entityId);
	}

	public void triggerBillingAsync(final Date runDate) {
		final Integer companyId = getCallerCompanyId();
		Thread t = new Thread(new Runnable() {
			IBillingProcessSessionBean processBean = Context
					.getBean(Context.Name.BILLING_PROCESS_SESSION);

			public void run() {
				processBean.trigger(runDate, companyId);
			}
		});

		t.start();
	}

	public void triggerCollectionsAsync(final Date runDate) {
		final Integer companyId = getCallerCompanyId();
		Thread t = new Thread(new Runnable() {
			IBillingProcessSessionBean processBean = Context
					.getBean(Context.Name.BILLING_PROCESS_SESSION);

			public void run() {
				processBean.reviewUsersStatus(companyId, runDate);
			}
		});
		t.start();
	}

	public boolean triggerBilling(Date runDate) {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		return processBean.trigger(runDate, getCallerCompanyId());
	}

	public void triggerAgeing(Date runDate) {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		processBean.reviewUsersStatus(getCallerCompanyId(), runDate);
	}

	/**
	 * Returns true if the ageing process is currently running for the caller's
	 * entity, false if not.
	 *
	 * @return true if ageing process is running, false if not
	 */
	@Transactional(readOnly = true)
	public boolean isAgeingProcessRunning() {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		return processBean.isAgeingProcessRunning(getCallerCompanyId());
	}

	/**
	 * Returns the status of the last run (or currently running) billing
	 * process.
	 *
	 * @return billing process status
	 */
	@Transactional(readOnly = true)
	public ProcessStatusWS getBillingProcessStatus() {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		return processBean.getBillingProcessStatus(getCallerCompanyId());
	}

	/**
	 * Returns the status of the last run (or currently running) ageing process.
	 *
	 * That the ageing process currently does not report a start date, end date,
	 * or process id. The status returned by this method will only report the
	 * RUNNING/FINISHED/FAILED state of the process.
	 *
	 * @return ageing process status
	 */
	@Transactional(readOnly = true)
	public ProcessStatusWS getAgeingProcessStatus() {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		return processBean.getAgeingProcessStatus(getCallerCompanyId());
	}

	@Transactional(readOnly = true)
	public BillingProcessConfigurationWS getBillingProcessConfiguration()
			throws SessionInternalError {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		BillingProcessConfigurationDTO configuration = processBean
				.getConfigurationDto(getCallerCompanyId());

		return ConfigurationBL.getWS(configuration);
	}

	public Integer createUpdateBillingProcessConfiguration(
			BillingProcessConfigurationWS ws) throws SessionInternalError {

		// validation
		if (!ConfigurationBL.validate(ws)) {
			throw new SessionInternalError("Error: Invalid Next Run Date.");
		}
		BillingProcessConfigurationDTO dto = ConfigurationBL.getDTO(ws);

		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		return processBean.createUpdateConfiguration(getCallerId(), dto);
	}

	/**
	 * This method creates or updates the commission process configuration.
	 * 
	 * @param ws
	 * @return
	 * @throws SessionInternalError
	 */
	public Integer createUpdateCommissionProcessConfiguration(
			CommissionProcessConfigurationWS ws) throws SessionInternalError {

		// validation
		try {
			if (!CommissionProcessConfigurationBL.validate(ws)) {
				throw new SessionInternalError(
						"Error: Invalid configuration",
						new String[] { "partner.error.commissionProcess.invalidDate" });
			}
		} catch (SessionInternalError e) {
			throw e;
		}

		CommissionProcessConfigurationDTO dto = CommissionProcessConfigurationBL.getDTO(ws);

		return CommissionProcessConfigurationBL.createUpdate(dto);
	}

	/**
	 * Triggers the partner commission process.
	 */
	public void calculatePartnerCommissions() {
		IUserSessionBean userSession = Context
				.getBean(Context.Name.USER_SESSION);
		userSession.calculatePartnerCommissions(getCallerCompanyId());
	}

	/**
	 * Triggers the partner commission process asynchronously.
	 */
	public void calculatePartnerCommissionsAsync() {
		final Integer companyId = getCallerCompanyId();
		Thread t = new Thread(new Runnable() {
			IUserSessionBean userSession = Context
					.getBean(Context.Name.USER_SESSION);

			public void run() {
				userSession.calculatePartnerCommissions(getCallerCompanyId());
			}
		});

		t.start();
	}

	/**
	 * Checks if the partner commission process is running or not.
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean isPartnerCommissionRunning() {
		IUserSessionBean userSession = Context
				.getBean(Context.Name.USER_SESSION);
		return userSession.isPartnerCommissionRunning(getCallerCompanyId());
	}

	/**
	 * Gets all the partner commission runs
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public CommissionProcessRunWS[] getAllCommissionRuns() {
		List<CommissionProcessRunDTO> commissionProcessRuns = new CommissionProcessRunDAS()
				.findAllByEntity(new CompanyDAS().find(getCallerCompanyId()));

		if (commissionProcessRuns != null && commissionProcessRuns.size() > 0) {
			CommissionProcessRunWS[] commissionProcessRunWSes = new CommissionProcessRunWS[commissionProcessRuns
					.size()];
			int index = 0;
			for (CommissionProcessRunDTO commissionProcessRun : commissionProcessRuns) {
				commissionProcessRunWSes[index] = CommissionProcessConfigurationBL.getCommissionProcessRunWS(
						commissionProcessRun);
				index++;
			}
			return commissionProcessRunWSes;
		} else {
			return null;
		}
	}

	/**
	 * Gets all the commissions for a given processRunId
	 * 
	 * @param processRunId
	 * @return
	 */
	@Transactional(readOnly = true)
	public CommissionWS[] getCommissionsByProcessRunId(Integer processRunId) {
		List<CommissionDTO> commissions = new CommissionDAS()
				.findAllByProcessRun(
						new CommissionProcessRunDAS().find(processRunId),
						getCallerCompanyId());

		if (commissions != null && commissions.size() > 0) {
			CommissionWS[] commissionWSes = new CommissionWS[commissions.size()];
			int index = 0;
			for (CommissionDTO commission : commissions) {
				commissionWSes[index] =CommissionProcessConfigurationBL.getCommissionWS((commission));
				index++;
			}
			return commissionWSes;
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public BillingProcessWS getBillingProcess(Integer processId) {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		BillingProcessDTOEx dto = processBean.getDto(processId,
				getCallerLanguageId());

		return BillingProcessBL.getWS(dto);
	}

	@Transactional(readOnly = true)
	public Integer getLastBillingProcess() throws SessionInternalError {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		return processBean.getLast(getCallerCompanyId());
	}

	@Transactional(readOnly = true)
	public OrderProcessWS[] getOrderProcesses(Integer orderId) {
		OrderDTO order = new OrderBL(orderId).getDTO();

		if (order == null)
			return new OrderProcessWS[0];

		List<OrderProcessWS> ws = new ArrayList<OrderProcessWS>(order
				.getOrderProcesses().size());
		for (OrderProcessDTO process : order.getOrderProcesses())
			ws.add(OrderBL.getOrderProcessWS(process));

		return ws.toArray(new OrderProcessWS[ws.size()]);
	}

	@Transactional(readOnly = true)
	public OrderProcessWS[] getOrderProcessesByInvoice(Integer invoiceId) {
		InvoiceDTO invoice = new InvoiceBL(invoiceId).getDTO();

		if (invoice == null)
			return new OrderProcessWS[0];

		List<OrderProcessWS> ws = new ArrayList<OrderProcessWS>(invoice
				.getOrderProcesses().size());
		for (OrderProcessDTO process : invoice.getOrderProcesses())
			ws.add(OrderBL.getOrderProcessWS(process));

		return ws.toArray(new OrderProcessWS[ws.size()]);
	}

	@Transactional(readOnly = true)
	public BillingProcessWS getReviewBillingProcess() {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		BillingProcessDTOEx dto = processBean.getReviewDto(
				getCallerCompanyId(), getCallerLanguageId());

		return BillingProcessBL.getWS(dto);
	}

	public BillingProcessConfigurationWS setReviewApproval(Boolean flag)
			throws SessionInternalError {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);
		BillingProcessConfigurationDTO dto = processBean.setReviewApproval(
				getCallerId(), getCallerCompanyId(), flag);

		return ConfigurationBL.getWS(dto);
	}

	@Transactional(readOnly = true)
	public Integer[] getBillingProcessGeneratedInvoices(Integer processId) {
		IBillingProcessSessionBean processBean = Context
				.getBean(Context.Name.BILLING_PROCESS_SESSION);

		// todo: IBillingProcessSessionBean#getGeneratedInvoices() should have a
		// proper generic return type
		@SuppressWarnings("unchecked")
		Collection<InvoiceDTO> invoices = processBean
				.getGeneratedInvoices(processId);

		List<Integer> ids = new ArrayList<Integer>(invoices.size());
		for (InvoiceDTO invoice : invoices)
			ids.add(invoice.getId());
		return ids.toArray(new Integer[ids.size()]);
	}

	/*
	 * Mediation process
	 */
	public void triggerMediation() {
		//TODO CAN WE TRIGGER ALL MEDIATION TOGETHER? PERFORMANCE ISSUES
		MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
		IMediationSessionBean mediationBean = Context.getBean(Context.Name.MEDIATION_SESSION);

        Integer callerCompanyId = getCallerCompanyId();
        List<MediationConfiguration> mediationConfigurations = mediationBean.getAllConfigurations(callerCompanyId, true);
		for(MediationConfiguration mediationConfiguration: mediationConfigurations){
            new Thread(() -> mediationService.launchMediation(callerCompanyId, mediationConfiguration.getId(), mediationConfiguration.getMediationJobLauncher())).start();
		}
	}

	/**
	 * Triggers the mediation process for a specific configuration and returns
	 * the mediation process id of the running process.
	 *
	 * @param cfgId
	 *            mediation configuration id
	 * @return mediation process id
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UUID triggerMediationByConfiguration(Integer cfgId) {
        MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
        MediationProcessService mediationProcessService = Context.getBean("mediationProcessService");
		IMediationSessionBean mediationBean = Context
				.getBean(Context.Name.MEDIATION_SESSION);
        MediationConfiguration configuration = mediationBean.getMediationConfiguration(cfgId);

		mediationService.launchMediation(getCallerCompanyId(), cfgId,
                configuration.getMediationJobLauncher());
		return mediationProcessService.getLastMediationProcessId(getCallerCompanyId());
	}

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public UUID launchMediation(Integer mediationCfgId, String jobName, File file) {
        MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
        MediationProcessService mediationProcessService = Context.getBean("mediationProcessService");
        mediationService.launchMediation(getCallerCompanyId(), mediationCfgId, jobName, file);
        return mediationProcessService.getLastMediationProcessId(getCallerCompanyId());
    }

    /**
	 * Undo mediation for process id
	 *
	 * @throws SessionInternalError
	 */
	public void undoMediation(UUID processId) throws SessionInternalError {
		OrderService orderService = Context.getBean(OrderService.BEAN_NAME);
		orderService.undoMediation(processId);
	}

	@Transactional(readOnly = true)
	public boolean isMediationProcessRunning() throws SessionInternalError {
        MediationService service = Context.getBean(MediationService.BEAN_NAME);
        return service.isMediationProcessRunning();
	}

	/**
	 * Returns the status of the last run (or currently running) mediation
	 * process.
	 *
	 * @return mediation process status
	 */
	@Transactional(readOnly = true)
	public ProcessStatusWS getMediationProcessStatus() {
        MediationService service = Context.getBean(MediationService.BEAN_NAME);
        MediationProcessService processService = Context.getBean(MediationProcessService.BEAN_NAME);
        List<MediationProcess> latestMediationProcess =
				processService.findLatestMediationProcess(getCallerCompanyId(), 0, 1);
        if (!latestMediationProcess.isEmpty()) {
            if (service.mediationProcessStatus().equals(MediationProcessStatus.COMPLETED)) {
                return new ProcessStatusWS(ProcessStatusWS.State.FINISHED, latestMediationProcess.get(0));
            }
            return new ProcessStatusWS(ProcessStatusWS.State.RUNNING, latestMediationProcess.get(0));
        }
        return null;
	}

	/**
	 * Returns the mediation process for the given process id.
	 *
	 * @param mediationProcessId
	 *            mediation process id
	 * @return mediation process, or null if not found
	 */
	@Transactional(readOnly = true)
	public MediationProcess getMediationProcess(UUID mediationProcessId) {
        MediationProcessService mediationProcessService = Context.getBean("mediationProcessService");
        return mediationProcessService.getMediationProcess(mediationProcessId);
	}

	@Transactional(readOnly = true)
	public MediationProcess[] getAllMediationProcesses() {
        //TODO MODULARIZATION: Check this logic
        MediationProcessService mediationProcessService = Context.getBean("mediationProcessService");
        return mediationProcessService.findLatestMediationProcess(getCallerCompanyId(), 0, 10000).toArray(new MediationProcess[0]);
	}

	@Transactional(readOnly = true)
	public JbillingMediationRecord[] getMediationEventsForOrder(Integer orderId) {
        MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
        return mediationService.getMediationRecordsForOrder(orderId).toArray(new JbillingMediationRecord[0]);
	}

	@Transactional(readOnly = true)
	public JbillingMediationRecord[] getMediationEventsForOrderDateRange(
			Integer orderId, Date startDate, Date endDate, int offset,
			int limit) {
        MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
        List<Filter> filters = new ArrayList<>();
        if (orderId != null) filters.add(new Filter("orderId", FilterConstraint.EQ, orderId));
        if (startDate != null && endDate != null) filters.add(new Filter("eventDate", FilterConstraint.DATE_BETWEEN, startDate, endDate));
        return mediationService.findMediationRecordsByFilters(offset, limit, filters).toArray(new JbillingMediationRecord[0]);
	}

	@Transactional(readOnly = true)
	public JbillingMediationRecord[] getMediationEventsForInvoice(
			Integer invoiceId) {
        MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
        InvoiceWS invoiceWS = getInvoiceWS(invoiceId);
        List<JbillingMediationRecord> records = new LinkedList<>();
        for (Integer orderId: invoiceWS.getOrders()) {
            records.addAll(mediationService.getMediationRecordsForOrder(orderId));
        }
        return records.toArray(new JbillingMediationRecord[0]);
	}

	/**
	 * Returns the Mediation Records for a given mediation process id.
	 * 
	 * @param mediationProcessId
	 *            mediation process Id : mandatory field
	 * @param page
	 *            if provided then records next to the offset will be returned
	 *            else return from the start.
	 * @param size
	 *            maximum number of records accepted
	 * @param startDate
	 *            filter by start date
	 * @param endDate
	 *            filter by end date
	 * @return mediation records
	 */
	@Transactional(readOnly = true)
	public JbillingMediationRecord[] getMediationRecordsByMediationProcess(
            UUID mediationProcessId, Integer page,
			Integer size, Date startDate, Date endDate) {
        MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
        List<Filter> filters = new ArrayList<>();
        if (mediationProcessId != null) filters.add(new Filter("processId", FilterConstraint.EQ, mediationProcessId));
        if (startDate != null && endDate != null) filters.add(new Filter("eventDate", FilterConstraint.DATE_BETWEEN, startDate, endDate));
        return mediationService.findMediationRecordsByFilters(page, size, filters).toArray(new JbillingMediationRecord[0]);
	}

	@Transactional(readOnly = true)
	public RecordCountWS[] getNumberOfMediationRecordsByStatuses() {
        MediationProcessService processService = Context.getBean(MediationProcessService.BEAN_NAME);
        List<MediationProcess> latestMediationProcess = processService.findLatestMediationProcess(getCallerCompanyId(), 0, 1);
        if (latestMediationProcess.size() == 0) {
            return null;
        }
        MediationProcess process = latestMediationProcess.get(0);
		return getNumberOfMediationRecordsByStatusesByMediationProcess(process.getId());
	}

	@Transactional(readOnly = true)
	public RecordCountWS[] getNumberOfMediationRecordsByStatusesByMediationProcess(
            UUID mediationProcessId) {
		MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
		List<JbillingMediationRecord> mediationRecordsForProcess = mediationService.getMediationRecordsForProcess(mediationProcessId);
		List<RecordCountWS> recordCountWSes = new ArrayList<>();
		for (JbillingMediationRecord.STATUS status: JbillingMediationRecord.STATUS.values()) {
			long count = mediationRecordsForProcess.stream().filter(mr -> mr.getStatus().equals(status)).count();
			recordCountWSes.add(new RecordCountWS(status.getId(), count));
		}
		long errorCount = mediationService.getMediationErrorRecordsForProcess(mediationProcessId).size();
		recordCountWSes.add(new RecordCountWS(Constants.MEDIATION_RECORD_STATUS_ERROR_DETECTED, errorCount));
		return recordCountWSes.toArray(new RecordCountWS[0]);
	}

	@Transactional(readOnly = true)
	public MediationConfigurationWS[] getAllMediationConfigurations() {
		IMediationSessionBean mediationBean = Context
				.getBean(Context.Name.MEDIATION_SESSION);

		List<MediationConfiguration> configurations = mediationBean
				.getAllConfigurations(getCallerCompanyId(), true);
		List<MediationConfigurationWS> ws = MediationConfigurationBL
				.getWS(configurations);
		return ws.toArray(new MediationConfigurationWS[ws.size()]);
	}

	public Integer createMediationConfiguration(MediationConfigurationWS cfg) {
		IMediationSessionBean mediationBean = Context
				.getBean(Context.Name.MEDIATION_SESSION);
		MediationConfiguration dto = MediationConfigurationBL.getDTO(cfg);
		// force the calling company to be the owner of this mediation config
		dto.setEntityId(getCallerCompanyId());
		return mediationBean.createConfiguration(dto, getCallerCompanyId(),
				getCallerId());
	}

	public Integer[] updateAllMediationConfigurations(
			List<MediationConfigurationWS> configurations)
			throws SessionInternalError {

		for (MediationConfigurationWS configuration : configurations) {
			if (configuration.getName() == null
					|| configuration.getName().length() < 1) {
				throw new SessionInternalError(
						"Name can not be empty.",
						new String[] { "MediationConfigurationWS,name,validation.mediation.config.name.empty" });
			} else if (configuration.getName().length() > 50) {
				throw new SessionInternalError(
						"Name is more than 50 characters",
						new String[] { "MediationConfigurationWS,name,validation.mediation.config.name.long" });
			}
		}
		// update all configurations
		List<MediationConfiguration> dtos = MediationConfigurationBL
				.getDTO(configurations);
		List<MediationConfiguration> updated;
		try {
			IMediationSessionBean mediationBean = Context
					.getBean(Context.Name.MEDIATION_SESSION);
			updated = mediationBean.updateAllConfiguration(dtos,
					getCallerCompanyId(), getCallerId());
		} catch (InvalidArgumentException e) {
			throw new SessionInternalError(e);
		}

		// return list of updated ids
		List<Integer> ids = new ArrayList<Integer>(updated.size());
		for (MediationConfiguration cfg : updated) {
			ids.add(cfg.getId());
		}
		return ids.toArray(new Integer[ids.size()]);
	}

	public void deleteMediationConfiguration(Integer cfgId)
			throws SessionInternalError {

		IMediationSessionBean mediationBean = Context
				.getBean(Context.Name.MEDIATION_SESSION);
		try {
			mediationBean.delete(cfgId, getCallerCompanyId(), getCallerId());
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}

	}

	/*
	 * Provisioning
	 */

	public void triggerProvisioning() {
		IProvisioningProcessSessionBean provisioningBean = Context
				.getBean(Context.Name.PROVISIONING_PROCESS_SESSION);
		provisioningBean.trigger();
	}

	public void updateOrderAndLineProvisioningStatus(Integer inOrderId,
			Integer inLineId, String result) throws SessionInternalError {
		IProvisioningProcessSessionBean provisioningBean = Context
				.getBean(Context.Name.PROVISIONING_PROCESS_SESSION);
		provisioningBean.updateProvisioningStatus(inOrderId, inLineId, result);
	}

	public void updateLineProvisioningStatus(Integer orderLineId,
			Integer provisioningStatus) throws SessionInternalError {
		IProvisioningProcessSessionBean provisioningBean = Context
				.getBean(Context.Name.PROVISIONING_PROCESS_SESSION);
		provisioningBean.updateProvisioningStatus(orderLineId,
				provisioningStatus);
	}

	/*
	 * Preferences
	 */
	public void updatePreferences(PreferenceWS[] prefList) {
		PreferenceBL bl = new PreferenceBL();
		for (PreferenceWS pref : prefList) {
			bl.createUpdateForEntity(getCallerCompanyId(), pref
					.getPreferenceType().getId(), pref.getValue());
		}
	}

	public void updatePreference(PreferenceWS preference) {

		// User InActive Management Feature: restrict maximum number of
		// in-active days to 90
		// defined in
		// Constants.MAX_VALUE_FOR_PREFERENCE_EXPIRE_INACTIVE_ACCOUNTS_IN_DAYS
		if (Constants.PREFERENCE_EXPIRE_INACTIVE_AFTER_DAYS.equals(preference
				.getPreferenceType().getId())
				&& Constants.MAX_VALUE_FOR_PREFERENCE_EXPIRE_INACTIVE_ACCOUNTS_IN_DAYS
						.compareTo(Integer.parseInt(preference.getValue())) < 0) {
			LOG.debug("Preference type : "
					+ preference.getPreferenceType().getId()
					+ " value obtained = "
					+ preference.getValue()
					+ " is greater then max value allowed ("
					+ Constants.MAX_VALUE_FOR_PREFERENCE_EXPIRE_INACTIVE_ACCOUNTS_IN_DAYS
					+ ")");
			String errorMessages[] = new String[1];
			errorMessages[0] = "PreferenceWS,value,preferences.update.max.value.error,"
					+ Constants.MAX_VALUE_FOR_PREFERENCE_EXPIRE_INACTIVE_ACCOUNTS_IN_DAYS;
			throw new SessionInternalError("Preference Type "
					+ preference.getPreferenceType().getId() + ": ",
					errorMessages);
		}
		
		if (preference.getPreferenceType().getId() == Constants.PREFERENCE_PARTNER_DEFAULT_COMMISSION_TYPE && StringUtils.isNotBlank(preference.getValue().trim())) {
				preference.setValue(StringUtils.trim(preference.getValue().toUpperCase()));		
				if(!(PartnerCommissionType.INVOICE.name().equals(preference.getValue()) ? true : (PartnerCommissionType.PAYMENT.name().equals(preference.getValue()) ? true : false))) {
				String errorMessages[] = new String[1];
				errorMessages[0] = "PreferenceWS,value,validation.error.agent.commisiontype.preference,"
						+ preference.getValue();
				throw new SessionInternalError("Error in preference value "
						+ preference.getValue() + ": ", errorMessages);
			}
		}

		if (preference.getPreferenceType().getId() == Constants.PREFERENCE_USE_BLACKLIST) {
			PluggableTaskDTO dto = ((PluggableTaskDAS) Context
					.getBean(Context.Name.PLUGGABLE_TASK_DAS)).findNow(Integer
					.valueOf(preference.getValue()));
			if (dto == null
					|| (dto != null && (dto.getType().getCategory().getId() != Constants.PLUGGABLE_TASK_PAYMENT || !dto
							.getEntityId().equals(getCallerCompanyId())))) {
				String errorMessages[] = new String[1];
				errorMessages[0] = "PreferenceWS,value,validation.error.email.preference.use.blacklist,"
						+ preference.getValue();
				throw new SessionInternalError("Error in preference value "
						+ preference.getValue() + ": ", errorMessages);
			}
		}

		new PreferenceBL().createUpdateForEntity(getCallerCompanyId(),
				preference.getPreferenceType().getId(), preference.getValue());
	}

	@Transactional(readOnly = true)
	public PreferenceWS getPreference(Integer preferenceTypeId) {
		PreferenceDTO preference = null;
		try {
			preference = new PreferenceBL(getCallerCompanyId(),
					preferenceTypeId).getEntity();
		} catch (DataAccessException e) {
			/* ignore */
		}

		if (preference != null) {
			// return preference if set
			return PreferenceBL.getWS(preference);

		} else {
			// preference is not set, return empty
			PreferenceTypeDTO preferenceType = new PreferenceTypeDAS()
					.find(preferenceTypeId);
			return preferenceType != null ? PreferenceBL.getWS(preferenceType)
					: null;
		}
	}

	/*
	 * Currencies
	 */
	@Transactional(readOnly = true)
	public CurrencyWS[] getCurrencies() {
		CurrencyBL currencyBl = new CurrencyBL();

		CurrencyDTO[] currencies;
		try {
			currencies = currencyBl.getCurrencies(getCallerLanguageId(),
					getCallerCompanyId());
		} catch (SQLException e) {
			throw new SessionInternalError(
					"Exception fetching currencies for entity "
							+ getCallerCompanyId(), e);
		} catch (NamingException e) {
			throw new SessionInternalError(
					"Exception fetching currencies for entity "
							+ getCallerCompanyId(), e);
		}

		// Id of the default currency for this entity
		Integer entityDefault = currencyBl
				.getEntityCurrency(getCallerCompanyId());

		// convert to WS
		List<CurrencyWS> ws = new ArrayList<CurrencyWS>(currencies.length);
		for (CurrencyDTO currency : currencies) {
			ws.add(CurrencyBL.getCurrencyWS(currency, (currency.getId() == entityDefault)));
		}

		return ws.toArray(new CurrencyWS[ws.size()]);
	}

	@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = { CurrencyInUseSessionInternalError.class })
	public void updateCurrencies(CurrencyWS[] currencies)
			throws SessionInternalError {

		UserDAS userDAS = new UserDAS();
		PartnerDAS partnerDAS = new PartnerDAS();
		OrderDAS orderDAS = new OrderDAS();
		ItemDAS itemDAS = new ItemDAS();
		PlanItemDAS planItemDAS = new PlanItemDAS();

		List<CurrencyWS> inActiveInUseCurrencies = new ArrayList<CurrencyWS>();

		Integer entityId = getCallerCompanyId();
		for (CurrencyWS currency : currencies) {

			if (!currency.getInUse()) {
				// This is a possible de-activation triggered by un-checking the
				// active flag on UI.
				Long inUseCount = 0l;

				Integer currencyId = currency.getId();

				// currency in use for users
				inUseCount += userDAS.findUserCountByCurrencyAndEntity(
						currencyId, entityId);

				// currrency in use for orders
				inUseCount += orderDAS.findOrderCountByCurrencyAndEntity(
						currencyId, entityId);

				// currrency in use for products and plans (plan is a product,
				// so no separate handling required)
				inUseCount += itemDAS.findProductCountByCurrencyAndEntity(
						currencyId, entityId);

				// currrency in use for planItem
				inUseCount += planItemDAS.findPlanItemCountByCurrencyAndEntity(
						currencyId, entityId);

				if (inUseCount > 0) {
					LOG.debug("Currency " + currency.getCode() + " is in use.");
					inActiveInUseCurrencies.add(currency);
				} else {
					updateCurrency(currency);
				}
			} else {
				updateCurrency(currency);
			}
		}

		if (!inActiveInUseCurrencies.isEmpty()) {
			String inUseCurrencies = "";
			for (CurrencyWS ws : inActiveInUseCurrencies) {
				inUseCurrencies += ws.getCode() + Constants.SINGLE_SPACE;
			}
			String errorMessages[] = new String[1];
			if (inActiveInUseCurrencies.size() > 1) {
				// there is more than one inactive in use currency, so use
				// plural form of the message
				errorMessages[0] = "CurrencyWS,inUse,currencies.updated.currencies.inactive.yet.in.use,"
						+ inUseCurrencies;
			} else {
				errorMessages[0] = "CurrencyWS,inUse,currencies.updated.currency.inactive.yet.in.use,"
						+ inUseCurrencies;
			}

			LOG.debug("Currency(s) %s is in use.", inUseCurrencies);
			throw new SessionInternalError("Currency(s) " + inUseCurrencies
					+ " is in use.", errorMessages);
		}
	}

	public void updateCurrency(CurrencyWS ws) throws SessionInternalError {
		final Integer entityId = getCallerCompanyId();
		Integer companyId = getCallerCompanyId();
		CurrencyDTO currency = new CurrencyDTO(ws);

		CurrencyBL currencyBl = new CurrencyBL(currency.getId());

		if (currency.getRate() != null) {
			if (currency.getRateAsDecimal().compareTo(BigDecimal.ZERO) <= 0) {
				String errorMessages[] = new String[1];
				errorMessages[0] = "CurrencyWS,rate,currencies.updated.error.rate.can.not.be.zero.or.less,"
						+ currency.getDescription();
				throw new SessionInternalError("Currency " + currency.getId()
						+ ": ", errorMessages);
			}
		}

		if (currency.getSysRate() != null) {
			if (currency.getSysRate().compareTo(BigDecimal.ZERO) <= 0) {
				String errorMessages[] = new String[1];
				errorMessages[0] = "CurrencyWS,sysRate,currencies.updated.error.sys.rate.can.not.be.zero.or.less,"
						+ currency.getDescription();
				throw new SessionInternalError("Currency " + currency.getId()
						+ ":", errorMessages);
			}
		}

		// update currency
		currencyBl.update(currency, companyId);

		// set as entity currency if flagged as default
		if (ws.isDefaultCurrency()) {
			CurrencyBL.setEntityCurrency(entityId, currency.getId());
		}

		// update the description if its changed
		if ((ws.getDescription() != null && !ws.getDescription().equals(
				currency.getDescription()))) {
			currency.setDescription(ws.getDescription(), getCallerLanguageId());
		}

		// update exchange rates for date
		final Date fromDate = ws.getFromDate();
		currencyBl.setOrUpdateExchangeRate(ws.getRateAsDecimal(), entityId,
				fromDate);
	}

	public Integer createCurrency(CurrencyWS ws) {
		CurrencyDTO currency = new CurrencyDTO(ws);
		Integer entityId = getCallerCompanyId();

		// save new currency
		CurrencyBL currencyBl = new CurrencyBL(currency.getId());

		currencyBl.create(currency, entityId);
		if (ws.getRate() != null) {
			currencyBl.setOrUpdateExchangeRate(ws.getRateAsDecimal(), entityId,
                    new Date());
		}
		currency = currencyBl.getEntity();

		// set as entity currency if flagged as default
		if (ws.isDefaultCurrency()) {
			currencyBl.setEntityCurrency(entityId, currency.getId());
		}

		// set description
		if (ws.getDescription() != null) {
			currency.setDescription(ws.getDescription(), getCallerLanguageId());
		}

		return currency.getId();
	}

	public boolean deleteCurrency(Integer currencyId)
			throws SessionInternalError {

		try {
			CurrencyBL currencyBl = new CurrencyBL(currencyId);
			return currencyBl.delete();
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}
	}

	@Transactional(readOnly = true)
	public CompanyWS getCompany() {
		CompanyDTO company = new CompanyDAS().find(getCallerCompanyId());
		LOG.debug(company);
		return EntityBL.getCompanyWS(company);
	}

	@Transactional(readOnly = true)
	public CompanyWS[] getCompanies() {
		try {
			UserWS user = getUserWS(getCallerId());

			if (Constants.TYPE_SYSTEM_ADMIN.equals(user.getMainRoleId())) {
				return EntityBL.getCompaniesWS(new CompanyDAS().findEntities());
			} else {
				throw new SessionInternalError("You are not authorized to execute this action");
			}
		} catch (Exception ex) {
			throw new SessionInternalError(ex);
		}
	}

	public void updateCompany(CompanyWS companyWS) {
		new EntityBL().updateEntityAndContact(companyWS, getCallerCompanyId(),
				getCallerId());
	}

	/*
	 * Notifications
	 */
	public void createUpdateNotification(Integer messageId, MessageDTO dto) {
		if (null == messageId) {
			new NotificationBL().createUpdate(getCallerCompanyId(), dto);
		} else {
			new NotificationBL(messageId).createUpdate(getCallerCompanyId(),
					dto);
		}
	}

	/* Secured via WSSecurityMethodMapper entry. */
	public void createCustomerNote(CustomerNoteWS note) {
		if (note.getNoteId() == 0) {

			CustomerNoteDTO customerNoteDTO = new CustomerNoteDTO();
			CustomerNoteDAS customerNoteDAS = new CustomerNoteDAS();
			customerNoteDTO.setCreationTime(new Date());
			customerNoteDTO.setNoteTitle(note.getNoteTitle());
			customerNoteDTO.setNoteContent(note.getNoteContent());
			customerNoteDTO.setCustomer(new CustomerDAS().find(note
					.getCustomerId()));
			customerNoteDTO.setUser(new UserDAS().find(note.getUserId()));
			customerNoteDTO.setCompany(new CompanyDAS()
					.find(getCallerCompanyId()));
			customerNoteDAS.save(customerNoteDTO);
			customerNoteDAS.flush();
			customerNoteDAS.clear();
		}
	}

	/*
	 * Plug-ins
	 */
	@Transactional(readOnly = true)
	public PluggableTaskWS getPluginWS(Integer pluginId) {
		PluggableTaskDTO dto = new PluggableTaskBL(pluginId).getDTO();
		return PluggableTaskBL.getWS(dto);
	}



	public Integer createPlugin(PluggableTaskWS plugin) {
		Integer pluginId = new PluggableTaskBL().create(getCallerId(),
				new PluggableTaskDTO(getCallerCompanyId(), plugin));

		if (!com.sapienter.jbilling.common.Util
				.getSysPropBooleanTrue(Constants.PROPERTY_RUN_API_ONLY_BUT_NO_BATCH)) {
			rescheduleScheduledPlugin(pluginId);
		} else {
			LOG.debug(
					"Plugin %s will not be scheduled since API Only Server is set to true.",
					pluginId);
		}
		return pluginId;
	}


	@Transactional(readOnly = true)
	public PluggableTaskWS[] getPluginsWS(Integer entityId, String className){
		if (null == entityId || null == className){
			throw new SessionInternalError("Required parameters not found!!");
		}
		return PluggableTaskBL.getByClassAndEntity(entityId, className);
	}

	public void updatePlugin(PluggableTaskWS plugin) {
		new PluggableTaskBL().update(getCallerId(), new PluggableTaskDTO(
				getCallerCompanyId(), plugin));

		if (!com.sapienter.jbilling.common.Util
				.getSysPropBooleanTrue(Constants.PROPERTY_RUN_API_ONLY_BUT_NO_BATCH)) {
			rescheduleScheduledPlugin(plugin.getId());
		} else {
			LOG.debug(
					"Plugin %s will not be scheduled since API Only Server is set to true.",
					plugin.getId());
		}
	}

	public void deletePlugin(Integer id) {

		unscheduleScheduledPlugin(id);
		new PluggableTaskBL(id).delete(getCallerId());
		// invalidate the plug-in cache to clear the deleted plug-in reference
		PluggableTaskDAS pluggableTaskDas = Context
				.getBean(Context.Name.PLUGGABLE_TASK_DAS);
		pluggableTaskDas.invalidateCache();
	}

	/*
	 * Quartz jobs
	 */
	/**
	 * This method reschedules an existing scheduled task that got changed. If
	 * not existing, the new plugin may need to be scheduled only if it is an
	 * instance of {@link IScheduledTask}
	 */
	public void rescheduleScheduledPlugin(Integer pluginId) {
		LOG.debug("Rescheduling... " + pluginId);
		try {
			IScheduledTask scheduledTask = getScheduledTask(pluginId);

			if (scheduledTask != null) {
				try {
					SchedulerBootstrapHelper bootStrapper = Context
							.getBean("schedulerBootstrapHelper");
					bootStrapper.rescheduleJob(scheduledTask);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		LOG.debug("done.");
	}
	
	public void triggerScheduledTask(Integer pluginId, Date date){
		try {

			ScheduledTask task = (ScheduledTask) getScheduledTask(pluginId);

			if(task == null){
				LOG.error("Scheduled task not found.");
				return;
			}

			task.executeOn(date);

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/*
	 * Quartz jobs
	 */
	/**
	 * This method unschedules an existing scheduled task before it is deleted
	 */
	public void unscheduleScheduledPlugin(Integer pluginId) {
		LOG.debug("Unscheduling... " + pluginId);
		try {
			IScheduledTask scheduledTask = getScheduledTask(pluginId);

			if (scheduledTask != null) {
				try {
					SchedulerBootstrapHelper bootStrapper = Context
							.getBean("schedulerBootstrapHelper");
					bootStrapper.unScheduleExisting(scheduledTask);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		LOG.debug("done.");
	}

	private IScheduledTask getScheduledTask(Integer pluginId) {
		try {
			PluggableTaskDTO task = new PluggableTaskBL(pluginId).getDTO();
			if (Constants.PLUGGABLE_TASK_SCHEDULED.equals(task.getType()
					.getCategory().getId())) {
				PluggableTaskManager<IScheduledTask> manager = new PluggableTaskManager<IScheduledTask>(
						getCallerCompanyId(),
						com.sapienter.jbilling.server.util.Constants.PLUGGABLE_TASK_SCHEDULED);
				IScheduledTask scheduledTask = manager.getInstance(task
						.getType().getClassName(), task.getType().getCategory()
						.getInterfaceName(), task);
				LOG.debug(task.getParameters());
				return scheduledTask;
			}
		} catch (PluggableTaskException e) {
			e.printStackTrace();
		}
		LOG.debug("done.");
		return null;
	}

	/*
	 * Plans and special pricing
	 */
	@Transactional(readOnly = true)
	public PlanWS getPlanWS(Integer planId) {
		PlanBL bl = new PlanBL(planId);
		return PlanBL.getWS(bl.getEntity());
	}

	@Transactional(readOnly = true)
	public PlanWS[] getAllPlans() {
		List<PlanDTO> plans = new PlanDAS().findAll(getCallerCompanyId());
		List<PlanWS> ws = PlanBL.getWS(plans);
		return ws.toArray(new PlanWS[ws.size()]);
	}

	public Integer createPlan(PlanWS plan) {
		PlanDTO planDTO = PlanBL.getDTO(plan);
		return new PlanBL().create(planDTO);
	}

	public void updatePlan(PlanWS plan) {
		PlanBL bl = new PlanBL(plan.getId());
		bl.update(PlanBL.getDTO(plan));
	}

	public void deletePlan(Integer planId) {
		new PlanBL(planId).delete();
	}

	public void addPlanPrice(Integer planId, PlanItemWS price) {
		PlanBL bl = new PlanBL(planId);
		bl.addPrice(PlanItemBL.getDTO(price));
	}

	@Transactional(readOnly = true)
	public boolean isCustomerSubscribed(Integer planId, Integer userId) {
		PlanBL bl = new PlanBL(planId);
		return bl.isSubscribed(userId, new Date());
	}

	@Transactional(readOnly = true)
	public boolean isCustomerSubscribedForDate(Integer planId, Integer userId,
			Date eventDate) {
		PlanBL bl = new PlanBL(planId);
		return bl.isSubscribed(userId, eventDate);
	}

	@Transactional(readOnly = true)
	public Usage getItemUsage(Integer excludedOrderId, Integer itemId,
			Integer owner, List<Integer> userIds, Date startDate, Date endDate) {
		return new UsageDAS().findUsageByItem(excludedOrderId, itemId, owner,
				userIds, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public Integer[] getSubscribedCustomers(Integer planId) {
		List<CustomerDTO> customers = new PlanBL().getCustomersByPlan(planId);

		int i = 0;
		Integer[] customerIds = new Integer[customers.size()];
		for (CustomerDTO customer : customers) {
			customerIds[i++] = customer.getId();
		}
		return customerIds;
	}

	@Transactional(readOnly = true)
	public Integer[] getPlansBySubscriptionItem(Integer itemId) {
		List<PlanDTO> plans = new PlanBL().getPlansBySubscriptionItem(itemId);

		int i = 0;
		Integer[] planIds = new Integer[plans.size()];
		for (PlanDTO plan : plans) {
			planIds[i++] = plan.getId();
		}
		return planIds;
	}

	@Transactional(readOnly = true)
	public Integer[] getPlansByAffectedItem(Integer itemId) {
		List<PlanDTO> plans = new PlanBL().getPlansByAffectedItem(itemId);

		int i = 0;
		Integer[] planIds = new Integer[plans.size()];
		for (PlanDTO plan : plans) {
			planIds[i++] = plan.getId();
		}
		return planIds;
	}


	public PlanItemWS createCustomerPrice(Integer userId, PlanItemWS planItem,
			Date expiryDate) {
		PlanItemDTO dto = PlanItemBL.getDTO(planItem);
		CustomerPriceDTO price = new CustomerPriceBL(userId).create(dto);
		price.setPriceExpiryDate(expiryDate);
		return PlanItemBL.getWS(price.getPlanItem());
	}

	public void updateCustomerPrice(Integer userId, PlanItemWS planItem,
			Date expiryDate) {
		PlanItemDTO dto = PlanItemBL.getDTO(planItem);
		CustomerPriceBL customerPrice = new CustomerPriceBL(userId, dto.getId());
		customerPrice.update(dto);
		customerPrice.getEntity().setPriceExpiryDate(expiryDate);
	}

	public void deleteCustomerPrice(Integer userId, Integer planItemId) {
		new CustomerPriceBL(userId, planItemId).delete();
	}

	@Transactional(readOnly = true)
	public PlanItemWS[] getCustomerPrices(Integer userId) {
		List<PlanItemDTO> prices = new CustomerPriceBL(userId)
				.getCustomerPrices();
		List<PlanItemWS> ws = PlanItemBL.getWS(prices);
		return ws.toArray(new PlanItemWS[ws.size()]);
	}

	@Transactional(readOnly = true)
	public PlanItemWS getCustomerPrice(Integer userId, Integer itemId) {
		CustomerPriceBL bl = new CustomerPriceBL(userId);
		return PlanItemBL.getWS(bl.getPrice(itemId, null));
	}

	@Transactional(readOnly = true)
	public PlanItemWS getCustomerPriceForDate(Integer userId, Integer itemId,
			Date pricingDate, Boolean planPricingOnly) {
		CustomerPriceBL bl = new CustomerPriceBL(userId);
		return PlanItemBL.getWS(bl.getPriceForDate(itemId, planPricingOnly,
				pricingDate));
	}

	// account types
	public PlanItemWS createAccountTypePrice(Integer accountTypeId,
			PlanItemWS planItem, Date expiryDate) {
		PlanItemDTO dto = PlanItemBL.getDTO(planItem);
		AccountTypePriceDTO price = new AccountTypePriceBL(accountTypeId)
				.create(dto);
		price.setPriceExpiryDate(expiryDate);

		return PlanItemBL.getWS(price.getPlanItem());
	}

	public void updateAccountTypePrice(Integer accountTypeId,
			PlanItemWS planItem, Date expiryDate) {
		PlanItemDTO dto = PlanItemBL.getDTO(planItem);
		AccountTypePriceBL accountTypePriceBl = new AccountTypePriceBL(
				accountTypeId, dto.getId());
		accountTypePriceBl.update(dto);
		accountTypePriceBl.getPrice().setPriceExpiryDate(expiryDate);

	}

	public void deleteAccountTypePrice(Integer accountTypeId, Integer planItemId) {
		new AccountTypePriceBL(accountTypeId, planItemId).delete();
	}

	@Transactional(readOnly = true)
	public PlanItemWS[] getAccountTypePrices(Integer accountTypeId) {
		List<PlanItemDTO> prices = new AccountTypePriceBL(accountTypeId)
				.getAccountTypePrices();
		List<PlanItemWS> ws = PlanItemBL.getWS(prices);
		return ws.toArray(new PlanItemWS[ws.size()]);
	}

	@Transactional(readOnly = true)
	public PlanItemWS getAccountTypePrice(Integer accountTypeId, Integer itemId) {
		AccountTypePriceBL bl = new AccountTypePriceBL(accountTypeId);
		return PlanItemBL.getWS(bl.getPrice(itemId));
	}

	/*
	 * Asset
	 */
	public Integer createAsset(AssetWS asset) throws SessionInternalError {

		validateAsset(asset);
		AssetBL assetBL = new AssetBL();
		AssetDTO dto = assetBL.getDTO(asset);

		// do validation
		checkItemAllowsAssetManagement(dto);

		// set default meta field values and validate
		MetaFieldHelper.updateMetaFieldDefaultValuesWithValidation(dto.getEntity().getLanguageId(),
                dto.getItem().findItemTypeWithAssetManagement().getAssetMetaFields(), dto);

		assetBL.checkForDuplicateIdentifier(dto);
		checkOrderAndStatus(dto);
		assetBL.checkContainedAssets(dto.getContainedAssets(), 0);

		return assetBL.create(dto, getCallerId());
	}

	public void updateAsset(AssetWS asset) throws SessionInternalError {
		updateAsset(asset, getCallerCompanyId());
	}

	public void validateAsset(AssetWS asset) {
		boolean foundAsset = true;
		boolean isRoot = new CompanyDAS().isRoot(getCallerCompanyId());
		if (asset.getContainedAssetIds() != null && isRoot && !asset.isGlobal()) {
			for (Integer assetIds : asset.getContainedAssetIds()) {
				AssetDTO assets = new AssetDAS().find(assetIds);
				for (Integer assetEntities : asset.getEntities()) {
					Set<CompanyDTO> assetEntity = new HashSet<CompanyDTO>(
							assets.getEntities());
					for (CompanyDTO entity : assetEntity) {
						if (assetEntities.equals(entity.getId())
								|| assets.isGlobal()) {
							foundAsset = true;
						} else {
							foundAsset = false;
							throw new SessionInternalError(
									"The child company asset can not available for root company ",
									new String[] { "AssetWS,containedAssets,validation.child.asset.not.add.root.asset" });
						}
					}
				}
			}
		}
	}


	public void updateAsset(AssetWS asset, Integer entityId)
			throws SessionInternalError {

		validateAsset(asset);

		AssetBL assetBL = new AssetBL(asset.getId());
		AssetDTO persistentAsset = assetBL.getEntity();

		AssetDTO dto = new AssetBL().getDTO(asset);
		// VALIDATION
		// can not change the status if it is internal e.g. 'Belongs to Group'
		if (persistentAsset.getAssetStatus().getIsInternal() == 1
				&& (persistentAsset.getAssetStatus().getId() != dto
						.getAssetStatus().getId())) {
			throw new SessionInternalError(
					"Asset has an internal status which may not be changed",
					new String[] { "AssetWS,assetStatus,asset.validation.status.change.internal" });
		}
		if (persistentAsset.getAssetStatus().getIsOrderSaved() == 1
				&& (persistentAsset.getAssetStatus().getId() != dto
						.getAssetStatus().getId())) {
			throw new SessionInternalError(
					"Asset belongs to an order and  the status may not be changed",
					new String[] { "AssetWS,assetStatus,asset.validation.status.change.fromordersaved" });
		}
		if (dto.getAssetStatus().getIsOrderSaved() == 1
				&& (persistentAsset.getAssetStatus().getId() != dto
						.getAssetStatus().getId())) {
			throw new SessionInternalError(
					"Asset status can not be changed to Ordered Status",
					new String[] { "AssetWS,assetStatus,asset.validation.status.change.toordersaved" });
		}
		checkItemAllowsAssetManagement(dto);
        ItemDTO item = dto.getItem();
        MetaFieldHelper.updateMetaFieldDefaultValuesWithValidation(item.getEntity().getId(),
                item.findItemTypeWithAssetManagement().getAssetMetaFields(), dto);
		assetBL.checkForDuplicateIdentifier(dto);
		checkOrderAndStatus(dto);
		assetBL.checkContainedAssets(dto.getContainedAssets(), dto.getId());

		assetBL.update(dto, getCallerId());
	}

	@Transactional(readOnly = true)
	public AssetWS getAsset(Integer assetId) {
		AssetBL bl = new AssetBL();
		return bl.getWS(bl.find(assetId));
	}

	@Transactional(readOnly = true)
	@Override
	public AssetWS getAssetByIdentifier(String assetIdentifier) {
		Integer assetId = new AssetDAS()
				.getAssetsForIdentifier(assetIdentifier);
		if (assetId != null) {
			AssetBL bl = new AssetBL();
			return bl.getWS(bl.find(assetId));
		}
		return null;
	}


	public void deleteAsset(Integer assetId) throws SessionInternalError {
		AssetDTO asset = checkAssetById(assetId);
		if(null == asset || asset.getDeleted() == Integer.valueOf(1)){
			throw new SessionInternalError(
					"Asset do not exist.",
					new String[] { "AssetWS,identifier,asset.validation.resource.entity.not.found"});
		}
		AssetBL bl = new AssetBL(asset);
		asset = bl.getEntity();
		AssetReservationDTO activeReservation = new AssetReservationDAS()
				.findActiveReservationByAsset(assetId);
		if (activeReservation != null) {
			throw new SessionInternalError(
					"Asset can not be deleted.Its reserved for customer",
					new String[] { "AssetWS,assetStatus,asset.validation.status.reserved" });
		}
		if (asset.getAssetStatus().getIsInternal() == 1) {
			throw new SessionInternalError(
					"Asset has an internal status which may not be changed",
					new String[] { "AssetWS,assetStatus,asset.validation.status.change.internal" });
		}
		if (asset.getAssetStatus().getIsOrderSaved() == 1) {
			throw new SessionInternalError(
					"Asset can not be deleted.Its already in use.",
					new String[] { "AssetWS,asset,asset.validation.status.order.saved.already" });
		}
		new AssetBL().delete(assetId, getCallerId());
	}

	private AssetDTO checkAssetById(Integer id){
		return new AssetDAS().findNow(id);
	}

	/**
	 * Gets all assets linked to the category (ItemTypeDTO) through products
	 * (ItemDTO).
	 * 
	 * @param categoryId
	 *            Category (ItemTypeDTO) identifier.
	 * @return
	 */
	@Transactional(readOnly = true)
	public Integer[] getAssetsForCategory(Integer categoryId) {
		return new AssetBL().getAssetsForCategory(categoryId);
	}

	/**
	 * Gets all asset ids linked to the product (ItemDTO)
	 * 
	 * @param itemId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Integer[] getAssetsForItem(Integer itemId) {
		return new AssetBL().getAssetsForItem(itemId);
	}

	/**
	 * Import a file containing assets.
	 *
	 *
	 * @param itemId
	 *            ItemDTO id the assets will be linked to
	 * @param identifierColumnName
	 *            column name of the asset 'identifier' attribute
	 * @param notesColumnName
	 *            column name of 'notes' attribute
	 * @param sourceFilePath
	 *            path to the input file
	 * @param errorFilePath
	 *            path to the error file
	 * @return job execution id
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Long startImportAssetJob(int itemId, String identifierColumnName,
			String notesColumnName, String globalColumnName,
			String entitiesColumnName, String sourceFilePath,
			String errorFilePath) throws SessionInternalError {
		ItemDTO item = new ItemBL(itemId).getEntity();
		int entityId = getCallerCompanyId();
		if (!item.isGlobal()
				&& !item.getEntities()
						.contains(new CompanyDAS().find(entityId))) {
			throw new SessionInternalError(
					"Item not available/shared with the caller Entity.");
		}

		JobLauncher asyncJobLauncher = Context
				.getBean(Context.Name.BATCH_ASYNC_JOB_LAUNCHER);
		Job assetLoadJob = Context.getBean(Context.Name.BATCH_ASSET_LOAD_JOB);

		// Job Parameters for Spring Batch
		Map jobParams = new HashMap();
        jobParams.put(Constants.BATCH_JOB_PARAM_ENTITY_ID, new JobParameter(
                Integer.toString(entityId)));

		jobParams.put(AssetImportConstants.JOB_PARM_ITEM_ID, new JobParameter(
				new Long(itemId)));

		jobParams.put(AssetImportConstants.JOB_PARM_INPUT_FILE,
				new JobParameter(sourceFilePath));
		jobParams.put(AssetImportConstants.JOB_PARM_ERROR_FILE,
				new JobParameter(errorFilePath));
		jobParams.put(AssetImportConstants.JOB_PARM_ID_COLUMN,
				new JobParameter(identifierColumnName));
		jobParams.put(AssetImportConstants.JOB_PARM_NOTES_COLUMN,
				new JobParameter(notesColumnName));
		jobParams.put(AssetImportConstants.JOB_PARM_USER_ID, new JobParameter(
				new Long(getCallerId())));
		jobParams.put(AssetImportConstants.JOB_PARM_GLOBAL, new JobParameter(
				globalColumnName));
		jobParams.put(AssetImportConstants.JOB_PARM_ENTITIES, new JobParameter(
				entitiesColumnName));
		jobParams.put(AssetImportConstants.JOB_PARM_ENTITY_ID,
				new JobParameter(new Long(entityId)));
		jobParams.put("startDate", new JobParameter(new Date()));

		try {
			// execute the job asynchronously
			JobExecution execution = asyncJobLauncher.run(assetLoadJob,
					new JobParameters(jobParams));
			Long executionId = execution.getId();

			return executionId;
		} catch (Exception e) {
			LOG.error("Unable to start asset import job", e);
			throw new SessionInternalError("Unable to start asset import job",
					e);
		}
	}

	/**
	 * Gets all the transitions for the asset
	 * 
	 * @param assetId
	 *            - AssetDTO id
	 * @return
	 */
	@Transactional(readOnly = true)
	public AssetTransitionDTOEx[] getAssetTransitions(Integer assetId) {
		return AssetTransitionBL.getWS(new AssetTransitionBL()
				.getTransitions(assetId));
	}

	/**
	 * Find all assets which match search criteria. Filters with field names
	 * 'id', 'status' and 'identifier' will be used as filters on asset
	 * attributes, any other field names will be used to match meta fields. You
	 * can order by any of the properties of AssetDTO.
	 *
	 * @see AssetDAS#findAssets(int,
	 *      com.sapienter.jbilling.server.util.search.SearchCriteria)
	 *
	 * @param criteria
	 * @return
	 */
	public AssetSearchResult findAssets(int productId, SearchCriteria criteria) {
		return new AssetBL().findAssets(productId, criteria);
	}

	/*
	 * Asset Helper Methods
	 */

	/**
	 * Validations for asset manager type linked to the item. Do the following
	 * checks - if the item allows asset management, it must be linked to a
	 * category which allows asset management - item may never be linked to more
	 * than 1 category allowing asset management - if assets are already linked
	 * to this item, the type allowing asset management may not be removed or
	 * changed.
	 * 
	 * @param newDto
	 *            - with changes applied
	 * @param oldDto
	 *            - currrent persistent object
	 */
	private void validateAssetManagementForItem(ItemDTO newDto, ItemDTO oldDto) {
		List<Integer> assetManagementTypes = extractAssetManagementTypes(newDto
				.getTypes());

		// if the item allows asset management, it must be linked to one
		// category which allows asset management
		if (newDto.getAssetManagementEnabled() == 1) {
			if (assetManagementTypes.size() < 1) {
				throw new SessionInternalError(
						"Product must belong to a category which allows asset management",
						new String[] { "ItemDTOEx,types,product.validation.no.assetmanagement.type.error" });
			}
		}
		// only 1 asset management type allowed
		if (assetManagementTypes.size() > 1) {
			throw new SessionInternalError(
					"Product belongs to more than one category which allows asset management",
					new String[] { "ItemDTOEx,types,product.validation.multiple.assetmanagement.types.error" });
		}

		// checks only if this is an update
		if (oldDto != null) {
			// in the current persisted object, find the item type which allows
			// asset management
			Integer currentAssetManagementType = null;
			for (ItemTypeDTO typeDTO : oldDto.getItemTypes()) {
				if (typeDTO.getAllowAssetManagement() == 1) {
					currentAssetManagementType = typeDTO.getId();
					break;
				}
			}

			if (currentAssetManagementType != null) {
				int assetCount = new AssetBL().countAssetsForItem(oldDto
						.getId());
				if (assetCount > 0) {
					// asset management type may not be removed
					if (assetManagementTypes.isEmpty())
						throw new SessionInternalError(
								"Asset management category may not be removed",
								new String[] { "ItemDTOEx,types,product.validation.assetmanagement.removed.error" });

					// asset management type may not be changed
					if (!currentAssetManagementType.equals(assetManagementTypes
							.get(0)))
						throw new SessionInternalError(
								"Asset management category may not be changed",
								new String[] { "ItemDTOEx,types,product.validation.assetmanagement.changed.error" });
				}
			}
		}
	}

	/**
	 * Extract all ItemTypes which allows asset management from the list of
	 * provided ItemType ids. This method loads all the ItemTypes for the
	 * provded ids and checks if they allow asset management. The ones that do
	 * will be returned.
	 *
	 * @param types
	 *            - ItemType ids
	 * @return Ids of ItemTypes allowing asset management.
	 */
	private List<Integer> extractAssetManagementTypes(Integer[] types) {
		List<Integer> typeIds = new ArrayList<Integer>(2);

		ItemTypeBL itemTypeBL = new ItemTypeBL();
		for (Integer typeId : types) {
			itemTypeBL.set(typeId);
			ItemTypeDTO itemTypeDTO = itemTypeBL.getEntity();
			if (itemTypeDTO.getAllowAssetManagement() == 1) {
				typeIds.add(typeId);
			}
		}
		return typeIds;
	}

	/**
	 * Check that the type has only one status which is 'default' and one which
	 * has 'order saved' checked. Check that status names are unique
	 */
	private void validateItemCategoryStatuses(ItemTypeDTO dto)
			throws SessionInternalError {
		// no need to do further checking if the type doesn't allow asset
		// management
		if (dto.getAllowAssetManagement() == 0) {
			return;
		}

		// status names must be unique
		Set<String> statusNames = new HashSet<String>(dto.getAssetStatuses()
				.size() * 2);
		// list of errors found
		List<String> errors = new ArrayList<String>(2);

		// keep count of the number of 'default' and 'order create' statuses
		int defaultCount = 0;
		int createOrderCount = 0;

		for (AssetStatusDTO statusDTO : dto.getAssetStatuses()) {
			if (statusDTO.getDeleted() == 0) {
				if (statusDTO.getIsDefault() == 1) {
					defaultCount++;
				}
				if (statusDTO.getIsOrderSaved() == 1) {
					createOrderCount++;
				}
				if (statusNames.contains(statusDTO.getDescription())) {
					errors.add("ItemTypeWS,statuses,validation.error.category.status.description.unique,"
							+ statusDTO.getDescription());
				}
				if (statusDTO.getIsAvailable() == 1
						&& statusDTO.getIsOrderSaved() == 1) {
					errors.add("ItemTypeWS,statuses,validation.error.category.status.both.available.ordersaved");
				}
				if (statusDTO.getIsDefault() == 1
						&& statusDTO.getIsOrderSaved() == 1) {
					errors.add("ItemTypeWS,statuses,validation.error.category.status.both.default.ordersaved");
				} else {
					statusNames.add(statusDTO.getDescription());
				}
			}
		}

		if (defaultCount != 1) {
			errors.add("ItemTypeWS,statuses,validation.error.category.status.default.one");
		}

		if (createOrderCount != 1) {
			errors.add("ItemTypeWS,statuses,validation.error.category.status.order.saved.one");
		}

		if (errors.size() > 0) {
			throw new SessionInternalError(
					"Category Status validation failed.",
					errors.toArray(new String[errors.size()]));

		}
	}

	/**
	 * If the asset belongs to an order, it must have a status of unavailable
	 * 
	 * @param dto
	 * @throws SessionInternalError
	 */
	private void checkOrderAndStatus(AssetDTO dto) throws SessionInternalError {
		if (dto.getOrderLine() != null
				&& dto.getAssetStatus().getIsAvailable() == 1) {
			throw new SessionInternalError(
					"An asset belonging to an order must have an unavailable status",
					new String[] { "AssetWS,assetStatus,asset.validation.status.not.unavailable" });
		}
	}

	/**
	 * Check that the item linked to the asset allows asset management.
	 * 
	 * @param dto
	 * @throws SessionInternalError
	 */
	private void checkItemAllowsAssetManagement(AssetDTO dto)
			throws SessionInternalError {
		if (dto.getItem().getAssetManagementEnabled() == 0) {
			throw new SessionInternalError(
					"The item does not allow asset management",
					new String[] { "AssetWS,itemId,asset.validation.item.not.assetmanagement" });
		}
	}

	/*
	 * Route Based Rating
	 */
	public Integer createRoute(RouteWS routeWS, File routeFile)
			throws SessionInternalError {
		RouteBL routeBL = new RouteBL();

		CompanyDTO company = EntityBL.getDTO(getCompany());

		RouteDTO routeDTO = routeBL.toDTO(routeWS);
		routeDTO.setCompany(company);
		routeDTO.setName(routeWS.getName());
		routeDTO.setTableName(routeBL.createRouteTableName(company.getId(),
				routeDTO.getName()));

		RouteDTO rootRoute = routeBL.getRootRouteTable(company.getId());
		if (routeWS.getRootTable() != null
				&& routeWS.getRootTable().booleanValue()) {
			if ((null != rootRoute && (null == routeWS.getId() || routeWS
					.getId() <= 0))
					||

					(null != rootRoute && null != routeWS.getId()
							&& routeWS.getId() > 0 && rootRoute.getId() != routeWS
							.getId().intValue())) {

				throw new SessionInternalError(
						"There can be only one root table per company",
						new String[] { "RouteWS,rootTable,route.validation.only.one.root.table.allowed" });

			}
		}

		if (routeWS.getId() != null) {
			new RouteBL(routeDTO.getId()).update(routeDTO, routeFile);
			return routeWS.getId();
		} else {
			return new RouteBL().create(routeDTO, routeFile);
		}
	}


	public void deleteRoute(Integer routeId) throws SessionInternalError {
		new RouteBL(routeId).delete();
	}

	public RouteWS getRoute(Integer routeId) {
		RouteDAS das = new RouteDAS();
		RouteDTO routeDTO = das.findNow(routeId);
		if (routeId == null || routeDTO == null) {
			return null;
		}
		RouteBL bl = new RouteBL(routeId);
		return bl.toWS();
	}

	public Integer createMatchingField(MatchingFieldWS matchingFieldWS)
			throws SessionInternalError {

		MatchingFieldDTO matchingFieldDTO = new MatchingFieldDTO();
		MatchingFieldDAS matchingFieldDAS = new MatchingFieldDAS();

		matchingFieldDTO.setDescription(matchingFieldWS.getDescription());
		matchingFieldDTO.setOrderSequence(Integer.parseInt(matchingFieldWS
				.getOrderSequence()));
		matchingFieldDTO.setMediationField(matchingFieldWS.getMediationField());
		matchingFieldDTO.setMatchingField(matchingFieldWS.getMatchingField());
		matchingFieldDTO.setRequired(matchingFieldWS.getRequired());
		matchingFieldDTO.setType(MatchingFieldType.valueOf(matchingFieldWS.getType()));
		matchingFieldDTO.setRoute(new RouteDAS().find(matchingFieldWS
				.getRouteId()));
		matchingFieldDTO.setRouteRateCard(new RouteRateCardDAS()
				.find(matchingFieldWS.getRouteRateCardId()));

		calculateMatchingFieldData(matchingFieldDTO, matchingFieldWS);
		matchingFieldDTO = matchingFieldDAS.save(matchingFieldDTO);

		return matchingFieldDTO.getId();

	}


	public void deleteMatchingField(Integer matchingFieldId)
			throws SessionInternalError {
		try {
			MatchingFieldDAS matchingFieldDAS = new MatchingFieldDAS();
			MatchingFieldDTO matchingFieldDTO = matchingFieldDAS
					.getMatchingFieldById(matchingFieldId);
			matchingFieldDAS.delete(matchingFieldDTO);
			matchingFieldDAS.flush();
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}
	}

	@Transactional(readOnly = true)
	public MatchingFieldWS getMatchingField(Integer matchingFieldId)
			throws SessionInternalError {
		MatchingFieldDAS matchingFieldDAS = new MatchingFieldDAS();
		MatchingFieldDTO matchingFieldDTO = matchingFieldDAS
				.findNow(matchingFieldId);
		if (matchingFieldDTO != null) {
			MatchingFieldWS matchingFieldWS = new RouteBL()
					.convertMatchingFieldDTOToMatchingFieldWS(matchingFieldDTO);
			return matchingFieldWS;
		}
		return null;
	}


	public boolean updateMatchingField(MatchingFieldWS matchingFieldWS)
			throws SessionInternalError {
		MatchingFieldDAS matchingFieldDAS = new MatchingFieldDAS();
		MatchingFieldDTO matchingFieldDTO = matchingFieldDAS
				.findNow(matchingFieldWS.getId());
		matchingFieldDTO.setDescription(matchingFieldWS.getDescription());
		matchingFieldDTO.setOrderSequence(Integer.parseInt(matchingFieldWS
				.getOrderSequence()));
		matchingFieldDTO.setMediationField(matchingFieldWS.getMediationField());
		matchingFieldDTO.setMatchingField(matchingFieldWS.getMatchingField());
		matchingFieldDTO.setRequired(matchingFieldWS.getRequired());
		matchingFieldDTO.setType(MatchingFieldType.valueOf(matchingFieldWS.getType()));

		calculateMatchingFieldData(matchingFieldDTO, matchingFieldWS);
		matchingFieldDTO = matchingFieldDAS.save(matchingFieldDTO);
		return true;
	}

	/**
	 * Calculate the matching field data that will be used for performance
	 * optimization
	 *
	 * @param matchingFieldDTO
	 * @param matchingFieldWS
	 */
	private void calculateMatchingFieldData(MatchingFieldDTO matchingFieldDTO,
			MatchingFieldWS matchingFieldWS) {

		Integer longestValue = null;
		Integer smallestValue = null;

		if (matchingFieldWS.getRouteId() != null) {
			RouteBL routeBL = new RouteBL(matchingFieldWS.getRouteId());
			longestValue = routeBL.getLongestValueFor(matchingFieldWS
					.getMatchingField());
			smallestValue = routeBL.getSmallestValueFor(matchingFieldWS
					.getMatchingField());
		} else if (matchingFieldWS.getRouteRateCardId() != null) {
			RouteBasedRateCardBL rateCardBL = new RouteBasedRateCardBL(
					matchingFieldWS.getRouteRateCardId());
			longestValue = rateCardBL.getLongestValueFor(matchingFieldWS
					.getMatchingField());
			smallestValue = rateCardBL.getSmallestValueFor(matchingFieldWS
					.getMatchingField());
		} else {
			throw new SessionInternalError(
					"Either route or route rate card needs to be specified for a matching field",
					new String[] { "MatchingFieldWS,routeId,matching.field.route.mandatory" });
		}

		matchingFieldDTO.setLongestValue(longestValue);
		matchingFieldDTO.setSmallestValue(smallestValue);
		matchingFieldDTO.setMandatoryFieldsQuery("obsoleted");
	}

	/**
	 * Create a record in a route table. An id may be specified for the record.
	 * If it is not specified the max + 1 id will used.
	 *
	 *
	 *
	 * @param routeRecord
	 *            RouteDTO id
	 * @param routeId
	 * @return
	 */
	public Integer createRouteRecord(RouteRecordWS routeRecord, Integer routeId)
			throws SessionInternalError {
		validateRouteRecord(routeRecord);
		RouteDTO dto = new RouteBL(routeId).getEntity();
		RouteBeanFactory factory = new RouteBeanFactory(dto);
		ITableUpdater updater = factory.getRouteUpdaterInstance();
		return updater.create(routeRecordToMap(routeRecord));
	}

	public Integer createRouteRateCardRecord(RouteRateCardWS record,
			Integer routeRateCardId) throws SessionInternalError {
		record.validate();
		RouteRateCardDTO dto = new RouteBasedRateCardBL(routeRateCardId)
				.getEntity();
		RouteRateCardBeanFactory factory = new RouteRateCardBeanFactory(dto);
		ITableUpdater updater = factory.getRouteRateCardUpdaterInstance();
		return updater.create(record.routeRateCardRecordToMap());
	}

	/**
	 * Update and entry in a route table.
	 *
	 * @param routeRecord
	 *            - RouteDTO.id
	 * @param routeId
	 *
	 */
	public void updateRouteRecord(RouteRecordWS routeRecord, Integer routeId)
			throws SessionInternalError {
		validateRouteRecord(routeRecord);
		RouteDTO dto = new RouteBL(routeId).getEntity();
		RouteBeanFactory factory = new RouteBeanFactory(dto);
		ITableUpdater updater = factory.getRouteUpdaterInstance();
		updater.update(routeRecordToMap(routeRecord));
	}

	public void updateRouteRateCardRecord(RouteRateCardWS record,
			Integer routeRateCardId) throws SessionInternalError {
		record.validate();
		RouteRateCardDTO dto = new RouteBasedRateCardBL(routeRateCardId)
				.getEntity();
		RouteRateCardBeanFactory factory = new RouteRateCardBeanFactory(dto);
		ITableUpdater updater = factory.getRouteRateCardUpdaterInstance();
		updater.update(record.routeRateCardRecordToMap());
	}

	/**
	 * Delete a route record.
	 *
	 * @param routeId
	 *            RouteDTO.id
	 * @param recordId
	 *            record id.
	 */
	public void deleteRouteRecord(Integer routeId, Integer recordId)
			throws SessionInternalError {
		RouteDTO dto = new RouteBL(routeId).getEntity();
		RouteBeanFactory factory = new RouteBeanFactory(dto);
		ITableUpdater updater = factory.getRouteUpdaterInstance();
		updater.delete(recordId);
	}

	/**
	 * Delete a route rate card record.
	 *
	 * @param routeRateCardId
	 *            RouteRateCardDTO.id
	 * @param recordId
	 *            record id.
	 */

	public void deleteRateCardRecord(Integer routeRateCardId, Integer recordId)
			throws SessionInternalError {
		RouteRateCardDTO dto = new RouteBasedRateCardBL(routeRateCardId)
				.getEntity();
		RouteRateCardBeanFactory factory = new RouteRateCardBeanFactory(dto);
		ITableUpdater updater = factory.getRouteRateCardUpdaterInstance();
		updater.delete(recordId);
	}

	/**
	 * Get the contents of a route table in CSV format.
	 * 
	 * @param routeId
	 * @return
	 */
	@Transactional(readOnly = true)
	public String getRouteTable(Integer routeId) {
		RouteDTO dto = new RouteBL(routeId).getEntity();
		RouteBeanFactory factory = new RouteBeanFactory(dto);
		ITableUpdater updater = factory.getRouteUpdaterInstance();
		List<Map<String, String>> table = updater.list();
		if (table.isEmpty()) {
			return "";
		}

		List exportableList = new ArrayList<Exportable>();
		exportableList.add(new ExportableMap(table));

		CsvExporter<ExportableMap> exporter = CsvExporter
				.createExporter(ExportableMap.class);
		return exporter.export(exportableList);

	}

	public SearchResultString searchDataTable(Integer routeId,
			SearchCriteria criteria) {
		RouteDTO dto = new RouteBL(routeId).getEntity();
		RouteBeanFactory factory = new RouteBeanFactory(dto);
		ITableUpdater updater = factory.getRouteUpdaterInstance();
		SearchResultString result = updater.search(criteria);
		return result;
	}

	public Set<String> searchDataTableWithFilter(Integer routeId, String filters, String searchName) {

		String sortIndex = "id";
		String sortOrder = "ASC";
		Integer maxRows = 500;
		int currentPage = 1;

		int rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows;

		//build the criteria object
		SearchCriteria criteria = new SearchCriteria();
		criteria.setOffset(rowOffset);
		criteria.setMax(maxRows);
		criteria.setSort(sortIndex);
		criteria.setDirection(SearchCriteria.SortDirection.ASC);
		criteria.setTotal(-1);

		List<com.sapienter.jbilling.server.util.search.BasicFilter> criteriaFilters = new ArrayList<com.sapienter.jbilling.server.util.search.BasicFilter>();

		//build the search filters
		LOG.debug("Filters come from parameter:  " + filters);

//		String should be join using com.google.common.base.Joiner
		Map<String, String> filtersMap;
		if(StringUtils.trimToNull(filters) != null) {
			filtersMap = Splitter.on("~~").withKeyValueSeparator("=").split(filters);
		} else {
			filtersMap = new HashMap<String, String>();
		}

		for(Map.Entry<String, String> entry : filtersMap.entrySet()) {
			if(StringUtils.trimToNull(entry.getValue()) != null) {
				com.sapienter.jbilling.server.util.search.Filter.FilterConstraint  constraint = com.sapienter.jbilling.server.util.search.Filter.FilterConstraint.ILIKE;
				criteriaFilters.add(new com.sapienter.jbilling.server.util.search.BasicFilter(entry.getKey(), constraint, entry.getValue()));
			}
		}
		criteria.setFilters(criteriaFilters.toArray(new com.sapienter.jbilling.server.util.search.BasicFilter[criteriaFilters.size()]));

		LOG.debug("Criteria filter in new method is:  " + criteria);

		RouteDTO dto = new RouteBL(routeId).getEntity();
		RouteBeanFactory factory = new RouteBeanFactory(dto);

		List<String> columnNames = factory.getTableDescriptorInstance().getColumnsNames();
		SearchResultString result = searchDataTable(1, criteria);
		int searchNameIdx = columnNames.indexOf(searchName.toLowerCase());

		Set<String> filteredResult = new HashSet<String>();
		for(List<String> resultRow : result.getRows()) {
			filteredResult.add(resultRow.get(searchNameIdx));
		}

		return filteredResult;
	}

	public SearchResultString searchRouteRateCard(Integer routeRateCardId,
			SearchCriteria criteria) {
		RouteRateCardDTO dto = new RouteBasedRateCardBL(routeRateCardId)
				.getEntity();
		RouteRateCardBeanFactory factory = new RouteRateCardBeanFactory(dto);
		ITableUpdater updater = factory.getRouteRateCardUpdaterInstance();
		SearchResultString result = updater.search(criteria);
		return result;
	}

	public Integer createDataTableQuery(DataTableQueryWS queryWS)
			throws SessionInternalError {
		DataTableQueryBL bl = new DataTableQueryBL();
		queryWS.setUserId(getCallerId());
		DataTableQueryDTO dto = DataTableQueryBL.convertToDto(queryWS);
		dto = bl.create(dto);
		return dto.getId();
	}

	@Transactional(readOnly = true)
	public DataTableQueryWS getDataTableQuery(int id)
			throws SessionInternalError {
		DataTableQueryBL bl = new DataTableQueryBL();
		bl.set(id);
		DataTableQueryDTO dto = bl.getEntity();
		return DataTableQueryBL.convertToWS(dto);
	}

	public void deleteDataTableQuery(int id) throws SessionInternalError {
		DataTableQueryBL bl = new DataTableQueryBL();
		bl.delete(id);
	}

	public DataTableQueryWS[] findDataTableQueriesForTable(int routeId)
			throws SessionInternalError {
		DataTableQueryBL bl = new DataTableQueryBL();
		List<DataTableQueryDTO> result = bl.findDataTableQueriesForTable(
				routeId, getCallerId());
		return bl.convertToWsArray(result, false);

	}

	/**
	 * Validates that - at least one entry has a value. - all names are not
	 * empty
	 *
	 * @param record
	 */
	private void validateRouteRecord(RouteRecordWS record) {
		for (NameValueString nv : record.getAttributes()) {
			if (nv.getName().trim().length() == 0) {
				throw new SessionInternalError(
						"Route record field has empty name",
						new String[] { "RouteRecordWS,fields,route.record.validation.attr.no.name" });
			}
			if (nv.getValue().trim().length() > 0) {
				return;
			}
		}
		throw new SessionInternalError(
				"Route record can not have all empty values",
				new String[] { "RouteRecordWS,fields,route.record.validation.no.data" });
	}

	/**
	 * Convert an array of name value pairs to a map with the key the name.
	 *
	 *
	 * @param record
	 * @return
	 */
	private Map<String, String> routeRecordToMap(RouteRecordWS record) {
		Map<String, String> result = new HashMap<String, String>(
				record.getAttributes().length * 2);
		if (record.getId() != null) {
			result.put("id", record.getId().toString());
		}
		if (record.getRouteId() != null) {
			result.put("routeid", record.getRouteId());
		}
		if (record.getName() != null) {
			result.put("name", record.getName());
		}

		for (NameValueString nv : record.getAttributes()) {
			result.put(nv.getName(), nv.getValue());
		}
		return result;
	}

	public Integer createRouteRateCard(RouteRateCardWS routeRateCardWS,
			File routeRateCardFile) throws SessionInternalError {
		RouteRateCardDTO routeRateCardDTO = RouteBasedRateCardBL.getRouteRateCardDTO(routeRateCardWS, getCallerCompanyId());
		routeRateCardDTO.setCompany(EntityBL.getDTO(getCompany()));
		routeRateCardDTO.setName(routeRateCardWS.getName());
		routeRateCardDTO.setTableName(new RouteBasedRateCardBL()
				.createRouteRateCardTableName(getCallerCompanyId(),
						routeRateCardWS.getName()));
		return new RouteBasedRateCardBL().create(routeRateCardDTO,
				routeRateCardFile);
	}


	public void deleteRouteRateCard(Integer routerRateCardId) {
		new RouteBasedRateCardBL(routerRateCardId).delete();
	}


	public void updateRouteRateCard(RouteRateCardWS routeRateCardWS,
			File routeRateCardFile) {

		RouteRateCardDTO routeRateCardDTO = RouteBasedRateCardBL
				.getRouteRateCardDTO(routeRateCardWS,getCallerCompanyId());

		routeRateCardDTO.setName(routeRateCardWS.getName());
		routeRateCardDTO.setTableName(new RouteBasedRateCardBL()
				.createRouteRateCardTableName(getCallerCompanyId(),
						routeRateCardWS.getName()));
		routeRateCardDTO.setCompany(EntityBL.getDTO(getCompany()));
		new RouteBasedRateCardBL(routeRateCardDTO.getId()).update(
				routeRateCardDTO, routeRateCardFile);
	}

	@Transactional(readOnly = true)
	public RouteRateCardWS getRouteRateCard(Integer routeRateCardId) {
		RouteRateCardDAS das = new RouteRateCardDAS();
		RouteRateCardDTO routeRateCardDTO = das.findNow(routeRateCardId);
		if (routeRateCardId == null || routeRateCardDTO == null) {
			return null;
		}
		RouteBasedRateCardBL bl = new RouteBasedRateCardBL(routeRateCardId);
		return bl.getWS();
	}

	/*
	 * Rate Card
	 */
	public Integer createRateCard(RateCardWS rateCardWs, File rateCardFile) {
		RateCardDTO rateCardDTO = RateCardBL.getDTO(rateCardWs);
		rateCardDTO.setCompany(new CompanyDAS().find(getCallerCompanyId()));
		rateCardDTO.setGlobal(rateCardWs.isGlobal());
		rateCardDTO.setChildCompanies(convertToCompanyDTO(rateCardWs
				.getChildCompanies()));
		return new RateCardBL().create(rateCardDTO, rateCardFile);
	}

	public void updateRateCard(RateCardWS rateCardWs, File rateCardFile)
			throws SessionInternalError {
		// Not changing the company of the rate card. It will remain the company
		// that created the ratecard
		RateCardDTO rateCardDTO = RateCardBL.getDTO(rateCardWs);
		rateCardDTO.setGlobal(rateCardWs.isGlobal());
		rateCardDTO.setChildCompanies(convertToCompanyDTO(rateCardWs
				.getChildCompanies()));
		try {
			new RateCardBL(rateCardDTO.getId()).update(rateCardDTO,
					rateCardFile);
		} catch (Exception e) { // needed because the sql exception :(
			LOG.error("Exception in web service: Updating ratecard", e);
			throw new SessionInternalError("Error updating ratecard");
		}
	}

	public void deleteRateCard(Integer rateCardId) {
		new RateCardBL(rateCardId).delete();
	}

	/*
	 * Rating Unit
	 */

	public Integer createRatingUnit(RatingUnitWS ratingUnitWS)
			throws SessionInternalError {

		RatingUnitDTO ratingUnitDTO = RatingUnitBL.getDTO(ratingUnitWS,getCallerCompanyId());
		ratingUnitDTO = new RatingUnitBL().create(ratingUnitDTO);

		return ratingUnitDTO.getId();
	}

	public void updateRatingUnit(RatingUnitWS ratingUnitWS)
			throws SessionInternalError {

		RatingUnitDTO ratingUnitDTO = RatingUnitBL.getDTO(ratingUnitWS,getCallerCompanyId());
		new RatingUnitBL().update(ratingUnitDTO);

	}

	public boolean deleteRatingUnit(Integer ratingUnitId)
			throws SessionInternalError {
		try {
			RatingUnitBL bl = new RatingUnitBL(ratingUnitId);
			return bl.delete();
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}
	}

	@Transactional(readOnly = true)
	public RatingUnitWS getRatingUnit(Integer ratingUnitId)
			throws SessionInternalError {

		RatingUnitDAS das = new RatingUnitDAS();
		RatingUnitDTO ratingUnitDTO = das.findNow(ratingUnitId);
		if (ratingUnitId == null || ratingUnitDTO == null) {
			return null;
		}
		RatingUnitBL bl = new RatingUnitBL(ratingUnitId);
		return bl.getWS();
	}

	public RatingUnitWS[] getAllRatingUnits() throws SessionInternalError {
		RatingUnitDAS das = new RatingUnitDAS();
		List<RatingUnitDTO> types = das.findAll(getCallerCompanyId());
		RatingUnitWS[] wsTypes = new RatingUnitWS[types.size()];
		for (int i = 0; i < types.size(); i++) {
			wsTypes[i] = RatingUnitBL.getWS(types.get(i));
		}
		return wsTypes;
	}

	@Transactional(readOnly = true)
	public AssetAssignmentWS[] getAssetAssignmentsForAsset(Integer assetId) {
		AssetAssignmentDAS assignmentDAS = new AssetAssignmentDAS();
		List<AssetAssignmentDTO> assignments = assignmentDAS
				.getAssignmentsForAsset(assetId);
		return AssetAssignmentBL.toWS(assignments);
	}

	@Transactional(readOnly = true)
	public AssetAssignmentWS[] getAssetAssignmentsForOrder(Integer orderId) {
		AssetAssignmentDAS assignmentDAS = new AssetAssignmentDAS();
		List<AssetAssignmentDTO> assignments = assignmentDAS
				.getAssignmentsForOrder(orderId);
		return AssetAssignmentBL.toWS(assignments);
	}

	public Integer findOrderForAsset(Integer assetId, Date date) {
		if (null == assetId)
			return null;// mandatory parameter
		OrderDTO order = (new AssetAssignmentBL()).findOrderForAsset(assetId,
				date);
		return null != order ? order.getId() : null;
	}

	public Integer[] findOrdersForAssetAndDateRange(Integer assetId,
			Date startDate, Date endDate) {
		if (null == assetId || null == startDate || null == endDate)
			return null;
		List<OrderDTO> orders = (new AssetAssignmentBL())
				.findOrdersForAssetAndDateRange(assetId, startDate, endDate);
		Integer[] ids = new Integer[orders.size()];
		for (int i = 0; i < orders.size(); i++) {
			ids[i] = orders.get(i).getId();
		}
		return ids;
	}

	public Integer createMetaFieldGroup(MetaFieldGroupWS metafieldGroupWs) {

		MetaFieldGroup mfGroup = MetaFieldGroupBL.getDTO(metafieldGroupWs);
		mfGroup.setDateCreated(new Date());
		mfGroup.setDateUpdated(new Date());
		mfGroup.setEntityId(getCallerCompanyId());

		Integer id = new MetaFieldGroupBL(mfGroup).save();
		mfGroup.setId(id);
		List<InternationalDescriptionWS> descriptions = metafieldGroupWs
				.getDescriptions();
		for (InternationalDescriptionWS description : descriptions) {
			if (description.getLanguageId() != null
					&& description.getContent() != null) {
				if (description.isDeleted()) {
					mfGroup.deleteDescription(description.getLanguageId());
				} else {
					mfGroup.setDescription(description.getContent(),
							description.getLanguageId());
				}
			}
		}

		return id;

	}


	public void updateMetaFieldGroup(MetaFieldGroupWS metafieldGroupWs) {
		MetaFieldGroup mfGroup = MetaFieldGroupBL.getDTO(metafieldGroupWs);
		mfGroup.setEntityId(getCallerCompanyId());
		new MetaFieldGroupBL().update(mfGroup);

	}


	public void deleteMetaFieldGroup(Integer metafieldGroupId) {
		MetaFieldGroupBL metafieldGroupBL = new MetaFieldGroupBL();
		metafieldGroupBL.set(metafieldGroupId);
		// security check for metafieldGroup belongs to caller company
		if (metafieldGroupBL.getEntity() != null
				&& metafieldGroupBL.getEntity().getEntityId() == getCallerCompanyId()) {
			metafieldGroupBL.delete();
		} else {
			throw new SessionInternalError(
					"MetaField Group not found",
					new String[] { "MetaFieldGroup,metafield group,cannot.delete.metafieldgroup.error" });

		}
	}

	@Transactional(readOnly = true)
	public MetaFieldGroupWS getMetaFieldGroup(Integer metafieldGroupId)
			throws SessionInternalError {
		MetaFieldGroupBL metafieldGroupBL = new MetaFieldGroupBL();
		metafieldGroupBL.set(metafieldGroupId);

		MetaFieldGroupWS metaFieldGroupWS = null;
		try {
			// security check for metafieldGroup belongs to caller company
			if (metafieldGroupBL.getEntity() != null
					&& Objects.equals(metafieldGroupBL.getEntity().getEntityId(), getCallerCompanyId())) {
				metaFieldGroupWS = MetaFieldGroupBL.getWS(metafieldGroupBL
						.getEntity());
			}
		} catch (Exception e) {
			System.out.println("********* "+e.getMessage());
			e.printStackTrace();
			throw new SessionInternalError(
					"Exception retrieving MetaFieldGroup object",
					e,
					new String[] { "MetaFieldGroup,,cannot.get.metafieldgroup.error" });

		}
		return metaFieldGroupWS;
	}

	public Integer createMetaField(MetaFieldWS metafieldWs) {

		if (metafieldWs.getDataType().equals(DataType.SCRIPT)
				&& (null == metafieldWs.getFilename() || metafieldWs
						.getFilename().isEmpty())) {
			throw new SessionInternalError(
					"Script Meta Fields must define filename",
					new String[] { "MetaFieldWS,filename,metafield.validation.filename.required" });
		}

		MetaField metafield = MetaFieldBL.getDTO(metafieldWs,
				getCallerCompanyId());
		metafield = new MetaFieldBL().create(metafield);

		if (metafield != null)
			return metafield.getId();
		else
			throw new SessionInternalError(
					"MetaField can't be created",
					new String[] { "MetaField,metafield,cannot.save.metafield.error" });
	}


	public void updateMetaField(MetaFieldWS metafieldWs) {

		if (metafieldWs.getDataType().equals(DataType.SCRIPT)
				&& (null == metafieldWs.getFilename() || metafieldWs
						.getFilename().isEmpty())) {
			throw new SessionInternalError(
					"Script Meta Fields must define filename",
					new String[] { "MetaFieldWS,filename,metafield.validation.filename.required" });
		}

		MetaField metafield = MetaFieldBL.getDTO(metafieldWs,
				getCallerCompanyId());
		new MetaFieldBL().update(metafield);
	}


	public void deleteMetaField(Integer metafieldId) {
		MetaFieldBL metafieldBL = new MetaFieldBL();
		MetaField metafield = metafieldBL.getMetaField(metafieldId);
		try {

			// security check for metafieldGroup belongs to caller company
			if (metafield != null
					&& metafield.getEntityId().equals(getCallerCompanyId())) {
				metafieldBL.deleteIfNotParticipant(metafieldId);

			} else {
				throw new SessionInternalError(
						"MetaField not found",
						new String[] { "MetaField,metafield,not.found.metafield.error" });

			}
		} catch (Exception e) {
			throw new SessionInternalError(
					"Exception deleting MetaField",
					e,
					new String[] { "MetaField,metafield,cannot.delete.metafield.error" });
		}
	}

	@Transactional(readOnly = true)
	public MetaFieldWS getMetaField(Integer metafieldId) {

		MetaField metafield = MetaFieldBL.getMetaField(metafieldId);
		try {

			// security check for metafieldGroup belongs to caller company
			if (metafield != null
					&& Objects.equals(metafield.getEntityId(), getCallerCompanyId())) {
				return MetaFieldBL.getWS(metafield);

			} else {
				throw new SessionInternalError(
						"MetaField not found",
						new String[] { "MetaField,metafield,not.found.metafield.error" });

			}
		} catch (Exception e) {
			throw new SessionInternalError(
					"Exception converting MetaField to WS object",
					e,
					new String[] { "MetaField,metafield,cannot.convert.metafield.error" });
		}
	}

	@Transactional(readOnly = true)
	public MetaFieldWS[] getMetaFieldsForEntity(String entityType) {
		List<MetaField> metaFields = MetaFieldBL.getAvailableFieldsList(
				getCallerCompanyId(),
				new EntityType[] { EntityType.valueOf(entityType) });
		return MetaFieldBL.convertMetaFieldsToWS(metaFields);
	}

	@Transactional(readOnly = true)
	public MetaFieldGroupWS[] getMetaFieldGroupsForEntity(String entityType) {
		List<MetaFieldGroup> metaFieldGroups = new MetaFieldGroupBL()
				.getAvailableFieldGroups(getCallerCompanyId(),
						EntityType.valueOf(entityType));
		return MetaFieldGroupBL.convertMetaFieldGroupsToWS(metaFieldGroups);
	}

	public OrderWS processJMRData(UUID processId, String recordKey,
			Integer userId, Integer currencyId, Date eventDate,
			String description, Integer productId, String quantity,
			String pricing) {
		MediationProcessService mediationProcessService = Context.getBean("MediationProcessService");
		Integer configId = mediationProcessService.getCfgIdForMediattionProcessId(processId);
		JbillingMediationRecord JMR = new JbillingMediationRecord(JbillingMediationRecord.STATUS.UNPROCESSED, JbillingMediationRecord.TYPE.MEDIATION, getCallerCompanyId()
				, configId, recordKey, userId, eventDate, new BigDecimal(quantity), description, currencyId, productId, null, null, null, new BigDecimal(pricing), null, processId);
		return processJMRRecord(processId, JMR);
	}

	public OrderWS processJMRRecord(UUID processId,
			JbillingMediationRecord JMR) {
        MediationProcessService mediationProcessService = Context.getBean(MediationProcessService.BEAN_NAME);
        MediationProcess mediationProcess = mediationProcessService.getMediationProcess(processId);
        if (mediationProcess != null) throw new IllegalArgumentException("Mediation process for id:" + processId + " don't exist.");
        MediationConfiguration mediationConfiguration = new MediationConfigurationDAS().find(mediationProcess.getConfigurationId());
        MediationContext mediationContext = new MediationContext();
        mediationContext.setMediationCfgId(mediationConfiguration.getId());
        mediationContext.setJobName(mediationConfiguration.getMediationJobLauncher());
        mediationContext.setProcessIdForMediation(processId);
        mediationContext.setRecordToProcess(JMR);
        MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
        return getOrder(mediationService.launchMediation(mediationContext).get(0).getOrderId());
	}

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UUID processCDR(Integer configId, List<String> callDataRecords) {
        MediationConfiguration mediationConfiguration = new MediationConfigurationDAS().find(configId);
        MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
        mediationService.processCdr(getCallerCompanyId(), configId,
                mediationConfiguration.getMediationJobLauncher(), StringUtils.join(callDataRecords, "\n"));
        MediationProcessService mediationProcessService = Context.getBean(MediationProcessService.BEAN_NAME);
        return mediationProcessService.getLastMediationProcessId(getCallerCompanyId());
    }

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UUID runRecycleForConfiguration(Integer configId) {
		MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
		MediationProcessService mediationProcessService = Context.getBean("mediationProcessService");

		String jobName = getJobNameForMediationCfg(configId);
		Integer entityId = getCallerCompanyId();

		mediationService.recycleCdr(entityId, configId, jobName);
        return mediationProcessService.getLastMediationProcessId(getCallerCompanyId());
	}

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public UUID runRecycleForMediationProcess(UUID processId) {
		MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
		MediationProcessService mediationProcessService = Context.getBean("mediationProcessService");

		int cfgId = mediationProcessService.getCfgIdForMediattionProcessId(processId);
		String jobName = getJobNameForMediationCfg(cfgId);
		Integer entityId = getCallerCompanyId();

		mediationService.recycleCdr(entityId, cfgId, jobName, processId);
        return mediationProcessService.getLastMediationProcessId(getCallerCompanyId());
	}

	private String getJobNameForMediationCfg(int configId) {
		MediationConfigurationDAS mediationConfigurationDAS = new MediationConfigurationDAS();
		MediationConfiguration mediationConfiguration = mediationConfigurationDAS.find(configId);
		return mediationConfiguration.getMediationJobLauncher();
	}

	/*
	 * Diameter Protocol
	 */

	public DiameterResultWS createSession(String sessionId, Date timestamp,
			BigDecimal units, String data) throws SessionInternalError {
		LOG.debug(
				"Parameters received: sessionId - %s, timestamp - %s, units - %s, data - %s",
				sessionId, timestamp, units, data);

		try {
			DiameterUserLocator userLoc = Context
					.getBean(Context.Name.DIAMETER_USER_LOCATOR);
			DiameterItemLocator itemLoc = Context
					.getBean(Context.Name.DIAMETER_ITEM_LOCATOR);
			DiameterBL diameter = new DiameterBL(userLoc, itemLoc,
					getCallerCompanyId());

			PricingFieldsHelper fieldsHelper = new PricingFieldsHelper(data);

			return diameter.createSession(sessionId, timestamp, units,
					fieldsHelper.getFields());
		} catch (Exception ex) {
			return new DiameterResultWS(
					DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
		}
	}

	public DiameterResultWS reserveUnits(String sessionId, Date timestamp,
										 int units, String data) throws SessionInternalError {
		try {
			DiameterUserLocator userLoc = Context
					.getBean(Context.Name.DIAMETER_USER_LOCATOR);
			DiameterItemLocator itemLoc = Context
					.getBean(Context.Name.DIAMETER_ITEM_LOCATOR);
			DiameterBL diameter = new DiameterBL(userLoc, itemLoc,
					getCallerCompanyId());

			PricingFieldsHelper fieldsHelper = new PricingFieldsHelper(data);

			return diameter.reserveUnits(sessionId, timestamp, units,
					fieldsHelper.getFields());
		} catch (Exception ex) {
			return new DiameterResultWS(
					DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
		}
	}

	public DiameterResultWS updateSession(String sessionId, Date timestamp,
			BigDecimal usedUnits, BigDecimal reqUnits, String data)
			throws SessionInternalError {
		try {
			DiameterUserLocator userLoc = Context
					.getBean(Context.Name.DIAMETER_USER_LOCATOR);
			DiameterItemLocator itemLoc = Context
					.getBean(Context.Name.DIAMETER_ITEM_LOCATOR);
			DiameterBL diameter = new DiameterBL(userLoc, itemLoc,
					getCallerCompanyId());

			PricingFieldsHelper fieldsHelper = new PricingFieldsHelper(data);

			return diameter.updateSession(sessionId, timestamp, usedUnits,
					reqUnits, fieldsHelper.getFields());
		} catch (Exception ex) {
			return new DiameterResultWS(
					DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
		}
	}

	public DiameterResultWS extendSession(String sessionId, Date timestamp,
										  BigDecimal usedUnits, BigDecimal reqUnits)
			throws SessionInternalError {
		try {
			DiameterUserLocator userLoc = Context
					.getBean(Context.Name.DIAMETER_USER_LOCATOR);
			DiameterItemLocator itemLoc = Context
					.getBean(Context.Name.DIAMETER_ITEM_LOCATOR);
			DiameterBL diameter = new DiameterBL(userLoc, itemLoc,
					getCallerCompanyId());
			return diameter.extendSession(sessionId, timestamp, usedUnits,
					reqUnits);
		} catch (Exception ex) {
			return new DiameterResultWS(
					DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
		}
	}

	public DiameterResultWS endSession(String sessionId, Date timestamp,
			BigDecimal usedUnits, int causeCode) throws SessionInternalError {
		DiameterUserLocator userLoc = Context
				.getBean(Context.Name.DIAMETER_USER_LOCATOR);
		DiameterItemLocator itemLoc = Context
				.getBean(Context.Name.DIAMETER_ITEM_LOCATOR);
		DiameterBL diameter = new DiameterBL(userLoc, itemLoc,
				getCallerCompanyId());
		return diameter.endSession(sessionId, timestamp, usedUnits, causeCode);
	}

	public DiameterResultWS consumeReservedUnits(String sessionId,
			Date timestamp, int usedUnits, int causeCode)
			throws SessionInternalError {
		DiameterUserLocator userLoc = Context
				.getBean(Context.Name.DIAMETER_USER_LOCATOR);
		DiameterItemLocator itemLoc = Context
				.getBean(Context.Name.DIAMETER_ITEM_LOCATOR);
		DiameterBL diameter = new DiameterBL(userLoc, itemLoc,
				getCallerCompanyId());
		return diameter.consumeReservedUnits(sessionId, timestamp, usedUnits,
				causeCode);
	}

	/**
	 * 
	 * @param entities
	 * @return
	 */
	private Set<CompanyDTO> convertToCompanyDTO(List<Integer> entities) {
		Set<CompanyDTO> childEntities = new HashSet<CompanyDTO>(0);

		for (Integer entity : entities) {
			childEntities.add(new CompanyDAS().find(entity));
		}

		return childEntities;
	}

	private void validateItemMandatoryDependenciesCycle(Integer rootItemId,
			Collection<Integer> dependencies) {
		if (dependencies == null || dependencies.isEmpty()
				|| rootItemId == null)
			return;
		if (dependencies.contains(rootItemId)) {
			String errorCode = "ItemDTOEx,mandatoryItems,product.error.dependencies.cycle";
			throw new SessionInternalError(
					"Cycle in product mandatory dependencies was found",
					new String[] { errorCode });
		}
		ItemDAS itemDas = new ItemDAS();
		for (Integer dependentItemId : dependencies) {
			ItemDTO item = itemDas.find(dependentItemId);
			if (item != null && item.getDependencies() != null
					&& !item.getDependencies().isEmpty()) {
				List<Integer> childDependencies = new LinkedList<Integer>();
				for (ItemDependencyDTO dependencyDTO : item.getDependencies()) {
					if (dependencyDTO.getType().equals(ItemDependencyType.ITEM)
							&& dependencyDTO.getMinimum() > 0) {
						childDependencies.add(dependencyDTO
								.getDependentObjectId());
					}
				}
				validateItemMandatoryDependenciesCycle(rootItemId,
						childDependencies);
			}
		}
	}

	public Integer createOrUpdateDiscount(DiscountWS discount) {
		Integer languageId = getCallerLanguageId();
		discount.setEntityId(getCallerCompanyId());

		DiscountBL bl = new DiscountBL();
		return bl.createOrUpdate(discount, languageId);
	}

	@Transactional(readOnly = true)
	public OrderPeriodWS[] getOrderPeriods() throws SessionInternalError {

		Integer entityId = getCallerCompanyId();

		OrderPeriodDAS periodDas = new OrderPeriodDAS();
		List<OrderPeriodDTO> orderPeriods = periodDas.getOrderPeriods(entityId);
		OrderPeriodWS[] periods = new OrderPeriodWS[orderPeriods.size()];
		int index = 0;
		for (OrderPeriodDTO periodDto : orderPeriods) {
			periods[index++] =OrderBL.getOrderPeriodWS(periodDto);
		}

		return periods;
	}

	/**
	 * Select orderChangeStatuses for current entity
	 * 
	 * @return List of orderChangeStatuses
	 */
	@Transactional(readOnly = true)
	public OrderChangeStatusWS[] getOrderChangeStatusesForCompany() {
		List<OrderChangeStatusDTO> statusDTOs = new OrderChangeStatusDAS()
				.findOrderChangeStatuses(getCallerCompanyId());
		List<OrderChangeStatusWS> results = new LinkedList<OrderChangeStatusWS>();
		List<LanguageDTO> languages = new LanguageDAS().findAll();
		for (OrderChangeStatusDTO status : statusDTOs) {
			OrderChangeStatusWS ws = OrderChangeStatusBL.getWS(status);
			for (LanguageDTO lang : languages) {
				if (ws.getDescription(lang.getId()) != null) {
					continue;
				}
				InternationalDescriptionDTO descriptionDTO = status
						.getDescriptionDTO(lang.getId());
				if (descriptionDTO != null
						&& descriptionDTO.getContent() != null) {
					ws.addDescription(DescriptionBL.getInternationalDescriptionWS(
							descriptionDTO));
				}
			}
			results.add(ws);
		}
		return results.toArray(new OrderChangeStatusWS[results.size()]);
	}

	/**
	 * Create orderChangeStatus with validation
	 *
	 * @param orderChangeStatusWS
	 *            input OrderChangeStatus
	 * @return id of OrderChangeStatus created
	 * @throws SessionInternalError
	 *             if validation fails
	 */
	public Integer createOrderChangeStatus(
			OrderChangeStatusWS orderChangeStatusWS)
			throws SessionInternalError {
		OrderChangeStatusDTO orderChangeStatusDTO = OrderChangeStatusBL.getDTO(orderChangeStatusWS);
		orderChangeStatusDTO = OrderChangeStatusBL.createOrderChangeStatus(
				orderChangeStatusDTO, getCallerCompanyId());

		if (orderChangeStatusWS.getDescriptions() != null
				&& orderChangeStatusWS.getDescriptions().size() > 0) {
			for (InternationalDescriptionWS desc : orderChangeStatusWS
					.getDescriptions()) {
				if (!OrderChangeStatusBL.isDescriptionUnique(
						getCallerCompanyId(), orderChangeStatusDTO.getId(),
						desc.getLanguageId(), desc.getContent())) {
					String[] errorMessages = new String[] { "OrderChangeStatusWS,descriptions,orderChangeStatusWS.error.unique.name" };
					throw new SessionInternalError(
							"Order Change Status validation error",
							errorMessages);
				}
				orderChangeStatusDTO.setDescription(desc.getContent(),
						desc.getLanguageId());
			}
		}

		return orderChangeStatusDTO.getId();
	}

	/**
	 * Update orderChangeStatus with validation
	 * 
	 * @param orderChangeStatusWS
	 *            input updated OrderChangeStatus
	 * @throws SessionInternalError
	 *             if validation fails
	 */

	public void updateOrderChangeStatus(OrderChangeStatusWS orderChangeStatusWS)
			throws SessionInternalError {
		OrderChangeStatusDTO orderChangeStatusDTO = OrderChangeStatusBL.getDTO(orderChangeStatusWS);
		OrderChangeStatusBL.updateOrderChangeStatus(orderChangeStatusDTO,
				getCallerCompanyId());
		orderChangeStatusDTO = new OrderChangeStatusDAS()
				.find(orderChangeStatusDTO.getId());

		if (orderChangeStatusWS.getDescriptions() != null
				&& orderChangeStatusWS.getDescriptions().size() > 0) {
			for (InternationalDescriptionWS desc : orderChangeStatusWS
					.getDescriptions()) {
				if (!OrderChangeStatusBL.isDescriptionUnique(
						getCallerCompanyId(), orderChangeStatusDTO.getId(),
						desc.getLanguageId(), desc.getContent())) {
					String[] errorMessages = new String[] { "OrderChangeStatusWS,descriptions,orderChangeStatusWS.error.unique.name" };
					throw new SessionInternalError(
							"Order Change Status validation error",
							errorMessages);
				}
				orderChangeStatusDTO.setDescription(desc.getContent(),
						desc.getLanguageId());
			}
		}
	}


	public void deleteOrderChangeStatus(Integer id) throws SessionInternalError {
		OrderChangeStatusBL.deleteOrderChangeStatus(id, getCallerCompanyId());
	}

	/**
	 * Create, update or delete orderChangeSatuses
	 * 
	 * @param orderChangeStatuses
	 *            array of ws objects for create/update/delete
	 * @throws SessionInternalError
	 *             if some operation fails
	 */
	public void saveOrderChangeStatuses(
			OrderChangeStatusWS[] orderChangeStatuses)
			throws SessionInternalError {
		for (OrderChangeStatusWS ws : orderChangeStatuses) {
			if (ws.getId() != null && ws.getId() > 0) {
				if (ws.getDeleted() > 0) {
					deleteOrderChangeStatus(ws.getId());
				} else {
					updateOrderChangeStatus(ws);
				}
			} else {
				createOrderChangeStatus(ws);
			}
		}
	}

	/**
	 * This method checks whether the paymentInstruments are unique or not. It
	 * tries to add all the paymentInstruments in a HashSet. If any one of the
	 * additions fails, this means that the payment instrument already exists
	 * 
	 * @param paymentInstruments
	 * @throws SessionInternalError
	 */
	private void validateUniquePaymentInstruments(
			List<PaymentInformationWS> paymentInstruments)
			throws SessionInternalError {
		HashSet<PaymentInformationWS> validatorSet = new HashSet<PaymentInformationWS>();

		for (PaymentInformationWS paymentInstrument : paymentInstruments) {
			if (!validatorSet.add(paymentInstrument)) {
				throw new SessionInternalError(
						"Duplicate payment method not allowed",
						new String[] { "PaymentWS,paymentMethodId,validation.error.duplicate.payment.method" });
			}
		}
	}

	/**
	 * Select OrderChangeTypes for current entity
	 * 
	 * @return List of OrderChangeTypeWS found
	 */
	@Transactional(readOnly = true)
	public OrderChangeTypeWS[] getOrderChangeTypesForCompany() {
		List<OrderChangeTypeWS> result = new LinkedList<OrderChangeTypeWS>();
		for (OrderChangeTypeDTO dto : new OrderChangeTypeDAS()
				.findOrderChangeTypes(getCallerCompanyId())) {
			result.add(OrderChangeTypeBL.getWS(dto));
		}
		return result.toArray(new OrderChangeTypeWS[result.size()]);
	}

	/**
	 * Find OrderChangeType by name for current entity
	 * 
	 * @param name
	 *            name for search
	 * @return OrderChangeType found or null
	 */
	@Transactional(readOnly = true)
	public OrderChangeTypeWS getOrderChangeTypeByName(String name) {
		OrderChangeTypeDTO dto = new OrderChangeTypeDAS()
				.findOrderChangeTypeByName(name, getCallerCompanyId());
		return dto != null ? OrderChangeTypeBL.getWS(dto) : null;
	}

	@Transactional(readOnly = true)
	public OrderChangeTypeWS getOrderChangeTypeById(Integer orderChangeTypeId) {
		OrderChangeTypeDTO dto = new OrderChangeTypeDAS()
				.find(orderChangeTypeId);
		return dto != null ? OrderChangeTypeBL.getWS(dto) : null;
	}

	public Integer createUpdateOrderChangeType(
			OrderChangeTypeWS orderChangeTypeWS) {
		OrderChangeTypeDTO dto = OrderChangeTypeBL.getDTO(orderChangeTypeWS,
				getCallerCompanyId());
		OrderChangeTypeBL changeTypeBL = new OrderChangeTypeBL();
		OrderChangeTypeWS existedTypeWithSameName = getOrderChangeTypeByName(dto
				.getName());
		// name should be unique within entity
		if (existedTypeWithSameName != null
				&& (dto.getId() == null || !dto.getId().equals(
						existedTypeWithSameName.getId()))) {
			throw new SessionInternalError(
					"Order Change Type validation failed: name is not unique",
					new String[] { "OrderChangeTypeWS,name,OrderChangeTypeWS.validation.error.name.not.unique,"
							+ dto.getName() });
		}

		return changeTypeBL.createUpdateOrderChangeType(dto,
				getCallerCompanyId());
	}


	public void deleteOrderChangeType(Integer orderChangeTypeId) {
		OrderChangeTypeDAS das = new OrderChangeTypeDAS();
		if (das.isOrderChangeTypeInUse(orderChangeTypeId)) {
			throw new SessionInternalError(
					"Order Change Type validation failed: name is not unique",
					new String[] { "OrderChangeTypeWS.delete.error.type.in.use" });
		}
		new OrderChangeTypeBL().delete(orderChangeTypeId, getCallerCompanyId());
	}

	/**
	 * Select order changes for order
	 * 
	 * @param orderId
	 *            target order id for changes select
	 * @return List of orderChangeWS objects
	 */
	@Transactional(readOnly = true)
	public OrderChangeWS[] getOrderChanges(Integer orderId) {
		List<OrderChangeDTO> orderChanges = new OrderChangeDAS()
				.findByOrder(orderId);
		List<OrderChangeWS> result = new LinkedList<OrderChangeWS>();
		Map<OrderChangeDTO, OrderChangeWS> dtoToWsMap = new HashMap<OrderChangeDTO, OrderChangeWS>();
		for (OrderChangeDTO dto : orderChanges) {
			OrderChangeWS ws = dtoToWsMap.get(dto);
			if (ws == null) {
				ws = OrderChangeBL
						.getWS(dto, getCallerLanguageId(), dtoToWsMap);
			}
			result.add(ws);
		}
		return result.toArray(new OrderChangeWS[result.size()]);
	}

	/*
	 * Payment Methods
	 */
	@Transactional(readOnly = true)
	public PaymentMethodTemplateWS getPaymentMethodTemplate(Integer templateId) {
		PaymentMethodTemplateDAS das = new PaymentMethodTemplateDAS();
		PaymentMethodTypeBL bl = new PaymentMethodTypeBL();

		PaymentMethodTemplateDTO dto = das.findNow(templateId);

		if (templateId == null || dto == null) {
			return null;
		}
		return bl.getWS(dto, getCallerCompanyId());
	}

	/**
	 * Create a payment method type
	 * 
	 * @param paymentMethodType
	 *            instance
	 * @return Id of created payment method type
	 */
	public Integer createPaymentMethodType(PaymentMethodTypeWS paymentMethodType) {
		if (paymentMethodType.getMetaFields() != null) {
			for (MetaFieldWS field : paymentMethodType.getMetaFields()) {
				if (field.getDataType().equals(DataType.SCRIPT)
						&& (null == field.getFilename() || field.getFilename()
						.isEmpty())) {
					throw new SessionInternalError(
							"Script Meta Fields must define filename",
							new String[] { "PaymentMethodTypeWS,metaFields,metafield.validation.filename.required" });
				}
			}
		}

		PaymentMethodTypeBL bl = new PaymentMethodTypeBL(paymentMethodType);

		List<PaymentMethodTypeDTO> paymentMethodTypeDTOs = new PaymentMethodTypeDAS()
				.findByMethodName(paymentMethodType.getMethodName().trim(),
						getCallerCompanyId());

		if (paymentMethodTypeDTOs != null && paymentMethodTypeDTOs.size() > 0) {
			throw new SessionInternalError(
					"Payment Method Type already exists with method name "
							+ paymentMethodType.getMethodName(),
					new String[] { "PaymentMethodTypeWS,methodName,validation.error.methodname.already.exists" });
		}

		PaymentMethodTypeDTO dto = bl.getDTO(getCallerCompanyId());
		dto = bl.create(dto);

		return dto.getId();
	}

	@Transactional(readOnly = true)
	public ProvisioningCommandWS[] getProvisioningCommands(
			ProvisioningCommandType type, Integer typeId) {
		if (type != null) {
			ProvisioningCommandBL bl = new ProvisioningCommandBL();
			List<ProvisioningCommandWS> commandsList = bl.getCommandWSList(
					type, typeId);
			return (null == commandsList) ? null : commandsList
					.toArray(new ProvisioningCommandWS[commandsList.size()]);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public ProvisioningCommandWS getProvisioningCommandById(
			Integer provisioningCommandId) {

		ProvisioningCommandBL bl = new ProvisioningCommandBL(
				provisioningCommandId);
		return bl.getCommandWS(bl.getProvisioningCommandDTO());
	}

	@Transactional(readOnly = true)
	public ProvisioningRequestWS[] getProvisioningRequests(
			Integer provisioningCommandId) {
		if (provisioningCommandId != null) {
			ProvisioningCommandBL cmdBL = new ProvisioningCommandBL();
			List<ProvisioningRequestWS> requestList = cmdBL
					.getRequestList(provisioningCommandId);
			return (null == requestList) ? null : requestList
					.toArray(new ProvisioningRequestWS[requestList.size()]);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public ProvisioningRequestWS getProvisioningRequestById(
			Integer provisioningRequestId) {

		ProvisioningRequestBL bl = new ProvisioningRequestBL(
				provisioningRequestId);
		return bl.getProvisioningRequestWS(bl.getProvisioningRequest());
	}

	@Transactional(readOnly = true)
	public List<ProvisioningCommandWS> getCommandsByEntityId(Integer entityId)
			throws SessionInternalError {
		ProvisioningCommandDAS das = new ProvisioningCommandDAS();
		ProvisioningCommandBL bl = new ProvisioningCommandBL();
		List<ProvisioningCommandDTO> cmdList = das
				.findCommandsByEntityId(entityId);
		List<ProvisioningCommandWS> cmdWSList = new ArrayList<ProvisioningCommandWS>();
		if (cmdList != null) {
			for (ProvisioningCommandDTO dto : cmdList) {
				ProvisioningCommandWS ws = bl.getCommandWS(dto);
				cmdWSList.add(ws);
			}
		}
		return cmdWSList;
	}

	@Transactional(readOnly = true)
	public List<ProvisioningCommandWS> getConvertedProvisioningTypeWS(
			List<ProvisioningCommandDTO> cmdList) {
		ProvisioningCommandBL bl = new ProvisioningCommandBL();
		List<ProvisioningCommandWS> cmdWSList = new ArrayList<ProvisioningCommandWS>();
		if (cmdList != null) {
			for (ProvisioningCommandDTO dto : cmdList) {
				ProvisioningCommandWS ws = bl.getCommandWS(dto);
				cmdWSList.add(ws);
			}
		}
		return cmdWSList;
	}

	public CustomerUsagePoolDTO createCustomerUsagePool(CustomerUsagePoolWS ws) {
		CustomerUsagePoolDTO customerUsagePoolDto = CustomerUsagePoolBL.getDTO(ws);
		CustomerUsagePoolBL bl = new CustomerUsagePoolBL();
		return bl.createOrUpdateCustomerUsagePool(customerUsagePoolDto);
	}

	public CustomerUsagePoolDTO updateCustomerUsagePool(
			CustomerUsagePoolWS customerUsagePoolWs) {
		CustomerUsagePoolDTO customerUsagePoolDto = CustomerUsagePoolBL.getDTO(customerUsagePoolWs);
		CustomerUsagePoolBL bl = new CustomerUsagePoolBL();
		return bl.createOrUpdateCustomerUsagePool(customerUsagePoolDto);
	}

	@Transactional(readOnly = true)
	public List<CustomerUsagePoolDTO> getCustomerUsagePools(Integer customerId) {

		if (customerId == null)
			throw new SessionInternalError("Customer Id cannot be null.");
		List<CustomerUsagePoolDTO> customerUsagePools = new CustomerUsagePoolDAS()
				.findCustomerUsagePoolByCustomerId(customerId);

		Collections
				.sort(customerUsagePools,
						CustomerUsagePoolDTO.CustomerUsagePoolsByPrecedenceOrCreatedDateComparator);

		return customerUsagePools;
	}

	@Transactional(readOnly = true)
	public CustomerUsagePoolWS getCustomerUsagePoolById(
			Integer customerUsagePoolId) {
		CustomerUsagePoolBL customerUsagePoolBl = new CustomerUsagePoolBL(
				customerUsagePoolId);
		CustomerUsagePoolWS customerUsagePoolWS = customerUsagePoolBl
				.getWS(customerUsagePoolBl.getEntity());
		return customerUsagePoolWS;
	}

	@Transactional(readOnly = true)
	public CustomerUsagePoolWS[] getCustomerUsagePoolsByCustomerId(
			Integer customerId) {
		List<CustomerUsagePoolDTO> customerUsagePools = new CustomerUsagePoolBL()
				.getCustomerUsagePoolsByCustomerId(customerId);
		List<CustomerUsagePoolWS> customerUsagePoolsWsList = new ArrayList<CustomerUsagePoolWS>();

		for (CustomerUsagePoolDTO customerUsagePool : customerUsagePools) {
			CustomerUsagePoolWS ws = CustomerUsagePoolBL.getCustomerUsagePoolWS(customerUsagePool);
			customerUsagePoolsWsList.add(ws);
		}
		return customerUsagePoolsWsList
				.toArray(new CustomerUsagePoolWS[customerUsagePoolsWsList
						.size()]);
	}

	public void updatePaymentMethodType(PaymentMethodTypeWS paymentMethodType) {
		if (paymentMethodType.getMetaFields() != null) {
			for (MetaFieldWS field : paymentMethodType.getMetaFields()) {
				if (field.getDataType().equals(DataType.SCRIPT)
						&& (null == field.getFilename() || field.getFilename()
								.isEmpty())) {
					throw new SessionInternalError(
							"Script Meta Fields must define filename",
							new String[] { "PaymentMethodTypeWS,metaFields,metafield.validation.filename.required" });
				}
			}
		}

		PaymentMethodTypeBL bl = new PaymentMethodTypeBL(paymentMethodType);
		List<PaymentMethodTypeDTO> paymentMethodTypeDTOs = new PaymentMethodTypeDAS()
				.findByMethodName(paymentMethodType.getMethodName(),
						getCallerCompanyId());
		String originalMethodName = new PaymentMethodTypeDAS().find(
				paymentMethodType.getId()).getMethodName();

		if ((originalMethodName.equals(paymentMethodType.getMethodName())
				&& paymentMethodTypeDTOs != null && paymentMethodTypeDTOs
				.size() > 1)
				|| (!originalMethodName.equals(paymentMethodType
						.getMethodName()) && paymentMethodTypeDTOs != null && paymentMethodTypeDTOs
						.size() > 0)) {
			throw new SessionInternalError(
					"Payment Method Type already exists with method name "
							+ paymentMethodType.getMethodName(),
					new String[] { "PaymentMethodTypeWS,methodName,validation.error.methodname.already.exists" });
		}
		PaymentInformationDAS paymentInformationDAS = new PaymentInformationDAS();
		PaymentMethodTypeWS existing = new PaymentMethodTypeBL(
				paymentMethodType.getId()).getWS();
		List<Integer> removedMethodType = (List<Integer>) CollectionUtils
				.subtract(existing.getAccountTypes(),
						paymentMethodType.getAccountTypes());
		for (Integer accountTypeId : removedMethodType) {
			long l = paymentInformationDAS
					.findByAccountTypeAndPaymentMethodType(accountTypeId,
							paymentMethodType.getId());
			if (l > 0) {
				throw new SessionInternalError(
						"",
						new String[] { "PaymentMethodTypeWS,accountType,validation.error.account.inUse" });
			}
		}
		PaymentMethodTypeDTO dto = bl.getDTO(getCallerCompanyId());

		bl.update(dto);
	}

	/**
	 * Gets payment method type and return it after converting to ws object if
	 * none is found then null is returned
	 * 
	 * @return PaymentMethodTypeWS
	 */
	@Transactional(readOnly = true)
	public PaymentMethodTypeWS getPaymentMethodType(Integer paymentMethodTypeId) {
		if (paymentMethodTypeId == null) {
			return null;
		}

		PaymentMethodTypeBL bl = new PaymentMethodTypeBL(paymentMethodTypeId);
		return bl.getWS();
	}

	public boolean deletePaymentMethodType(Integer paymentMethodTypeId)
			throws SessionInternalError {
		try {
			PaymentMethodTypeBL bl = new PaymentMethodTypeBL(
					paymentMethodTypeId);
			return bl.delete();
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}
	}

	/**
	 * Remove an existing payment instrument
	 */
	public boolean removePaymentInstrument(Integer instrumentId) {
		PaymentInformationDAS das = new PaymentInformationDAS();
		boolean removed = false;

		PaymentInformationDTO dto = das.findNow(instrumentId);
		if (dto != null) {
			try {
				das.delete(dto);
				removed = true;
			} catch (Exception e) {
				LOG.error("Could not delete payment instrument. Exception is: "
						+ e);
			}
		}
		return removed;
	}

	/*
	 * Method added to fix #7375 #7375 - A product gets added by the name of
	 * plan when a plan is unsuccessful getting saved, after a FUP is being
	 * selected-
	 */
	public Integer createPlan(PlanWS plan, ItemDTOEx product) {
		// #12253 - setting this value to skip product level metafield
		// validation while creating a plan.
		product.setIsPlan(true);
		Integer id = createItem(product);
		product.setId(id);
		plan.setItemId(id);
		return createPlan(plan);
	}

	/*
	 * Method added to fix #7375 #7375 - A product gets added by the name of
	 * plan when a plan is unsuccessful getting saved, after a FUP is being
	 * selected-
	 */

	public void updatePlan(PlanWS plan, ItemDTOEx product) {
		// #12253-setting this value to skip product level metafield
		// validation while creating a plan.
		product.setIsPlan(true);
		updateItem(product);
		updatePlan(plan);
	}


	public void deleteOrderStatus(OrderStatusWS orderStatus)
			throws SessionInternalError {
		OrderDAS orderDAS = new OrderDAS();
		OrderChangeDAS orderChangeDAS = new OrderChangeDAS();
		try {
			if ((orderDAS.orderHasStatus(orderStatus.getId(),
					getCallerCompanyId()) == false)
					&& ((orderChangeDAS.orderChangeHasStatus(orderStatus
							.getId())) == false)) {
				OrderStatusBL bl = new OrderStatusBL(orderStatus.getId());
				bl.delete(getCallerCompanyId());
				return;
			}

			throw new SessionInternalError(
					"Cannot Delete. Order Status currently in use.",
					new String[] { "OrderStatusWS,statusExist,validation.error.status.used" });
		} catch (Exception e) {
			throw new SessionInternalError(e);
		}
	}

	public void deleteEdiFileStatus(Integer ediStatusId)
			throws SessionInternalError {

		if(new EDITypeDAS().countByStatusId(ediStatusId)>0){
			throw new SessionInternalError("Could not delete EDI Status Type",
					new String[]{"cannot.delete.edi.status.edi.type.using"});
		}

		EDIFileStatusDAS ediFileStatusDAS =new EDIFileStatusDAS();
		EDIFileStatusDTO ediFileStatusDTO= ediFileStatusDAS.find(ediStatusId);
		new EDIFileStatusDAS().delete(ediFileStatusDTO);


	}

	@Override
	public Integer createUpdateOrderStatus(OrderStatusWS orderStatusWS)
			throws SessionInternalError {
		OrderStatusBL orderStatusBL = new OrderStatusBL();
		for (InternationalDescriptionWS desc : orderStatusWS.getDescriptions()) {
			try {
				if (!orderStatusBL.isOrderStatusValid(orderStatusWS,
						getCallerCompanyId(), desc.getContent())) {
					throw new SessionInternalError(
							"Order status exist ",
							new String[] { "OrderStatusWS,status,validation.error.status.already.exists" });
				}
			} catch (Exception e) {
				throw new SessionInternalError(e);
			}
		}
		Integer orderId = orderStatusBL.create(orderStatusWS,
				getCallerCompanyId(), getCompany().getLanguageId());
		return orderId;
	}

	@Override
	public Integer createUpdateEdiStatus(EDIFileStatusWS ediFileStatusWS)
			throws SessionInternalError {
		EDIFileStatusBL ediFileStatusBL = new EDIFileStatusBL();
		Integer ediStatusId = ediFileStatusBL.create(ediFileStatusWS);
		return ediStatusId;
	}

	@Override
	public OrderStatusWS findOrderStatusById(Integer orderStatusId) {
		OrderStatusWS orderStatusWS = new OrderStatusDAS()
				.findOrderStatusById(orderStatusId);
		return orderStatusWS != null ? orderStatusWS : null;
	}

	@Override
	public EDIFileStatusWS findEdiStatusById(Integer ediStatusId) {
		EDIFileStatusDTO ediFileStatusDTO = new EDIFileStatusDAS().find(ediStatusId);
		return new EDIFileStatusBL().getWS(ediFileStatusDTO);
	}

	@Transactional(readOnly = true)
	public int getDefaultOrderStatusId(OrderStatusFlag flag, Integer entityId) {
		// #7853 - If no order statuses are configured via the configuration
		// menu an exception is shown on the 'create order' UI. Following
		// is exception handling added to take care of the issue.
		try {
			return new OrderStatusDAS().getDefaultOrderStatusId(flag, entityId);
		} catch (Exception e) {
			throw new SessionInternalError(
					"Order validation failed. No order status found for the order",
					new String[] { "OrderWS,orderStatus,No order status found for the order" });
		}
	}

	private void validateLines(OrderWS order) {
		// #7761 - moved out of validateOrder function due to problem in
		// removing order line
		List<Integer> usedCategories = new ArrayList<Integer>();
		OrderBL orderBl = new OrderBL();
		ItemBL itemBl = new ItemBL();

		if (order.getOrderLines() != null) {

			if (order.getOrderLines().length == 1) {
				if (new ItemDAS().find(order.getOrderLines()[0].getItemId())
						.getPercentage() != null) {
					throw new SessionInternalError(
							"Order can not create for line percentage product",
							new String[] { "validation.error.order.linePercentage.product" });
				}
			}

			for (OrderLineWS line : order.getOrderLines()) {

				itemBl.set(line.getItemId());
				if (!orderBl.isPeriodValid(itemBl.getEntity(),
						order.getActiveSince(), order.getActiveUntil())) {
					throw new SessionInternalError(
							"Validity period of order should be within validity period of plan/product",
							new String[] { "validation.order.line.not.added.valdidity.period" });
				}

				if (!orderBl.isCompatible(order.getUserId(),
						itemBl.getEntity(), order.getActiveSince(),
						order.getActiveUntil(), usedCategories, line)) {
					throw new SessionInternalError(
							"User can subscribe only to one plan/product from given category",
							new String[] { "validation.order.line.not.added.not.compatible" });
				}
			}
		}
	}

	@Override
	public Integer reserveAsset(Integer assetId, Integer userId) {
		final AssetReservationBL assetReservationBL = new AssetReservationBL();
		return assetReservationBL.reserveAsset(assetId, getCallerId(), userId);
	}

	@Override
	public void releaseAsset(Integer assetId, Integer userId) {
		new AssetReservationBL().releaseAsset(assetId, getCallerId(), userId);
	}

	@Transactional(readOnly = true)
	public PluggableTaskTypeWS getPluginTypeWS(Integer id) {
		PluggableTaskTypeDAS das = new PluggableTaskTypeDAS();
		PluggableTaskTypeDTO dto = das.find(id);
		return PluggableTaskBL.getPluggableTaskTypeWS(dto);
	}

	@Transactional(readOnly = true)
	public PluggableTaskTypeWS getPluginTypeWSByClassName(String className) {
		PluggableTaskTypeDAS das = new PluggableTaskTypeDAS();
		PluggableTaskTypeDTO dto = das.findByClassName(className);
		return PluggableTaskBL.getPluggableTaskTypeWS(dto);
	}

	@Transactional(readOnly = true)
	public PluggableTaskTypeCategoryWS getPluginTypeCategory(Integer id) {
		PluggableTaskTypeCategoryDAS das = new PluggableTaskTypeCategoryDAS();
		PluggableTaskTypeCategoryDTO dto = das.find(id);
		return PluggableTaskBL.getPluggableTaskTypeCategoryWS(dto);
	}

	@Transactional(readOnly = true)
	public PluggableTaskTypeCategoryWS getPluginTypeCategoryByInterfaceName(
			String interfaceName) {
		PluggableTaskTypeCategoryDAS das = new PluggableTaskTypeCategoryDAS();
		PluggableTaskTypeCategoryDTO dto = das
				.findByInterfaceName(interfaceName);
		return PluggableTaskBL.getPluggableTaskTypeCategoryWS(dto);
	}

	/*
	 * Subscription products
	 */

	/**
	 * Iterates over subscription lines of each order and creates a subaccount
	 * having a subscription order for each subscription lines, Then the
	 * subscription line is removed from the original order and change related
	 * to that line is also removed from changes list
	 * 
	 * @param parentAccountId
	 *            id of the user account for which order is being created
	 * @param order
	 *            original order containing subscription products
	 * @param createInvoice
	 *            a flag to indicate if invoices for subscription orders should
	 *            be geneated
	 * @param orderChanges
	 *            a modfiable list of changes for order
	 * 
	 * @return Integer list a list of created subscription order ids
	 */
	public Integer[] createSubscriptionAccountAndOrder(Integer parentAccountId,
			OrderWS order, boolean createInvoice,
			List<OrderChangeWS> orderChanges) {
		if (parentAccountId == null || order == null) {
			LOG.error("To create subscription orders, user or order can not be null");
		}

		List<OrderLineWS> nslines = new ArrayList<OrderLineWS>();
		List<OrderLineWS> slines = new ArrayList<OrderLineWS>();

		List<OrderChangeWS> schanges = new ArrayList<OrderChangeWS>();

		if (orderChanges != null) {
			schanges = new ArrayList<OrderChangeWS>();
		}

		List<Integer> subscriptionItems = new ArrayList<Integer>();
		OrderDAS orderDas = new OrderDAS();

		// separate subscription lines/changes from non subscription
		// lines/changes
		if (order.getOrderLines() != null) {
			for (OrderLineWS line : order.getOrderLines()) {

				int typeId;
				// if type id is not given in the line then evaluate if from
				// product
				if (line.getTypeId() != null) {
					typeId = line.getTypeId();
				} else {
					ItemBL itemBl = new ItemBL(line.getItemId());
					typeId = itemBl.getEntity().getItemTypes().iterator()
							.next().getOrderLineTypeId();
				}

				if (typeId == Constants.ORDER_LINE_TYPE_SUBSCRIPTION) {
					slines.add(line);

					if (orderChanges != null) {
						OrderChangeWS change = null;
						for (OrderChangeWS c : orderChanges) {
							if (c.getItemId().intValue() == line.getItemId()
									.intValue()) {
								change = c;
							}
						}

						if (change != null) {
							orderChanges.remove(change);
							schanges.add(change);
						}
					}

					if (subscriptionItems.contains(line.getItemId())
							|| orderDas.isSubscribed(order.getUserId(),
									line.getItemId(), order.getActiveSince(),
									order.getActiveUntil())) {

						SessionInternalError sie = new SessionInternalError(
								"Already subscribed, Can not subscribe to a subscription item twice.");
						sie.setErrorMessages(new String[] { "subscription.item.already.subscribed,"
								+ line.getItemId() });
						throw sie;

					} else {
						subscriptionItems.add(line.getItemId());
					}

				} else {
					nslines.add(line);
				}
			}
		}

		// if no subscription lines found
		if (slines.size() < 1) {
			return null;
		}

		// set parent account to allow subaccounts as we are going to create
		// internal subaccounts
		UserWS parentUser = getUserWS(parentAccountId);

		if (!parentUser.getIsParent()) {
			LOG.debug("Allowing user %s to have subaccounts.",
					parentUser.getId());
			parentUser.setIsParent(true);
			updateUser(parentUser);
		}

		int childs = parentUser.getChildIds().length;

		List<Integer> sorders = new ArrayList<Integer>(slines.size());
		Integer userId = order.getUserId();

		for (OrderLineWS sl : slines) {
			childs++;

			// create a sub account
			UserWS child = cloneChildUser(parentUser);
			child.setUserName(parentUser.getUserName() + "-"
					+ String.format("%03d", childs));
			child.setId(createUser(child));

			// create subscription order for subaccount
			order.setUserId(child.getUserId());

			OrderLineWS[] orderLine = new OrderLineWS[1];
			orderLine[0] = sl;
			order.setOrderLines(orderLine);

			OrderChangeWS[] orderChange = new OrderChangeWS[1];
			for (OrderChangeWS change : schanges) {
				if (change.getItemId().intValue() == sl.getItemId()) {
					orderChange[0] = change;
				}
			}

			OrderWS created = doCreateOrder(order, orderChange, true);
			sorders.add(created.getId());

			if (createInvoice) {
				doCreateInvoice(created.getId());
			}
		}

		// set only non subscription lines in original order
		order.setUserId(userId);
		LOG.debug("Non subscription lines are: %s", nslines.size());
		order.setOrderLines(nslines.toArray(new OrderLineWS[nslines.size()]));

		return sorders.toArray(new Integer[sorders.size()]);
	}

	private UserWS cloneChildUser(UserWS user) {
		UserWS clone = new UserWS();

		// clone as less information as required to create a user
		clone.setPassword(Util
				.getSysProp(Constants.SUBSCRIPTION_ACCOUNT_PASSWORD));
		clone.setCurrencyId(user.getCurrencyId());
		clone.setDeleted(user.getDeleted());
		clone.setCreateDatetime(new Date());
		clone.setLanguageId(user.getLanguageId());
		clone.setParentId(user.getId());
		clone.setStatus(user.getStatus());
		clone.setStatusId(user.getStatusId());
		clone.setIsParent(false);
		clone.setUseParentPricing(false);
		clone.setExcludeAgeing(user.getExcludeAgeing());
		clone.setCompanyName(user.getCompanyName());
		clone.setEntityId(user.getEntityId());
		clone.setMainRoleId(Constants.TYPE_CUSTOMER);

		clone.setAccountTypeId(user.getAccountTypeId());
		// clone meta fields
		List<MetaFieldValueWS> metaFields = new ArrayList<MetaFieldValueWS>();
		for (MetaFieldValueWS ws : user.getMetaFields()) {
			metaFields.add(ws.clone());
		}
		clone.setMetaFields(metaFields.toArray(new MetaFieldValueWS[metaFields
				.size()]));

		clone.setTimelineDatesMap(user.getTimelineDatesMap());

		return clone;
	}

	public Integer createOrEditLanguage(LanguageWS languageWS) {
		LanguageDTO languageDTO = DescriptionBL.getLanguageDTO(languageWS);
		languageDTO = new LanguageDAS().save(languageDTO);
		return languageDTO.getId();
	}

	public AssetWS[] findAssetsByProductCode(String productCode) {
		Integer companyId = getCallerCompanyId();
		return new AssetBL().findAssetsByProductCode(productCode, companyId);
	}

	public AssetStatusDTOEx[] findAssetStatuses(String identifier) {
		return new AssetBL().findAssetStatuses(identifier);
	}

	public AssetWS findAssetByProductCodeAndIdentifier(String productCode,
			String identifier) {
		Integer companyId = getCallerCompanyId();
		return new AssetBL().findAssetByProductCodeAndIdentifier(productCode,
				identifier, companyId);
	}

	public AssetWS[] findAssetsByProductCodeAndStatus(String productCode,
			Integer assetStatusId) {
		Integer companyId = getCallerCompanyId();
		return new AssetBL().findAssetsByProductCode(productCode,
				assetStatusId, companyId);
	}

	@Transactional(readOnly = true)
	public JbillingMediationErrorRecord[] getMediationErrorRecordsByMediationProcess(
            UUID mediationProcessId, Integer mediationRecordStatusId) {
        MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
        return mediationService.getMediationErrorRecordsForProcess(mediationProcessId).toArray(new JbillingMediationErrorRecord[0]);
//		IMediationSessionBean mediation = (IMediationSessionBean) Context
//				.getBean(Context.Name.MEDIATION_SESSION);
//		List<MediationErrorRecordWS> records = mediation
//				.getMediationErrorRecordsByMediationProcess(
//						getCallerCompanyId(), mediationProcessId,
//						mediationRecordStatusId, null, null, null, null);
//		if (records == null)
//			return null;
//		return records.toArray(new MediationErrorRecordWS[records.size()]);
	}

    public UserWS copyCompany(String childCompanyTemplateName, Integer entityId, List<String> importEntities, boolean isCompanyChild, boolean copyProducts, boolean copyPlans){
        return new CopyCompanyBL().copyCompany(childCompanyTemplateName, entityId,importEntities, isCompanyChild, copyProducts, copyPlans);
    }

	/**
	 * Retrieves a orderPeriod with its period unit and other details.
	 * 
	 * @param orderPeriodId
	 *            The id of the orderPeriod to be returned
	 */
	@Transactional(readOnly = true)
	public OrderPeriodWS getOrderPeriodWS(Integer orderPeriodId) {
		OrderPeriodDTO orderPeriod = new OrderPeriodDAS().find(orderPeriodId);
		OrderPeriodWS orderPeriodWs = OrderBL.getOrderPeriodWS(orderPeriod);
		return orderPeriodWs;
	}

	public Integer createOrderPeriod(OrderPeriodWS orderPeriod)
			throws SessionInternalError {
		if (orderPeriod.getDescriptions() != null
				&& orderPeriod.getDescriptions().size() > 0) {
			int descriptionLength = orderPeriod.getDescriptions().get(0)
					.getContent().length();
			if (descriptionLength < 1 || descriptionLength > 4000) {
				throw new SessionInternalError(
						"Description should be between 1 and 4000 characters long");
			}
		}

		OrderPeriodDAS periodDas = new OrderPeriodDAS();
		OrderPeriodDTO periodDto = new OrderPeriodDTO();
		periodDto.setCompany(new CompanyDAS().find(getCallerCompanyId()));
		periodDto.setValue(orderPeriod.getValue());
		if (null != orderPeriod.getPeriodUnitId()) {
			periodDto.setUnitId(orderPeriod.getPeriodUnitId().intValue());
		}
		periodDto = periodDas.save(periodDto);

		if (orderPeriod.getDescriptions() != null
				&& orderPeriod.getDescriptions().size() > 0) {
			periodDto.setDescription(((InternationalDescriptionWS) orderPeriod
					.getDescriptions().get(0)).getContent(),
					((InternationalDescriptionWS) orderPeriod.getDescriptions()
							.get(0)).getLanguageId());
		}
		LOG.debug("Converted to DTO: %s", periodDto);
		periodDas.flush();
		periodDas.clear();
		return periodDto.getId();
	}

	/**
	 * Validate if pro-rate box is checked and order period and billing cycle
	 * period should be the same. If not same show validation message.
	 */
	private void validateProrating(OrderWS order) throws SessionInternalError {
		if (order == null) {
			throw new SessionInternalError("Null parameter");
		}

		OrderDTO orderDto = new OrderBL().getDTO(order);

		MainSubscriptionDTO mainSubscription = orderDto.getUser().getCustomer()
				.getMainSubscription();
		Integer billingCycleUnit = mainSubscription.getSubscriptionPeriod()
				.getPeriodUnit().getId();
		Integer billingCycleValue = mainSubscription.getSubscriptionPeriod()
				.getValue();

		BillingProcessConfigurationDTO billingConfiguration = new ConfigurationBL(
				orderDto.getUser().getEntity().getId()).getDTO();
		ProratingType companyLevelProratingType = billingConfiguration
				.getProratingType();

		boolean isPlanProrateValid = true;

		// validation for plan throught API.
		if (orderDto.getProrateFlagValue()) {

			for (OrderLineDTO lineDto : orderDto.getLines()) {
				if (lineDto.getItem().getPlans().size() > 0
						&& lineDto.getDeleted() == 0) {
					PlanDTO planDto = lineDto.getItem().getPlans().iterator()
							.next();
					Integer planPeriodUnit = planDto.getPeriod()
							.getPeriodUnit().getId();
					Integer planPeriodValue = planDto.getPeriod().getValue();
					if (companyLevelProratingType.isProratingAutoOn()) {
						if (!planPeriodUnit.equals(billingCycleUnit)
								&& planPeriodValue.equals(billingCycleValue)) {
							isPlanProrateValid = false;
						}
					}
				}
			}
		}

		if (orderDto.getProrateFlagValue() || !isPlanProrateValid) {
            if (null != orderDto
                    && null != orderDto.getOrderPeriod().getUnitId()) {
                if (!(orderDto.getOrderPeriod().getUnitId()
                        .equals(billingCycleUnit) && orderDto.getOrderPeriod()
                        .getValue().equals(billingCycleValue))) {
                    throw new SessionInternalError(
                            "Order Period unit should equal to Customer billing period unit",
                            new String[] { "OrderWS,billingCycleUnit,order.period.unit.should.equal" });
                }
            }
        }
    }

    /**
     * Validate order period description should not be duplicate.
     *
     * @param orderPeriod
     * @param orderPeriodDto
     */
    private void validateOrderPeriod(OrderPeriodWS orderPeriod,
                                     OrderPeriodDTO orderPeriodDto) {

        Integer entityId = getCallerCompanyId();
        Integer languageId = getCallerLanguageId();

        OrderPeriodDAS periodDas = new OrderPeriodDAS();
        List<OrderPeriodDTO> orderPeriods = periodDas.getOrderPeriods(entityId);

        for (OrderPeriodDTO orderPeriodObj : orderPeriods) {
            if (null == orderPeriodDto
                    && (orderPeriod.getDescription(languageId).getContent()
                    .trim().equals(orderPeriodObj.getDescription()))) {
                throw new SessionInternalError(
                        "Duplicate Description ",
                        new String[] { "OrderPeriodWS,content,order.period.description.already.exists" });
            } else if (null != orderPeriodDto
                    && !orderPeriod.getDescription(languageId).getContent()
                    .trim().equals(orderPeriodDto.getDescription())) {
                if (orderPeriod.getDescription(languageId).getContent()
                        .equals(orderPeriodObj.getDescription())) {
                    throw new SessionInternalError(
                            "Duplicate Description ",
                            new String[] { "OrderPeriodWS,content,order.period.description.already.exists" });
                }
            }
        }
    }

    /**
     * Validate Order Active Since Date if An invoice is already generated for
     * given order, then not allowed to change the Active since date.
     *
     * @param order
     * @throws SessionInternalError
     */
    private void validateActiveSinceDate(OrderWS order)
            throws SessionInternalError {
        if (order == null) {
            throw new SessionInternalError("Null parameter");
        }

        OrderDTO orderDto = new OrderDAS().find(order.getId());

        // Get Minimum Period start date of order for non-review records.
        Date firstInvoicePeriodStartDate = new OrderProcessDAS()
                .getFirstInvoicePeriodStartDateByOrderId(order.getId());
        if (null != firstInvoicePeriodStartDate
                && !firstInvoicePeriodStartDate.equals(order.getActiveSince())) {
            if (order.getActiveSince().compareTo(orderDto.getActiveSince()) != 0) {
                throw new SessionInternalError(
                        "Not allowed to changes Active since date",
                        new String[]{"OrderWS,activeSince,order.acitve.since.date.not.allowed.to.changes"});
            }
        }
        OrderChangeWS[] oldChanges = getOrderChanges(order.getId());
        for (OrderChangeWS oldChange : oldChanges) {
            if (com.sapienter.jbilling.common.Util.truncateDate(
                    oldChange.getStartDate()).before(
                    com.sapienter.jbilling.common.Util.truncateDate(order
                            .getActiveSince()))) {
                throw new SessionInternalError(
                        "Not allowed to changes Active since date",
                        new String[]{"OrderWS,activeSince,validation.error.incorrect.start.date"});
            }
        }
    }

    @Transactional(readOnly = true)
	@Override
	public Long getMediationErrorRecordsCount(Integer mediationConfigurationId) {
        MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);
        return Long.valueOf(mediationService.getMediationErrorRecordsForMediationConfigId(mediationConfigurationId).size());
	}

	/**
	 * Queries the data source for a Enumeration entity filtered by
	 * <code>enumerationId</code>.
	 *
	 * @param enumerationId
	 *            representing the desired enumeration entity.
	 * @return {@link com.sapienter.jbilling.server.util.EnumerationWS} object
	 *         representing the result set or null if record does not exist.
	 * @throws com.sapienter.jbilling.common.SessionInternalError
	 *             if invalid input parameters!
	 */
	@Transactional(readOnly = true)
	public EnumerationWS getEnumeration(Integer enumerationId)
			throws SessionInternalError {
		if (null == enumerationId || Integer.valueOf(0) >= enumerationId) {
			String[] errors = new String[] { "EnumerationWS,id,enumeration.id.null.or.negative" };
			throw new SessionInternalError("enumeration.Id.null.or.negative",
					errors);
		}
		EnumerationBL enumerationBL = new EnumerationBL();
		EnumerationDTO enumerationDTO = enumerationBL.getEnumeration(
				enumerationId, getCallerCompanyId());
		if (null == enumerationDTO) {
			return null;
		}
		return EnumerationBL.convertToWS(enumerationDTO);
	}

	/**
	 * Queries the data source for a Enumeration entity with exact name match.
	 *
	 * @param name
	 *            of the entity.
	 * @return {@link com.sapienter.jbilling.server.util.EnumerationWS} object
	 *         representing the result set or null if record does not exist.
	 * @throws com.sapienter.jbilling.common.SessionInternalError
	 *             if invalid input parameters!
	 */
	@Transactional(readOnly = true)
	public EnumerationWS getEnumerationByName(String name)
			throws SessionInternalError {
		if (null == name || Integer.valueOf(0).equals(name.length())) {
			String[] errors = new String[] { "EnumerationWS,name,enumeration.name.empty" };
			throw new SessionInternalError("enumeration.name.empty", errors);
		}
		EnumerationBL enumerationBL = new EnumerationBL();
		EnumerationDTO enumerationDTO = enumerationBL.getEnumerationByName(
				name, getCallerCompanyId());
		if (null == enumerationDTO) {
			return null;
		}
		return EnumerationBL.convertToWS(enumerationDTO);
	}

	/**
	 *
	 * Queries the data source for all
	 * {@link com.sapienter.jbilling.server.util.EnumerationWS} entities
	 * filtered by <code>entityId</code>. Optionally the result set can be
	 * constrained with a <code>max</code> number of entities or all will be
	 * fetched. Also starting from <code>offset</code> position is optional.
	 *
	 * @param max
	 *            representing maximum number of rows (optional).
	 * @param offset
	 *            representing the offset (optional).
	 *
	 * @return list of {@link com.sapienter.jbilling.server.util.EnumerationWS}
	 *         entities, representing the result set.
	 */
	@Transactional(readOnly = true)
	public List<EnumerationWS> getAllEnumerations(Integer max, Integer offset) {
		EnumerationBL enumerationBL = new EnumerationBL();
		List<EnumerationDTO> allDTOs = enumerationBL.getAllEnumerations(
				getCallerCompanyId(), max, offset);
		if (null == allDTOs) {
			return null;
		}
		List<EnumerationWS> enumerationsList = new ArrayList<EnumerationWS>();
		for (EnumerationDTO enumerationDTO : allDTOs) {
			if (null != enumerationDTO) {
				enumerationsList.add(EnumerationBL.convertToWS(enumerationDTO));
			}
		}
		return enumerationsList;
	}

	/**
	 * Queries the data source for a number representing the count of all
	 * persisted {@link com.sapienter.jbilling.server.util.EnumerationWS}
	 * entities.
	 *
	 * @return number of persisted entities.
	 */
	@Transactional(readOnly = true)
	public Long getAllEnumerationsCount() {
		return new EnumerationBL()
				.getAllEnumerationsCount(getCallerCompanyId());
	}

	/**
	 * New Enumeration entity is created or persisted to the data source. Also
	 * used for updating an existing <code>Enumeration</code> entity. However if
	 * we try to persist entity with the name already persisted, exception is
	 * thrown. Also validation is involved.
	 *
	 * @param enumeration
	 *            {@link com.sapienter.jbilling.server.util.EnumerationWS}
	 *            entity that is going to be saved.
	 * @return Id of the created or updated Enumeration entity.
	 * @throws SessionInternalError
	 *             if entity with the same name is already persisted or invalid
	 *             input parameters.
	 */
	public Integer createUpdateEnumeration(EnumerationWS enumeration)
			throws SessionInternalError {
		validateEnumeration(enumeration);
		EnumerationBL enumerationBL = new EnumerationBL();
		if (null == enumeration.getEntityId()
				|| Integer.valueOf(0).equals(enumeration.getEntityId())) {
			enumeration.setEntityId(getCallerCompanyId());
		}
		EnumerationDTO dto = EnumerationBL.convertToDTO(enumeration);
		// Take care of enumerations with duplicate names
		if (enumerationBL.exists(dto.getId(), dto.getName(), dto.getEntityId())) {
			String[] errors = new String[] { "EnumerationWS,name,enumeration.name.exists,"
					+ enumeration.getName() };
			throw new SessionInternalError("enumeration.name.exists", errors);
		}

		// Save or update
		if (dto.getId() > 0) {
			enumerationBL.set(dto.getId());
			return enumerationBL.update(dto);
		} else {
			return enumerationBL.create(dto);
		}
	}

	/**
	 * Deletes the {@link com.sapienter.jbilling.server.util.db.EnumerationDTO}
	 * entity from the data source, identified by <code>enumerationId</code>.
	 *
	 * @param enumerationId
	 *            representing the enumeration entity that is going to be
	 *            deleted.
	 * @return true or false depending if the deletion was successful or not.
	 * @throws SessionInternalError
	 *             if enumeration exists in some meta field or invalid input
	 *             parameter.
	 */

    public boolean deleteEnumeration(Integer enumerationId) throws SessionInternalError {
        if (null == enumerationId || Integer.valueOf(0) >= enumerationId) {
            String[] errors = new String[]{"EnumerationWS,id,enumeration.id.null.or.negative"};
            throw new SessionInternalError("enumeration.id.null.or.negative", errors);
        }
        EnumerationBL enumerationBL = new EnumerationBL(enumerationId);
        if (new MetaFieldDAS().getFieldCountByDataTypeAndName(DataType.ENUMERATION, enumerationBL.getEntity().getName(),getCallerCompanyId()) > 0 ||
                new MetaFieldDAS().getFieldCountByDataTypeAndName(DataType.LIST, enumerationBL.getEntity().getName(),getCallerCompanyId()) > 0) {
            String[] errors = new String[]{"EnumerationWS,id,enumeration.delete.failed," + enumerationId};
            throw new SessionInternalError("enumeration.delete.failed", errors);
        }
        enumerationBL.delete();
        return true;
    }

	/**
	 * Validates the {@link com.sapienter.jbilling.server.util.EnumerationWS}
	 * object and its values.
	 *
	 * @param enumeration
	 *            validated object.
	 * @throws com.sapienter.jbilling.common.SessionInternalError
	 *             if some validation fails.
	 */
	private void validateEnumeration(EnumerationWS enumeration)
			throws SessionInternalError {
		if (null == enumeration) {
			String[] errors = new String[] { "EnumerationWS,EnumerationWS,enumeration.null" };
			throw new SessionInternalError("enumeration.null", errors);
		}

		// validate name
		String name = enumeration.getName();
		if (null == name || Integer.valueOf(0).equals(name.length())) {
			String[] errors = new String[] { "EnumerationWS,name,enumeration.name.empty" };
			throw new SessionInternalError("enumeration.name.empty", errors);
		}

		// validate at least one enum-value
		List<EnumerationValueWS> values = enumeration.getValues();
		if (null == values || Integer.valueOf(0).equals(values.size())) {
			String[] errors = new String[] { "EnumerationWS,values,enumeration.values.missing" };
			throw new SessionInternalError("enumeration.value.missing", errors);
		}

		// validate enumeration values
		Set<String> valuesSet = new HashSet<String>();
		for (EnumerationValueWS value : enumeration.getValues()) {
			LOG.debug("value = " + value);

			// empty value
			String val = value.getValue();
			if (null == val || Integer.valueOf(0).equals(val.length())) {
				String[] errors = new String[] { "EnumerationWS,values.value,enumeration.value.missing" };
				throw new SessionInternalError("enumeration.value.missing",
						errors);
			}

			// max length
			if (val.length() > Constants.ENUMERATION_VALUE_MAX_LENGTH) {
				String[] errors = new String[] { "EnumerationWS,values.value,enumeration.value.max.length" };
				throw new SessionInternalError("enumeration.value.max.length",
						errors);
			}

			// duplicate
			if (valuesSet.contains(val)) {
				String[] errors = new String[] { "EnumerationWS,values.value,enumeration.value.duplicated" };
				throw new SessionInternalError("enumeration.value.duplicated",
						errors);
			}

			valuesSet.add(value.getValue());
		}

	}

	@Transactional(readOnly = true)
	@Override
	public List<AssetWS> getAssetsByUserId(Integer userId) {
		AssetDAS assetDAS = new AssetDAS();

		// OrderDAS orderDas = new OrderDAS();
		// List<OrderDTO> orderDtos = orderDas.findOrdersByUser(userId);
		List<AssetWS> assets = new ArrayList<AssetWS>();
		// List<OrderLineDTO> lines = null;
		List<AssetDTO> assetDtos = assetDAS.findAssetsByUser(userId);
		// for(OrderDTO dto: orderDtos){
		// lines = dto.getLines();
		// for(OrderLineDTO olDto: lines){
		// assetDtos = olDto.getAssets();
		for (AssetDTO assetDto : assetDtos) {
			assets.add(AssetBL.getWS(assetDto));
		}
		// }
		// }
		LOG.debug("Assets :" + assets.size());
		return assets;
	}

    @Transactional(readOnly = true)
	@Override
	public void resetPassword(int userId) {
		UserDAS userDAS = new UserDAS();
		UserDTO user = userDAS.find(userId);
		EmailResetPasswordService emailResetPasswordService = Context
				.getBean(Context.Name.PASSWORD_SERVICE);

		emailResetPasswordService.resetPassword(user);

	}


	public CustomerEnrollmentWS getCustomerEnrollment(Integer customerEnrollmentId) throws SessionInternalError{
		return new CustomerEnrollmentBL().getCustomerEnrollmentWS(customerEnrollmentId);
	}

	public EDITypeWS getEDIType(Integer ediTypeId) throws SessionInternalError{

		EDITypeDTO ediTypeDTO = new EDITypeDAS().find(ediTypeId);
		EDITypeWS ediTypeWS = new EDITypeBL().getWS(ediTypeDTO);
		return ediTypeWS;
	}

	public Integer createUpdateEnrollment(CustomerEnrollmentWS customerEnrollmentWS)  throws SessionInternalError{
		CustomerEnrollmentBL customerEnrollmentBL=new CustomerEnrollmentBL();
		CustomerEnrollmentDTO customerEnrollmentDTO=customerEnrollmentBL.getDTO(customerEnrollmentWS);
		return new CustomerEnrollmentBL().save(customerEnrollmentDTO);
	}


	public CustomerEnrollmentWS validateCustomerEnrollment(CustomerEnrollmentWS customerEnrollmentWS)  throws SessionInternalError{
		CustomerEnrollmentBL customerEnrollmentBL=new CustomerEnrollmentBL();
		LOG.debug("customerEnrollmentWS is:  " + customerEnrollmentWS);
		CustomerEnrollmentDTO customerEnrollmentDTO=customerEnrollmentBL.getDTO(customerEnrollmentWS);
		new CustomerEnrollmentBL().validateEnrollment(customerEnrollmentDTO);
		return customerEnrollmentBL.getWS(customerEnrollmentDTO);

	}

	public void deleteEnrollment(Integer customerEnrollmentId) throws SessionInternalError{
		new CustomerEnrollmentBL().delete(customerEnrollmentId);
	}


	public Integer createEDIType(EDITypeWS ediTypeWS, File ediFormatFile){
		return new EDITypeBL().createEDIType(ediTypeWS, ediFormatFile);
	}

	public void deleteEDIType(Integer ediTypeId) {
		new EDITypeBL().deleteEDIType(ediTypeId);
	}

	public  int generateEDIFile(Integer ediTypeId, Integer entityId, String fileName, Collection input) throws SessionInternalError {
		FileFormat fileFormat = FileFormat.getFileFormat(ediTypeId);
        IFileGenerator generator = new FlatFileGenerator(fileFormat, entityId, fileName, input);
		return generator.validateAndSaveInput().getId();
	}

	public int parseEDIFile(Integer ediTypeId, Integer entityId, File parserFile) throws SessionInternalError {
		FileFormat fileFormat = FileFormat.getFileFormat(ediTypeId);
		FlatFileParser fileParser = new FlatFileParser(fileFormat, parserFile, entityId);
		return fileParser.parseAndSaveFile().getId();
	}

	public int saveEDIFileRecord(EDIFileWS ediFileWS) throws SessionInternalError {
		return new EDIFileBL().saveEDIFile(ediFileWS);
	}

	public void updateEDIFileStatus(Integer fileId, String statusName, String comment) throws SessionInternalError{
		new EDIFileBL().updateEDIFileStatus(fileId, statusName, comment);
	}

	public List<CompanyWS> getAllChildEntities(Integer parentId) throws SessionInternalError {
		return new EntityBL().getChildEntities(parentId);
	}

    /*
     * Payment Transfer API
     */
    public void transferPayment(PaymentTransferWS paymentTransfer) {
        //Validate transfer payment API parameters.
        validateTrasferPayment(paymentTransfer);

        PaymentBL paymentBL = new PaymentBL(paymentTransfer.getPaymentId());
        PaymentTransferBL paymentTransferBL = new PaymentTransferBL();
        PaymentDTOEx paymentDTOEx = paymentBL.getDTOEx(getCallerLanguageId());
        paymentDTOEx.setBaseUser(paymentBL.getDTO().getBaseUser());
        Integer paymentTransferId = paymentTransferBL.createPaymentTransfer(paymentDTOEx, paymentTransfer);

        if (null != paymentTransferId) {
            PaymentDTO payment = paymentBL.transferPaymentToUser(paymentTransfer);
            // let know about this payment with an event
            paymentBL = new PaymentBL(payment.getId());
            if (paymentBL.getEntity() == null) {
                return;
            }

            paymentDTOEx = paymentBL.getDTOEx(getCallerLanguageId());
            PaymentSuccessfulEvent event = new PaymentSuccessfulEvent(
                    getCallerCompanyId(),paymentDTOEx);
            EventManager.process(event);
        }
    }

    @Transactional(readOnly=true)
    public PaymentTransferWS getLatestPaymentTransfer(Integer userId) {
        try {
            if (userId == null) {
                return null;
            }
            PaymentTransferWS paymentTransfer = null;
            PaymentTransferBL paymentTransferBL = new PaymentTransferBL();
            Integer latestPaymentTransferId = paymentTransferBL.getLatestPaymentTransfer(userId);

            if (latestPaymentTransferId != null) {
                paymentTransferBL.setPaymentTransferDTO(latestPaymentTransferId);
                paymentTransfer = paymentTransferBL.getWS(paymentTransferBL.getPaymentTransferDTO());
            }

            return paymentTransfer;

        } catch(Exception e) {
            LOG.error("Exception in web service: getting latest payment transfer" + " for user " + userId, e);
            throw new SessionInternalError("Error getting latest payment transfer");
        }
    }

    /**
     * Validate payment transfer methods parameters
     * @param paymentTransfer
     */
    private void validateTrasferPayment(PaymentTransferWS paymentTransfer) {

        if (paymentTransfer.getPaymentId() == null) {
            throw new SessionInternalError("Payment Id is required", new String[] {"PaymentTransferDTO,paymentId,validation.error.is.required"});
        } else if (null == paymentTransfer.getFromUserId()) {
            throw new SessionInternalError("From UserId is required", new String[] {"PaymentTransferDTO,fromUserId,validation.error.is.required"});
        } else if (null == paymentTransfer.getToUserId()) {
            throw new SessionInternalError("To UserId is required", new String[] {"PaymentTransferDTO,toUserId,validation.error.is.required"});
        }

        // validate if payment linked to invoices
        PaymentBL bl = new PaymentBL(paymentTransfer.getPaymentId());

        if (bl.getEntity() == null) {
            return;
        }

        if (bl.getEntity().getDeleted() == 1) {
            throw new SessionInternalError("Payment is not found.", new String[] {"PaymentTransferDTO,paymentId,payment.is.not.found"});
        }
        // apply validations for refund payments
        if(bl.getEntity().getIsRefund() == 1) {
            LOG.debug("This payment %s is a refund so we cannot transfer it.", paymentTransfer.getPaymentId());
            throw new SessionInternalError("A Refund cannot be transfer",
                    new String[] {"validation.error.transfer.refund.payment"});
        }

        // Validate payment method should be Bank
        if (bl.getEntity().getPaymentMethod().getId() != Constants.PAYMENT_METHOD_BANK_WIRE) {
            throw new SessionInternalError("Bank payment method applicable only for payment transfer.", new String[] {"PaymentTransferDTO,toUserId,validation.bank.payment.method.applicable.only"});
        }

        // Validate payment not linked to any invoices.
        if (null != bl.getEntity().getInvoicesMap() && bl.getEntity().getInvoicesMap().size() > 0) {
            throw new SessionInternalError("Please remove linked invoices to transfer this payment", new String[] {"PaymentTransferDTO,paymentId,payment.cant.transfer.linked"});
        }
    }

    /**
     * get Last Payment Transfer By Payment Id
     */
    public PaymentTransferWS getLastPaymentTransferByPaymentId(Integer paymentId) {
        PaymentTransferBL paymentTransferBL = new PaymentTransferBL();
        PaymentTransferWS ws = paymentTransferBL.getWS(paymentTransferBL.getLastPaymentTransferByPaymentId(paymentId));
        return ws;
    }

    /*
     * get all payment transfers in between dates
     */
    public List<Integer> getAllPaymentTransfersByDateRange(Integer entityId, Date fromDate, Date toDate) {
        return new PaymentTransferBL().getAllPaymentTransfersByDateRange(entityId, fromDate, toDate);
    }

    /*
     * get all payment transfers by user id
     */
    public List<PaymentTransferWS> getAllPaymentTransfersByUserId(Integer userId) {
        return new PaymentTransferBL().getAllPaymentTransfersByUserId(userId);
    }

	/*
	*  This method update the EDI File status and also trigger an Event called UpdateEDIStatus
	*  Whenever UpdateEDIStatus Event trigger, It process EDI file and escape all the Business condition
	*
	*  */
	public void updateEDIStatus(EDIFileWS ediFileWS, EDIFileStatusWS statusWS) throws SessionInternalError{
		new EDIFileBL().updateStatus(ediFileWS, statusWS);
	}

	public UserWS getUserByAccountNumber(String customerAccountNumber) {
		UserDTO user = new UserDAS().findByMetaFieldNameAndValue(getCallerCompanyId(), FileConstants.UTILITY_CUST_ACCT_NR, customerAccountNumber);

		return UserBL.getWS(DTOFactory.getUserDTOEx(user));
	}

    public UserWS getUserBySupplierID(String supplierId) {
        UserDTO user = new UserDAS().findSingleByMetaFieldNameAndValue(FileConstants.CUSTOMER_SUPPLIER_ID_META_FIELD_NAME, supplierId);

        return UserBL.getWS(DTOFactory.getUserDTOEx(user));
    }

	public void processMigrationPayment(PaymentWS paymentWS) throws SessionInternalError{
		IPaymentSessionBean session = (IPaymentSessionBean) Context.getBean(Context.Name.PAYMENT_SESSION);
		Integer[] invoiceIds = getUnpaidInvoicesOldestFirst(paymentWS.getUserId());
		PaymentBL paymentBL = new PaymentBL();
		InvoiceBL invoiceBL = new InvoiceBL();
		PaymentDTOEx payment = new PaymentDTOEx(paymentWS);
		payment.setPaymentResult(new PaymentResultDAS().find(CommonConstants.RESULT_ENTERED));
		payment.setBalance(payment.getAmount());
		paymentBL.create(payment, paymentWS.getUserId());
		PaymentDTO paymentDTO = paymentBL.getDTO();
		for(Integer invoiceId: invoiceIds){
			invoiceBL.set(invoiceId);
			session.applyPayment(paymentDTO.getId(), invoiceBL.getDTO().getId());
			paymentBL.set(paymentDTO.getId());
			paymentDTO = paymentBL.getDTO();
			if(paymentDTO.getBalance().compareTo(BigDecimal.ZERO) <= 0){
				break;
			}

		}
	}

	/**
	 * Returns an array of IDs for all unpaid invoices under the given user ID.
	 *
	 * @param userId user IDs
	 * @return array of un-paid invoice IDs with oldest first
	 */
	@Transactional(readOnly=true)
	public Integer[] getUnpaidInvoicesOldestFirst(Integer userId) {
		try {
			CachedRowSet rs = new InvoiceBL().getPayableInvoicesByUserOldestFirst(userId);
			Integer[] invoiceIds = new Integer[rs.size()];
			int i = 0;
			while (rs.next())
				invoiceIds[i++] = rs.getInt(1);

			rs.close();
			return invoiceIds;

		} catch (SQLException e) {
			throw new SessionInternalError("Exception occurred querying payable invoices.");
		} catch (Exception e) {
			throw new SessionInternalError("An un-handled exception occurred querying payable invoices.");
		}
	}

	public Integer createAdjustmentOrderAndInvoice(String customerPrimaryAccount, OrderWS order, OrderChangeWS[] orderChanges){
		UserWS user = getUserBySupplierID(customerPrimaryAccount);
		if(user == null){
			return null;
		}

		order.setUserId(user.getId());
        order.setOrderStatusWS(findOrderStatusById(getDefaultOrderStatusId(OrderStatusFlag.INVOICE, user.getEntityId())));

        Integer orderChangeStatus = getOrderChangeApplyStatus(user.getEntityId());
        for(OrderChangeWS orderChange : orderChanges) {
            orderChange.setStatusId(orderChangeStatus);
        }

        OrderBL orderBL = new OrderBL();
        Integer orderId = orderBL.create(user.getEntityId(), null,
                orderBL.getDTO(order));
//		Integer orderId = createUpdateOrder(order, orderChanges);
		Integer invoiceId = createInvoiceFromOrder(orderId, null);
        InvoiceBL invoiceBL = new InvoiceBL(invoiceId);
        invoiceBL.getEntity().setCreateDatetime(order.getActiveSince());
        invoiceBL.getEntity().setDueDate(order.getActiveSince());
        return invoiceId;
    }

    private OrderWS getOrderWSForHistoricalDataMigration(Integer userId, Integer itemId, OrderStatusWS orderStatusWS, Integer orderPeriod, String date,
    		String amount, String adjustmentType, String referenceNumber){
		OrderWS newOrder = new OrderWS();
		newOrder.setUserId(userId);
		newOrder.setBillingTypeId(2);
		newOrder.setCurrencyId(1);
		newOrder.setNotes("Reference Number:" + referenceNumber + ", Adj Type:" + adjustmentType);

		Date convertedDate = convertDateForMigration(date);
		newOrder.setActiveSince(convertedDate);
		newOrder.setNextBillableDay(convertedDate);
		newOrder.setOrderStatusWS(orderStatusWS);
		newOrder.setPeriod(orderPeriod);
		OrderLineWS line  = new OrderLineWS();
		line.setPrice(amount);
		line.setTypeId(1);
		line.setQuantity("1");
		line.setAmount(amount);
		line.setDescription("Adjustment Order line");
		line.setItemId(itemId);
		line.setUseItem(false);
		OrderLineWS[] lines = new OrderLineWS[1];
		lines[0] = line;
 		newOrder.setOrderLines(lines);
		return newOrder;
	}

    private Date convertDateForMigration(String date){
        if(date == null || date.isEmpty()) {
            return null;
        }
		SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yyyy");
		try {
			return (Date) sf.parseObject(date);
		} catch (ParseException e) {
			LOG.warn("Unparsable date "+date);
			return null;
		}
	}

    private OrderChangeWS createOrderChangeForAdjustmentOrder(Integer itemId, String description, OrderWS orderSaved, String quantity, Integer entityId, String amount) {
		OrderChangeWS orderChange = new OrderChangeWS();
		orderChange.setItemId(itemId);
		orderChange.setStartDate(orderSaved.getActiveSince());
		orderChange.setNextBillableDate(orderSaved.getNextBillableDay());
		orderChange.setApplicationDate(orderSaved.getActiveSince());
		orderChange.setQuantity(quantity);
		orderChange.setPrice(amount);
		orderChange.setUserAssignedStatusId(getOrderChangeApplyStatus(entityId));
		orderChange.setDescription(description);
		orderChange.setOrderChangeTypeId(1);
		if(orderSaved.getId()!=null)
		orderChange.setOrderId(orderSaved.getId());
		orderChange.setUseItem(0);
		return orderChange;
    }

    private Integer getOrderChangeApplyStatus(Integer entityId){
    	OrderChangeStatusWS[] list = getOrderChangeStatusesForCompany();
    	Integer statusId = null;
    	for(OrderChangeStatusWS orderChangeStatus : list){
    		if(orderChangeStatus.getApplyToOrder().equals(ApplyToOrder.YES)){
    			statusId = orderChangeStatus.getId();
    			break;
    		}
    	}
    	if(statusId != null){
    		return statusId;
    	}else{
    		return null;
    	}
    }

    public String createPaymentForHistoricalDateMigration(String customerPrimaryAccount, Integer chequePmId, String amount, String date) {
    	UserWS user = getUserBySupplierID(customerPrimaryAccount);
    	if(user == null){
    		return null;
    	}
    	PaymentWS payment = createPaymentForMigration(user.getUserId(), chequePmId, amount, date);
    	processMigrationPayment(payment);
    	return "created";
    }

    public String adjustUserBalance(String customerPrimaryAccount, String amount, Integer chequePmId, String date) {
        UserWS user = getUserBySupplierID(customerPrimaryAccount);
        if(user == null){
            return "failure";
        }
        BigDecimal balance = user.getOwingBalanceAsDecimal();
        BigDecimal requiredBalance = new BigDecimal(amount);
        BigDecimal adjustment = requiredBalance.subtract(balance);
        Date adjustmentDate = convertDateForMigration(date);
        if(adjustmentDate == null) adjustmentDate = new Date();

        LOG.debug("Balance [%s], Required Balance [%s], Adjustment[%s]", balance, requiredBalance, adjustment);

        if(adjustment.compareTo(BigDecimal.ZERO) > 0) {
            IInvoiceSessionBean session = Context
                    .getBean(Context.Name.INVOICE_SESSION);

            NewInvoiceContext newInvoiceDTO = new NewInvoiceContext();

            UserDTO userDTO = new UserDAS().find(user.getId());
            newInvoiceDTO.setBaseUser(userDTO);
            CurrencyDTO currency = new CurrencyDAS()
                    .find(1);
            newInvoiceDTO.setCurrency(currency);
            newInvoiceDTO.setCreateDatetime(adjustmentDate);
            newInvoiceDTO.setDueDate(adjustmentDate);
            newInvoiceDTO.setTotal(adjustment);
            newInvoiceDTO.setInvoiceStatus(new InvoiceStatusDAS().find(CommonConstants.INVOICE_STATUS_UNPAID));
//            newInvoiceDTO.setToProcess(invoiceWS.getToProcess());
            newInvoiceDTO.setBalance(adjustment);
            newInvoiceDTO.setCarriedBalance(BigDecimal.ZERO);
            newInvoiceDTO.setIsReview(0); // set fake value if null
            newInvoiceDTO.setDeleted(0);
            newInvoiceDTO.setCustomerNotes("Adjustment for user balance.");
//            newInvoiceDTO.setPublicNumber();
            newInvoiceDTO.setLastReminder(adjustmentDate);
//            newInvoiceDTO.setOverdueStep(invoiceWS.getOverdueStep());
            newInvoiceDTO.setCreateTimestamp(new Date());

            // if create date time is given then we can assume that that
            // is the billing date, otherwise we will fake the billing date
            newInvoiceDTO.setBillingDate(adjustmentDate);

            com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO dbInvoiceLineDTO = new com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO();

//            dbInvoiceLineDTO.setItem(itemDTO);
            dbInvoiceLineDTO.setAmount(adjustment);
            dbInvoiceLineDTO.setQuantity(BigDecimal.ONE);
            dbInvoiceLineDTO.setPrice(adjustment);
            dbInvoiceLineDTO.setDeleted(0);
            dbInvoiceLineDTO.setDescription("Adjustment for user balance");
//            dbInvoiceLineDTO.setSourceUserId(invoiceLineDTO.getSourceUserId());
            dbInvoiceLineDTO.setIsPercentage(0);
            dbInvoiceLineDTO.setInvoiceLineType(new InvoiceLineTypeDTO(3)); // Due
            // invoice
            // line
            // type

            newInvoiceDTO.getResultLines().add(dbInvoiceLineDTO);

            LOG.debug("Invoice: %s", newInvoiceDTO);
            InvoiceDTO newInvoice = session.create(user.getEntityId(),
                    user.getUserId(), newInvoiceDTO);
        } else if(adjustment.compareTo(BigDecimal.ZERO) < 0) {
            adjustment = adjustment.abs();
            PaymentWS payment = new PaymentWS();
            payment.setAmount(adjustment);
            payment.setIsRefund(0);
            payment.setMethodId(chequePmId);
            payment.setPaymentDate(adjustmentDate);
            payment.setCreateDatetime(adjustmentDate);
            payment.setResultId(4);
            payment.setCurrencyId(1);
            payment.setUserId(user.getId());
            payment.setPaymentNotes("Payment during migration");
            payment.setPaymentPeriod(1);

            LOG.debug("Payment: %s", payment);
            processMigrationPayment(payment);
        } else {
            LOG.debug("No adjustment required");
        }

        return "success";
    }

    private PaymentWS createPaymentForMigration(Integer userId, Integer chequePmId, String amount, String pmtDate){
        Date date = convertDateForMigration(pmtDate);
		PaymentWS payment = new PaymentWS();
		payment.setAmount(amount.replace("-", ""));
		payment.setIsRefund(0);
		payment.setMethodId(chequePmId);
		payment.setPaymentDate(date);
		payment.setCreateDatetime(date);
		payment.setResultId(4);
		payment.setCurrencyId(1);
		payment.setUserId(userId);
		payment.setPaymentNotes("Payment during migration");
		payment.setPaymentPeriod(1);

//		PaymentInformationWS ach = createACHForMigrationPayment("Frodo Baggins", "Shire Financial Bank", "123456789", "123456789", chequePmId);
//		payment.getPaymentInstruments().add(ach);
		return payment;
	}

//    private PaymentInformationWS createACHForMigrationPayment(String customerName,
//			String bankName, String routingNumber, String accountNumber,Integer chequePmId) {
//    	String ACH_MF_ROUTING_NUMBER = "Bank Routing Number";
//    	String ACH_MF_BANK_NAME = "Bank Name";
//    	String ACH_MF_CUSTOMER_NAME = "Customer Name";
//    	String ACH_MF_ACCOUNT_NUMBER = "Bank Account Number";
//    	String ACH_MF_ACCOUNT_TYPE = "Bank Account Type";
//		PaymentInformationWS cc = new PaymentInformationWS();
//		cc.setPaymentMethodTypeId(chequePmId);
//		cc.setProcessingOrder(new Integer(1));
//		cc.setPaymentMethodId(5);
//
//		List<MetaFieldValueWS> metaFields = new ArrayList<MetaFieldValueWS>(5);
//
//		MetaFieldValueWS routing = createMetaField(ACH_MF_ROUTING_NUMBER, false, true, DataType.STRING, 1, routingNumber);
//		metaFields.add(routing);
//
//		MetaFieldValueWS customer = createMetaField(ACH_MF_CUSTOMER_NAME, false, true, DataType.STRING, 2, customerName);
//		metaFields.add(customer);
//
//		MetaFieldValueWS account = createMetaField(ACH_MF_ACCOUNT_NUMBER, false, true, DataType.STRING, 3, accountNumber);
//		metaFields.add(account);
//
//		MetaFieldValueWS bank = createMetaField(ACH_MF_BANK_NAME, false, true, DataType.STRING, 4, bankName);
//		metaFields.add(bank);
//
//		MetaFieldValueWS type = createMetaField(ACH_MF_ACCOUNT_TYPE, false, true, DataType.ENUMERATION, 5, "CHECKING");
//		metaFields.add(type);
//
//		MetaFieldValueWS[] metaFieldsArray = new MetaFieldValueWS[metaFields.size()];
//		cc.setMetaFields(metaFields.toArray(metaFieldsArray));
//
//		return cc;
//	}

	private MetaFieldValueWS createMetaField(String fieldName, boolean disabled, boolean mandatory,DataType dataType, Integer displayOrder, String value){
		MetaFieldValueWS metaField = new MetaFieldValueWS();
		metaField.setDataType(dataType);
		metaField.setDisabled(disabled);
		metaField.setMandatory(mandatory);
		metaField.setDisplayOrder(displayOrder);
		metaField.setFieldName(fieldName);
		metaField.setStringValue(value);
		return metaField;
	}
}
