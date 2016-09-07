package com.sapienter.jbilling.server.pricing.cache;

import java.math.BigDecimal;

/**
 * Extension to report free call types back to Diameter. Allows diameter to 
 * determine that a call type does not require pricing. Other clients will 
 * see a normal zero-valued BigDecimal.
 */
public class FreePriceBigDecimal extends BigDecimal {

	private static final long serialVersionUID = 5685941736728014182L;

	public FreePriceBigDecimal() {
		super(0);
	}
}
