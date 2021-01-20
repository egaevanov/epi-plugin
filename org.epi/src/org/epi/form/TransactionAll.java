package org.epi.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.component.Combobox;
import org.compiere.model.MBPartner;
import org.compiere.model.MColumn;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MWindow;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

public class TransactionAll {
	
	private final String DOC_DRAFT = "DR";
	private final String DOC_INPROGRESS = "IP";
	private final String DOC_COMPLETE = "CO";
	private final String DOC_REVERSE= "RE";
	private final String DOC_CLOSE = "CL";
		

	public CLogger log = CLogger.getCLogger(TransactionAll.class);
	public Properties ctx = Env.getCtx();
	public int AD_Client_ID = Env.getAD_Client_ID(ctx);
	
	Vector<Vector<Object>> data = null;
	
	protected ArrayList<KeyNamePair> loadTrx(int AD_Org_ID) {
		ArrayList<KeyNamePair> list = new ArrayList<KeyNamePair>();
		
		StringBuilder SQLTrxType = new StringBuilder();
		SQLTrxType.append("SELECT AD_Tab_ID ");
		SQLTrxType.append("FROM ISM_ViewTransactionConf ");
		SQLTrxType.append("WHERE AD_Client_ID = ? ");
		SQLTrxType.append("AND AD_Org_ID = ? ");

		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(SQLTrxType.toString(), null);
			pstmt.setInt(1, Env.getAD_Client_ID(ctx));	
			pstmt.setInt(2, AD_Org_ID);	
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				
				MTab tab = new MTab(ctx, rs.getInt(1), null);
//				MTable table = new MTable(ctx, tab.getAD_Table_ID(), null);
				
				list.add(new KeyNamePair(tab.getAD_Tab_ID(), tab.getName()));
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
	
	
	protected ArrayList<KeyNamePair> loadDocType(int AD_Tab_ID) {
		ArrayList<KeyNamePair> list = new ArrayList<KeyNamePair>();
		
		StringBuilder SQLTrxType = new StringBuilder();
		SQLTrxType.append("SELECT C_DocType_ID ");
		SQLTrxType.append(" FROM ISM_ViewTransactionConfLine a ");
		SQLTrxType.append(" LEFT JOIN ISM_ViewTransactionConf b ON a.ISM_ViewTransactionConf_ID = b.ISM_ViewTransactionConf_ID ");
		SQLTrxType.append(" WHERE b.AD_Tab_ID = "+AD_Tab_ID);
		SQLTrxType.append(" AND a.AD_Client_ID = "+AD_Client_ID);
		SQLTrxType.append(" AND a.IsActive = 'Y'");

		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(SQLTrxType.toString(), null);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				
				MDocType docType = new MDocType(ctx, rs.getInt(1), null);
				
				list.add(new KeyNamePair(docType.getC_DocType_ID(), docType.getName()));
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
			result_ID = DB.getSQLValueEx(null, sqlPriceList.toString(),AD_Client_ID);
		}

		

		return result_ID;

	}
	
	
	public int getIDFromComboBoxFromSystem(Combobox combobox, String tableName,String selectColumnName) {
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
			result_ID = DB.getSQLValueEx(null, sqlPriceList.toString(),0);
		}

		

