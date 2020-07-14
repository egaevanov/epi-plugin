package org.epi.validator;

import java.math.BigDecimal;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrg;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.epi.model.X_C_OrderlineDtl;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class EPIOrdLineDtlValidator {
	
	public static CLogger log = CLogger.getCLogger(EPIOrdLineDtlValidator.class);

	public static String executeOrdLineDtl(Event event, PO po) {
		
		String msgOrdLineDtl = "";
		X_C_OrderlineDtl ordLineDtl = (X_C_OrderlineDtl) po;
		
		MOrg org = new MOrg(ordLineDtl.getCtx(), ordLineDtl.getAD_Org_ID(), null);

		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			if (event.getTopic().equals(IEventTopics.PO_AFTER_CHANGE)||event.getTopic().equals(IEventTopics.PO_AFTER_NEW)||event.getTopic().equals(IEventTopics.PO_AFTER_DELETE)) {			
				msgOrdLineDtl = beforeSaveEPI(ordLineDtl);	
			}
			
		}
		
	return msgOrdLineDtl;

	}
	
	private static String beforeSaveEPI(X_C_OrderlineDtl LineDtl) {
		
		String rslt = "";
		
		MOrderLine OrdLine = new MOrderLine(LineDtl.getCtx(), LineDtl.getC_OrderLine_ID(), LineDtl.get_TrxName());
		int C_Order_ID = OrdLine.getC_Order_ID();
		MOrder ord = new MOrder(LineDtl.getCtx(), C_Order_ID, null);
		
		if(ord.isSOTrx()) {
		
			StringBuilder getCountLineDtl = new StringBuilder();
			getCountLineDtl.append("SELECT COUNT(C_OrderlineDtl_ID)");
			getCountLineDtl.append(" FROM C_OrderlineDtl ");
			getCountLineDtl.append(" WHERE AD_Client_ID = " + LineDtl.getAD_Client_ID());
			getCountLineDtl.append(" AND C_OrderLine_ID = "+ LineDtl.getC_OrderLine_ID());
			Integer cntLine = DB.getSQLValueEx(LineDtl.get_TrxName(), getCountLineDtl.toString());
						
			StringBuilder getLineNet = new StringBuilder();
			getLineNet.append("SELECT SUM(LineNetAmt)");
			getLineNet.append(" FROM C_OrderlineDtl ");
			getLineNet.append(" WHERE AD_Client_ID = " + LineDtl.getAD_Client_ID());
			getLineNet.append(" AND C_OrderLine_ID = "+ LineDtl.getC_OrderLine_ID());
			
			BigDecimal LineNetAmt = DB.getSQLValueBD(LineDtl.get_TrxName(), getLineNet.toString());
			
			if(cntLine > 0) {
				OrdLine.setPriceEntered(LineNetAmt.divide(OrdLine.getQtyEntered()));
				OrdLine.setPriceActual(LineNetAmt.divide(OrdLine.getQtyEntered()));
				//OrdLine.setLineNetAmt(LineNetAmt);
				OrdLine.saveEx();
			}
		}
		
		return rslt;
		
	}
	

}
