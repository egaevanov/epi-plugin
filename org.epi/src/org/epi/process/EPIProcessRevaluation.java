package org.epi.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MJournal;
import org.compiere.model.MOrg;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.epi.utils.FinalVariableGlobal;

public class EPIProcessRevaluation extends SvrProcess{

	
	private int p_C_BankAccount_ID = 0;
	private int p_GL_Journal_ID = 0;
	
	
	@Override
	protected void prepare() {
		
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("C_BankAccount_ID"))
				p_C_BankAccount_ID  = (int)para[i].getParameterAsInt();
		
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		p_GL_Journal_ID = getRecord_ID();
		
	}

	@Override
	protected String doIt() throws Exception {
		
		if(p_GL_Journal_ID <= 0) {
			return "";
		}

	
			
		
		MJournal journal = new MJournal(getCtx(), p_GL_Journal_ID, get_TrxName());
		
		MOrg org = new MOrg(journal.getCtx(), journal.getAD_Org_ID(), null);

		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			
			Integer C_Convertion_Rate_ID = 0;
			
			C_Convertion_Rate_ID = EPICheckCurrency.ConvertionRateCheck(getAD_Client_ID(), journal.getC_Currency_ID(), journal.getDateAcct(), get_TrxName());
			
			if(C_Convertion_Rate_ID == null) {
				C_Convertion_Rate_ID = 0;
			}
			
			if(C_Convertion_Rate_ID <= 0) {
				return "Currency Rate Setup Is Not Available";
			}
			
			StringBuilder SQLExecFuncRevaluation = new StringBuilder();
			SQLExecFuncRevaluation.append("SELECT f_epi_get_bc_revaluation(?,?)");
			
			PreparedStatement pstmt = null;
	     	ResultSet rs = null;
				try {
					pstmt = DB.prepareStatement(SQLExecFuncRevaluation.toString(), null);
					pstmt.setInt(1, p_GL_Journal_ID);	
					pstmt.setInt(2, p_C_BankAccount_ID);	
					
					rs = pstmt.executeQuery();
					while (rs.next()) {
						
					}

				} catch (SQLException err) {
					log.log(Level.SEVERE, SQLExecFuncRevaluation.toString(), err);
				} finally {
					DB.close(rs, pstmt);
					rs = null;
					pstmt = null;
				}
			
		}
		
		
		
		
		return "";
	}

}
