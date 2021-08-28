package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_AssetDisposal {
	
	
	public String disposal_no;
	public String asset_id;
	public Integer location_id;
	public Integer project_id;
	public String disposal_date;
	public String approval_date;
	public String description;
	public String disposal_method;
	public String disposal_reason;
	public String cust_id;
	public BigDecimal acc_depre;
	public BigDecimal acc_depre_change;
	public BigDecimal sold_amount;
	public Integer bankaccount_id;
	
	public API_Model_AssetDisposal(String disposal_no,
			String asset_id,
			Integer location_id,
			Integer project_id,
			String disposal_date,
			String approval_date,
			String description,
			String disposal_method,
			String disposal_reason,
			BigDecimal acc_depre,
			BigDecimal acc_depre_change,
			BigDecimal sold_amount,
			Integer bankaccount_id) {
		
		
		this.disposal_no = disposal_no;
		this.asset_id = asset_id;
		this.location_id = location_id;
		this.project_id = project_id;
		this.disposal_date = disposal_date;
		this.approval_date = approval_date;
		this.description = description;
		this.disposal_method = disposal_method;
		this.disposal_reason = disposal_reason;
		this.acc_depre = acc_depre;
		this.acc_depre_change = acc_depre_change;
		this.sold_amount = sold_amount;
		this.bankaccount_id = bankaccount_id;
		
		
	}

}
