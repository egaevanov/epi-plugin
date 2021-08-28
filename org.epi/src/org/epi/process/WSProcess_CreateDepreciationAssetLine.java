package org.epi.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.model.MAsset;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.X_A_Depreciation_Exp_Cus;

public class WSProcess_CreateDepreciationAssetLine extends SvrProcess {
	
	private int p_GL_Journal = 0;
	private int p_AD_Client_ID = 0;
	private int p_AD_Org_ID = 0;
	private int p_C_Period_ID = 0;
	
	
	@Override
	protected void prepare() {
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("AD_Client_ID")) {
				p_AD_Client_ID  = (int)para[i].getParameterAsInt();
			}else if(name.equals("AD_Org_ID")) {
				p_AD_Org_ID  = (int)para[i].getParameterAsInt();
			}else if(name.equals("C_Period_ID")) {
				p_C_Period_ID  = (int)para[i].getParameterAsInt();
			}else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		p_GL_Journal = getRecord_ID();
		
	}

	@Override
	protected String doIt() throws Exception {

		MJournal journal = new MJournal(getCtx(), p_GL_Journal, get_TrxName());
		ArrayList<Integer> arrayDepreAss= new ArrayList<Integer>();
		
		StringBuilder SQLGetAsset = new StringBuilder();
		SQLGetAsset.append("SELECT A_Depreciation_Exp_Cus_ID ");
		SQLGetAsset.append(" FROM A_Depreciation_Exp_Cus ");
		SQLGetAsset.append(" WHERE AD_Client_ID = ?");
		SQLGetAsset.append(" AND AD_Org_ID = ?");
		SQLGetAsset.append(" AND C_Period_ID = ?");
		
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(SQLGetAsset.toString(), null);
				pstmt.setInt(1,p_AD_Client_ID);	
				pstmt.setInt(2,p_AD_Org_ID);	
				pstmt.setInt(3,p_C_Period_ID);	

				rs = pstmt.executeQuery();
				while (rs.next()) {

					X_A_Depreciation_Exp_Cus deprePost = new X_A_Depreciation_Exp_Cus(getCtx(), rs.getInt(1), get_TrxName());
					MAsset asset = new MAsset(getCtx(), deprePost.getA_Asset_ID(), get_TrxName());	
					
					
					arrayDepreAss.add(deprePost.getA_Depreciation_Exp_Cus_ID());
					
					for (int i = 0 ; i < 2 ; i++) {
					
						MJournalLine journalLine = new MJournalLine(getCtx(), 0, get_TrxName());
						
						journalLine.setGL_Journal_ID(p_GL_Journal);
						journalLine.setAD_Org_ID(p_AD_Org_ID);
						journalLine.setDescription(deprePost.getDescription());
						journalLine.setA_Asset_ID(asset.getA_Asset_ID());
						journalLine.setC_Currency_ID(journal.getC_Currency_ID());
						journalLine.setC_Project_ID(asset.getC_Project_ID());
						journalLine.setC_ConversionType_ID(journal.getC_ConversionType_ID());
						journalLine.setDateAcct(journal.getDateAcct());
												
						if(i == 0) {
							//debit
							journalLine.setAccount_ID(deprePost.getDR_Account_ID());
							journalLine.setAmtSourceDr(deprePost.getExpense());
							journalLine.setAmtSourceCr(Env.ZERO);
							journalLine.setAmtAcct(deprePost.getExpense(), Env.ZERO);
						}else if(i == 1) {
							//credit
							journalLine.setAccount_ID(deprePost.getCR_Account_ID());
							journalLine.setAmtSourceDr(Env.ZERO);
							journalLine.setAmtSourceCr(deprePost.getExpense());
							journalLine.setAmtAcct(Env.ZERO, deprePost.getExpense());					
						}
						
						journalLine.saveEx();

					}
					
					deprePost.setProcessed(true);
					deprePost.saveEx();
					
				}
				
				
				if(journal != null) {
					
					journal.processIt(MJournal.ACTION_Complete);
					
					if(journal.save()) {
						
						for (int i = 0 ; i < arrayDepreAss.size() ; i++) {
							
							X_A_Depreciation_Exp_Cus UpdatedeprePost = new X_A_Depreciation_Exp_Cus(getCtx(), arrayDepreAss.get(i), get_TrxName());
							MAsset asset = new MAsset(getCtx(), UpdatedeprePost.getA_Asset_ID(), get_TrxName());	
							
							asset.setAssetDepreciationDate(journal.getDateAcct());
							asset.setIsDepreciated(true);
							asset.setA_Asset_Status("DP");
							asset.saveEx();
							
							UpdatedeprePost.setProcessed(true);
							UpdatedeprePost.saveEx();

							
							
						}
						
						
						
					}
					
				}
				
										
			} catch (SQLException err) {
				
				log.log(Level.SEVERE, SQLGetAsset.toString(), err);
				rollback();
				
			} finally {
				
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
				
			}	
		
			
			
			
		
		return "";
	}

}
