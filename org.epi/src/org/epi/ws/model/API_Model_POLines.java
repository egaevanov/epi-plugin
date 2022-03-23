package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_POLines {
	
	public Integer po_line;
	public String material_id;
	public Integer charge_id;
	public String description;
	public BigDecimal qty_order;
	public BigDecimal price;
	
	public API_Model_POLines(Integer po_line,
			String material_id,
			Integer charge_id,
			String description,
			BigDecimal qty_order,
			BigDecimal price) {

		this.po_line = po_line;
		this.material_id = material_id;
		this.charge_id = charge_id;
		this.description = description;
		this.qty_order = qty_order;
		this.price = price;
			
	}
	

}
