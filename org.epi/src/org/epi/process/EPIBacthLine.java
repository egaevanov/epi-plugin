package org.epi.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MJournalBatch;
import org.compiere.model.MOrg;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.epi.utils.FinalVariableGlobal;

public class EPIBacthLine extends SvrProcess {
	
	private int p_C_Period_ID = 0;
	private int p_GL_JournalBatch_ID = 0;
	
	
	@Override
	protected void prepare() {
		
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("C_Period_ID"))
				p_C_Period_ID  = (int)para[i].getParameterAsInt();
		
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		p_GL_JournalBatch_ID = getRecord_ID();
		
	}


	@Override
	protected String doIt() throws Exception {

		if(p_GL_JournalBatch_ID < 0)
			return"";
		
		MJournalBatch batch = new MJournalBatch(getCtx(), p_GL_JournalBatch_ID, get_TrxName());
				
		MOrg org = new MOrg(batch.getCtx(), batch.getAD_Org_ID(), null);

		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)||
				org.getValue().toUpperCase().equals(FinalVariableGlobal.TBU)||
					org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {
						
			StringBuilder SQLExecFuncRevaluation = new StringBuilder();
			SQLExecFuncRevaluation.append("SELECT get_glbatch_line(?,?)");
			
			PreparedStatement pstmt = null;
	     	ResultSet rs = null;
				try {
					pstmt = DB.prepareStatement(SQLExecFuncRevaluation.toString(), null);
					pstmt.setInt(1, p_GL_JournalBatch_ID);	
					pstmt.setInt(2, p_C_Period_ID);	
					
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