		return result_ID;

	}
	
	protected Vector<Vector<Object>> getTrxData(int AD_Tab_ID, int C_DocType_ID,Combobox TypeTrx,int ISM_ViewTransactionConf_ID,int C_BPartner_ID,Timestamp DateFrom,Timestamp DateTo,String DateFilter) {
		
		boolean IsSOTrx = true;
		String SOTrx = "Y";
		
		MTab tab = new MTab(ctx, AD_Tab_ID, null);	
		MWindow window = new MWindow(ctx, tab.getAD_Window_ID(), null);
		MTable table = new MTable(ctx, tab.getAD_Table_ID(), null);
		ArrayList<String>ListColumn = new ArrayList<String>();
		
		HashMap<String, Integer> MapColumnSeq = new HashMap<String, Integer>();
		HashMap<Integer, String> MapReference = new HashMap<Integer, String>();
		
		

		
		String tableName = table.getTableName();
		
		IsSOTrx = window.isSOTrx();
		
		if(!IsSOTrx) {
			SOTrx = "N";
		}
		
		
		
		StringBuilder SQLGetData = new StringBuilder();
		SQLGetData.append("SELECT ");
		SQLGetData.append(table.getTableName()+"_ID,");

		
	
		
		StringBuilder SQLcolumnName = new StringBuilder();
		SQLcolumnName.append("SELECT Name,Reference,sequence,AD_Column_ID");
		SQLcolumnName.append(" FROM  ISM_ViewTransactionColumn ");
		SQLcolumnName.append(" WHERE AD_Client_ID = "+AD_Client_ID);
		SQLcolumnName.append(" AND AD_Org_ID = "+ Env.getAD_Org_ID(ctx));
		SQLcolumnName.append(" AND ISM_ViewTransactionConf_ID ="+ISM_ViewTransactionConf_ID);
		SQLcolumnName.append(" Order By sequence asc ");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(SQLcolumnName.toString(), null);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				
					MColumn column = new MColumn(ctx, rs.getInt(4), null);
				
					ListColumn.add(column.getColumnName());	
					MapColumnSeq.put(column.getColumnName(), rs.getInt(3));
					MapReference.put(rs.getInt(3), rs.getString(2));
					
					
			}

		} catch (SQLException e) {
			log.log(Level.SEVERE, SQLcolumnName.toString(), e);
		} finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		
		
		for (int i = 0 ; i < ListColumn.size(); i++) {
			
			if(i==0) {
				SQLGetData.append(ListColumn.get(i)+",");
			}else if(i == ListColumn.size()-1) {
				SQLGetData.append(ListColumn.get(i));
			}else {
				SQLGetData.append(ListColumn.get(i)+",");
			}
			

		}
		
		
		

		SQLGetData.append(" FROM "+tableName);
		SQLGetData.append(" WHERE AD_Client_ID = "+AD_Client_ID);
		SQLGetData.append(" AND AD_Org_ID = "+Env.getAD_Org_ID(ctx));
		
		if(C_BPartner_ID > 0) {
			SQLGetData.append(" AND C_BPartner_ID = "+C_BPartner_ID);
		}
		
		if(tableName.equals(MOrder.Table_Name) ||tableName.equals(MInvoice.Table_Name)) {
		SQLGetData.append(" AND C_DocTypeTarget_ID  = "+ C_DocType_ID);
		}else {
			SQLGetData.append(" AND C_DocType_ID  = "+ C_DocType_ID);
		}
		
		if(tableName.equals(MPayment.Table_Name)) {
			SQLGetData.append(" AND IsReceipt = '"+SOTrx+"'");
		}else {
			SQLGetData.append("  AND IsSOtrx = '"+SOTrx+"'");
		}
		
		if (DateFrom != null && DateTo == null){
			Timestamp now = new Timestamp(System.currentTimeMillis()); 
			SQLGetData.append(" AND ("+DateFilter+" BETWEEN '"+DateFrom+"' AND '"+now+"') ");
		}else if (DateFrom != null && DateTo != null){
			SQLGetData.append(" AND ("+DateFilter+" BETWEEN '"+DateFrom+"' AND '"+DateTo+"') ");
		}
		
		
		data = new Vector<Vector<Object>>();

		PreparedStatement pstmtData = null;
		ResultSet rsData = null;
		try {
			pstmtData = DB.prepareStatement(SQLGetData.toString(), null);

			rsData = pstmtData.executeQuery();
			while (rsData.next()) {
				
				Vector<Object> line = new Vector<Object>(6);
				line.add((Boolean)false); 	//1
				KeyNamePair kp = new KeyNamePair(rsData.getInt(1), rsData.getString(2));
				line.add(kp);					//2

				for(int i=1 ; i <= MapColumnSeq.size() ; i++) {
					
					if(i>1) {
						if(i == MapColumnSeq.get("C_BPartner_ID")) {
							MBPartner bp = new MBPartner(ctx, rsData.getInt(MapColumnSeq.get("C_BPartner_ID")+1), null);
							line.add(bp.getName());
							
						}else{
							
							if(MapReference.get(i).toUpperCase().equals("S")) {
								
								String rslt = rsData.getString(i+1);
								if (rslt == null)
									rslt = "";
								
								line.add(rslt); 						//
							
							}else if(MapReference.get(i).toUpperCase().equals("B")) {
								line.add(rsData.getBigDecimal(i+1)); 						//					
							}else if(MapReference.get(i).toUpperCase().equals("N")) {
								line.add(rsData.getInt(i+1)); 						//
							}else if(MapReference.get(i).toUpperCase().equals("D")) {
								
								SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
								String fromDBstr = format.format(rsData.getDate(i+1));
																
								line.add(fromDBstr); 						
							}
							
						}
					}
					
					
				}
				
				data.add(line);
				
			}

		} catch (SQLException e) {
			log.log(Level.SEVERE, SQLGetData.toString(), e);
		} finally {
			DB.close(rsData, pstmtData);
			rsData = null;
			pstmtData = null;
		}
			
		return data;
	}
	
	public String getDocStatus(String doc) {
		
		String rs = "";
		
		if(doc.toUpperCase().equals(DOC_DRAFT)) {
			rs = "Draft";
		}else if(doc.toUpperCase().equals(DOC_INPROGRESS)) {
			rs = "In Progress";
		}else if(doc.toUpperCase().equals(DOC_COMPLETE)) {
			rs = "Complete";
		}else if(doc.toUpperCase().equals(DOC_REVERSE)) {
			rs = "Reverse";
		}else if(doc.toUpperCase().equals(DOC_CLOSE)) {
			rs = "Close";
		}
		
		return rs;
		
	}
	
//	private String getQueryData(String tableName, boolean IsSOTrx) {
//		
//		String rs = "";
//			
//			if(tableName.toUpperCase().equals(MOrder.Table_Name.toUpperCase())) {
//				
//				if(IsSOTrx) {
//					
//				}else {
//					
//				}
//				
//			}else if(tableName.toUpperCase().equals(MInOut.Table_Name.toUpperCase())) {
//				if(IsSOTrx) {
//					
//				}else {
//					
//				}
//			}else if(tableName.toUpperCase().equals(MInvoice.Table_Name.toUpperCase())) {
//				if(IsSOTrx) {
//					
//				}else {
//					
//				}
//			}else if(tableName.toUpperCase().equals(MPayment.Table_Name.toUpperCase())) {
//				if(IsSOTrx) {
//					
//				}else {
//					
//				}
//			}else if(tableName.toUpperCase().equals(MJournal.Table_Name.toUpperCase())) {
//			
//			}
//		
//		
//		return rs;
//	}
	
}
