package org.epi.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;

import org.compiere.util.DB;

public class DataSetupValidation {
	
	
public static boolean IsValidDataMaster(int AD_Client_ID, int AD_Org_ID,String TableName,String ColumnName, String Value){
		
		boolean rs = true;
		
		StringBuilder SQLCheck = new StringBuilder();
		SQLCheck.append("SELECT "+TableName+"_ID");
		SQLCheck.append(" FROM "+TableName);
		SQLCheck.append(" WHERE AD_Client_ID = "+ AD_Client_ID );
		SQLCheck.append(" AND AD_Org_ID = "+ AD_Org_ID );
		SQLCheck.append(" AND "+ ColumnName + " ='"+Value+"'");

		Integer rslt = DB.getSQLValueEx(null, SQLCheck.toString());
		
		if(rslt < 0 || rslt == null){
			
			rslt = 0;
			
		}
		
		if(rslt > 0){
			rs = false;
		}
		
		return rs;
		
	}

public static Timestamp convertStringToTimeStamp(String strDate) throws ParseException {
	
	Timestamp rs = null;
	
//	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date = Date.valueOf(strDate);
	
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	
	rs = new Timestamp(cal.getTimeInMillis());
	
    return rs;
      
}
	

}
