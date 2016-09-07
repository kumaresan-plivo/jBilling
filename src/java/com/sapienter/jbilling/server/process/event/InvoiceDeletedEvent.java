package com.sapienter.jbilling.server.process.event;

import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.system.event.Event;

/**
 * 
 * @author Khobab
 *
 */
public class InvoiceDeletedEvent implements Event {
	
	private final InvoiceDTO invoice;
	private final Integer entityId;
    
    public InvoiceDeletedEvent (InvoiceDTO invoice) {
    	this.invoice = invoice;
    	this.entityId = invoice.getBaseUser().getEntity().getId();
    }
    
    
	public Integer getEntityId() {
		return entityId;
	}
	
	/**
	 * Invoice returned from here is in hibernate session.
	 * Changes will be reflected in db
	 * @return
	 */
	public InvoiceDTO getInvoice() {
		return invoice;
	}


	@Override
	public String getName() {
		return "InvoiceDeletedEvent";
	}

}
