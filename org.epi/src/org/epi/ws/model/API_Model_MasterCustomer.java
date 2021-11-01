package org.epi.ws.model;

public class API_Model_MasterCustomer {
	
	public String bp_value;
	public Integer bp_group;
	public String bp_name;
	public String bp_address;
	public String bp_phone;
	public String bp_fax;
	public String bp_city;
	public String bp_postal;
	public Integer bp_termin_id;
	public String bp_ws_location;

	
	public API_Model_MasterCustomer(String bp_value,
			Integer bp_group,
			String bp_name,
			String bp_address,
			String bp_phone,
			String bp_fax,
			String bp_city,
			String bp_postal,
			Integer bp_termin_id,
			String bp_ws_location){
		
		this.bp_value = bp_value;
		this.bp_group = bp_group;
		this.bp_name = bp_name;
		this.bp_address = bp_address;
		this.bp_phone = bp_phone;
		this.bp_fax = bp_fax;
		this.bp_city = bp_city;
		this.bp_postal = bp_postal;
		this.bp_termin_id = bp_termin_id;
		this.bp_ws_location = bp_ws_location;
	}

}
