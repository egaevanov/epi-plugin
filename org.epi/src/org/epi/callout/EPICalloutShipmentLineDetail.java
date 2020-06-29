package org.epi.callout;

import java.math.BigDecimal;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.Env;
import org.epi.model.I_M_InOutLineDtl;

public class EPICalloutShipmentLineDetail  extends CalloutEngine implements IColumnCallout{

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		
		if(mField.getColumnName().equals(I_M_InOutLineDtl.COLUMNNAME_QtyInternalUse)) {
			return QtyChange(ctx, WindowNo, mTab, mField, value);

		}
		
		else if(mField.getColumnName().equals(I_M_InOutLineDtl.COLUMNNAME_PriceEntered)) {
			return PriceChange(ctx, WindowNo, mTab, mField, value);

		}
		
		
		return "";
	}
	
	public String QtyChange (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		BigDecimal Qty = (BigDecimal)mTab.getValue(I_M_InOutLineDtl.COLUMNNAME_QtyInternalUse);
		BigDecimal price = (BigDecimal)mTab.getValue(I_M_InOutLineDtl.COLUMNNAME_PriceEntered);
//		Integer M_InventoryLine_ID = (Integer) mTab.getValue("M_InventoryLine_ID");
//		Integer M_InOutLine_ID = (Integer) mTab.getValue(I_M_InOutLineDtl.COLUMNNAME_M_InOutLine_ID);
//		Integer M_InOutLineDtl_ID = (Integer) mTab.getValue(I_M_InOutLineDtl.COLUMNNAME_M_InOutLineDtl_ID);
		
//		if(M_InventoryLine_ID == null) {
//			M_InventoryLine_ID = 0;
//		}
		
		if( Qty == null || price == null) {
			
			return"";
		}
					
		if(price.compareTo(Env.ZERO)>= 0 ) {
			
			BigDecimal LineNetAmt = Qty.multiply(price);
			mTab.setValue(I_M_InOutLineDtl.COLUMNNAME_LineNetAmt, LineNetAmt);		
	
		}
		
//		Integer M_SaveInv_ID = (Integer)mTab.getValue("M_SaveInv_ID");
//		if(M_SaveInv_ID == null) {
//			M_SaveInv_ID = 0;
//		}
		
//		if(M_SaveInv_ID >= 0) {
//			
//			X_M_SaveInv CoalReceipt = new X_M_SaveInv(ctx, M_SaveInv_ID, null);
//			MInOutLine ShipLine = new MInOutLine(ctx, M_InOutLine_ID, null);
//			MProduct productLine = new MProduct(ctx, ShipLine.getM_Product_ID(), null);
//			X_M_InOutLineDtl ShipLineDtl = new X_M_InOutLineDtl(ctx, M_InOutLineDtl_ID, null);
//			BigDecimal AvailableCoalQty = Env.ZERO;
//					
//			if(M_InventoryLine_ID == 0 && !productLine.get_ValueAsBoolean("IsAutoShipment")) {
//			
//				AvailableCoalQty = ShipLineDtl.getQtyInternalUse().add(ISMQtyValidatorGlobalOH.getAvailableCoalQtyPerLocator(CoalReceipt.getM_Product_ID(),CoalReceipt.getM_Locator_ID(),0,CoalReceipt.get_TrxName()));
//			}
//			BigDecimal QtyInternalUse = (BigDecimal)mTab.getValue(I_C_OrderlineDtl.COLUMNNAME_QtyInternalUse);
//			if(QtyInternalUse.compareTo(AvailableCoalQty) > 0) {
//				
//				mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_QtyInternalUse, AvailableCoalQty);
//				mTab.setValue(I_C_OrderlineDtl.COLUMNNAME_LineNetAmt, AvailableCoalQty.multiply(price));	
//
//				return "Max Qty Available for this Product : "+AvailableCoalQty;
//				
//			}
//			
//		}

	return"";

	}
	
	public String PriceChange (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
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
