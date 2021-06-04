package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_Customer {
	
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
	
	public String Wise_Company_Name;
	public String Wise_Location;
	public String Wise_Address;
	public String Wise_Phone_Number;
	public String Wise_Fax;
	public String Wise_City;
	public String Wise_PostalCode;
	public String Wise_WS_Location;
	public String Wise_IsPO;
	public String Wise_IsTS;
	public String Wise_IsBAST;
	public String Wise_IsGR;

	
	public API_Model_Customer(String IsActive,Integer C_BPartner_ID,String IsVendor,String IsCustomer,String Address1,
			String Phone,String Phone2,String Postal,String Name,BigDecimal CreditLimit,Integer C_BPartner_Location_ID
			,Integer C_Location_ID,String Wise_Company_Name,
			String Wise_Location,
			String Wise_Address,
			String Wise_Phone_Number,
			String Wise_Fax,
			String Wise_City,
			String Wise_PostalCode,
			String Wise_WS_Location,
			String Wise_IsPO,
			String Wise_IsTS,
			String Wise_IsBAST,
			String Wise_IsGR){
		
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
		
		this.Wise_Company_Name = Wise_Company_Name;
		this.Wise_Location = Wise_Location;
		this.Wise_Address = Wise_Address;
		this.Wise_Phone_Number = Wise_Phone_Number;
		this.Wise_Fax = Wise_Fax;
		this.Wise_City = Wise_City;
		this.Wise_PostalCode = Wise_PostalCode;
		this.Wise_WS_Location = Wise_WS_Location;
		this.Wise_IsPO = Wise_IsPO;
		this.Wise_IsTS = Wise_IsTS;
		this.Wise_IsBAST = Wise_IsBAST;
		this.Wise_IsGR = Wise_IsGR;
	}

}
