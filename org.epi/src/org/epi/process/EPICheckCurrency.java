package org.epi.process;

import java.sql.Timestamp;

import org.compiere.util.DB;

public class EPICheckCurrency {
	
	
	public static Integer ConvertionRateCheck(int AD_Client_ID , int C_Currency_ID, Timestamp DateTrx,String trxName) {
		
		Integer rslt = 0;
		
		StringBuilder SQLCheckCurRate = new StringBuilder();
		SQLCheckCurRate.append("SELECT c_conversion_rate_id ");
		SQLCheckCurRate.append(" FROM c_conversion_rate");
		SQLCheckCurRate.append(" WHERE isactive = 'Y'");
		SQLCheckCurRate.append(" AND ad_client_id = "+ AD_Client_ID);
		SQLCheckCurRate.append(" AND c_currency_id = "+ C_Currency_ID);
		SQLCheckCurRate.append(" AND validfrom <= '"+ DateTrx+"'");
		SQLCheckCurRate.append(" AND validto >= '"+ DateTrx+"'");
		
		rslt = DB.getSQLValueEx(trxName, SQLCheckCurRate.toString());
		
		return rslt;
		
	}

}
