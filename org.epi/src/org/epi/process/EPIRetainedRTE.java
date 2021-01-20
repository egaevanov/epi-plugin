package org.epi.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MPeriod;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.epi.model.X_Fact_Acct_RELog;

public class EPIRetainedRTE extends SvrProcess{

	private int p_AD_Client_ID = 0;
	private int p_AD_Org_ID = 0;
	private int p_C_AcctSchema_ID = 0;
	private int p_C_Period_ID = 0;
	private int p_fact_acct_relog_id = 0;
	
	@Override
	protected void prepare() {
		
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
						
			if (para[i].getParameter() == null)
				;		
			else if (name.equals("AD_Client_ID")) {
				p_AD_Client_ID = para[i].getParameterAsInt();	
			}else if (name.equals("AD_Org_ID")) {
				p_AD_Org_ID = para[i].getParameterAsInt();	
			}else if (name.equals("C_AcctSchema_ID")) {
				p_C_AcctSchema_ID = para[i].getParameterAsInt();	
			}else if (name.equals("C_Period_ID")) {
				p_C_Period_ID = para[i].getParameterAsInt();	
			}else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
	}

	@Override
	protected String doIt() throws Exception {
	
	StringBuilder SQLCheckCYE = new StringBuilder();
		
		boolean IsExist = false;
		
		SQLCheckCYE.append("SELECT fact_acct_relog_id,gl_journal_id ");
		SQLCheckCYE.append(" FROM fact_acct_relog far ");
		SQLCheckCYE.append(" left join Fact_Acct_RE re ON far.Fact_Acct_RE_id = re.Fact_Acct_RE_id ");
		SQLCheckCYE.append(" WHERE far.AD_Client_ID = ? ");
		SQLCheckCYE.append(" AND far.AD_Org_ID = ? ");
//		SQLCheckCYE.append(" AND C_AcctSchema_ID = ?");
		SQLCheckCYE.append(" AND C_Period_ID = ? ");
		SQLCheckCYE.append(" AND re.Value = 'RTE' ");

		
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(SQLCheckCYE.toString(), null);
				pstmt.setInt(1,p_AD_Client_ID);	
				pstmt.setInt(2,p_AD_Org_ID);	
				pstmt.setInt(3,p_C_Period_ID);	
				//pstmt.setInt(4,p_C_AcctSchema_ID);	


				rs = pstmt.executeQuery();
				while (rs.next()) {
					
					p_fact_acct_relog_id = rs.getInt(1);
					
					if(p_fact_acct_relog_id > 0) {
						
						IsExist = true;
						
					}
					
				}

			} catch (SQLException err) {
				
				log.log(Level.SEVERE, SQLCheckCYE.toString(), err);
			
				
			} finally {
				
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
				
			}	
			
			
			
			if(IsExist) {
				X_Fact_Acct_RELog RELog = new X_Fact_Acct_RELog(getCtx(), p_fact_acct_relog_id, get_TrxName());
								
				ReActiveDocument(RELog.getGL_Journal_ID());
				
				MJournal journal = new MJournal(getCtx(), RELog.getGL_Journal_ID(), get_TrxName());
				MJournalLine[] lines = journal.getLines(false);

				
				StringBuilder SQLGetLine = new StringBuilder();
				SQLGetLine.append("SELECT coa_id,dr,cr,re_hdr");
				SQLGetLine.append(" FROM f_get_value_process_re(?,?,?,?,?)");
			
				
				PreparedStatement pstmtLine = null;
		     	ResultSet rsLine = null;
					try {
						pstmtLine = DB.prepareStatement(SQLGetLine.toString(), null);
						pstmtLine.setInt(1,p_AD_Client_ID);	
						pstmtLine.setInt(2,p_AD_Org_ID);	
						pstmtLine.setInt(3,p_C_AcctSchema_ID);	
						pstmtLine.setInt(4,p_C_Period_ID);	
						pstmtLine.setString(5,"RTE");

						rsLine = pstmtLine.executeQuery();
						while (rsLine.next()) {
							
							
							for(MJournalLine line: lines) {
								MJournalLine journalLine = new MJournalLine(getCtx(), line.getGL_JournalLine_ID(), get_TrxName());
								
								journalLine.setAccount_ID(rsLine.getInt(1));
								journalLine.setAmtSourceDr(rsLine.getBigDecimal(2));
								journalLine.setAmtSourceCr(rsLine.getBigDecimal(3));
								journalLine.setAmtAcct(rsLine.getBigDecimal(2), rsLine.getBigDecimal(3));
								journalLine.saveEx();
								
							}
						
						}

					} catch (SQLException err) {
						
						log.log(Level.SEVERE, SQLGetLine.toString(), err);
					
						
					} finally {
						
						DB.close(rsLine, pstmtLine);
						rsLine = null;
						pstmtLine = null;
						
					}	
				
					journal.processIt(MJournal.ACTION_Complete);
					journal.saveEx();
					
				
			}else {
				
				
				MJournal journal = new MJournal(getCtx(), 0, get_TrxName());
				int count = 0;
				
				MPeriod period  = new MPeriod(getCtx(), p_C_Period_ID, get_TrxName());	
				MAcctSchema acctSchema = new MAcctSchema(getCtx(), p_C_AcctSchema_ID, get_TrxName());
			
				int p_re_hdr_id = 0;
				
				journal.setAD_Org_ID(p_AD_Org_ID);
				journal.setC_AcctSchema_ID(p_C_AcctSchema_ID);
				journal.setDescription("GL Retained Earnings");
				journal.setPostingType("A");
				journal.setDateAcct(period.getEndDate());
				journal.setC_Period_ID(p_C_Period_ID);
				journal.setDateDoc(period.getEndDate());
				journal.setC_Currency_ID(acctSchema.getC_Currency_ID());
				journal.setC_DocType_ID(1000164);
				journal.setGL_Category_ID(1000057);
				journal.setC_ConversionType_ID(114);
				journal.saveEx();
				
				
				StringBuilder SQLGetLine = new StringBuilder();
				SQLGetLine.append("SELECT coa_id,dr,cr,re_hdr");
				SQLGetLine.append(" FROM f_get_value_process_re(?,?,?,?,?)");
			
				
				PreparedStatement pstmtLine = null;
		     	ResultSet rsLine = null;
					try {
						pstmtLine = DB.prepareStatement(SQLGetLine.toString(), null);
						pstmtLine.setInt(1,p_AD_Client_ID);	
						pstmtLine.setInt(2,p_AD_Org_ID);	
						pstmtLine.setInt(3,p_C_AcctSchema_ID);	
						pstmtLine.setInt(4,p_C_Period_ID);	
						pstmtLine.setString(5,"RTE");

						rsLine = pstmtLine.executeQuery();
						while (rsLine.next()) {
						
						MJournalLine journalLine = new MJournalLine(getCtx(), 0, get_TrxName());
						p_re_hdr_id = rsLine.getInt(4);
						journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
						journalLine.setAD_Org_ID(p_AD_Org_ID);
						journalLine.setDescription(journal.getDescription());
						journalLine.setC_Currency_ID(journal.getC_Currency_ID());
						journalLine.setAccount_ID(rsLine.getInt(1));
						journalLine.setAmtSourceDr(rsLine.getBigDecimal(2));
						journalLine.setAmtSourceCr(rsLine.getBigDecimal(3));
						journalLine.setAmtAcct(rsLine.getBigDecimal(2), rsLine.getBigDecimal(3));
						journalLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());
						journalLine.setDateAcct(period.getEndDate());
						journalLine.saveEx();
						
						count++;
						
						}

					} catch (SQLException err) {
						
						log.log(Level.SEVERE, SQLGetLine.toString(), err);
					
						
					} finally {
						
						DB.close(rsLine, pstmtLine);
						rsLine = null;
						pstmtLine = null;
						
					}	
				
				
			
