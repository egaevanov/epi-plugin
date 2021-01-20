package org.epi.validator;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MOrg;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.epi.model.X_TBU_BAOperation;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class TBUBAOperationValidator {
	

	public static CLogger log = CLogger.getCLogger(TBUBAOperationValidator.class);

	public static String executeTBUBAOperation(Event event, PO po) {
		
		String msgBAOp= "";
		X_TBU_BAOperation OpLine = (X_TBU_BAOperation) po;
		
		MOrg org = new MOrg(OpLine.getCtx(), OpLine.getAD_Org_ID(), null);
		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.TBU)) {
			if (event.getTopic().equals(IEventTopics.DOC_BEFORE_REACTIVATE)) {
				msgBAOp = beforeReActiveTBU(OpLine,event);	
			}
		}
	return msgBAOp;

	}
	
	
	private static String beforeReActiveTBU(X_TBU_BAOperation BAOp,Event event) {
		
	
		
		if(BAOp.isInvoiced()) {
			
			return "Document Cant Re-Activated Because Already Invoiced";
		}
		
		return"";
	}
	
	

}
