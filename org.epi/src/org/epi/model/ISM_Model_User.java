package org.epi.model;


public class ISM_Model_User {

	public String IsACtive;
	public String Name;
	public String Value;
	public String Description;
	public String Password;
	public String EMail;
	public String NotificationType;
	public Integer AD_Org_ID;
	public Integer AD_User_ID;

	public ISM_Model_User(String IsActive, String Name, String Value,
			String Description, String Password, String EMail,
			String NotificationType, Integer AD_Org_ID, Integer AD_User_ID) {
		
		this.IsACtive = IsActive;
		this.Name = Name;
		this.Value = Value;
		this.Description = Description;
		this.Password = Password;
		this.EMail = EMail;
		this.NotificationType = NotificationType;
		this.AD_Org_ID = AD_Org_ID;
		this.AD_User_ID = AD_User_ID;
		
	}

}
