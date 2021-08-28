package org.epi.ws.model;

public class API_Model_MasterLocation {
	
	public String location_id;
	public String location_code;
	public String location_name;

	
	public API_Model_MasterLocation(String location_id,
			String location_code,
			String location_name) {
		
		
		this.location_id= location_id;
		this.location_code= location_code;
		this.location_name = location_name;
		
		
	}

}