				if(count > 0) {
					
					
					journal.processIt(MJournal.ACTION_Complete);
					if(journal.save()) {
												
						X_Fact_Acct_RELog RELog = new X_Fact_Acct_RELog(getCtx(), 0, get_TrxName());
						
						RELog.setAD_Org_ID(p_AD_Org_ID);
						RELog.setFact_Acct_RE_ID(p_re_hdr_id);
						RELog.setGL_Journal_ID(journal.getGL_Journal_ID());
						RELog.saveEx();
						
					}
	
				}
				
			}

		
		
		return "";
	}
	
	public void ReActiveDocument(int GL_Journal_ID){
		
		MJournal journal = new MJournal(getCtx(), GL_Journal_ID, get_TrxName());
//		MJournalLine[] lines  = journal.getLines(false);

		
		if(journal != null && journal.getDocStatus().equals(MJournal.DOCSTATUS_Completed)) {
			
			if(journal.processIt(MJournal.DOCACTION_Re_Activate)) 
				journal.saveEx();
//
//				
//				
//				StringBuilder SQLGetLine = new StringBuilder();
//				SQLGetLine.append("SELECT coa_id,dr,cr,re_hdr");
//				SQLGetLine.append(" FROM f_get_value_process_re(?,?,?,?,?)");
//			
//				
//				PreparedStatement pstmtLine = null;
//		     	ResultSet rsLine = null;
//					try {
//						pstmtLine = DB.prepareStatement(SQLGetLine.toString(), null);
//						pstmtLine.setInt(1,p_AD_Client_ID);	
//						pstmtLine.setInt(2,p_AD_Org_ID);	
//						pstmtLine.setInt(3,p_C_AcctSchema_ID);	
//						pstmtLine.setInt(4,p_C_Period_ID);	
//						pstmtLine.setString(5,"CYE");
//	
//						rsLine = pstmtLine.executeQuery();
//						while (rsLine.next()) {
//						
//						
//							for(MJournalLine line : lines) {
//								
//								
//								MJournalLine jLine = new MJournalLine(getCtx(), line.getGL_JournalLine_ID(), get_TrxName());
//								jLine.setAmtSourceDr(rsLine.getBigDecimal(2));
//								jLine.setAmtSourceCr(rsLine.getBigDecimal(3));
//								jLine.saveEx();
//								
//							}
//							
//						
//						
//						}
//	
//					} catch (SQLException err) {
//						
//						log.log(Level.SEVERE, SQLGetLine.toString(), err);
//					
//						
//					} finally {
//						
//						DB.close(rsLine, pstmtLine);
//						rsLine = null;
//						pstmtLine = null;
//						
//					}	
//				
//					
//					journal.processIt(MJournal.ACTION_Complete);
//					journal.saveEx();
//					
//				}
			
		}
		
		
	}

}
