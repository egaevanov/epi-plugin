package org.epi.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Combobox;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MInvoice;
import org.compiere.model.MTable;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/**
 * 
 * @author Tegar N
 *
 */

public class InvoiceComplete {
	
	public Properties ctx = Env.getCtx();


	
	public CLogger log = CLogger.getCLogger(InvoiceComplete.class);
	
	protected Vector<Vector<Object>> getDataInvoice(int AD_Client_ID,int AD_Org_ID,Timestamp DateInvoiced1,Timestamp DateInvoiced2,
			int C_Bpartner_ID,String DocStat,Boolean IsAR,IMiniTable MiniTable) {
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();

		Timestamp now = new Timestamp(System.currentTimeMillis()); 
		
		StringBuilder SQLPelunasan = new StringBuilder();		
		SQLPelunasan.append("SELECT ci.DateAcct, "); 	//1
		SQLPelunasan.append(" ci.DateInvoiced, ");		//2
		SQLPelunasan.append(" ci.C_Invoice_ID, ");		//3
		SQLPelunasan.append(" ci.DocumentNo, ");		//4
		SQLPelunasan.append(" ci.POReference, ");		//5
		SQLPelunasan.append(" bp.C_Bpartner_ID, ");		//6
		SQLPelunasan.append(" bp.Name, ");				//7
		SQLPelunasan.append(" ci.Description, ");		//8
		SQLPelunasan.append(" cp.C_Project_ID, ");		//9
		SQLPelunasan.append(" cp.Name, ");				//10
		SQLPelunasan.append(" ci.GrandTotal, ");		//11
		SQLPelunasan.append(" ci.TaxReference, ");		//12
		SQLPelunasan.append(" ci.DocStatus ");			//13

		SQLPelunasan.append("FROM C_Invoice ci ");
		SQLPelunasan.append("LEFT JOIN C_BPartner bp ON bp.C_Bpartner_ID = ci.C_Bpartner_ID ");
		SQLPelunasan.append("LEFT JOIN C_Project cp ON cp.C_Project_ID = ci.C_Project_ID ");
		SQLPelunasan.append("WHERE ci.AD_Client_ID = ? ");
		SQLPelunasan.append("AND ci.AD_Org_ID = ? ");
		
		if(C_Bpartner_ID >0 ) {
			SQLPelunasan.append("AND bp.C_BPartner_ID =  "+C_Bpartner_ID);
		}
		SQLPelunasan.append("AND ci.IsPaid = 'N' ");
		
		if(IsAR) {
			SQLPelunasan.append("AND ci.IsSoTrx = 'Y' ");
		}else {
			SQLPelunasan.append("AND ci.IsSoTrx = 'N' ");
		}
		
		if(DocStat != null && DocStat != "" && !DocStat.isEmpty()) {
			SQLPelunasan.append("AND ci.Docstatus = '"+DocStat+"'");

		}
		
		if (DateInvoiced1 != null && DateInvoiced2 == null){
			SQLPelunasan.append(" AND (ci.DateInvoiced BETWEEN '"+DateInvoiced1+"' AND '"+now+"') ");
		}else if (DateInvoiced1 != null && DateInvoiced2 != null){
			SQLPelunasan.append(" AND (ci.DateInvoiced BETWEEN '"+DateInvoiced1+"' AND '"+DateInvoiced2+"') ");
		}
			
		SQLPelunasan.append(" ORDER BY ci.DateInvoiced ASC ");

		PreparedStatement pstmtLns = null;
		ResultSet rsLns = null;
		
		try {
			pstmtLns = DB.prepareStatement(SQLPelunasan.toString(), null);
			pstmtLns.setInt(1, AD_Client_ID);
			if(AD_Org_ID > 0){
				pstmtLns.setInt(2, AD_Org_ID);
			}
//			if(C_Bpartner_ID > 0 && AD_Org_ID > 0){
//				pstmtLns.setInt(3, C_Bpartner_ID);
//			}else if(C_Bpartner_ID > 0 && AD_Org_ID == 0){
//				pstmtLns.setInt(2, C_Bpartner_ID);
//			}

			rsLns = pstmtLns.executeQuery();
			while (rsLns.next()) {
				
			Vector<Object> line = new Vector<Object>(6);
			KeyNamePair InvPair = new KeyNamePair(rsLns.getInt(3), rsLns.getString(4)) ;
			KeyNamePair BPPair = new KeyNamePair(rsLns.getInt(6), rsLns.getString(7)) ;
			KeyNamePair projectPair = new KeyNamePair(rsLns.getInt(9), rsLns.getString(10)) ;
			
			String DocStatus = "";
			
			if(rsLns.getString(13).toUpperCase().equals(MInvoice.DOCSTATUS_Completed)) {
				DocStatus = "Complete";
			}else if(rsLns.getString(13).toUpperCase().equals(MInvoice.DOCSTATUS_Drafted)) {
				DocStatus = "Draft";
			}else if(rsLns.getString(13).toUpperCase().equals(MInvoice.DOCSTATUS_InProgress)) {
				DocStatus = "In Progress";
			}else if(rsLns.getString(13).toUpperCase().equals(MInvoice.DOCSTATUS_Invalid)) {
				DocStatus = "Invalid";
			}else if(rsLns.getString(13).toUpperCase().equals(MInvoice.DOCSTATUS_Reversed)) {
				DocStatus = "Reverse";
			}else if(rsLns.getString(13).toUpperCase().equals(MInvoice.DOCSTATUS_Voided)) {
				DocStatus = "Void";
			}

			
				line.add(false);
				line.add(rsLns.getTimestamp(1));
				line.add(rsLns.getTimestamp(2));
				line.add(InvPair);
				line.add(rsLns.getString(5));
				line.add(BPPair);
				line.add(rsLns.getString(8));
				line.add(projectPair);
				line.add(rsLns.getBigDecimal(11));
				line.add(rsLns.getString(12));
				line.add(DocStatus);



				data.add(line);
			}

		} catch (SQLException e) {
			log.log(Level.SEVERE, SQLPelunasan.toString(), e);
		} finally {
			DB.close(rsLns, pstmtLns);
			rsLns = null;
			pstmtLns = null;
		}
			
		return data;
		
	}
	
