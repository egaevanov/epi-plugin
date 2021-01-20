package org.epi.callout;

import java.math.BigDecimal;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MOrg;
import org.compiere.util.Env;
import org.epi.model.X_TBU_OperationLine;
import org.epi.utils.FinalVariableGlobal;

public class CallOutBAOperationLine extends CalloutEngine implements IColumnCallout  {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
		
		Integer AD_Org_ID = (Integer)mTab.getValue("AD_Org_ID");
		
		if(AD_Org_ID == null) {
			AD_Org_ID = 0;
		}
		
		if(AD_Org_ID <=0)
			return"";		
		
		
		MOrg org = new MOrg(Env.getCtx(),AD_Org_ID, null);
		
		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.TBU)) {
			
			if(mField.getColumnName().equals(X_TBU_OperationLine.COLUMNNAME_Qty)){
				return TBUBAOperationLineQtyChange(ctx, WindowNo, mTab, mField, value);
			}else if(mField.getColumnName().equals(X_TBU_OperationLine.COLUMNNAME_Price)){
				return TBUBAOperationLineQtyChange(ctx, WindowNo, mTab, mField, value);
			}
			
			
		}
		
		
		
		return "";
	}
	
	
	public String TBUBAOperationLineQtyChange (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		
		Integer TBU_BAOperation_ID = (Integer)mTab.getValue("TBU_BAOperation_ID");

		
		if(TBU_BAOperation_ID == null) {
			TBU_BAOperation_ID = 0;
		}
		
		
		BigDecimal Qty = (BigDecimal)mTab.getValue(X_TBU_OperationLine.COLUMNNAME_Qty);
		BigDecimal price = (BigDecimal)mTab.getValue(X_TBU_OperationLine.COLUMNNAME_Price);
		
		
		if(Qty == null) {
			Qty = Env.ZERO;
		}
		
		if(price == null) {
			price = Env.ZERO;
		}
		
		
		if(price.compareTo(Env.ZERO) < 0 || Qty.compareTo(Env.ZERO) < 0 ) {
			
			return"";
		}
		
		if(price.compareTo(Env.ZERO)>= 0 ) {
			
			BigDecimal LineNetAmt = Qty.multiply(price);
			mTab.setValue("LineNetAmt", LineNetAmt);		
	
		}
		
		return"";
	
	}

}
