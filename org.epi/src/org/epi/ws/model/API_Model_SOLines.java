package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_SOLines {
	
	public Integer so_line;
	public String description;
	public Integer charge_id;
	public BigDecimal qty_order;
	public BigDecimal price;
	
	public API_Model_SOLines(Integer so_line,
			String description,
			Integer charge_id,
			BigDecimal qty_order,
			BigDecimal price) {
		
		this.so_line = so_line;
		this.description = description;
		this.charge_id = charge_id;
		this.qty_order = qty_order;
		this.price = price;
		
	}

}
