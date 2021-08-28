package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_TimeSheetLines {
	
	public Integer line;
	public Integer so_line;
	public Integer charge_id;
	public String description;
	public BigDecimal qty_receipt;
	
	public API_Model_TimeSheetLines(Integer line,
			Integer so_line,
			Integer charge_id,
			String description,
			BigDecimal qty_receipt) {

		this.line = line;
		this.so_line = so_line;
		this.charge_id = charge_id;
		this.description = description;
		this.qty_receipt = qty_receipt;
		
	}

}
