package org.epi.ws.model;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.google.gson.JsonObject;

public class API_Model_GoodIssue {
	
//	public String gi_no;
//	public Integer project_id;
//	public Integer location_id;
//	public String issued_date;
//	public String approval_date;
//	public Integer asset_id;
//	public String description;
	
	public String gi_no;
	public String gi_date;
	public String description;
	public Integer account_dr;
	public Integer account_cr;
	public BigDecimal amount;
	public ArrayList<JsonObject> details;
	
	public API_Model_GoodIssue(String gi_no,
			String gi_date,
			String description,
			Integer account_dr,
			Integer account_cr,
			BigDecimal amount,
			ArrayList<JsonObject> details) {
		
		

//		this.gi_no = gi_no;
//		this.project_id = project_id;
//		this.location_id = location_id;
//		this.issued_date = issued_date;
//		this.approval_date = approval_date;
//		this.asset_id = asset_id;
//		this.description = description;
//		this.details = details;
		
		this.gi_no=gi_no;
		this.gi_date=gi_date;
		this.description=description;
		this.account_dr=account_dr;
		this.account_cr=account_cr;
		this.amount=amount;
		this.details=details;
				
		
	}

}
