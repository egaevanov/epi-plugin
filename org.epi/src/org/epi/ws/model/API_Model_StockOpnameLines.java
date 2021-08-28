package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_StockOpnameLines {
	
	public Integer line;
	public String material_id;
	public BigDecimal qty;
	public BigDecimal qty_before;
	public BigDecimal price;
	
	public API_Model_StockOpnameLines(
			Integer line,
			String material_id,
			BigDecimal qty,
			BigDecimal qty_before,
			BigDecimal price) {	
				
		this.line = line;
		this.material_id = material_id;
		this.qty = qty;
		this.qty_before = qty_before;
		this.price = price;
		
	}

}
