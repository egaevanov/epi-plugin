package org.epi.validator;

import java.math.BigDecimal;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MOrg;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.epi.model.MBAOperation;
import org.epi.model.X_TBU_OperationLine;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class TBUBAOperationLineValidator {
	
	
	
	public static CLogger log = CLogger.getCLogger(TBUBAOperationLineValidator.class);

	public static String executeTBUOpLine(Event event, PO po) {
		
		String msgOpLine= "";
		X_TBU_OperationLine OpLine = (X_TBU_OperationLine) po;
		
		MOrg org = new MOrg(OpLine.getCtx(), OpLine.getAD_Org_ID(), null);
		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.TBU)) {
			if (event.getTopic().equals(IEventTopics.PO_AFTER_NEW) ||event.getTopic().equals(IEventTopics.PO_AFTER_CHANGE) ) {
				msgOpLine = beforeSaveTBU(OpLine,event);	
			}
		}
	return msgOpLine;

	}
	
	
	private static String beforeSaveTBU(X_TBU_OperationLine OpLine,Event event) {
		MBAOperation BAOp = new MBAOperation(OpLine.getCtx(), OpLine.getTBU_BAOperation_ID(), OpLine.get_TrxName());
		
		X_TBU_OperationLine[] Lines = BAOp.getLines();
		BigDecimal grandTotal = Env.ZERO;
		for(X_TBU_OperationLine Line : Lines) {	
			grandTotal = grandTotal.add(Line.getLineNetAmt());	
		}
		BAOp.setGrandTotal(grandTotal);
		BAOp.saveEx();

		
		return"";
	}
	
	
	
}
