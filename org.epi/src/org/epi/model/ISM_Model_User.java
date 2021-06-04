package org.epi.model;

import java.util.HashMap;

import org.compiere.model.MUser;

public class ISM_Model_User {

	public String IsActive;
	public String Name;
	public String Value;
	public String Description;
	public String Password;
	public String EMail;
	public int AD_User_ID = 0;

	public ISM_Model_User(String IsActive, String Name, String Value,
			String Description, String Password, String EMail,
			String NotificationType, Integer AD_Org_ID, Integer AD_User_ID) {
		
		this.IsActive = IsActive;
		this.Name = Name;
		this.Value = Value;
		this.Description = Description;
		this.Password = Password;
		this.EMail = EMail;
		this.AD_User_ID = AD_User_ID;
	}
	
	
	public static HashMap<String, String> ISM_Model_User_Column(){
		
		HashMap<String, String> rs = new HashMap<String, String>();
		
		rs.put(MUser.COLUMNNAME_IsActive,MUser.COLUMNNAME_IsActive);
		rs.put(MUser.COLUMNNAME_Name,MUser.COLUMNNAME_Name);
		rs.put(MUser.COLUMNNAME_Value,MUser.COLUMNNAME_Value);
		rs.put(MUser.COLUMNNAME_Description,MUser.COLUMNNAME_Description);
		rs.put(MUser.COLUMNNAME_Password,MUser.COLUMNNAME_Password);
		rs.put(MUser.COLUMNNAME_EMail,MUser.COLUMNNAME_EMail);

		return rs;
		
	}

}
