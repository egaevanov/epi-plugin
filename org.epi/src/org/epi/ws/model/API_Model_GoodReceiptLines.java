package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_GoodReceiptLines {
	
	public String line;
	public Integer po_line;
	public String product_id;
	public Integer charge_id;
	public String description;
	public BigDecimal qty_receipt;
	public BigDecimal price;
	
	public API_Model_GoodReceiptLines(String line,
			Integer po_line,
			String product_id,
			Integer charge_id,
			String description,
			BigDecimal qty_receipt,
			BigDecimal price) {
		
		this.line = line;
		this.po_line = po_line;
		this.product_id = product_id;
		this.charge_id = charge_id;
		this.description = description;
		this.qty_receipt = qty_receipt;
		this.price = price;
		
		
	}

}
