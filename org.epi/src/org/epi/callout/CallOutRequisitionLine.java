package org.epi.callout;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MOrg;
import org.compiere.util.Env;
import org.epi.model.X_ISM_Budget_Line;
import org.epi.utils.FinalVariableGlobal;

public class CallOutRequisitionLine  extends CalloutEngine implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		Integer AD_Org_ID = (Integer)mTab.getValue("AD_Org_ID");
		
		if(AD_Org_ID <=0)
			return"";		
		
		
		MOrg org = new MOrg(Env.getCtx(),AD_Org_ID, null);

		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			
			if(mField.getColumnName().equals("ISM_Budget_Line_ID")){
				return BudgetCodeChangeEPI(ctx, WindowNo, mTab, mField, value);
			}
			
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {
			
			/*
			 * TODO
			 */		
		}
		
		
		
		
		return "";
	}
	
	
	public String BudgetCodeChangeEPI (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer ISM_Budget_Line_ID = (Integer)mTab.getValue("ISM_Budget_Line_ID");
		
		if(ISM_Budget_Line_ID == null) {
			
			return"";
		}
		
		X_ISM_Budget_Line BudgetLine = new X_ISM_Budget_Line(ctx, ISM_Budget_Line_ID, null);
		
		mTab.setValue("Budget_Code_Desc", BudgetLine.getDescription());

	return"";
	
	}

}
