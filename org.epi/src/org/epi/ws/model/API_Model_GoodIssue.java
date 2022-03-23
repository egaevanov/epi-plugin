package org.epi.ws.model;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class API_Model_GoodIssue {
	
	public String gi_no;
	public Integer project_id;
	public Integer location_id;
	public String issued_date;
	public String approval_date;
	public Integer asset_id;
	public String description;
	public ArrayList<JsonObject> details;
	
	public API_Model_GoodIssue(String gi_no,
			Integer project_id,
			Integer location_id,
			String issued_date,
			String approval_date,
			Integer asset_id,
			String description,
			ArrayList<JsonObject> details) {
		
		

		this.gi_no = gi_no;
		this.project_id = project_id;
		this.location_id = location_id;
		this.issued_date = issued_date;
		this.approval_date = approval_date;
		this.asset_id = asset_id;
		this.description = description;
		this.details = details;
				
		
	}

}
