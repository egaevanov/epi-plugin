package org.epi.process;

import org.compiere.util.CLogger;
import org.compiere.util.DB;

public class EPIParameterSupport {
	
	
	protected static CLogger	log = CLogger.getCLogger(EPIParameterSupport.class);


	public static Integer getID(int AD_Client_ID, String TableName,String ColumnName, Integer IntegerValue,String StringValue){
		
		Integer rs = 0;
		
		StringBuilder SQLCheck = new StringBuilder();
		SQLCheck.append("SELECT "+TableName+"_ID");
		SQLCheck.append(" FROM "+TableName);
		SQLCheck.append(" WHERE AD_Client_ID = "+ AD_Client_ID );
		
		if(ColumnName != null && ColumnName != "" && !ColumnName.isEmpty()) {
			SQLCheck.append(" AND "+ ColumnName + " = ?");
			rs = DB.getSQLValueEx(null, SQLCheck.toString(),StringValue != null ? StringValue:IntegerValue);
		}else {
			rs = DB.getSQLValueEx(null, SQLCheck.toString());

		}
		
		
		return rs;
		
	}
	
	public static Integer getDocType(int AD_Client_ID,String DocBaseType ,String DocSubTypeInv,String IsSoTrx){
		Integer rs = 0;
				
		StringBuilder getDocType = new StringBuilder();
		
		getDocType.append("SELECT C_DocType_ID ");
		getDocType.append(" FROM C_DocType ");
		getDocType.append(" WHERE AD_Client_ID = "+AD_Client_ID);
		getDocType.append(" AND DocBaseType = ? ");
		getDocType.append(" AND DocSubTypeInv = ? ");
		getDocType.append(" AND IsSOTrx = ? ");		
		
		rs = DB.getSQLValueEx(null, getDocType.toString(), new Object[]{DocBaseType,DocSubTypeInv,IsSoTrx});
		
		return rs;
		
	}
	
}