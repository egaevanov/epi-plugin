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
import org.epi.model.I_C_OrderlineDtl;
import org.epi.model.X_M_SaveInv;
import org.epi.process.EPIQtyValidatorGlobalOH;
import org.epi.utils.FinalVariableGlobal;

public class CallOutOrderLineDetail extends CalloutEngine implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab,GridField mField, Object value, Object oldValue) {
		
	Integer AD_Org_ID = (Integer)mTab.getValue("AD_Org_ID");
		
		if(AD_Org_ID <=0)
			return"";		
		
		
		MOrg org = new MOrg(Env.getCtx(),AD_Org_ID, null);
		
		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
		
			if(mField.getColumnName().equals(I_C_OrderlineDtl.COLUMNNAME_M_Product_ID)){
				return ProductChangeEPI(ctx, WindowNo, mTab, mField, value);	
			}else if(mField.getColumnName().equals(I_C_OrderlineDtl.COLUMNNAME_QtyInternalUse)) {
				return QtyChangeEPI(ctx, WindowNo, mTab, mField, value);
			}else if(mField.getColumnName().equals(I_C_OrderlineDtl.COLUMNNAME_PriceEntered)) {
				return PriceChangeEPI(ctx, WindowNo, mTab, mField, value);
			}else if(mField.getColumnName().equals("M_SaveInv_ID")) {
				return CoalReceiptInputEPI(ctx, WindowNo, mTab, mField, value);
			}
			
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {
			
			/*
			 * TODO
			 */		
		}
		
		
		
		
		return "";
	}

	
	
	public String ProductChangeEPI (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer M_Product_ID = (Integer)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_M_Product_ID);
		BigDecimal Qty = (BigDecimal)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_QtyInternalUse);
		BigDecimal price = (BigDecimal)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_PriceEntered);
		
		if(M_Product_ID == null) {
			
			return"";
		}
		
		MProduct product = new MProduct(ctx, M_Product_ID, null);
		
		mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_C_UOM_ID, product.getC_UOM_ID());
		
		if(Qty.compareTo(Env.ZERO)>= 0 && price.compareTo(Env.ZERO)>= 0) {
			
			BigDecimal GrandTotal = Qty.multiply(price);
			mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_LineNetAmt, GrandTotal);		
		}

	return"";
	}
	
	
	public String QtyChangeEPI (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer M_Product_ID = (Integer)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_M_Product_ID);
		BigDecimal Qty = (BigDecimal)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_QtyInternalUse);
		BigDecimal price = (BigDecimal)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_PriceEntered);
		
		if(Qty == null && M_Product_ID <= 0) {
			
			return"";
		}
		
				
		if(price.compareTo(Env.ZERO)>= 0 ) {
			
			BigDecimal GrandTotal = Qty.multiply(price);
			mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_LineNetAmt, GrandTotal);		
	
		}

		
		Integer M_SaveInv_ID = (Integer)mTab.getValue("M_SaveInv_ID");
		if(M_SaveInv_ID == null) {
			M_SaveInv_ID = 0;
		}
		
		if(M_SaveInv_ID >= 0) {
			BigDecimal QtyAvailable = (BigDecimal)mTab.getValue("QtyAvailable");
			BigDecimal QtyInternalUse = (BigDecimal)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_QtyInternalUse);
			if(QtyInternalUse.compareTo(QtyAvailable) > 0) {
				
				mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_QtyInternalUse, QtyAvailable);
				mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_LineNetAmt, QtyAvailable.multiply(price));	

				return "Max Qty Available for this Product : "+QtyAvailable;
				
			}
			
		}
		
		
	return"";
	}


	public String PriceChangeEPI (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
	
		Integer M_Product_ID = (Integer)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_M_Product_ID);
		BigDecimal Qty = (BigDecimal)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_QtyInternalUse);
		BigDecimal price = (BigDecimal)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_PriceEntered);

	
	if(Qty == null && M_Product_ID <= 0) {
		
		return"";
	}
	
			
	if(price.compareTo(Env.ZERO)>= 0 ) {
		
		BigDecimal GrandTotal = Qty.multiply(price);
		mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_LineNetAmt, GrandTotal);		

	}

	return"";
	}
	
	
	public String CoalReceiptInputEPI (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
				
		Integer M_SaveInv_ID = (Integer)mTab.getValue("M_SaveInv_ID");
		BigDecimal Qty = (BigDecimal)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_QtyInternalUse);
		
		if(M_SaveInv_ID == null) {
			M_SaveInv_ID = 0;
		}
		
		if(M_SaveInv_ID <= 0 ) {
			return "";
		}
		
		X_M_SaveInv CoalReceipt = new X_M_SaveInv(ctx, M_SaveInv_ID, null);
		
		BigDecimal AvailableCoalQty = EPIQtyValidatorGlobalOH.getAvailableCoalQtyPerLocator(CoalReceipt.getM_Product_ID(),CoalReceipt.getM_Locator_ID(),0,CoalReceipt.get_TrxName());

		BigDecimal priceReceipt = (BigDecimal) CoalReceipt.get_Value("PriceEntered");
		
		mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_PriceEntered, priceReceipt);
		mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_M_Locator_ID, CoalReceipt.getM_Locator_ID());
		mTab.setValue("QtyAvailable", AvailableCoalQty);
		
		MProduct product = new MProduct(ctx,  CoalReceipt.getM_Product_ID(), null);
		if(product.isActive()) {
			mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_M_Product_ID, CoalReceipt.getM_Product_ID());
			mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_C_UOM_ID, product.getC_UOM_ID());

		}else {
			mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_M_Product_ID, null);
			return "Product "+product.getName()+" Is Not Active, Please Check Master Product";
		}
		if(Qty.compareTo(Env.ZERO) >= 0) {
			
			mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_LineNetAmt,priceReceipt.multiply(Qty));
		}


	return"";
	}

}