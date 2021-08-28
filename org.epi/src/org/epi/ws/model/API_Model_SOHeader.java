package org.epi.ws.model;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class API_Model_SOHeader {
	
	public String so_no;
	public String so_type;
	public String unit_type;
	public String model;
	public Integer project_id;
	public String currency;
	public String po_cust;
	public String customer_id;
	public String so_date;
	public String location_id;
	public Integer termin_id;
	public String approval_date;
	public String remark;
	public ArrayList<JsonObject> details;

	public API_Model_SOHeader(String so_no,
			String so_type,
			String unit_type,
			String model,
			Integer project_id,
			String currency,
			String po_cust,
			String customer_id,
			String so_date,
			String location_id,
			Integer termin_id,
			String approval_date,
			String remark,
			ArrayList<JsonObject> details) {
		
		this.so_no = so_no;
		this.so_type = so_type;
		this.unit_type = unit_type;
		this.model = model;
		this.project_id = project_id;
		this.currency = currency;
		this.po_cust = po_cust;
		this.customer_id = customer_id;
		this.so_date = so_date;
		this.location_id = location_id;
		this.termin_id = termin_id;
		this.approval_date = approval_date;
		this.remark = remark;
		this.details = details;
		
	}
	
}
