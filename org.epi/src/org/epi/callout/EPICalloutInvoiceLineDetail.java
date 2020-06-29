package org.epi.callout;

import java.math.BigDecimal;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MProduct;
import org.compiere.util.Env;
import org.epi.model.I_C_InvoiceLineDtl;
import org.epi.model.I_C_OrderlineDtl;
import org.epi.model.X_M_SaveInv;
import org.epi.process.EPIQtyValidatorGlobalOH;

public class EPICalloutInvoiceLineDetail extends CalloutEngine implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
		
		if(mField.getColumnName().equals("M_SaveInv_ID")){
			return CoalReceiptInput(ctx, WindowNo, mTab, mField, value);
		}else if(mField.getColumnName().equals("QtyInternalUse"))	{
			return QtyChange(ctx, WindowNo, mTab, mField, value);
		}else if(mField.getColumnName().equals("PriceEntered"))	{
			return PriceChange(ctx, WindowNo, mTab, mField, value);
		}
		
		return "";
	}

	public String CoalReceiptInput (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		

		Integer M_SaveInv_ID = (Integer)mTab.getValue("M_SaveInv_ID");
		BigDecimal Qty = (BigDecimal)mTab.getValue("QtyInternalUse");
		
		if(M_SaveInv_ID == null) {
			M_SaveInv_ID = 0;
		}
		
		if(M_SaveInv_ID <= 0 ) {
			return "";
		}
		
		X_M_SaveInv CoalReceipt = new X_M_SaveInv(ctx, M_SaveInv_ID, null);
		
		BigDecimal AvailableCoalQty = EPIQtyValidatorGlobalOH.getAvailableCoalQtyPerLocator(CoalReceipt.getM_Product_ID(),CoalReceipt.getM_Locator_ID(),0,CoalReceipt.get_TrxName());

		BigDecimal priceReceipt = (BigDecimal) CoalReceipt.get_Value("PriceEntered");
		
		mTab.setValue(I_C_InvoiceLineDtl.COLUMNNAME_PriceEntered, priceReceipt);		
		
		MProduct product = new MProduct(ctx,  CoalReceipt.getM_Product_ID(), null);
		if(product.isActive()) {
			mTab.setValue(I_C_InvoiceLineDtl.COLUMNNAME_M_Product_ID, CoalReceipt.getM_Product_ID());
		}else {
			mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_M_Product_ID, null);
			return "Product "+product.getName()+" Is Not Active, Please Check Master Product";
		}
		
		if(Qty.compareTo(Env.ZERO) >= 0) {
			
			if(Qty.compareTo(AvailableCoalQty) >= 0) {
				mTab.setValue("QtyInternalUse",AvailableCoalQty);
				Qty = (BigDecimal)mTab.getValue("QtyInternalUse");
			}
			
			mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_LineNetAmt,priceReceipt.multiply(Qty));
		}
		

		return"";
	}
	
	public String PriceChange (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer M_Product_ID = (Integer)mTab.getValue(I_C_InvoiceLineDtl.COLUMNNAME_M_Product_ID);
		BigDecimal Qty = (BigDecimal)mTab.getValue("QtyInternalUse");
		BigDecimal price = (BigDecimal)mTab.getValue(I_C_InvoiceLineDtl.COLUMNNAME_PriceEntered);

	
		if(Qty == null && M_Product_ID <= 0) {
			
			return"";
		}
		
				
		if(price.compareTo(Env.ZERO)>= 0 ) {
			
			BigDecimal LineNetAmt = Qty.multiply(price);
			mTab.setValue("LineNetAmt", LineNetAmt);		
	
		}

	return"";
	
	}
	
	public String QtyChange (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer M_Product_ID = (Integer)mTab.getValue(I_C_InvoiceLineDtl.COLUMNNAME_M_Product_ID);
		BigDecimal Qty = (BigDecimal)mTab.getValue("QtyInternalUse");
		BigDecimal price = (BigDecimal)mTab.getValue(I_C_InvoiceLineDtl.COLUMNNAME_PriceEntered);
		
		if(Qty == null && M_Product_ID <= 0) {
			
			return"";
		}
		
				
		if(price.compareTo(Env.ZERO)>= 0 ) {
			
			BigDecimal LineNetAmt = Qty.multiply(price);
			mTab.setValue("LineNetAmt", LineNetAmt);		
	
		}

		
		Integer M_SaveInv_ID = (Integer)mTab.getValue("M_SaveInv_ID");
		if(M_SaveInv_ID == null) {
			M_SaveInv_ID = 0;
		}
		
		if(M_SaveInv_ID >= 0) {
			BigDecimal QtyAvailable = (BigDecimal)mTab.getValue("QtyAvailable");
			BigDecimal QtyInternalUse = (BigDecimal)mTab.getValue("QtyInternalUse");
			
			if(QtyInternalUse.compareTo(QtyAvailable) > 0) {
				
				mTab.setValue("QtyInternalUse", QtyAvailable);
				mTab.setValue("LineNetAmt", QtyAvailable.multiply(price));	

				return "Max Qty Available for this Product : "+QtyAvailable;
				
			}
			
		}
		
		
	return"";
	}
	
}
