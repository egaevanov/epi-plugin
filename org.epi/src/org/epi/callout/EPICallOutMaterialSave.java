package org.epi.callout;

import java.math.BigDecimal;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MProduct;
import org.compiere.util.Env;
import org.epi.model.I_M_SaveInv;

public class EPICallOutMaterialSave extends CalloutEngine implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab,GridField mField, Object value, Object oldValue) {
		
		if(mField.getColumnName().equals(I_M_SaveInv.COLUMNNAME_M_Product_ID)){
			return ProductChange(ctx, WindowNo, mTab, mField, value);
		}		
		
		else if(mField.getColumnName().equals(I_M_SaveInv.COLUMNNAME_QtyEntered)) {
			return QtyChange(ctx, WindowNo, mTab, mField, value);

		}
		
		else if(mField.getColumnName().equals(I_M_SaveInv.COLUMNNAME_PriceEntered)) {
			return PriceChange(ctx, WindowNo, mTab, mField, value);

		}
		
		return "";
	}

	
	
	public String ProductChange (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer M_Product_ID = (Integer)mTab.getValue(I_M_SaveInv.COLUMNNAME_M_Product_ID);
		BigDecimal Qty = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_QtyEntered);
		BigDecimal price = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_PriceEntered);
		
		if(M_Product_ID == null) {
			
			return"";
		}
		
		MProduct product = new MProduct(ctx, M_Product_ID, null);
		
		mTab.setValue(I_M_SaveInv.COLUMNNAME_C_UOM_ID, product.getC_UOM_ID());
		
		if(Qty.compareTo(Env.ZERO)>= 0 && price.compareTo(Env.ZERO)>= 0) {
			
			BigDecimal GrandTotal = Qty.multiply(price);
			mTab.setValue(I_M_SaveInv.COLUMNNAME_GrandTotal, GrandTotal);		
		}

	return"";
	}
	
	
	public String QtyChange (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer M_Product_ID = (Integer)mTab.getValue(I_M_SaveInv.COLUMNNAME_M_Product_ID);
		BigDecimal Qty = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_QtyEntered);
		BigDecimal price = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_PriceEntered);

		
		if(Qty == null && M_Product_ID <= 0) {
			
			return"";
		}
		
				
		if(price.compareTo(Env.ZERO)>= 0 ) {
			
			BigDecimal GrandTotal = Qty.multiply(price);
			mTab.setValue(I_M_SaveInv.COLUMNNAME_GrandTotal, GrandTotal);
	
		}

		
		
	return"";
	}


	public String PriceChange (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
	
	Integer M_Product_ID = (Integer)mTab.getValue(I_M_SaveInv.COLUMNNAME_M_Product_ID);
	BigDecimal Qty = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_QtyEntered);
	BigDecimal price = (BigDecimal)mTab.getValue(I_M_SaveInv.COLUMNNAME_PriceEntered);

	
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