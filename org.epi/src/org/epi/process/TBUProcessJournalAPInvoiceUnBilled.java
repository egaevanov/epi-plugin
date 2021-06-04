package org.epi.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.X_TBU_BAOperation;
import org.epi.model.X_TBU_OperationService;

public class TBUProcessJournalAPInvoiceUnBilled extends SvrProcess  {
	
	
	private int p_C_BPartner_ID = 0;
	private Timestamp p_StartDate = null;
	private Timestamp p_EndDate = null;
	private int p_C_Activity_ID = 0;
	private int p_GL_Journal_ID = 0;
	private int p_C_Project_ID = 0;
	private int p_ISM_Department_ID = 0;

	@Override
	protected void prepare() {
	
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("C_BPartner_ID"))
				p_C_BPartner_ID  = (int)para[i].getParameterAsInt();
			else if(name.equals("StartDate"))
				p_StartDate  = (Timestamp)para[i].getParameterAsTimestamp();
			else if(name.equals("EndDate"))
				p_EndDate  = (Timestamp)para[i].getParameterAsTimestamp();
			else if(name.equals("C_Project_ID"))
				p_C_Project_ID= (int)para[i].getParameterAsInt();
			else if(name.equals("C_Activity_ID"))
				p_C_Activity_ID = (int)para[i].getParameterAsInt();
			else if(name.equals("ISM_Department_ID"))
				p_ISM_Department_ID = (int)para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		
		p_GL_Journal_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		
		
		StringBuilder SQLgetDoctype = new StringBuilder();
		SQLgetDoctype.append("SELECT C_DocType_ID ");
		SQLgetDoctype.append(" FROM C_DocType ");
		SQLgetDoctype.append(" WHERE AD_Client_ID = "+getAD_Client_ID());
		SQLgetDoctype.append(" AND Name = 'Operation Cost' ");
		int C_DocType_ID = DB.getSQLValueEx(get_TrxName(), SQLgetDoctype.toString());
		
		
		StringBuilder SQLGetData = new StringBuilder();
		SQLGetData.append("SELECT TBU_BAOperation_ID ");
		SQLGetData.append(" FROM TBU_BAOperation ");
		SQLGetData.append(" WHERE C_BPartner_ID = ?");
		SQLGetData.append(" AND DocStatus = 'CO'");
		SQLGetData.append(" AND IsInvoiced = 'N'");
		SQLGetData.append(" AND C_Invoice_ID IS NULL");
		SQLGetData.append(" AND GL_Journal_ID IS NULL");
		SQLGetData.append(" AND IsUnbilled = 'N'");

		SQLGetData.append(" AND DateOperation BETWEEN '"+p_StartDate+"' AND '"+p_EndDate+"'");
		SQLGetData.append(" AND C_DocType_ID = ?");
		if(p_C_Activity_ID > 0) {
			SQLGetData.append(" AND C_Activity_ID = ?");
		}
		
		if(p_C_Project_ID > 0) {
			SQLGetData.append(" AND C_Project_ID = ?");
		}

		if(p_ISM_Department_ID > 0) {
			SQLGetData.append(" AND ISM_Department_ID = ?");
		}
		
		BigDecimal SumGrandTotal = Env.ZERO;
		MJournal journal = new MJournal(getCtx(), p_GL_Journal_ID, get_TrxName());

		
		StringBuilder SQLGetDataLine = new StringBuilder();
		SQLGetDataLine.append("SELECT DISTINCT (TBU_OperationService_ID)");
		SQLGetDataLine.append(" FROM TBU_OperationLine");
		SQLGetDataLine.append(" WHERE TBU_BAOperation_ID IN (");
		
		String IDs = "";
		String SQLGetDataLineStr = "";
		
		
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(SQLGetData.toString(), null);
				pstmt.setInt(1,p_C_BPartner_ID);	
				pstmt.setInt(2,C_DocType_ID);	
				if(p_C_Activity_ID > 0) {
					pstmt.setInt(3,p_C_Activity_ID);	
				}
				if(p_C_Project_ID > 0) {
					pstmt.setInt(4,p_C_Project_ID);	
				}
				if(p_ISM_Department_ID > 0) {
					pstmt.setInt(5,p_ISM_Department_ID);	
				}
				
				int rowCountIndex = 0;

				rs = pstmt.executeQuery();
				while (rs.next()) {
					rowCountIndex++;


					X_TBU_BAOperation BAOp = new X_TBU_BAOperation(getCtx(), rs.getInt(1), get_TrxName());
					IDs = IDs+BAOp.getTBU_BAOperation_ID()+",";
					BAOp.setGL_Journal_ID(p_GL_Journal_ID);
					BAOp.saveEx();
					
					SumGrandTotal = SumGrandTotal.add(BAOp.getGrandTotal());

														
				}
				
