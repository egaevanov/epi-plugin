package org.epi.ws.model;

public class API_Model_BAST {
	
	public String asset_id;
	public String customer_id;
	public String status;
	public String so_no;
	
	public API_Model_BAST(String asset_id,
			String customer_id,
			String status,
			String so_no) {
		
		this.asset_id = asset_id;
		this.customer_id = customer_id;
		this.status = status;
		this.so_no = so_no;
		
	}

}
