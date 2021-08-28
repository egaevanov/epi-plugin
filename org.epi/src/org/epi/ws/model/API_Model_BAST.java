package org.epi.ws.model;

public class API_Model_BAST {
	
	public String asset_id;
	public String customer_id;
	public String status;
	
	public API_Model_BAST(String asset_id,
			String customer_id,
			String status) {
		
		this.asset_id = asset_id;
		this.customer_id = customer_id;
		this.status = status;
		
	}

}
