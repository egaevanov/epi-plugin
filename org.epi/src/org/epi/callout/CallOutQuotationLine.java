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
import org.epi.model.X_TBU_OperationLine;
import org.epi.utils.FinalVariableGlobal;

public class CallOutQuotationLine extends CalloutEngine implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
	Integer AD_Org_ID = (Integer)mTab.getValue("AD_Org_ID");
		
		if(AD_Org_ID <=0)
			return"";		
		
		
		MOrg org = new MOrg(Env.getCtx(),AD_Org_ID, null);

		if(org.getValue().toUpperCase().equals(FinalVariableGlobal.ISM)) {
	
			if(mField.getColumnName().equals("M_Product_ID")){
				return CallOutUOM(ctx, WindowNo, mTab, mField, value);
			}else if(mField.getColumnName().equals("Qty")){
				return QuotationLineQtyChange(ctx, WindowNo, mTab, mField, value);
			}else if(mField.getColumnName().equals("Price")){
				return QuotationLineQtyChange(ctx, WindowNo, mTab, mField, value);
			}
			
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.EPI)) {
			
			/*
			 * TODO
			 */		
		}else if(org.getValue().toUpperCase().equals(FinalVariableGlobal.TBU)) {
			
			/*
			 * TODO
			 */		
		}
		return "";
	}
	
	

	public String CallOutUOM (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer M_Product_ID = (Integer)mTab.getValue("M_Product_ID");
		
		if(M_Product_ID == null) {
			return"";
		}
		
		
		MProduct prod = new MProduct(ctx, M_Product_ID, null);
		
		Integer C_UOM_ID = prod.getC_UOM_ID();
		
		if(C_UOM_ID == null || C_UOM_ID <= 0) {
			
			return "Anda Belum Menentukan UOM Pada Produk Yang Anda Pilih";
		}	
		
		mTab.setValue("C_UOM_ID", C_UOM_ID);

		return"";
	
	}
	
public String QuotationLineQtyChange (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		
		Integer C_Quotation_ID = (Integer)mTab.getValue("C_Quotation_ID");

		
		if(C_Quotation_ID == null) {
			C_Quotation_ID = 0;
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
