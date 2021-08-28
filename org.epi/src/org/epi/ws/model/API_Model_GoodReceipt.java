package org.epi.ws.model;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class API_Model_GoodReceipt {
	
	public String gr_no;
	public String po_no;
	public String gr_date;
	public String delivery_note;
	public String location_id;
	public String approval_date;
	public String asset_id;
	public ArrayList<JsonObject> details;
	public String remark;
	public Integer project_id;
	
	public API_Model_GoodReceipt(String gr_no,
			String po_no,
			String gr_date,
			String delivery_note,
			String location_id,
			String approval_date,
			String asset_id,
			ArrayList<JsonObject> details,
			String remark,
			Integer project_id) {
		
		
		this.gr_no = gr_no;
		this.po_no = po_no;
		this.gr_date = gr_date;
		this.delivery_note = delivery_note;
		this.location_id = location_id;
		this.approval_date = approval_date;
		this.asset_id = asset_id;
		this.details = details;
		this.remark = remark;
		this.project_id = project_id;
	}
	
}
