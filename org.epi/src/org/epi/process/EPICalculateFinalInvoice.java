package org.epi.process;

import java.math.BigDecimal;

import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class EPICalculateFinalInvoice {
	
	protected static CLogger	log = CLogger.getCLogger(EPICalculateFinalInvoice.class);
	
	
	public static BigDecimal CalculateFinalInvoice(int AD_Client_ID, int C_InvoiceLine_ID){
		
		BigDecimal rs = Env.ZERO;
		
		return rs;
		
	}
	
	
	public static Integer getInvoiceDP(int AD_Client_ID, int AD_Org_ID, int C_Order_ID, int C_Invoice_ID){
		
		Integer rs = 0;
		
		StringBuilder getRelationData = new StringBuilder();
				
		getRelationData.append("SELECT ci.C_Invoice_ID");
		getRelationData.append(" FROM C_Invoice ci");
		getRelationData.append(" LEFT JOIN C_Payment cp ON cp.AD_Client_ID = ci.AD_Client_ID AND cp.C_Invoice_ID = ci.C_Invoice_ID");
		getRelationData.append(" WHERE ci.AD_Client_ID = ? ");
		getRelationData.append(" AND ci.C_Order_ID = ? ");
		getRelationData.append(" AND ci.DocStatus = 'CO' ");
		getRelationData.append(" AND ci.IsPaid = 'Y'");
		getRelationData.append(" AND cp.C_Payment_ID IS NOT NULL ");
		getRelationData.append(" AND ci.C_Invoice_ID NOT IN ("+C_Invoice_ID+")");
		
		rs =  DB.getSQLValueEx(null,  getRelationData.toString(), new Object[] {AD_Client_ID,C_Order_ID});
		
		return rs;
	}
	
	
	

}
