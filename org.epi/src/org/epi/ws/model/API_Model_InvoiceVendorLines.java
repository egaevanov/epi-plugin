package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_InvoiceVendorLines {
	
	public Integer line;
	public BigDecimal qty;
	public BigDecimal price;
	public Integer c_tax_id;
	public Integer charge_id;

	public API_Model_InvoiceVendorLines(Integer line,
			BigDecimal qty,
			BigDecimal price,
			Integer c_tax_id,
			Integer charge_id) {
		
		this.line=line;
		this.qty=qty;
		this.price=price;
		this.c_tax_id=c_tax_id;
		this.charge_id=charge_id;
			
	}
	
}
