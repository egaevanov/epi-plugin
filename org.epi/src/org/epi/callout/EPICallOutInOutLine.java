package org.epi.callout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_M_InOutLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class EPICallOutInOutLine extends CalloutEngine implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
		
		if(mField.getColumnName().equals(I_M_InOutLine.COLUMNNAME_C_OrderLine_ID)){
			return OrderDataChange(ctx, WindowNo, mTab, mField, value);
		}	
		
		return "";
	}

	
public String OrderDataChange (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value){
		
		Integer C_OrderLine_ID = (Integer)mTab.getValue(I_M_InOutLine.COLUMNNAME_C_OrderLine_ID);
		
		
		if(C_OrderLine_ID == null) {
			
			return"";
		}
		
		
		
		MOrderLine OrdLine = new MOrderLine(Env.getCtx(), C_OrderLine_ID, null);
		
		
		if (OrdLine.getC_Charge_ID() > 0 && OrdLine.getM_Product_ID() <= 0) {
			mTab.setValue("C_Charge_ID", Integer.valueOf(OrdLine.getC_Charge_ID()));
			mTab.setValue("M_Product_ID", null);
			mTab.setValue("M_AttributeSetInstance_ID", null);
		}
		else {
			mTab.setValue("M_Product_ID", Integer.valueOf(OrdLine.getM_Product_ID()));
			mTab.setValue("M_AttributeSetInstance_ID", Integer.valueOf(OrdLine.getM_AttributeSetInstance_ID()));
			mTab.setValue("C_Charge_ID", null);
			
			MProduct product = new MProduct(Env.getCtx(), OrdLine.getM_Product_ID(), null);
			mTab.setValue(I_M_InOutLine.COLUMNNAME_C_UOM_ID, product.getC_UOM_ID());
			
		}
				
		BigDecimal MovementQty = OrdLine.getQtyOrdered().subtract(OrdLine.getQtyDelivered());
		BigDecimal runningqty = DB.getSQLValueBDEx(null, "SELECT SUM(MovementQty) FROM M_InOutLine WHERE M_InOut_ID=? AND M_InOutLine_ID!=? AND C_OrderLine_ID=?",
				Env.getContextAsInt(ctx, WindowNo, "M_InOut_ID"),
				Env.getContextAsInt(ctx, WindowNo, "M_InOutLine_ID"),
				OrdLine.get_ID());
		if (runningqty != null) {
			MovementQty = MovementQty.subtract(runningqty); // IDEMPIERE-1140
		}
		mTab.setValue("MovementQty", MovementQty);
		BigDecimal QtyEntered = MovementQty;
		if (OrdLine.getQtyEntered().compareTo(OrdLine.getQtyOrdered()) != 0)
			QtyEntered = QtyEntered.multiply(OrdLine.getQtyEntered())
				.divide(OrdLine.getQtyOrdered(), 12, RoundingMode.HALF_UP);
		mTab.setValue("QtyEntered", QtyEntered);
		//
		mTab.setValue("C_Activity_ID", Integer.valueOf(OrdLine.getC_Activity_ID()));
		mTab.setValue("C_Campaign_ID", Integer.valueOf(OrdLine.getC_Campaign_ID()));
		mTab.setValue("C_Project_ID", Integer.valueOf(OrdLine.getC_Project_ID()));
		mTab.setValue("C_ProjectPhase_ID", Integer.valueOf(OrdLine.getC_ProjectPhase_ID()));
		mTab.setValue("C_ProjectTask_ID", Integer.valueOf(OrdLine.getC_ProjectTask_ID()));
		mTab.setValue("AD_OrgTrx_ID", Integer.valueOf(OrdLine.getAD_OrgTrx_ID()));
		mTab.setValue("User1_ID", Integer.valueOf(OrdLine.getUser1_ID()));
		mTab.setValue("User2_ID", Integer.valueOf(OrdLine.getUser2_ID()));
		

	return"";
	}
	
}
