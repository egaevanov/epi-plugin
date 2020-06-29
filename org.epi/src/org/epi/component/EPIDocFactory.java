package org.epi.component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.adempiere.base.IDocFactory;
import org.compiere.acct.Doc;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_M_Inventory;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MTable;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.acct.EPIDocInventory;
import org.epi.acct.EPIDocInvoice;

public class EPIDocFactory implements IDocFactory{

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, int Record_ID,
			String trxName) {
		
		String tableName = MTable.getTableName(Env.getCtx(), AD_Table_ID);
		
		Doc doc = null;	
		
		StringBuffer sql = new StringBuffer("SELECT * FROM ").append(tableName)
								.append(" WHERE ").append(tableName)
								.append("_ID=? AND Processed='Y'");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement (sql.toString(), trxName);
			pstmt.setInt (1, Record_ID);
			rs = pstmt.executeQuery ();
			if (rs.next ())
				doc = getDocument(as, AD_Table_ID, rs, trxName);
			//else
				//s_log.severe("Not Found: " + tableName + "_ID=" + Record_ID);
		}
		catch (Exception e) {
			//s_log.log (Level.SEVERE, sql.toString(), e);
		}
		finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		return doc;
	}

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, ResultSet rs,
			String trxName) {
		
		String tableName = MTable.getTableName(Env.getCtx(), AD_Table_ID);
		
		if (tableName.equals(I_M_Inventory.Table_Name))
			return new EPIDocInventory(as, rs, trxName);
		if (tableName.equals(I_C_Invoice.Table_Name))
			return new EPIDocInvoice(as, rs, trxName);
		
		return null;
		
	}

}
