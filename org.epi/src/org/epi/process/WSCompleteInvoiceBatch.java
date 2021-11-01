package org.epi.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MInvoice;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

public class WSCompleteInvoiceBatch extends SvrProcess{

	
	private int C_Invoice_Batch_ID = 0;
	
	@Override
	protected void prepare() {
		
		C_Invoice_Batch_ID = getRecord_ID();
		
	}

	@Override
	protected String doIt() throws Exception {
	
		StringBuilder SQLGetInv  = new StringBuilder();
		
		SQLGetInv.append("SELECT C_Invoice_ID ");
		SQLGetInv.append(" FROM C_Invoice ");
		SQLGetInv.append(" WHERE C_Invoice_Batch_ID = ?");
		SQLGetInv.append(" AND DocStatus NOT IN ('CO') ");
		
		
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				
				pstmt = DB.prepareStatement(SQLGetInv.toString(), null);
				pstmt.setInt(1, C_Invoice_Batch_ID);	
			
				rs = pstmt.executeQuery();
				while (rs.next()) {
					
					int C_Invoice_ID = rs.getInt(1);
					
					MInvoice inv = new MInvoice(getCtx(), C_Invoice_ID, get_TrxName());
					inv.processIt(MInvoice.DOCACTION_Complete);
					inv.saveEx();
										
				}

			} catch (SQLException err) {
				log.log(Level.SEVERE, SQLGetInv.toString(), err);
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		
		return "";
	}

}
