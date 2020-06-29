package org.epi.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MInvoiceLine;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.model.X_C_InvoiceLineDtl;
import org.epi.model.X_M_InOutLineDtl;

public class EPIProcessInvoiceLineDtl extends SvrProcess{
	
	private int C_InvoiceLine_ID = 0;
	private int M_InoutLine_ID = 0;

	@Override
	protected void prepare() {

		
	C_InvoiceLine_ID = getRecord_ID();
		
	}

	@Override
	protected String doIt() throws Exception {

		MInvoiceLine invLine = new MInvoiceLine(getCtx(), C_InvoiceLine_ID, get_TrxName());

		
		StringBuilder SQLGetShipLineDetail = new StringBuilder();
		M_InoutLine_ID = invLine.getM_InOutLine_ID();
		
		SQLGetShipLineDetail.append("SELECT M_InOutLineDtl_ID ");
		SQLGetShipLineDetail.append(" FROM M_InOutLineDtl");
		SQLGetShipLineDetail.append(" WHERE AD_Client_ID = ?");
		SQLGetShipLineDetail.append(" AND M_InOutLine_ID = ?");
		
		int lineCnt = 10;
		BigDecimal LineNetAmt = Env.ZERO;
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(SQLGetShipLineDetail.toString(), null);
				pstmt.setInt(1,getAD_Client_ID());	
				pstmt.setInt(2,M_InoutLine_ID);	

				rs = pstmt.executeQuery();
				while (rs.next()) {

					X_M_InOutLineDtl ShipLineDetail = new X_M_InOutLineDtl(getCtx(), rs.getInt(1), get_TrxName());
					X_C_InvoiceLineDtl InvLineDetail = new X_C_InvoiceLineDtl(getCtx(), 0, get_TrxName());
					
					InvLineDetail.setAD_Org_ID(invLine.getAD_Org_ID());
					InvLineDetail.setC_InvoiceLine_ID(C_InvoiceLine_ID);
					InvLineDetail.setLineNo(lineCnt);
					InvLineDetail.setM_Product_ID(ShipLineDetail.getM_Product_ID());
					InvLineDetail.setPriceEntered(ShipLineDetail.getPriceEntered());
					InvLineDetail.setDescription(ShipLineDetail.getDescription());
					InvLineDetail.set_CustomColumn("C_OrderLineDtl_ID", ShipLineDetail.getC_OrderlineDtl_ID());
					InvLineDetail.set_CustomColumn("QtyInternalUse", ShipLineDetail.getQtyInternalUse());
					InvLineDetail.set_CustomColumn("LineNetAmt", ShipLineDetail.getLineNetAmt());
					InvLineDetail.saveEx();
					
					lineCnt = lineCnt+10;
					
					
					
					LineNetAmt = LineNetAmt.add((BigDecimal) InvLineDetail.get_Value("LineNetAmt"));

					
				}
				
				invLine.setPriceEntered(LineNetAmt);
				invLine.setPriceActual(LineNetAmt);
				invLine.saveEx();

			} catch (SQLException err) {
				
				log.log(Level.SEVERE, SQLGetShipLineDetail.toString(), err);
				rollback();
				
			} finally {
				
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
				
			}	
		
		return "";
	}

}