			if(rowCountIndex == 0) {
					
					return "Tidak Ada Data BA Operation yang Tergenerate";
			}

						
			} catch (SQLException err) {
				
				log.log(Level.SEVERE, SQLGetData.toString(), err);
				rollback();
				
			} finally {
				
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
				
			}	
			
			
			
			if (IDs != null && IDs.length() > 0 && IDs.charAt(IDs.length() - 1) == ',') {
		        SQLGetDataLineStr = IDs.substring(0, IDs.length() - 1)+")"; 
		}
		 
		
		StringBuilder SQLGetLineBAOperation = new StringBuilder();
		SQLGetLineBAOperation.append("SELECT SUM(Qty*Price)");
		SQLGetLineBAOperation.append("	FROM TBU_OperationLine ");
		SQLGetLineBAOperation.append(" WHERE TBU_OperationService_ID = ?");
		SQLGetLineBAOperation.append(" AND TBU_BAOperation_ID IN (");
		
		
		
		StringBuilder SQLGetAccount = new StringBuilder();
		SQLGetAccount.append("SELECT Account_ID ");
		SQLGetAccount.append(" FROM C_ValidCombination ");
		SQLGetAccount.append(" WHERE C_ValidCombination_ID = ");
		
		StringBuilder SQLGetBPAcct = new StringBuilder();
		SQLGetBPAcct.append(" (SELECT V_Liability_Unbilled_Acct  ");
		SQLGetBPAcct.append(" FROM C_BP_Vendor_Acct ");
		SQLGetBPAcct.append(" WHERE C_BPartner_ID = "+p_C_BPartner_ID);
		SQLGetBPAcct.append(" AND C_AcctSchema_ID = 1000003)");
		
		
		int lineNo = 20;
		MJournalLine journalLine = null;
		
		PreparedStatement pstmtLine = null;
     	ResultSet rsLine = null;
			try {
				pstmtLine = DB.prepareStatement(SQLGetDataLine+SQLGetDataLineStr, null);
				
				rsLine = pstmtLine.executeQuery();
				while (rsLine.next()) {
					
					
					X_TBU_OperationService serv = new X_TBU_OperationService(getCtx(), rsLine.getInt(1), get_TrxName());
					
					journalLine = new MJournalLine(getCtx(), 0, get_TrxName());
					journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
					journalLine.setAD_Org_ID(journal.getAD_Org_ID());
					journalLine.setLine(lineNo);
					journalLine.setC_BPartner_ID(p_C_BPartner_ID);
					journalLine.setC_Currency_ID(journal.getC_Currency_ID());
								
					journalLine.setC_Activity_ID(p_C_Activity_ID);
					journalLine.setC_Project_ID(p_C_Project_ID);
					journalLine.set_CustomColumn("ISM_Department_ID", p_ISM_Department_ID);					
					journalLine.setDateAcct(journal.getDateAcct());
					journalLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());	
					journalLine.setDescription(journal.getDescription());					
						
					StringBuilder SQLGetChargeAcct = new StringBuilder();
					SQLGetChargeAcct.append(" (SELECT Ch_Expense_Acct  ");
					SQLGetChargeAcct.append(" FROM C_Charge_Acct ");
					SQLGetChargeAcct.append(" WHERE C_Charge_ID = "+serv.getC_Charge_ID());
					SQLGetChargeAcct.append(" AND C_AcctSchema_ID = 1000003)");
						
					BigDecimal price = DB.getSQLValueBD(get_TrxName(), SQLGetLineBAOperation.toString()+SQLGetDataLineStr, new Object[] {serv.getTBU_OperationService_ID()});
					int Account_ID = DB.getSQLValue(get_TrxName(), SQLGetAccount.toString()+SQLGetChargeAcct.toString());
					journalLine.setAccount_ID(Account_ID);
					journalLine.setAmtSourceDr(price);
					journalLine.setAmtSourceCr(Env.ZERO);
					journalLine.setAmtAcct(price, Env.ZERO);
						
					
				
					journalLine.saveEx();	
											
					lineNo = lineNo+10;
				}
				
				
				if(journalLine != null) {
					

					journalLine = new MJournalLine(getCtx(), 0, get_TrxName());
					journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
					journalLine.setAD_Org_ID(journal.getAD_Org_ID());
					journalLine.setLine(10);
					journalLine.setC_BPartner_ID(p_C_BPartner_ID);
					journalLine.setC_Currency_ID(journal.getC_Currency_ID());					
					
					journalLine.setC_Activity_ID(p_C_Activity_ID);
					journalLine.setC_Project_ID(p_C_Project_ID);
					journalLine.set_CustomColumn("ISM_Department_ID", p_ISM_Department_ID);					
					journalLine.setDateAcct(journal.getDateAcct());
					journalLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());	
					journalLine.setDescription(journal.getDescription());					
					
					int Account_ID = DB.getSQLValue(get_TrxName(), SQLGetAccount.toString()+SQLGetBPAcct.toString());
					
					if(Account_ID <= 0) {
						
						return "Bussines Partner Account Belum Ditentukan";
					}
					
					journalLine.setAccount_ID(Account_ID);
					journalLine.setAmtSourceDr(Env.ZERO);
					journalLine.setAmtSourceCr(SumGrandTotal);
					journalLine.setAmtAcct(Env.ZERO, SumGrandTotal);
					journalLine.saveEx();
					
				}

			
			} catch (SQLException err) {
				rollback();
				log.log(Level.SEVERE,SQLGetDataLine+SQLGetDataLineStr, err);
				
			} finally {
				
				DB.close(rsLine, pstmtLine);
				rsLine = null;
				pstmtLine = null;
				
			}	
		
		return null;
	}

}
