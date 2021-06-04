package org.epi.ws.model;

import java.math.BigDecimal;

public class API_Model_Employee {
	
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
	
	public String Wise_NIK;
	public String Wise_Name;
	public String Wise_Position;
	public String Wise_Verifikator;
	public String Wise_Dep_Head;
	public String Wise_Job_Location;
	public String Wise_UserName;
	public String Wise_Department_Name;
	public String Wise_Akses_Level;
	public String Wise_Employee_Type;
	public String Wise_Email;
	public String Wise_Password;
	
	
	public API_Model_Employee(String IsActive,Integer C_BPartner_ID,String IsVendor,String IsCustomer,String Address1,
			String Phone,String Phone2,String Postal,String Name,BigDecimal CreditLimit,Integer C_BPartner_Location_ID
			,Integer C_Location_ID,String Wise_NIK,
			String Wise_Name,
			String Wise_Position,
			String Wise_Verifikator,
			String Wise_Dep_Head,
			String Wise_Job_Location,
			String Wise_UserName,
			String Wise_Department_Name,
			String Wise_Akses_Level,
			String Wise_Employee_Type,
			String Wise_Email,
			String Wise_Password){
		
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
		
		this.Wise_NIK = Wise_NIK;
		this.Wise_Name = Wise_Name;
		this.Wise_Position = Wise_Position;
		this.Wise_Verifikator = Wise_Verifikator;
		this.Wise_Dep_Head = Wise_Dep_Head;
		this.Wise_Job_Location = Wise_Job_Location;
		this.Wise_UserName = Wise_UserName;
		this.Wise_Department_Name = Wise_Department_Name;
		this.Wise_Akses_Level = Wise_Akses_Level;
		this.Wise_Employee_Type = Wise_Employee_Type;
		this.Wise_Email = Wise_Email;
		this.Wise_Password = Wise_Password;
		
	}

}
