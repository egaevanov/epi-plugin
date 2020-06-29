package org.epi.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

public class EPIProcessCreatePOFromPR extends SvrProcess {

	
	
	private int p_order = 0;
	private int p_requisition = 0;
	
	@Override
	protected void prepare() {

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null);
			
			else if(name.equals("C_Order_ID"))
				p_order  = (int)para[i].getParameterAsInt();
			
			else if(name.equals("M_Requisition_ID"))
				p_requisition  = (int)para[i].getParameterAsInt();
			
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
	}

	@Override
	protected String doIt() throws Exception {
		StringBuilder SQLPRToPOFunction = new StringBuilder();
		SQLPRToPOFunction.append("SELECT adempiere.f_create_line_po(?,?)");

		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(SQLPRToPOFunction.toString(), null);
				pstmt.setInt(1, p_order);	
				pstmt.setInt(2, p_requisition);	
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					
				}

			} catch (SQLException err) {
				log.log(Level.SEVERE, SQLPRToPOFunction.toString(), err);
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		
		return "";
	}


}
