package org.epi.ws.model;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class API_Model_StockOpname {
	
	
	public String stockopname_no;
	public Integer project_id;
	public String location_id;
	public String opname_date;
	public String approval_date;
	public String description;
	public ArrayList<JsonObject> details;
	
	public API_Model_StockOpname(String stockopname_no,
			Integer project_id,
			String location_id,
			String opname_date,
			String approval_date,
			String description,
			ArrayList<JsonObject> details) {
		

	this.stockopname_no = stockopname_no;
	this.project_id = project_id;
	this.location_id = location_id;
	this.opname_date = opname_date;
	this.approval_date = approval_date;
	this.description = description;
	this.details = details;
		
		
	}

}
