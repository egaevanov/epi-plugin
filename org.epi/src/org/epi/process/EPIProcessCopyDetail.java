package org.epi.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MInOutLine;
import org.compiere.model.MLocator;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.epi.model.X_C_OrderlineDtl;
import org.epi.model.X_M_InOutLineDtl;

public class EPIProcessCopyDetail extends SvrProcess{

	private int M_InOutLine_ID = 0;
	private int C_OrderLine_ID = 0;
	
	@Override
	protected void prepare() {

		M_InOutLine_ID = getRecord_ID();
			
	}

	@Override
	protected String doIt() throws Exception {
		
		MInOutLine line = new MInOutLine(getCtx(), M_InOutLine_ID, get_TrxName());
		C_OrderLine_ID = line.getC_OrderLine_ID();
		
		StringBuilder SQLGetSOLineDetail = new StringBuilder();
		
		SQLGetSOLineDetail.append("SELECT C_OrderlineDtl_ID ");
		SQLGetSOLineDetail.append(" FROM C_OrderlineDtl");
		SQLGetSOLineDetail.append(" WHERE AD_Client_ID = ?");
		SQLGetSOLineDetail.append(" AND C_OrderLine_ID = ?");
		
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(SQLGetSOLineDetail.toString(), null);
				pstmt.setInt(1,getAD_Client_ID());	
				pstmt.setInt(2,C_OrderLine_ID);	

				rs = pstmt.executeQuery();
				while (rs.next()) {

					X_C_OrderlineDtl SOLineDetail = new X_C_OrderlineDtl(getCtx(), rs.getInt(1), get_TrxName());
					X_M_InOutLineDtl InOutLineDetail = new X_M_InOutLineDtl(getCtx(), 0, get_TrxName());
					MLocator SOLocator = new MLocator(getCtx(), SOLineDetail.getM_Locator_ID(), get_TrxName());  
					
					InOutLineDetail.setAD_Org_ID(line.getAD_Org_ID());
					InOutLineDetail.setLineNo(SOLineDetail.getLineNo());
					InOutLineDetail.setM_InOutLine_ID(M_InOutLine_ID);
					InOutLineDetail.setC_OrderlineDtl_ID(SOLineDetail.getC_OrderlineDtl_ID());
					InOutLineDetail.setM_Locator_ID(SOLineDetail.getM_Locator_ID());
					InOutLineDetail.setM_Warehouse_ID(SOLocator.getM_Warehouse_ID());
					InOutLineDetail.setM_Product_ID(SOLineDetail.getM_Product_ID());
					InOutLineDetail.setC_UOM_ID(SOLineDetail.getC_UOM_ID());
					InOutLineDetail.setM_AttributeSetInstance_ID(SOLineDetail.getM_AttributeSetInstance_ID());
					InOutLineDetail.setDescription(SOLineDetail.getDescription());
					InOutLineDetail.setQtyInternalUse(SOLineDetail.getQtyInternalUse());
					InOutLineDetail.setPriceEntered(SOLineDetail.getPriceEntered());
					InOutLineDetail.setLineNetAmt(SOLineDetail.getLineNetAmt());
					InOutLineDetail.set_CustomColumn("M_SaveInv_ID", SOLineDetail.get_ValueAsInt("M_SaveInv_ID"));
					InOutLineDetail.saveEx();
					
				}

			} catch (SQLException err) {
				
				log.log(Level.SEVERE, SQLGetSOLineDetail.toString(), err);
				rollback();
				
			} finally {
				
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
				
			}	
		
		return "";
	}

}
