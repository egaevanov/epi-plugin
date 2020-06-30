package org.epi.callout;

import java.math.BigDecimal;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MOrg;
import org.compiere.util.Env;
import org.epi.model.I_M_InOutLineDtl;
import org.epi.utils.FinalVariableGlobal;

public class CallOutShipmentLineDetail  extends CalloutEngine implements IColumnCallout{

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		
		Integer AD_Org_ID = (Integer)mTab.getValue("AD_Org_ID");
		
		if(AD_Org_ID <=0)
			return"";		
		
		
		MOrg org = new MOrg(Env.getCtx(),AD_Org_ID, null);
		
		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			
			if(mField.getColumnName().equals(I_M_InOutLineDtl.COLUMNNAME_QtyInternalUse)) {
				return QtyChangeEPI(ctx, WindowNo, mTab, mField, value);
			}else if(mField.getColumnName().equals(I_M_InOutLineDtl.COLUMNNAME_PriceEntered)) {
				return PriceChangeEPI(ctx, WindowNo, mTab, mField, value);
			}
			
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {
			
			/*
			 * TODO
			 */		
		}
		
		
		
		return "";
	}
	
	public String QtyChangeEPI (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		BigDecimal Qty = (BigDecimal)mTab.getValue(I_M_InOutLineDtl.COLUMNNAME_QtyInternalUse);
		BigDecimal price = (BigDecimal)mTab.getValue(I_M_InOutLineDtl.COLUMNNAME_PriceEntered);

		if( Qty == null || price == null) {
			
			return"";
		}
					
		if(price.compareTo(Env.ZERO)>= 0 ) {
			
			BigDecimal LineNetAmt = Qty.multiply(price);
			mTab.setValue(I_M_InOutLineDtl.COLUMNNAME_LineNetAmt, LineNetAmt);		
	
		}
		


	return"";

	}
	
	public String PriceChangeEPI (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		BigDecimal Qty = (BigDecimal)mTab.getValue(I_M_InOutLineDtl.COLUMNNAME_QtyInternalUse);
		BigDecimal price = (BigDecimal)mTab.getValue(I_M_InOutLineDtl.COLUMNNAME_PriceEntered);

	
		if( Qty == null || price == null) {
			
			return"";
		}
			
		if(price.compareTo(Env.ZERO)>= 0 ) {
		
			BigDecimal LineNetAmt = Qty.multiply(price);
			mTab.setValue(I_M_InOutLineDtl.COLUMNNAME_LineNetAmt, LineNetAmt);			

		}

	return "";
	
	}

}
