package org.epi.ws.model;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class API_Model_POHeader {
	
	public String po_no;
	public String pr_no;
	public String po_type;
	public String unit_type;
	public String currency;
	public Integer project_id;
	public String location_id;
	public String vendor_id;
	public String deliverypoint;
	public String po_date;
	public String ppn;
	public String remaks;
	public Integer termin_id;
	public String approval_date;
	public ArrayList<JsonObject> details;
	
	public API_Model_POHeader(
			String po_no,
			String pr_no,
			String po_type,
			String unit_type,
			String currency,
			Integer project_id,
			String location_id,
			String vendor_id,
			String deliverypoint,
			String po_date,
			String ppn,
			String remaks,
			Integer termin_id,
			String approval_date,
			ArrayList<JsonObject> details) {
		
		this.po_no = po_no;
		this.pr_no = pr_no;
		this.po_type = po_type;
		this.unit_type = unit_type;
		this.currency = currency;
		this.project_id = project_id;
		this.location_id = location_id;
		this.vendor_id = vendor_id;
		this.deliverypoint = deliverypoint;
		this.po_date = po_date;
		this.ppn = ppn;
		this.remaks = remaks;
		this.termin_id = termin_id;
		this.approval_date = approval_date;
		this.details = details;
		
	}
	
}