	protected ArrayList<KeyNamePair> loadBP(int AD_Org_ID) {
		ArrayList<KeyNamePair> list = new ArrayList<KeyNamePair>();
		
		StringBuilder SQLTrxType = new StringBuilder();
		SQLTrxType.append("SELECT C_BPartner_ID,name ");
		SQLTrxType.append(" FROM C_BPartner ");
		SQLTrxType.append(" WHERE AD_Client_ID =  ?");
		SQLTrxType.append(" AND AD_Org_ID =  ?");

		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(SQLTrxType.toString(), null);
			pstmt.setInt(1, Env.getAD_Client_ID(ctx));	
			pstmt.setInt(2, AD_Org_ID);	
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
						
				list.add(new KeyNamePair(rs.getInt(1), rs.getString(2)));
			}

		} catch (SQLException e) {
			log.log(Level.SEVERE, SQLTrxType.toString(), e);
		} finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return list;
	}
	
	public int getIDFromComboBox(Combobox combobox, String tableName,String selectColumnName) {
		int result_ID = 0;
		String select_ID = tableName + "_ID";

		String cbValue = combobox.getText();

		StringBuilder sqlPriceList = new StringBuilder();

		sqlPriceList.append("SELECT ");
		sqlPriceList.append(select_ID);
		sqlPriceList.append(" FROM ");
		sqlPriceList.append(tableName);
		sqlPriceList.append(" WHERE AD_Client_ID = ? ");
		sqlPriceList.append(" AND " + selectColumnName + "= ");
		sqlPriceList.append(" '" + cbValue + "'");
		if(tableName.equals(MTable.Table_Name)) {
			sqlPriceList.append("AND IsView = 'N'");
			result_ID = DB.getSQLValueEx(null, sqlPriceList.toString(),0);
		}else {
			result_ID = DB.getSQLValueEx(null, sqlPriceList.toString(),Env.getAD_Client_ID(ctx));
		}

		

		return result_ID;

	}
	
	
	
}