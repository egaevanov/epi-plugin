package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_GoodIssueLines {
	
	public Integer line;
	public String  material_id;
	public BigDecimal qty_issued;
	public BigDecimal material_po_average;
	public Integer charge_id;

	
	public API_Model_GoodIssueLines(
			Integer line,
			String  material_id,
			BigDecimal qty_issued,
			BigDecimal material_po_average,
			Integer charge_id) {
	

		this.line = line;
		this.material_id = material_id;
		this.qty_issued = qty_issued;
		this.material_po_average = material_po_average;
		this.charge_id = charge_id;
		
		
	}

}
