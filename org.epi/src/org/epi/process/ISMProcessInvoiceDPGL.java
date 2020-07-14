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

public class ISMProcessInvoiceDPGL extends SvrProcess {
	

	private int p_C_Invoice_ID = 0;
	private int p_GL_Journal_ID = 0;
	

	@Override
	protected void prepare() {
		
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("C_Invoice_ID"))
				p_C_Invoice_ID  = (int)para[i].getParameterAsInt();
		
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

		MJournal GLJournal = new MJournal(getCtx(), p_GL_Journal_ID, null);
		MOrg org = new MOrg(GLJournal.getCtx(), GLJournal.getAD_Org_ID(), null);

		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {
			
			StringBuilder SQLExecFuncGLInvoiceDP = new StringBuilder();
			SQLExecFuncGLInvoiceDP.append("SELECT f_ism_get_invoice_dp(?,?)");
			
			PreparedStatement pstmt = null;
	     	ResultSet rs = null;
				try {
					pstmt = DB.prepareStatement(SQLExecFuncGLInvoiceDP.toString(), null);
					pstmt.setInt(1, p_GL_Journal_ID);	
					pstmt.setInt(2, p_C_Invoice_ID);	
					
					rs = pstmt.executeQuery();
					while (rs.next()) {
						
					}

				} catch (SQLException err) {
					log.log(Level.SEVERE, SQLExecFuncGLInvoiceDP.toString(), err);
				} finally {
					DB.close(rs, pstmt);
					rs = null;
					pstmt = null;
				}
			
		}
		
		
		
		return "";
	}
		

}
