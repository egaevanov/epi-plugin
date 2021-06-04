package org.epi.ws.model;

import java.math.BigDecimal;

/**
 * 
 * @author Tegar N
 *
 */
public class API_Model_Vendor{
	
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
	
	public String Wise_Vendor_Name;
	public String Wise_Vendor_ID;
	public String Wise_DeliveryPoint;
	public String Wise_Address;
	public String Wise_Phone_Number;
	public String Wise_Mobile_Number;
	public String Wise_PIC;
	public String Wise_Email;
	public String Wise_TermOfPayment;
	
	public API_Model_Vendor(String IsActive,Integer C_BPartner_ID,String IsVendor,String IsCustomer,String Address1,
			String Phone,String Phone2,String Postal,String Name,BigDecimal CreditLimit,Integer C_BPartner_Location_ID
			,Integer C_Location_ID,String Wise_Vendor_Name,
			String Wise_Vendor_ID,
			String Wise_DeliveryPoint,
			String Wise_Address,
			String Wise_Phone_Number,
			String Wise_Mobile_Number,
			String Wise_PIC,
			String Wise_Email,
			String Wise_TermOfPayment){
		
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
		
		this.Wise_Vendor_Name = Wise_Vendor_Name; 
		this.Wise_Vendor_ID = Wise_Vendor_ID; 
		this.Wise_DeliveryPoint = Wise_DeliveryPoint; 
		this.Wise_Address = Wise_Address; 
		this.Wise_Phone_Number = Wise_Phone_Number; 
		this.Wise_Mobile_Number = Wise_Mobile_Number; 
		this.Wise_PIC = Wise_PIC; 
		this.Wise_Email = Wise_Email; 
		this.Wise_TermOfPayment = Wise_TermOfPayment; 
	}
}


