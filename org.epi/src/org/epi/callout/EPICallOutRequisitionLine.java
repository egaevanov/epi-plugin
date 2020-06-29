package org.epi.callout;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.epi.model.X_ISM_Budget_Line;

public class EPICallOutRequisitionLine  extends CalloutEngine implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		if(mField.getColumnName().equals("ISM_Budget_Line_ID")){
			return BudgetCodeChange(ctx, WindowNo, mTab, mField, value);
		}
		
		return "";
	}
	
	
	public String BudgetCodeChange (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer ISM_Budget_Line_ID = (Integer)mTab.getValue("ISM_Budget_Line_ID");
		
		if(ISM_Budget_Line_ID == null) {
			
			return"";
		}
		
		X_ISM_Budget_Line BudgetLine = new X_ISM_Budget_Line(ctx, ISM_Budget_Line_ID, null);
		
		mTab.setValue("Budget_Code_Desc", BudgetLine.getDescription());

	return"";
	
	}

}
