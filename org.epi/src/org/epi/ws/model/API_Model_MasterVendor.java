package org.epi.ws.model;

public class API_Model_MasterVendor {
	
	public String vendor_pk_id;
	public String vendor_id;
	public String vendor_name;
	public String vendor_name2;
	public String address;
	public String phone;
	public String mobile;
	public String bp_pic;
	public String bp_city;
	public String bp_postal;
	public Integer bp_term_id;
	public String bp_ws_location;
	public String bp_email;
	
	public API_Model_MasterVendor(String vendor_pk_id,
			String vendor_id,
			String vendor_name,
			String vendor_name2,
			String address,
			String phone,
			String mobile){
		
		this.vendor_pk_id = vendor_pk_id;
		this.vendor_id = vendor_id;
		this.vendor_name = vendor_name;
		this.vendor_name2 = vendor_name2;
		this.address = address;
		this.phone = phone;
		this.mobile = mobile;
	}

}
