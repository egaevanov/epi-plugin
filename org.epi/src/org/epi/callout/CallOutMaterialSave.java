package org.epi.callout;

import java.math.BigDecimal;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MOrg;
import org.compiere.model.MProduct;
import org.compiere.util.Env;
import org.epi.model.I_M_SaveInv;
import org.epi.utils.FinalVariableGlobal;

public class CallOutMaterialSave extends CalloutEngine implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab,GridField mField, Object value, Object oldValue) {
		
		
	Integer AD_Org_ID = (Integer)mTab.getValue("AD_Org_ID");
		
		if(AD_Org_ID <=0)
			return"";		
		
		
		MOrg org = new MOrg(Env.getCtx(),AD_Org_ID, null);

		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			
			if(mField.getColumnName().equals(I_M_SaveInv.COLUMNNAME_M_Product_ID)){
				return ProductChangeEPI(ctx, WindowNo, mTab, mField, value);
			}else if(mField.getColumnName().equals(I_M_SaveInv.COLUMNNAME_QtyEntered)) {
				return QtyChangeEPI(ctx, WindowNo, mTab, mField, value);
			}else if(mField.getColumnName().equals(I_M_SaveInv.COLUMNNAME_PriceEntered)) {
				return PriceChangeEPI(ctx, WindowNo, mTab, mField, value);
			}
			
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {			
			/*
			 * TODO
			 */		
		}
		
	
		
		return "";
	}

	
	
	public String ProductChangeEPI (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer M_Product_ID = (Integer)mTab.getValue(I_M_SaveInv.COLUMNNAME_M_Product_ID);
		BigDecimal Qty = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_QtyEntered);
		BigDecimal price = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_PriceEntered);
		
		if(M_Product_ID == null) {
			
			return"";
		}
		
		MProduct product = new MProduct(ctx, M_Product_ID, null);
		
		mTab.setValue(I_M_SaveInv.COLUMNNAME_C_UOM_ID, product.getC_UOM_ID());
		
		if(price == null) {
			price = Env.ZERO;
		}
		
		if(Qty.compareTo(Env.ZERO)>= 0 && price.compareTo(Env.ZERO)>= 0) {
			
			BigDecimal GrandTotal = Qty.multiply(price);
			mTab.setValue(I_M_SaveInv.COLUMNNAME_GrandTotal, GrandTotal);		
		}

	return"";
	}
	
	
	public String QtyChangeEPI (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer M_Product_ID = (Integer)mTab.getValue(I_M_SaveInv.COLUMNNAME_M_Product_ID);
		BigDecimal Qty = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_QtyEntered);
		BigDecimal price = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_PriceEntered);

		if(price == null) {
			price = Env.ZERO;
		}
		
		if(Qty == null && M_Product_ID <= 0) {
			
			return"";
		}
		
				
		if(price.compareTo(Env.ZERO)>= 0 ) {
			
			BigDecimal GrandTotal = Qty.multiply(price);
			mTab.setValue(I_M_SaveInv.COLUMNNAME_GrandTotal, GrandTotal);
	
		}

		
		
	return"";
	}


	public String PriceChangeEPI (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
	
	Integer M_Product_ID = (Integer)mTab.getValue(I_M_SaveInv.COLUMNNAME_M_Product_ID);
	BigDecimal Qty = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_QtyEntered);
	BigDecimal price = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_PriceEntered);

	if(price == null) {
		price = Env.ZERO;
	}
	
	if(Qty == null && M_Product_ID <= 0) {
		
		return"";
	}
	
			
	if(price.compareTo(Env.ZERO)>= 0 ) {
		
		BigDecimal GrandTotal = Qty.multiply(price);
		mTab.setValue(I_M_SaveInv.COLUMNNAME_GrandTotal, GrandTotal);

	}

	return"";
	}

}