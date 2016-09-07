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

package com.sapienter.jbilling.server.security;

import com.sapienter.jbilling.client.authentication.CompanyUserDetails;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.item.AssetWS;
import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.order.OrderPeriodWS;
import com.sapienter.jbilling.server.order.OrderStatusWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.payment.PaymentWS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskWS;
import com.sapienter.jbilling.server.pricing.RatingUnitWS;
import com.sapienter.jbilling.server.process.AgeingWS;
import com.sapienter.jbilling.server.process.BillingProcessConfigurationWS;
import com.sapienter.jbilling.server.process.BillingProcessWS;
import com.sapienter.jbilling.server.security.methods.SecuredMethodType;
import com.sapienter.jbilling.server.usagePool.UsagePoolWS;
import com.sapienter.jbilling.server.user.CompanyWS;
import com.sapienter.jbilling.server.user.UserTransitionResponseWS;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.user.partner.CommissionProcessConfigurationWS;
import com.sapienter.jbilling.server.user.partner.PartnerWS;
import com.sapienter.jbilling.server.util.Context;

import com.sapienter.jbilling.server.util.SecurityValidator;
import grails.plugin.springsecurity.SpringSecurityService;

import org.apache.log4j.Logger;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Security advice for web-service method calls to ensure that only data belonging to the
 * web-service caller is accessed.
 *
 * @author Brian Cowdery
 * @since 01-11-2010
 */
