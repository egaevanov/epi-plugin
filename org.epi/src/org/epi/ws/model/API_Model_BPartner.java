package org.epi.ws.model;

import java.math.BigDecimal;

/**
 * 
 * @author Tegar N
 *
 */
public class API_Model_BPartner{
	
	public Integer C_BPartner_ID = 0;
	public String IsActive;
	public String IsVendor;
	public String IsCustomer;
	public String Address1;
	public String Phone;
	public String Phone2;
	public String Postal;
	public String Name;
	public BigDecimal CreditLimit;
	public Integer C_BPartner_Location_ID;
	public Integer C_Location_ID;
	public Integer C_City_ID;
	
	public API_Model_BPartner(String IsActive,Integer C_BPartner_ID,String IsVendor,String IsCustomer,String Address1,
			String Phone,String Phone2,String Postal,String Name,BigDecimal CreditLimit,Integer C_BPartner_Location_ID
			,Integer C_Location_ID,Integer C_City_ID){
		
		this.IsActive = IsActive;
		this.Name = Name;
		this.C_BPartner_ID = C_BPartner_ID;
		this.IsVendor = IsVendor;
		this.IsCustomer = IsCustomer;
		this.Address1 = Address1;
		this.Phone = Phone;
		this.Phone2 = Phone2;
		this.Postal = Postal;
		this.CreditLimit = CreditLimit;
		this.C_BPartner_Location_ID = C_BPartner_Location_ID;
		this.C_Location_ID = C_Location_ID;
		this.C_City_ID = C_City_ID;
	}
}


