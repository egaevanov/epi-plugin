package org.epi.validator;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MElementValue;
import org.compiere.model.MOrg;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.epi.utils.FinalVariableGlobal;
import org.osgi.service.event.Event;

public class EPIElementValueValidator {
	
	
	public static CLogger log = CLogger.getCLogger(EPIElementValueValidator.class);

	public static String executeElementValue(Event event, PO po) {
		
		String msgelValue= "";
		MElementValue elValue = (MElementValue) po;
		
		int AD_Org_ID = (int) elValue.get_ValueOld("AD_Org_ID");
		
		if(AD_Org_ID == 0) {
			
			AD_Org_ID = Env.getAD_Org_ID(Env.getCtx());
			
		}
		
		
		MOrg org = new MOrg(elValue.getCtx(), AD_Org_ID, null);
	
		
		if(event.getTopic().equals(IEventTopics.PO_AFTER_CHANGE)) {		

			if(org.getValue().equals(FinalVariableGlobal.EPI)||org.getValue().equals(FinalVariableGlobal.ISM)) {	
				msgelValue = afterSaveEPI(elValue);
			}
		}else if(event.getTopic().equals(IEventTopics.PO_AFTER_NEW)) {
			
			if(org.getValue().equals(FinalVariableGlobal.EPI)||org.getValue().equals(FinalVariableGlobal.ISM)) {	
				msgelValue = afterNewEPI(elValue);
			}
		}	
		
	return msgelValue;

	}
	
	
	public static String afterNewEPI(MElementValue elValue) {
		String rslt = "";

		
		int AD_Org_ID = (int) elValue.get_ValueOld("AD_Org_ID");
		
		if(AD_Org_ID == 0) {
			AD_Org_ID = Env.getAD_Org_ID(Env.getCtx());
		}
		
		StringBuilder SQLUpdate = new StringBuilder();
		SQLUpdate.append("UPDATE C_ElementValue ");
		SQLUpdate.append(" SET AD_Org_ID = "+AD_Org_ID);
		SQLUpdate.append(" WHERE C_ElementValue_ID = "+elValue.getC_ElementValue_ID());
		DB.executeUpdate(SQLUpdate.toString(), elValue.get_TrxName());
		
		
		return rslt;
	}
	
	public static String afterSaveEPI(MElementValue elValue) {
		String rslt = "";

		
		int AD_Org_ID = (int) elValue.get_ValueOld("AD_Org_ID");
		
		StringBuilder SQLUpdate = new StringBuilder();
		SQLUpdate.append("UPDATE C_ElementValue ");
		SQLUpdate.append(" SET AD_Org_ID = "+AD_Org_ID);
		SQLUpdate.append(" WHERE C_ElementValue_ID = "+elValue.getC_ElementValue_ID());
		DB.executeUpdate(SQLUpdate.toString(), elValue.get_TrxName());
		
		
		return rslt;
	}
	

}
