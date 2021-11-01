package org.epi.ws.model;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class API_Model_TimeSheetHeader {
	
	public String ts_no;
	public String so_no;
	public String ts_date;
	public String delivery_note;
	public String location_id;
	public String approval_date;
	public String asset_id;
	public String url_1;
	public String url_2;
	public String url_3;
	public String url_4;
	public ArrayList<JsonObject> detail;
	public Integer project_id;
	public String startdate;
	public String enddate;
	
	public API_Model_TimeSheetHeader(
			String ts_no,
			String so_no,
			String ts_date,
			String delivery_note,
			String location_id,
			String approval_date,
			String asset_id,
			String url_1,
			String url_2,
			String url_3,
			String url_4,
			ArrayList<JsonObject> detail,
			Integer project_id,
			String startdate,
			String enddate) {
		
		this.ts_no = ts_no;
		this.so_no = so_no;
		this.ts_date = ts_date;
		this.delivery_note = delivery_note;
		this.location_id = location_id;
		this.approval_date = approval_date;
		this.asset_id = asset_id;
		this.url_1 = url_1;
		this.url_2 = url_2;
		this.url_3 = url_3;
		this.url_4 = url_4;
		this.detail = detail;
		this.project_id = project_id;
		this.startdate = startdate;
		this.enddate = enddate;
	
	}

}