public class WSSecurityAdvice implements MethodBeforeAdvice {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(WSSecurityAdvice.class));

    private SpringSecurityService springSecurityService;
    private TransactionTemplate transactionTemplate;
    private SecurityValidator securityValidator;

    public SpringSecurityService getSpringSecurityService() {
        if (springSecurityService == null)
            springSecurityService = Context.getBean(Context.Name.SPRING_SECURITY_SERVICE);
        return springSecurityService;
    }

    public void setSpringSecurityService(SpringSecurityService springSecurityService) {
        this.springSecurityService = springSecurityService;
    }

    public TransactionTemplate getTransactionTemplate() {
        if (transactionTemplate == null) {
            PlatformTransactionManager transactionManager = Context.getBean(Context.Name.TRANSACTION_MANAGER);
            transactionTemplate = new TransactionTemplate(transactionManager);
        }
        return transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public SecurityValidator getSecurityValidator () {
        return securityValidator;
    }

    public void setSecurityValidator (SecurityValidator securityValidator) {
        this.securityValidator = securityValidator;
    }

    public Integer getCallerCompanyId() {
        CompanyUserDetails details = (CompanyUserDetails) getSpringSecurityService().getPrincipal();
        return details.getCompanyId();
    }

    /**
     * Validates that method call arguments are accessible to the web-service caller company.
     *
     * @param method method to call
     * @param args method arguments to validate
     * @param target method call target, may be null
     * @throws Throwable throws a SecurityException if the calling user does not have access to the given data
     */
    public void before(Method method, Object[] args, Object target) throws Throwable {
        LOG.debug("Validating web-service method '" + method.getName() + "()'");

        Validator.Type type = Validator.Type.VIEW;
        Validator annotation =  method.getAnnotation(Validator.class);
        if (annotation!=null) {
            type = annotation.type();
        }

        //Avoid un-authenticated calls, except for methods with validation type = NONE.
        if (!getSpringSecurityService().isLoggedIn() && !type.equals(Validator.Type.NONE))
            throw new SecurityException("Web-service call has not been authenticated.");
                
        // try validating the method call itself
        WSSecured securedMethod = getMappedSecuredWS(method, args);
        if (securedMethod != null)
            validate(securedMethod, type);

        // validate each method call argument
        for (Object o : args) {
            if (o != null) {
                if (o instanceof Collection) {
                    for (Object element : (Collection) o)
                        validate(element, type);

                } else if (o.getClass().isArray()) {
                    for (Object element : (Object[]) o)
                        validate(element, type);

                } else {
                    validate(o, type);
                }
            }
        }
    }

    /**
     * Attempt to map the method call as an instance of WSSecured so that it can be validated.
     *
     * @see com.sapienter.jbilling.server.security.WSSecurityMethodMapper
     *
     * @param method method to map
     * @param args method arguments
     * @return mapped method call, or null if method call is unknown
     */
    protected WSSecured getMappedSecuredWS(final Method method, final Object[] args) {
        return getTransactionTemplate().execute(new TransactionCallback<WSSecured>() {
            public WSSecured doInTransaction(TransactionStatus status) {
                return WSSecurityMethodMapper.getMappedSecuredWS(method, args);
            }
        });
    }

    /**
     * Attempt to map the given object as an instance of WSSecured so that it can be validated.
     *
     * @see com.sapienter.jbilling.server.security.WSSecurityEntityMapper
     *
     * @param o object to map
     * @return mapped object, or null if object is of an unknown type
     */
    protected WSSecured getMappedSecuredWS(final Object o) {
        LOG.debug("Non WSSecured object " + o.getClass().getSimpleName()
                  + ", attempting to map a secure class for validation.");

        return getTransactionTemplate().execute(new TransactionCallback<WSSecured>() {
            public WSSecured doInTransaction(TransactionStatus status) {
                return WSSecurityEntityMapper.getMappedSecuredWS(o);
            }
        });
    }

    /**
     * Attempt to validate the given object.
     *
     * @param o object to validate
     * @throws SecurityException thrown if user is accessing data that does not belonging to them
     */
    protected void validate(Object o, final Validator.Type validatorType) throws SecurityException {
        if (o != null) {
            if (o instanceof WSSecured) {
                validateEntityChange((WSSecured) o, validatorType);
            }

            final WSSecured secured = (o instanceof WSSecured)
                                      ? (WSSecured) o
                                      : getMappedSecuredWS(o);

            if (secured != null) {
                LOG.debug("Validating secure object " + secured.getClass().getSimpleName());

                getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        if (secured.getOwningUserId() != null)
                            securityValidator.validateUserAndCompany(secured, validatorType);
                        else if (secured.getOwningEntityId() != null) {
                            securityValidator.validateCompany(secured, null, validatorType);
                        }
                    }
                });
            }
        }
    }

    /**
     * This method validate is entity (user) was changed in input object (for update usually)
     * Changing the entity (company) is not allowed for invoices, items, orders, etc.
     * So, we should compare persisted object, is it owned by entity for caller user, or not
     * Not persisted objects (without id) is not checked
     * @param inputObject WSSecured input object for check entity in persisted one
     */
    protected void validateEntityChange(final WSSecured inputObject, final Validator.Type validatorType) {
        Integer persistedId = null;
        SecuredMethodType type = null;
        if (inputObject instanceof AgeingWS) {
            // do nothing, entity can't be changed
        } else if (inputObject instanceof AssetWS && ((AssetWS) inputObject).getId() != null) {
            persistedId = ((AssetWS) inputObject).getId();
            type = SecuredMethodType.ASSET;
        } else if (inputObject instanceof BillingProcessWS) {
            // do nothing, entity can't be changed
        } else if (inputObject instanceof InvoiceWS && ((InvoiceWS) inputObject).getId() != null) {
            type = SecuredMethodType.INVOICE;
            persistedId = ((InvoiceWS) inputObject).getId();
        } else if (inputObject instanceof ItemDTOEx && ((ItemDTOEx) inputObject).getId() != null) {
            type = SecuredMethodType.ITEM;
            persistedId = ((ItemDTOEx) inputObject).getId();
        } else if (inputObject instanceof OrderWS && ((OrderWS) inputObject).getId() != null) {
            type = SecuredMethodType.ORDER;
            persistedId = ((OrderWS) inputObject).getId();
        } else if (inputObject instanceof OrderPeriodWS && ((OrderPeriodWS) inputObject).getId() != null) {
            type = SecuredMethodType.ORDER_PERIOD;
            persistedId = ((OrderPeriodWS) inputObject).getId();
        } else if (inputObject instanceof OrderStatusWS && ((OrderStatusWS) inputObject).getId() != null) {
            type = SecuredMethodType.ORDER_STATUS;
            persistedId = ((OrderStatusWS) inputObject).getId();
        } else if (inputObject instanceof PartnerWS && ((PartnerWS) inputObject).getId() != null) {
            type = SecuredMethodType.PARTNER;
            persistedId = ((PartnerWS) inputObject).getId();
        } else if (inputObject instanceof PaymentWS && ((PaymentWS) inputObject).getId() > 0) {
            type = SecuredMethodType.PAYMENT;
            persistedId = ((PaymentWS) inputObject).getId();
        } else if (inputObject instanceof RatingUnitWS && ((RatingUnitWS) inputObject).getId() != null && ((RatingUnitWS) inputObject).getId() > 0) {
            type = SecuredMethodType.RATING_UNIT;
            persistedId = ((RatingUnitWS) inputObject).getId();
        } else if (inputObject instanceof UsagePoolWS && ((UsagePoolWS) inputObject).getId() > 0) {
            type = SecuredMethodType.USAGE_POOL;
            persistedId = ((UsagePoolWS) inputObject).getId();
        } else if (inputObject instanceof PluggableTaskWS) {
            // do nothing, entity can't be changed
        } else if (inputObject instanceof UserTransitionResponseWS) {
           // do nothing, entity can't be changed
        } else if (inputObject instanceof UserWS && ((UserWS) inputObject).getId() > 0) {
            persistedId = ((UserWS) inputObject).getId();
            type = SecuredMethodType.USER;
        } else if (inputObject instanceof CompanyWS && ((CompanyWS) inputObject).getId() > 0) {
            persistedId = ((CompanyWS) inputObject).getId();
            type = SecuredMethodType.COMPANY;
        } else if (inputObject instanceof CommissionProcessConfigurationWS && ((CommissionProcessConfigurationWS) inputObject).getId() > 0) {
            persistedId = ((CommissionProcessConfigurationWS) inputObject).getId();
            type = SecuredMethodType.COMISSION_PROCESS_CONFIGURATION;
        } else if (inputObject instanceof BillingProcessConfigurationWS && ((BillingProcessConfigurationWS) inputObject).getId() > 0) {
            persistedId = ((BillingProcessConfigurationWS) inputObject).getId();
            type = SecuredMethodType.BILLING_PROCESS_CONFIGURATION;
        }

        if (type != null && persistedId != null) {
            // validate user and entity in persisted object - they should be the same as for caller
            final SecuredMethodType finalType = type;
            final Integer finalId = persistedId;
            getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    WSSecured persistedSecuredObject = finalType.getMappedSecuredWS(finalId);
                    if (persistedSecuredObject != null) {
                        if (persistedSecuredObject.getOwningUserId() != null) {
                            securityValidator.validateUserAndCompany(persistedSecuredObject, validatorType);
                        } else if (persistedSecuredObject.getOwningEntityId() != null) {
                            securityValidator.validateCompany(persistedSecuredObject, null, validatorType);
                        }
                    }
                }
            } );
        }
    }

    
}
