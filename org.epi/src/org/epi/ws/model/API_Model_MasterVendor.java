package org.epi.ws.model;

public class API_Model_MasterVendor {
	
	public String bp_value;
	public Integer bp_group;
	public String bp_name;
	public String bp_name2;
	public String bp_address;
	public String bp_phone;
	public String bp_mobile;
	public String bp_pic;
	public String bp_city;
	public String bp_postal;
	public Integer bp_term_id;
	public String bp_ws_location;
	public String bp_email;
	
					


	
	public API_Model_MasterVendor(String bp_value,
			Integer bp_group,
			String bp_name,
			String bp_name2,
			String bp_address,
			String bp_phone,
			String bp_mobile,
			String bp_pic,
			String bp_city,
			String bp_postal,
			Integer bp_term_id,
			String bp_ws_location,
			String bp_email){
		
		this.bp_value = bp_value;
		this.bp_group = bp_group;
		this.bp_name = bp_name;
		this.bp_name2 = bp_name2;
		this.bp_address = bp_address;
		this.bp_phone = bp_phone;
		this.bp_mobile = bp_mobile;
		this.bp_pic = bp_pic;
		this.bp_city = bp_city;
		this.bp_postal = bp_postal;
		this.bp_term_id = bp_term_id;
		this.bp_ws_location = bp_ws_location;
		this.bp_email = bp_email;
	}

}
