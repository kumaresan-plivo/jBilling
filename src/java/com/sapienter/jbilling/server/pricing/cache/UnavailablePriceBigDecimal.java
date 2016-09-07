package com.sapienter.jbilling.server.pricing.cache;

import java.math.BigDecimal;

/**
 * Extension to report unavailable prices back to Diameter. Allows diameter to 
 * determine that a price is unavailable. Other clients will see a normal 
 * BigDecimal with a very large number.
 */
public class UnavailablePriceBigDecimal extends BigDecimal {

	private static final long serialVersionUID = 1712410040236614493L;

	public UnavailablePriceBigDecimal() {
		super("9999999999");
	}
}
