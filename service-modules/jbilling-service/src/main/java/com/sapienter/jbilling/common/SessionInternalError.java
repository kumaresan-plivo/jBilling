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

package com.sapienter.jbilling.common;

import org.apache.log4j.Logger;

import javax.xml.ws.WebFault;
import java.io.PrintWriter;
import java.io.StringWriter;

@WebFault(name = "SessionInternalError", targetNamespace = "http://jbilling/")
public class SessionInternalError extends RuntimeException {

    protected SessionInternalErrorMessages sessionInternalErrorMessages = new SessionInternalErrorMessages();
	private String errorMessages[] = null;
	private String params[] = null;
	private String uuid;
	
    public SessionInternalError() {
    }

    public SessionInternalError(String s) {
        super(s);
    }
    
    public SessionInternalError(String s, Class className, Exception e) {
        super(e);
        FormatLogger log = new FormatLogger(Logger.getLogger(className));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();

        log.fatal(s + e.getMessage() + "\n" + sw.toString());
        
    }

    public SessionInternalError(Exception e) {
        super(e);

        if (e instanceof SessionInternalError) {
            setErrorMessages(((SessionInternalError) e).getErrorMessages());
        }

        FormatLogger log = new FormatLogger(Logger.getLogger("com.sapienter.jbilling"));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        log.fatal("Internal error: " + e.getMessage() + "\n" + sw.toString());
    }

    public SessionInternalError(String message, Throwable e) {
        super(message + " Cause: " + e.getMessage(), e);
    }

    public SessionInternalError(String message, Throwable e, String[] errors) {
        super(message + getErrorsAsString(errors), e);
        setErrorMessages(errors);
    }

    public SessionInternalError(String message, String[] errors) {
        super(message + getErrorsAsString(errors));
        setErrorMessages(errors);
    }
    
    public SessionInternalError(String message, String[] errors, String[] params) {
        super(message + getErrorsAsString(errors));
        setErrorMessages(errors);
        setParams(params);
    }

    private static String getErrorsAsString(String[] errors){
        StringBuilder builder = new StringBuilder();
        if (errors != null) {
            builder.append(". Errors: ");
            for (String error : errors) {
                builder.append(error);
                builder.append(System.getProperty("line.separator"));
            }
        }
        return builder.toString();
    }

	public void setErrorMessages(String errors[]) {
        sessionInternalErrorMessages.setErrorMessages(errors);
	}

	public String[] getErrorMessages() {
		return sessionInternalErrorMessages.getErrorMessages();
	}

    public SessionInternalErrorMessages getFaultInfo() {
        return this.sessionInternalErrorMessages;
    }
	
    public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}
	
	public boolean hasParams() {
		return getParams() != null && getParams().length > 0;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void copyErrorInformation(Throwable throwable) {
		if(throwable instanceof SessionInternalError){
			SessionInternalError internal = (SessionInternalError) throwable;
			this.setErrorMessages(internal.getErrorMessages());
			this.setParams(internal.getParams());
		}
	}

}
